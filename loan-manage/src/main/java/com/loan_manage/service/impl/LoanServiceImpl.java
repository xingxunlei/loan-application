package com.loan_manage.service.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.*;

import com.github.pagehelper.PageHelper;
import com.loan_api.loan.YsbCollectionService;
import com.loan_entity.app.Card;
import com.loan_entity.app.NoteResult;
import com.loan_entity.loan.Collectors;
import com.loan_entity.loan.CollectorsLevelBack;
import com.loan_entity.loan.CollectorsList;
import com.loan_entity.loan.ExportWorkReport;
import com.loan_entity.manager.Order;
import com.loan_entity.manager_vo.PhoneBookVo;
import com.loan_entity.manager_vo.ReviewVo;
import com.loan_manage.entity.DownloadOrder;
import com.loan_manage.entity.Result;
import com.loan_manage.entity.PolyXinLi;
import com.loan_manage.mapper.*;
import com.loan_utils.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.vo.NormalExcelConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sun.misc.BASE64Encoder;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.loan_entity.app.BankVo;
import com.loan_entity.common.Constants;
import com.loan_entity.manager_vo.CardPicInfoVo;
import com.loan_entity.manager_vo.PrivateVo;
import com.loan_manage.mapper.OrderMapper;
import com.loan_manage.service.LoanService;
import com.loan_manage.service.RedisService;
import com.loan_manage.utils.Detect;
import com.loan_manage.utils.QueryParamUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service("loanService")
public class LoanServiceImpl implements LoanService {

	private Logger logger = Logger.getLogger(LoanServiceImpl.class);
	
	private static final String PIC_DIR = PropertiesReaderUtil.read("system", "picDir");
	private static final String POLY_XINLI_CREDIT_URL = PropertiesReaderUtil.read("system", "RC_CREDIT_URL");
	private static final String MODILE_CONTACT_URL = PropertiesReaderUtil.read("system", "MODILE_CONTACT_URL");
	 
	@Autowired
	OrderMapper orderMapper;
	
	@Autowired
	RedisService redisService;
	@Autowired
	RepayPlanMapper repayPlanMapper;
	
	@Autowired
	PerCouponMapper perCouponMapper;
	@Autowired
	BankInfoMapper bankInfoMapper;
	@Autowired
	CollectorsRemarkMapper collectorsRemarkMapper;
	@Autowired
	PersonMapper personMapper ;
	@Autowired
	CardMapper cardMapper ;
	@Autowired
	CollectorsListMapper collectorsListMapper;
	@Autowired
	CollectorsLevelBackMapper collectorsLevelBackMapper;
	@Autowired
	private YsbCollectionService ysbCollectionService;
	@Autowired
	private CollectorsMapper collectorsMapper;

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getOrders(Map<String, String[]> param, int offset, int size) {
		Map<String, Object> paramMap = QueryParamUtils.getargs(param);
		//redis组装查询参数
		Map<String, Object> map = redisService.getSerchParam(paramMap);
		if(Detect.notEmpty(map)){
			paramMap.putAll(map);
		}
		if(Detect.notEmpty(param.get("borrId"))){
			paramMap.put("borrId", param.get("borrId")[0]);
		}
		if(Detect.notEmpty(param.get("type"))){
			paramMap.put("type", param.get("type")[0]);
		}
		//还款时间排序
		if(!Detect.notEmpty(paramMap.get("selector") + "")){
			paramMap.put("selector","createDate");
			paramMap.put("desc","desc");
		}
		//模糊名字查询
		if(Detect.notEmpty(paramMap.get("name") + "") ||
				Detect.notEmpty(paramMap.get("createUser") + "") ||
				Detect.notEmpty(paramMap.get("collectionUser") + "")){
			paramMap = likeEntity(paramMap);
		}

		//分页查询
		PageHelper.offsetPage(offset, size);

		List<Map<String, Object>> result = orderMapper.getOrders(paramMap);

		if(Detect.notEmpty(result)){
			Map<String, String> redisParam = new HashMap<String, String>();
			redisParam.put(Constants.PERSON_KEY, "perId");
			redisParam.put(Constants.BANK_KEY, "bankId");
			redisParam.put(Constants.PRODUCT_KEY, "prodId");
			redisParam.put("createUserName", "createUser");
			redisParam.put("collectionUserName", "collectionUser");

			redisService.getDataByRedis(result, redisParam);
		}
		return result;
	}


