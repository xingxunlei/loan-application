package com.loan_server.app_service;

import com.alibaba.fastjson.JSONObject;
import com.jinhuhang.risk.service.juxinli.JuxinliAPI;
import com.loan_api.app.JuxinliService;
import com.loan_api.app.LoanService;
import com.loan_entity.Juxinli.FeatureDto;
import com.loan_entity.Juxinli.ReqDtoBasicInfo;
import com.loan_entity.Juxinli.ReqDtoBasicInfoContact;
import com.loan_entity.app.*;
import com.loan_entity.enums.JuxinliEnum;
import com.loan_entity.enums.SpecialUserEnum;
import com.loan_entity.manager.Review;
import com.loan_entity.manager_vo.ReqBackPhoneCheckVo;
import com.loan_server.app_mapper.*;
import com.loan_server.manager_mapper.ReviewMapper;
import com.loan_utils.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.JedisCluster;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by xuepengfei on 2017/10/20.
 */
public class JuxinliServiceImpl implements JuxinliService {

    private static Logger log = Logger.getLogger(JuxinliServiceImpl.class);

    @Autowired
    private LoanService loanService;
    @Autowired
    private PersonMapper personMapper;
    @Autowired
    private ContactMapper contactMapper;
    @Autowired
    private PrivateMapper privateMapper;
    @Autowired
    private CardMapper cardMapper;
    @Autowired
    private ReviewMapper reviewMapper;
    @Autowired
    private BorrowListMapper borrowListMapper;
    @Autowired
    private JedisCluster jedisCluster;
    @Autowired
    private JuxinliAPI juxinliAPI;


    //聚信立回调地址
    private  String jxlCallbackUrl = PropertiesReaderUtil.read("third", "jxlCallbackUrl");
    private  String productId = PropertiesReaderUtil.read("third", "productId");

