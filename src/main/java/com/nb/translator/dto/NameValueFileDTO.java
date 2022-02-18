package com.nb.translator.dto;

import java.util.List;

public class NameValueFileDTO {

	/**
	 * name of the file to which the data belong.
	 */
	private String fileName;
	
	/**
	 * key for whose value the translation is required.
	 */
	private String name;
	
	/**
	 * value for which the translation is required.
	 */
	private String value;
	
	/**
	 * List of the changed values.
	 */
	private List<String> changedValues;

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
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
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the changedValues
	 */
	public List<String> getChangedValues() {
		return changedValues;
	}

	/**
	 * @param changedValues the changedValues to set
	 */
	public void setChangedValues(List<String> changedValues) {
		this.changedValues = changedValues;
	}
	
}
