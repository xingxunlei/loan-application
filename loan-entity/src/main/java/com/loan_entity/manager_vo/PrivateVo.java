package com.loan_entity.manager_vo;

import java.io.Serializable;
import java.util.Date;

import com.loan_entity.app.Private;
import com.loan_entity.manager.PrivateOld;

/**
 * 
*描述：
*@author: Wanyan
*@date： 日期：2016年11月10日 时间：下午9:28:15
*@version 1.0
 */

public class PrivateVo extends PrivateOld implements Serializable {
   private String name;
   private String card_num;
   private String relatives_value;
   private String society_value;
   
   private String source;
   private String source_value;
   private String phone;
   private String address;
   private Date birthday;
   private String blacklist;
   private Date create_date; 
   
   
/**
 * @return the source
 */
public String getSource() {
	return source;
}
/**
 * @param source the source to set
 */
public void setSource(String source) {
	this.source = source;
}
/**
 * @return the source_value
 */
public String getSource_value() {
	return source_value;
}
/**
 * @param source_value the source_value to set
 */
public void setSource_value(String source_value) {
	this.source_value = source_value;
}
/**
 * @return the phone
 */
public String getPhone() {
	return phone;
}
/**
 * @param phone the phone to set
 */
public void setPhone(String phone) {
	this.phone = phone;
}
/**
 * @return the address
 */
public String getAddress() {
	return address;
}
/**
 * @param address the address to set
 */
public void setAddress(String address) {
	this.address = address;
}
/**
 * @return the birthday
 */
public Date getBirthday() {
	return birthday;
}
/**
 * @param birthday the birthday to set
 */
public void setBirthday(Date birthday) {
	this.birthday = birthday;
}
/**
 * @return the blacklist
 */
public String getBlacklist() {
	return blacklist;
}
/**
 * @param blacklist the blacklist to set
 */
public void setBlacklist(String blacklist) {
	this.blacklist = blacklist;
}
/**
 * @return the create_date
 */
public Date getCreate_date() {
	return create_date;
}
/**
 * @param create_date the create_date to set
 */
public void setCreate_date(Date create_date) {
	this.create_date = create_date;
}
/**
 * @return the relatives_value
 */
public String getRelatives_value() {
	return relatives_value;
}
/**
 * @param relatives_value the relatives_value to set
 */
public void setRelatives_value(String relatives_value) {
	this.relatives_value = relatives_value;
}
/**
 * @return the society_value
 */
public String getSociety_value() {
	return society_value;
}
/**
 * @param society_value the society_value to set
 */
public void setSociety_value(String society_value) {
	this.society_value = society_value;
}
/**
 * @return the name
 */
public String getName() {
	return name;
}
/**
 * @param name the name to set
 */
public void setName(String name) {
	this.name = name;
}
/**
 * @return the card_num
 */
public String getCard_num() {
	return card_num;
}
/**
 * @param card_num the card_num to set
 */
public void setCard_num(String card_num) {
	this.card_num = card_num;
}
   
   
}
