/**
 * 
 */
package com.lang.translator.dto;

import java.util.List;

/**
 * @author nimibans
 *
 */
public class Dictionary {

	private List<NameValueFileDTO> nameValueFileDTOList;

	/**
	 * @return the nameValueFileDTOList
	 */
	public List<NameValueFileDTO> getNameValueFileDTOList() {
		return nameValueFileDTOList;
	}

	/**
	 * @param nameValueFileDTOList the nameValueFileDTOList to set
	 */
	public void setNameValueFileDTOList(List<NameValueFileDTO> nameValueFileDTOList) {
		this.nameValueFileDTOList = nameValueFileDTOList;
	}
	
}
