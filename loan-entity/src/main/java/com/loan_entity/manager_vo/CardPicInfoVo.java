/**
*描述：
*@author: wanyan
*@date： 日期：2016年12月12日 时间：下午4:52:27
*@version 1.0
*/

package com.loan_entity.manager_vo;

import java.io.Serializable;

/**
 *描述：
 *@author: Wanyan
 *@date： 日期：2016年12月12日 时间：下午4:52:27
 *@version 1.0
 */
public class CardPicInfoVo  implements Serializable{
	
	private int id;
	private int perId;
	
	private String imageTypeZ;
	private String imageFormatZ;
	private String imageZ;
	private String imageTypeF;
	private String imageFormatF;
	private String imageF;
	private String image_urlZ;
	private String image_urlF;
	
	/**
	 * @return the image_urlZ
	 */
	public String getImage_urlZ() {
		return image_urlZ;
	}
	/**
	 * @param image_urlZ the image_urlZ to set
	 */
	public void setImage_urlZ(String image_urlZ) {
		this.image_urlZ = image_urlZ;
	}
	/**
	 * @return the image_urlF
	 */
	public String getImage_urlF() {
		return image_urlF;
	}
	/**
	 * @param image_urlF the image_urlF to set
	 */
	public void setImage_urlF(String image_urlF) {
		this.image_urlF = image_urlF;
	}
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the perId
	 */
	public int getPerId() {
		return perId;
	}
	/**
	 * @param perId the perId to set
	 */
	public void setPerId(int perId) {
		this.perId = perId;
	}
	/**
	 * @return the imageTypeZ
	 */
	public String getImageTypeZ() {
		return imageTypeZ;
	}
	/**
	 * @param imageTypeZ the imageTypeZ to set
	 */
	public void setImageTypeZ(String imageTypeZ) {
		this.imageTypeZ = imageTypeZ;
	}
	/**
	 * @return the imageFormatZ
	 */
	public String getImageFormatZ() {
		return imageFormatZ;
	}
	/**
	 * @param imageFormatZ the imageFormatZ to set
	 */
	public void setImageFormatZ(String imageFormatZ) {
		this.imageFormatZ = imageFormatZ;
	}
	/**
	 * @return the imageZ
	 */
	public String getImageZ() {
		return imageZ;
	}
	/**
	 * @param imageZ the imageZ to set
	 */
	public void setImageZ(String imageZ) {
		this.imageZ = imageZ;
	}
	/**
	 * @return the imageTypeF
	 */
	public String getImageTypeF() {
		return imageTypeF;
	}
	/**
	 * @param imageTypeF the imageTypeF to set
	 */
	public void setImageTypeF(String imageTypeF) {
		this.imageTypeF = imageTypeF;
	}
	/**
	 * @return the imageFormatF
	 */
	public String getImageFormatF() {
		return imageFormatF;
	}
	/**
	 * @param imageFormatF the imageFormatF to set
	 */
	public void setImageFormatF(String imageFormatF) {
		this.imageFormatF = imageFormatF;
	}
	/**
	 * @return the imageF
	 */
	public String getImageF() {
		return imageF;
	}
	/**
	 * @param imageF the imageF to set
	 */
	public void setImageF(String imageF) {
		this.imageF = imageF;
	}
	
}