	@Override
	public List<DownloadOrder> selectDownloadOrder(Map<String, Object> paramMap, String userNo) {

		//模糊名字查询
		if(Detect.notEmpty(paramMap.get("name") + "") ||
				Detect.notEmpty(paramMap.get("createUser") + "") ||
				Detect.notEmpty(paramMap.get("collectionUser") + "")){
			paramMap = likeEntity(paramMap);
		}
		addAuthLevel2queryMap(paramMap,userNo);
		List<DownloadOrder> downloadOrders = orderMapper.selectDownloadOrder(paramMap);
//		return buildOrder(downloadOrders);
		return downloadOrders;
	}

	private List<DownloadOrder> buildOrder(List<DownloadOrder> downloadOrders) {
		List<DownloadOrder> list = new ArrayList<>();
		long start = new Date().getTime();
		for(DownloadOrder downloadOrder : downloadOrders){
			String personId = downloadOrder.getPerId();
//			String type = downloadOrder.getType();
//			String rlState = downloadOrder.getRlState();
//			String productId = downloadOrder.getProdId();
//			String bankId = downloadOrder.getBankId();
			String createUserName = downloadOrder.getCreateUser();
			String collectionUserName = downloadOrder.getCollectionUser();

//			if("p".equals(rlState)){
//				downloadOrder.setState("处理中");
//			}else if("s".equals(rlState)){
//				downloadOrder.setState("成功");
//			}else if("f".equals(rlState)){
//				downloadOrder.setState("失败");
//			}
//
//			if (type.equals("2")){
//				downloadOrder.setTypeName("还款");
//			}else if (type.equals("4")){
//				downloadOrder.setTypeName("还款(代收)");
//			}else if (type.equals("5")){
//				downloadOrder.setTypeName("主动还款");
//			}else if (type.equals("7")){
//				downloadOrder.setTypeName("线下还款");
//			}else if (type.equals("6")){
//				downloadOrder.setTypeName("减免");
//			}else if (type.equals("8")){
//				downloadOrder.setTypeName("批量代扣");
//			}else{
//				downloadOrder.setTypeName("");
//			}

			//取得用户信息
//			JSONObject person = redisService.selectPersonFromRedis(Integer.valueOf(personId));
//			if(person != null){
//				downloadOrder.setName(StringUtils.isEmpty(person.getString("name")) ? "" : person.getString("name"));
//				downloadOrder.setIdCard(StringUtils.isEmpty(person.getString("idCard")) ? "" : person.getString("idCard"));
//				downloadOrder.setPhone(StringUtils.isEmpty(person.getString("phone")) ? "" : person.getString("phone"));
//			}
			//取得银行卡信息
//			JSONObject bankInfo = redisService.selectBankInfoFromRedis(bankId);
//			if(bankInfo != null){
//				downloadOrder.setBankNum(StringUtils.isEmpty(bankInfo.getString("bankNum")) ? "" : bankInfo.getString("bankNum"));
//			}
			//取得操作人
//			if(!StringUtils.isEmpty(createUserName) && createUserName.equals("8888")){
//				downloadOrder.setUsername("SYS");
//			}else if(!StringUtils.isEmpty(createUserName) && createUserName.equals("9999")){
//				downloadOrder.setUsername("特殊");
//			}else{
//				JSONObject createUser = redisService.selectCollertorsUserName(createUserName);
//				if(createUser != null){
//					downloadOrder.setUsername(StringUtils.isEmpty(createUser.getString("userName")) ? "" : createUser.getString("userName"));
//				}
//			}
//			if(StringUtils.isBlank(downloadOrder.getUsername())){
//				JSONObject createUser = redisService.selectCollertorsUserName(createUserName);
//				if(createUser != null){
//					downloadOrder.setUsername(StringUtils.isEmpty(createUser.getString("userName")) ? "" : createUser.getString("userName"));
//				}
//			}
//
//			//取得催收人
//			JSONObject collertorsUser = redisService.selectCollertorsUserName(collectionUserName);
//			if(collertorsUser != null){
//				downloadOrder.setBedueUser(StringUtils.isEmpty(collertorsUser.getString("userName")) ? "" : collertorsUser.getString("userName"));
//			}

			list.add(downloadOrder);
		}
		long end = new Date().getTime();
		logger.info("===========================> rawOrderList handle finished, elapse:"+(end -  start ));
		return list;
	}

