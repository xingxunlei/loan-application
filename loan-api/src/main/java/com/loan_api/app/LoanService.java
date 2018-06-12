package com.loan_api.app;

import java.util.List;

import com.loan_entity.Juxinli.ReqDtoBasicInfo;
import com.loan_entity.app.NoteResult;

/**
 *  借款模块接口
 * @author xuepengfei
 *2016年9月28日上午9:31:58
 */
public interface LoanService {



    /**
     * 用户借款状态节点
     * @param per_id 用户ID
     * @return
     */
    public NoteResult getBorrStatus(String per_id);
    
    /**
     * 获得产品金额
     * @param per_id 用户ID
     * @return 
     */
    public NoteResult getProductsMoney(String per_id);
    
    /**
     * 获得产品天数
     * @param per_id 用户ID
     * @param money 产品金额
     * @return 
     */
    public NoteResult getProductsday(String per_id,String money);
    
    /**
     * 根据金额和天数获得产品id
     * @param per_id 用户ID
     * @param money 产品金额
     * @param day 产品天数
     * @return 
     */
    public NoteResult getProductId(String per_id,String money,String day);
    
    /**
     * 选择产品金额和天数,获得产品ID及所有费用
     * @param per_id 用户ID
     * @return 
     */
    public NoteResult getProductCharge(Integer productId, String per_id,String money,String day);
    
    /**
     * 生成借款记录
     * @param per_id 用户ID
     * @param product_id 产品ID
     * @return
     */
    public NoteResult borrowProduct(String per_id,String product_id,String plan_repay,String borr_amount,String day,String coupon_id);
    
    /**
     * 检查用户是否认证完成
     * @param per_id 用户ID
     * @return
     */
    public NoteResult checkBpm(String per_id);
    
    /**
     * 新建认证流程
     * @param per_id 用户ID
     * @return
     */
    public NoteResult insertPerBpm(String per_id);
    
    /**
     * 插入身份证正面信息
     * @param per_id 用户ID
     * @param card_json 身份证信息json串
     * @param card_byte 身份证正面照片流
     * @return
     */
    public NoteResult insertCardInfoz(String per_id,String card_num,String name,String sex,
                               String nation,String birthday,String address,
                               String card_byte,String head_byte,String description);
    
    /**
     * 插入身份证反面信息
     * @param per_id 用户ID
     * @param card_json 身份证信息json串
     * @param card_byte 身份证反面照片流
     * @return
     */
    public NoteResult insertCardInfof(String per_id,String office,String start_date,
                               String end_date,String card_byte,String description);
    
    
    /**
     * 插入个人信息
     * @param per_id 用户ID @param qq_num QQ号  @param email 邮箱
     * @param usuallyaddress 常用地址 @param education 学历
     * @param marry 婚姻状况  @param getchild 生育状况
     * @param profession 职业 @param monthlypay 月薪
     * @param business 单位名 @param busi_province 单位所在省
     * @param busi_city 单位所在市 @param busi_address 单位详细地址
     * @param busi_phone 单位电话 @param relatives 亲属关系
     * @param relatives_name 亲属名字 @param rela_phone 亲属联系方式
     * @param society 社会关系 @param soci_phone 社会联系方式
     * @param society_name 社会关系名字
     * @return
     */
    public NoteResult insertPrivateInfo(String per_id,String qq_num,String email,
                                 String usuallyaddress,String education,String marry,
                                 String getchild,String profession,String monthlypay,
                                 String business,String busi_province,String busi_city,
                                 String busi_address,String busi_phone,String relatives,
                                 String relatives_name,String rela_phone,String society,
                                 String soci_phone,String society_name);
    
    /**
     * 获取自己的手机号，及运营商展示
     * @param per_id 用户ID
     * @return
     */
    public NoteResult getPhoneInfo(String per_id);

    /**
     * 获取用户本地的手机通讯录名单
     * @param per_id 用户ID
     * @param phone_list 用户本地的手机通讯录名单
     * @return
     */
    public NoteResult getPhoneList(String per_id,String phone_list);

     /**
     * 认证完成，修改用户、流程、借款状态
     * @param per_id 用户ID
     * @return
     */
    public NoteResult bpmFinish(String per_id);
    