    @Override
    public NoteResult backPhoneCheckMessage(ReqBackPhoneCheckVo callback) {
        //风控结果状态
        NoteResult result = new NoteResult(JuxinliEnum.JXL_ERROR.getCode(),"失败");
        try {
            Person person = personMapper.getPersonByPhone(callback.getPhone());
            Integer per_id = person.getId();
            //两种情况不允许再回调
            //1.借款状态不为申请中或者已取消，返回回调失败
            BorrowList borrowList = borrowListMapper.selectNow(per_id);
            if (!(CodeReturn.STATUS_APLLY.equals(borrowList.getBorrStatus()) || CodeReturn.STATUS_CANCEL.equals(borrowList.getBorrStatus()))){
                result.setCode(JuxinliEnum.JXL_SUCCESS.getCode());
                result.setInfo("借款状态有误，回调失败");
                return result;
            }
            //2.requestId 不是该用户当前的聚信立token，返回回调失败
            String juxinliToken = jedisCluster.get(RedisConst.JUXINLI_TOKEN + per_id);
            if (StringUtils.isEmpty(juxinliToken) || !callback.getRequestId().equals(juxinliToken)){
                //token为空或者 传过来的requestId 不为当前的token
                result.setCode(JuxinliEnum.JXL_SUCCESS.getCode());
                result.setInfo("token错误");
                return result;
            }
            //没有返回回调失败 继续走流程
            // 通过
            if (JuxinliEnum.JXL_SUCCESS.getCode().equals(callback.getNode_status())) {
                //解除聚信立5条风控规则 ，
                //   isManual 1审核 2解除   type 1通讯录 2聚信立
                loanService.manuallyReview(per_id.toString(), 2, 2, callback.getDescription());
                loanService.createBpmNode(per_id.toString(), "5", CodeReturn.STATUS_BPM_Y, callback.getDescription());
            }
            if (JuxinliEnum.JXL_REFUSE.getCode().equals(callback.getNode_status())) {
                // 认证失败 把手机节点改为NS003 借款状态改为审核未通过
                loanService.createBpmNode(per_id.toString(), "5", CodeReturn.STATUS_BPM_FAIL, callback.getDescription());
                borrowList.setBorrStatus(CodeReturn.STATUS_REVIEW_FAIL);
                if(borrowListMapper.updateByPrimaryKeySelective(borrowList)<1){
                    //借款状态没有更新成功 返回失败
                    result.setInfo("借款状态没有更新成功，回调失败");
                    return result;
                }
            }

            if (JuxinliEnum.JXL_MENUAL.getCode().equals(callback.getNode_status())) {
                //触碰聚信立5条风控规则 ，增加人工审核.相当于通过
                //             isManual 1审核 2解除   type 1通讯录 2聚信立
                loanService.manuallyReview(per_id.toString(), 2, 1, callback.getDescription());
                loanService.createBpmNode(per_id.toString(), "5", CodeReturn.STATUS_BPM_Y, callback.getDescription());
            }
            if (JuxinliEnum.JXL_ERROR.getCode().equals(callback.getNode_status())) {
                //异常 ，改回到NS001
                //             isManual 1审核 2解除   type 1通讯录 2聚信立
                loanService.createBpmNode(per_id.toString(), "5", CodeReturn.STATUS_BPM_N, callback.getDescription());
            }
            result.setCode(JuxinliEnum.JXL_SUCCESS.getCode());
            result.setInfo("成功");

        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return result;
    }

    @Override
    public NoteResult goBlack(String Phone) {
        NoteResult result = new NoteResult(CodeReturn.FAIL_CODE,"Fail");
        try {
            Person person = personMapper.getPersonByPhone(Phone);
            person.setBlacklist("Y");

            // 插入拉黑记录
            Review review = new Review();
            review.setBorrId(person.getId());
            review.setCreateDate(new Date());
            review.setReason("聚信立拉黑");
            review.setReviewType("3");
            review.setStatus("Y");
            review.setEmployNum(SpecialUserEnum.USER_SYS.getCode());

            if (personMapper.updateByPrimaryKeySelective(person)+reviewMapper.insert(review)>1){
                result.setCode(CodeReturn.SUCCESS_CODE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;

    }

    @Override
    public NoteResult risk(ReqDtoBasicInfo reqDtoBasicInfo) {
        NoteResult result = new NoteResult(JuxinliEnum.JXL_ERROR.getCode(),"手机认证异常");

        try {
            String per_id = reqDtoBasicInfo.getPer_id();
            String requestId;
            String password = reqDtoBasicInfo.getPassword();
            //先查出 person
            Person person = personMapper.selectByPrimaryKey(Integer.valueOf(per_id));
            if (person == null) {
                result.setInfo("用户不存在");
                return result;
            }
            // command_code为空  第一步，上传服务密码
            if (StringUtils.isEmpty(reqDtoBasicInfo.getCode())) {
                //存服务密码
                person.setPhoneService(Base64.encode(reqDtoBasicInfo.getPassword()));
                personMapper.updateByPrimaryKeySelective(person);
                // 生成redis key  存入requestId
                requestId = BorrNum_util.createBorrNum();
                jedisCluster.set(RedisConst.JUXINLI_TOKEN + per_id, requestId);
                jedisCluster.expire(RedisConst.JUXINLI_TOKEN + per_id, 24 * 60 * 60 * 1000);

            } else {
                //command_code不为空 不是第一步 取服务密码
                password = Base64.decode(person.getPhoneService());
                //如果redis key 失效了 返回重新认证
                if (StringUtils.isEmpty(jedisCluster.get(RedisConst.JUXINLI_TOKEN + per_id))) {
                    result.setInfo("验证超时，请返回重新认证");
                    return result;
                }
                //redis key 没有失效
                requestId = jedisCluster.get(RedisConst.JUXINLI_TOKEN + per_id);

            }

            //封装dubbo接口的第一个参数
            reqDtoBasicInfo.setRequestId(requestId);
            reqDtoBasicInfo.setPassword(password);
            Card card = cardMapper.selectByPerId(Integer.valueOf(per_id));
            String name = card.getName();
            String idValue = card.getCardNum();
            String cellPhone = person.getPhone();
            Private info = privateMapper.selectByPerId(Integer.valueOf(per_id));
            String address = info.getUsuallyaddress();
            String corpAddress = info.getBusiAddress();
            String corpTel = info.getBusiPhone();
            reqDtoBasicInfo.setName(name);
            reqDtoBasicInfo.setId_card_num(idValue);
            reqDtoBasicInfo.setHome_addr(address);
            reqDtoBasicInfo.setWork_addr(corpAddress);
            reqDtoBasicInfo.setWork_tel(corpTel);
            reqDtoBasicInfo.setCell_phone_num(cellPhone);

            //封装2个联系人信息
            List<ReqDtoBasicInfoContact> list = new ArrayList<>();

            ReqDtoBasicInfoContact relatives = new ReqDtoBasicInfoContact();
            relatives.setContact_tel(info.getRelaPhone());
            //亲属姓名
            relatives.setContact_name(info.getRelativesName());
            //亲属关系（0.1.2.3）
            relatives.setContact_type(info.getRelatives());
            list.add(relatives);

            ReqDtoBasicInfoContact society = new ReqDtoBasicInfoContact();
            society.setContact_tel(info.getSociPhone());
            //社会关系姓名
            society.setContact_name(info.getSocietyName());
            //社会关系（4.5.6）
            society.setContact_type(info.getSociety());
            list.add(society);
            reqDtoBasicInfo.setBaseInfoContacts(list);
            //通讯录
            List<Contact> contacts = contactMapper.selectAllByPerId(Integer.valueOf(per_id));
            if (contacts.size() == 1) {
                //通讯录文件存储 传文件地址
                reqDtoBasicInfo.setRelation_contacts_url(contacts.get(0).getContactName());
            }
            //封装dubbo接口方法的第二个参数 回调地址
            FeatureDto featureDto = new FeatureDto();
            featureDto.setCallbackURI(jxlCallbackUrl);

            log.info("聚信立请求参数："+Integer.valueOf(productId)+","+JSONObject.toJSONString(reqDtoBasicInfo)+"+"+JSONObject.toJSONString(featureDto));
            String requestResult = juxinliAPI.runRequestReport(Integer.valueOf(productId),JSONObject.toJSONString(reqDtoBasicInfo),JSONObject.toJSONString(featureDto));
            log.info("聚信立返回参数："+requestResult);
            String code = JSONObject.parseObject(requestResult).getString("code");
            String msg = JSONObject.parseObject(requestResult).getString("msg");

            //根据请求结果 APP后台改变用户节点的状态及人工审核状态等。
            if (JuxinliEnum.JXL_SUCCESS.getCode().equals(code)) {
                //0000：成功	状态为NS002	跳转下一认证节点
                loanService.createBpmNode(per_id, "5", CodeReturn.STATUS_BPM_Y, msg);
                //过人工审核模型
                //   isManual 1审核 2解除   type 1通讯录 2聚信立
                loanService.manuallyReview(per_id, 2, 2, msg);

            } else if (JuxinliEnum.JXL_REFUSE.getCode().equals(code)) {
                //1000：失败	状态为NS003	拒绝，提示信用等级过低
                loanService.createBpmNode(per_id, "5", CodeReturn.STATUS_BPM_FAIL, msg);
                //更改借款状态为审核未通过
                BorrowList borrowList = borrowListMapper.selectNow((Integer.valueOf(per_id)));
                borrowList.setBorrStatus(CodeReturn.STATUS_REVIEW_FAIL);
                if (borrowListMapper.updateByPrimaryKeySelective(borrowList)<1){
                    //borrowList没有更新成功 返回异常
                    result.setInfo("数据更新失败");
                    return result;
                }
            } else if (JuxinliEnum.JXL_ERROR.getCode().equals(code)) {
                //2000：异常-- 展示后台信息	保持原来的状态 NS001
                loanService.createBpmNode(per_id, "5", CodeReturn.STATUS_BPM_N, msg);
                result.setInfo(msg);
            } else if (JuxinliEnum.JXL_MENUAL.getCode().equals(code)) {
                //8888：人工审核	状态为NS002	跳转下一认证节点
                loanService.createBpmNode(per_id, "5", CodeReturn.STATUS_BPM_Y, msg);
                //过人工审核模型
                //   isManual 1审核 2解除   type 1通讯录 2聚信立
                loanService.manuallyReview(per_id, 2, 1, msg);

            } else if (JuxinliEnum.JXL_COLLECTING.getCode().equals(code)) {
                //10008：已受理采集	状态为NS004	跳转下一认证节点
                loanService.createBpmNode(per_id, "5", CodeReturn.STATUS_BPM_UP, msg);
            } else {
                //其余（10002,10003,10001,10022） 状态不变 NS001 传code给APP
                loanService.createBpmNode(per_id, "5", CodeReturn.STATUS_BPM_N, msg);
            }

            //APP后台改变完成，把code传回APP前端（只有2000异常时传msg）
            result.setCode(code);
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
    }
}