	public Map<String, Object> likeEntity(Map<String, Object> map){
		if(map != null){
			if(Detect.notEmpty(map.get("name")+ "")){

				Card cd = new Card();
				cd.setName(map.get("name") + "");
				List<Card> cards = cardMapper.select(cd);
				if(Detect.notEmpty(cards)){
					List perIds = new ArrayList();
					for (Card card : cards){
						perIds.add(card.getPerId());
					}
					map.put("perIds",perIds);
				}else{
					map.put("perId", -1);
				}
			}
			if(Detect.notEmpty(map.get("createUser")+ "")){
				CollectorsLevelBack cb = new CollectorsLevelBack();
				cb.setUserName(map.get("createUser") + "");
				List<CollectorsLevelBack> collectorsLevelBacks = collectorsLevelBackMapper.select(cb);
				if (Detect.notEmpty(collectorsLevelBacks)) {
					List createUser = new ArrayList();
					for (CollectorsLevelBack collectorsLevelBack : collectorsLevelBacks) {
						createUser.add(collectorsLevelBack.getUserSysno());
					}
					map.put("createUsers", createUser);
				} else {
					map.put("perId", -1);
				}
			}
			if(Detect.notEmpty(map.get("collectionUser")+ "")){

				CollectorsLevelBack cb = new CollectorsLevelBack();
				cb.setUserName(map.get("collectionUser") + "");
				List<CollectorsLevelBack> collectorsLevelBacks = collectorsLevelBackMapper.select(cb);
				if(Detect.notEmpty(collectorsLevelBacks)){
					List collectorsUser = new ArrayList();
					for (CollectorsLevelBack collectorsLevelBack : collectorsLevelBacks){
						collectorsUser.add(collectorsLevelBack.getUserSysno());
					}
					map.put("collectorsUsers",collectorsUser);
				}else{
					map.put("perId", -1);
				}
			}
		}
		return map;
	}

	@Override
	public Map<String, Object> getBorrowList(String borrId) {
		List<Map<String, Object>> result = repayPlanMapper.selectReplanAndBorrowList(borrId);
		if(Detect.notEmpty(result)){
			Map<String, String> map = new HashMap<String, String>();
			map.put(Constants.PRODUCT_KEY, "prodId");
			redisService.getDataByRedis(result, map);
			return result.get(0);
		}
		return null;
	}

	@Override
	public List<Map<String, Object>> getPerCoupon(String couponId, String prodId) {
		List<Map<String, Object>> result = perCouponMapper.getCoupon(couponId);
		
		if(Detect.notEmpty(result)){
			result.get(0).put("prodId", prodId);
			Map<String, String> map = new HashMap<String, String>();
			map.put(Constants.PRODUCT_KEY, "prodId");
			redisService.getDataByRedis(result, map);
			return result;
		}
		return result;
	}

	@Override
	public List<BankVo> getBanks(Integer perId) {
		if(Detect.isPositive(perId)){
			Map<String,Object> queryMap = new HashMap<String,Object>();
            queryMap.put("perId",perId);
            return bankInfoMapper.selectBankInfosByPerId(queryMap);
		}
		
		return null;
	}

	@Override
	public List getMemos(Map<String, String[]> param , Integer borrId) {
		Map<String, Object> paramMap = QueryParamUtils.getargs(param);
		paramMap.put("contractSysno",borrId);

		return collectorsRemarkMapper.selectRemarkInfo(paramMap);
	}

	@Override
	public List<ExportWorkReport> workReport(Map<String, Object> map) {
		return collectorsListMapper.getWorkReport(map);
	}

	@Override
	public void exportWorkReport(Map<String, Object> map, HttpServletRequest request, HttpServletResponse response) {
		List workReport = collectorsListMapper.getWorkReport(map);
		Map<String, Object> expotMap = new HashMap<String,Object>();
		expotMap.put(NormalExcelConstants.FILE_NAME, "催收工作报表");
		expotMap.put(NormalExcelConstants.CLASS, ExportWorkReport.class);
		expotMap.put(NormalExcelConstants.DATA_LIST, workReport);
		expotMap.put(NormalExcelConstants.PARAMS,new ExportParams());
		ExcelUtils.jeecgSingleExcel(expotMap, request, response);
	}