    /**
     * 新增认证流程明细   返回下一认证节点名称，如果没有下一流程 则认证成功
     * @param per_id 用户id
     * @param node_name 认证节点名称
     * @return
     */
    public NoteResult createBpmNode(String per_id,String node_id,String node_status,String description);
    
    /**
     * 合同签约，状态改为已签约，添加签约时间
     * @param per_id 用户ID
     * @param borr_id 合同id
     * @param prod_id 产品id
     * @return
     */
    public NoteResult signingBorrow(String per_id, String borr_id,String name,String money,String day);
    
    /**
     * 取消借款申请。判断合同状态，在申请中的合同才能取消借款申请。
     * @param per_id 用户ID
     * @param borr_id 合同id
     * @return
     */
    public NoteResult cancelAskBorrow(String per_id,String borr_id);
       
    /**
     * 根据用户id查询姓名及身份证号
     * @param per_id  用户id
     * @return
     */
    public NoteResult getIDNumber(String per_id);
    
    /**
     * 查询所有省市信息
     * @return
     */
    public NoteResult getCity();
    
    /**
     * 获取当前节点的认证状态
     * @param per_id
     * @param node_id
     * @return
     */
    public NoteResult getNodeStatus(String per_id,String node_id);
    
    /**
     * 获取签约界面信息
     * @param per_id
     * @return
     */
    public NoteResult getSignInfo(String per_id);
    /**
     * 人脸识别页面   获取身份证正面照
     * @param per_id
     * @return
     */
    public NoteResult getCardz(String per_id);
    
    /**
     * 获取姓名及手机号
     * @param per_id
     * @return
     */
    public NoteResult getNamePhone(String per_id);
    
    /**
     * 获取可以代扣及支付的银行卡列表
     * @return
     */
    public NoteResult getBankList();
    
    /**
     * 根据系统名称获取当前最新版本号及是否强制更新
     * @param name 系统名称：ios/andriod
     * @return
     */
    public NoteResult getVersion(String name,String version);
    
    /**
     * 获取首页滚动条显示内容
     * @param per_id
     * @return
     */
    public NoteResult getRolling(String per_id);
    
    /**
     * 获取合同信息
     * @param per_id
     * @param borr_id
     * @param money
     * @param day
     * @param planRepay
     * @return
     */
    public NoteResult getContract(String per_id,String borr_id);
    
    /**
     * 增加调用第三方接口次数
     * @param per_id
     * @param type
     * @param count
     * @param status
     * @return
     */
    public NoteResult addCount(String per_id,String type,String count,String status);
    
    /**
     * 是否可以使用优惠券
     * @param per_id
     * @return
     */
    public NoteResult useCoupon(String per_id);
    
    /**验证tokenId
     * @param per_id
     * @param tokenId
     * @return
     */
    public String verifyTokenId(String per_id,String token);
    
//    /**身份证正面认证（骏聿）
//     * @param per_id
//     * @param photo 
//     * @return
//     */
//    public NoteResult ocrFront(String per_id,String photo);
//    
//    /**身份证反面认证（骏聿）
//     * @param per_id
//     * @param photo
//     * @return
//     */
//    public NoteResult ocrBack(String per_id,String photo);
//    
//    /**人脸识别认证（骏聿）
//     * @param per_id
//     * @param photos
//     * @return
//     */
//    public NoteResult compareAll(String per_id,String photos);
    
    /**修改黑名单状态
     * @param himid_list
     * @param blacklist
     * @return
     */
    public NoteResult goBlackList(String himid_list,String blacklist);
    
    /**还款页面信息
     * @param per_id
     * @return
     */
    public NoteResult repayInfo(String per_id);
    
    // public NoteResult cardOcr(String headFile, String cardFile);
    
    
    /**激活优惠券
     * @param per_id
     * @param coupon_id
     * @return
     */
    public NoteResult activateCoupon(String per_id,String coupon_id);
    
    public NoteResult banner();

    public NoteResult canBinding(String per_id);

    public NoteResult saveVerifyPhoto(byte[] bytes, String per_id);

    public NoteResult juxinliInfo();

    public NoteResult saveTemPic(byte[] bytes, String per_id, String response);

    public String getContacts(String per_id);

    public boolean manuallyReview(String perId, int type,int isManual, String description);


    /**
     * 芝麻信用申请
     */
    NoteResult zhima(String per_id);

    NoteResult getProductInfo(String per_id);

    NoteResult getBasicInfo();
}
