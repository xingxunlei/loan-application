/**
*描述：
*@author: wanyan
*@date： 日期：2016年11月4日 时间：下午3:10:16
*@version 1.0
*/

package com.loan_web.manager;

import net.sf.json.JSONObject;

import com.loan_entity.manager_vo.ReqBackPhoneCheckVo;

/**
 *描述：
 *@author: Wanyan
 *@date： 日期：2016年11月4日 时间：下午3:10:16
 *@version 1.0
 */
public class main_server {

	/**
	 *描述：
	 *@author: wanyan
	 *@date： 日期：2016年11月4日 时间：下午3:10:16
	 *@param args
	 *@version 1.0
	 */

	public static void main(String[] args) {
		String test = "{\"phone\":\"18501773154\",\"node_status\":\"0000\",\"node_date\":\"2016-11-04T14:48:25.9202007+08:00\",\"description\":\"通过\"}";
		System.out.println(test);
		 JSONObject jsonobject = JSONObject.fromObject(test);
		 System.out.println(jsonobject);
		 
		 ReqBackPhoneCheckVo passport = new ReqBackPhoneCheckVo();
		 passport.setPhone(jsonobject.getString("phone"));
		 passport.setNode_date(jsonobject.getString("node_date"));
		 passport.setDescription(jsonobject.getString("description"));
		
		if("0000".equals(jsonobject.getString("node_status"))){
			passport.setNode_status("NS002");
		}else{
			passport.setNode_status("NS003");
		}
		System.out.println(passport.getNode_status()+"_____"+passport.getDescription());

	}

}