	@Override
	public PrivateVo getUserInfo(int perId) {
		return personMapper.queryUserInfo(perId);
	}


	@Override
	public CardPicInfoVo getCardInfo(int perId) {
		// TODO Auto-generated method stub
		CardPicInfoVo cardPicInfoVo = personMapper.queryCardPicById(perId);
		if(cardPicInfoVo != null){
			if (cardPicInfoVo.getImageZ()==null && cardPicInfoVo.getImage_urlZ()!=null) {
				String path = PIC_DIR + cardPicInfoVo.getImage_urlZ();
				String data = GetImageStr(path);
				cardPicInfoVo.setImageZ(data);
			}
			if (cardPicInfoVo.getImageF()==null && cardPicInfoVo.getImage_urlF()!=null) {
				String path = PIC_DIR + cardPicInfoVo.getImage_urlF();
				String data = GetImageStr(path);
				cardPicInfoVo.setImageF(data);
			}
		}
		return cardPicInfoVo;
	}
	
	public String GetImageStr(String path) {//将图片文件转化为字节数组字符串，并对其进行Base64编码处理  
        String imgFile = path;//待处理的图片  
        InputStream in = null;  
        byte[] data = null;  
        //读取图片字节数组  
        try   
        {  
            in = new FileInputStream(imgFile);          
            data = new byte[in.available()];  
            in.read(data);  
            in.close();  

        }catch (IOException e)  {  
            e.printStackTrace();  
        } catch (Exception e) {
			e.printStackTrace();
		}  
        BASE64Encoder encoder = new BASE64Encoder();  
        return encoder.encode(data);//返回Base64编码过的字节数组字符串  
    }

	@Override
	public List getPolyXinliCredit(String idCard, String name, int offset, int size, Integer perId) {
		logger.info("getPolyXinliCredit:-----------idCard:" + idCard +"-----name:" + name);
		String result = "";
		try {
			JSONObject json = new JSONObject();
			json.put("name", name);
			json.put("idcard", idCard);
			json.put("code", "1001");
			result = HttpUtils.sendPost(POLY_XINLI_CREDIT_URL, json.toJSONString());
			result = URLDecoder.decode(result, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}

		if(Detect.notEmpty(result)){
			JSONArray jsonArray = new JSONArray();
			List<PolyXinLi> resutlArray =  JSONObject.parseArray(result,PolyXinLi.class);
			if(resutlArray != null && resutlArray.size() > 0){
				//排序
				Collections.sort(resutlArray);

				JSONArray contactJsons = getContact(perId);
				for(int i = 0; i < size && i<resutlArray.size(); i++){
					if (contactJsons != null) {
						for (Object json : contactJsons){
							if(((JSONObject)json).get("phone").equals(resutlArray.get(i + offset).getPhone_num())){
								resutlArray.get(i + offset).setYMName(((JSONObject)json).get("name") + "");
							}
						}
					}
					if(i + offset < resutlArray.size()){
						jsonArray.add(resutlArray.get(i + offset));
					}
				}
				return getPageObject(jsonArray, offset, size, resutlArray.size());
			}
		}
		return null;
	}
	
	public Page getPageObject(List list, int offset, int size, int totalSize){
		Page page = new Page();
		page.add(list);
		page.setCount(true);
		page.setPageNum(offset/size + 1);
		page.setPageSize(size);
		page.setStartRow(offset);
		page.setEndRow(offset + size);
		page.setTotal(totalSize);
		page.setPages(totalSize/size);
		page.setReasonable(false);
		page.setPageSizeZero(false);
		return page;
	}

	@Override
	public String getCreditInvestigation(String idCard, String name) {
		String result = "";
		logger.info("getCreditInvestigation:-----------idCard:" + idCard +"-----name:" + name);
		try {
			JSONObject json = new JSONObject();
			json.put("name", name);
			json.put("idcard", idCard);
			json.put("code", "1002");
			result = HttpUtils.sendPost(POLY_XINLI_CREDIT_URL, json.toJSONString());
			result = URLDecoder.decode(result, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	public JSONArray getContact(Integer perId) {
		String result = getPhoneBookResult(perId);
		if(Detect.notEmpty(result)){
			JSONObject json = JSONObject.parseObject(result);
			if(json.get("code").equals("200")){
				return  JSONObject.parseArray(json.get("data").toString());
			}
		}
		return null;
	}

	@Override
	public List getContact(Integer perId, String phones, int offset, int size) {
		logger.info("getContact:-----------phones:" + phones );

		if(Detect.isPositive(perId)){

			JSONArray resutlArray =  getContact(perId);
			if(resutlArray != null){
				JSONArray jsonArray = new JSONArray();

				for(int i = 0; i < size; i++){
					if(i + offset < resutlArray.size()){
						jsonArray.add(resutlArray.get(i + offset));
					}
				}
				return getPageObject(jsonArray, offset, size, resutlArray.size());
			}
		}
		return null;
	}

	@Override
	public List<PhoneBookVo> getContactForExport(Integer perId) {
		if(Detect.isPositive(perId)){
			String result = getPhoneBookResult(perId);
			if(StringUtils.isNotBlank(result)){
				JSONObject json = JSONObject.parseObject(result);
				if(json.get("code").equals("200")){
					return  JSONObject.parseArray(json.get("data").toString(),PhoneBookVo.class);
				}
			}
		}
		return null;
	}

	private String getPhoneBookResult(Integer perId) {
		String result = "";
		try {
            JSONObject json = new JSONObject();
            json.put("per_id", perId);
            json.put("md5sign", VerifyMD5.creatSign(perId + ""));
             result = HttpUtils.sendPost(MODILE_CONTACT_URL, HttpUtils.toParam(json));
        } catch (Exception e) {
            e.printStackTrace();
        }
		return result;
	}


	public JSONArray getIntersection (JSONArray resutlArray, String[] phones) {
		JSONArray jsonArray = new JSONArray();
		if(Detect.notEmpty(phones) && Detect.notEmpty(resutlArray)){
			for (String phone :phones ) {
				for (int i = 0; i< resutlArray.size(); i++){
					if(phone.equals(((JSONObject)resutlArray.get(i)).get("phone"))){
						jsonArray.add(resutlArray.get(i));
					}
				}
			}
		}
		return jsonArray;
	}

	@Override
	public void saveCollectorsList(String borrId, String status) {
		CollectorsList collectorsList = new CollectorsList();
		collectorsList.setContractSysno(borrId);
		collectorsList.setStatus(status);
		collectorsListMapper.updateByPrimaryKeySelective(collectorsList);
	}

	@Override
	public List<ReviewVo> getReviewVoBlackList(int perId) {
		return personMapper.getReviewVoBlackList(perId);
	}

	@Override
	public Result queryCostState(String serialNo) {

		Result result = new Result();

		Order queryOrder = new Order();
		queryOrder.setSerialNo(serialNo);
		Order order = orderMapper.selectOne(queryOrder);
		if(order == null){
			result.setCode(Result.F);
			result.setMessage("未查询到对应的流水号订单");
			return result;
		}
		if(order.getCreationDate() == null){
			result.setCode(Result.F);
			result.setMessage("订单创建时间为空");
			return result;
		}
		//如果插入时间和当前时间相差小于5min，不予以查询
		long difference = DateUtil.getTimeDifference(new Date(),order.getCreationDate());
		if(difference < 1){
			result.setCode(Result.P);
			result.setMessage("第三方支付正在处理中!");
			return result;
		}


		NoteResult noteResult = ysbCollectionService.orderStatus(serialNo);
		if("00".equals(noteResult.getCode())){
			result.setCode(Result.S);
			result.setMessage("第三方支付处理完成!");
		}else if("10".equals(noteResult.getCode())){
			result.setCode(Result.P);
			result.setMessage("第三方支付正在处理中!");
		}else if("20".equals(noteResult.getCode())){
			result.setCode(Result.F);
			result.setMessage("第三方支付处理失败!");
		}else{
			result.setCode(Result.FAIL);
			result.setMessage(noteResult.getInfo());
		}
		return result;
	}

	private void addAuthLevel2queryMap(Map<String, Object> queryMap, String userNo) {
		Collectors c = new Collectors();
		c.setUserSysno(userNo);
		Collectors collectors = collectorsMapper.selectOne(c);
		queryMap.put("levelType",collectors == null ? "" : collectors.getLevelType());
		queryMap.put("companyId",collectors == null ? "" : collectors.getUserGroupId());
	}
}
