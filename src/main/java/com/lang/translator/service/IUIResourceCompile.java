package com.lang.translator.service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public interface IUIResourceCompile {

	void setBaseFilePath(String path);

	void initProcess(String uniqeuId, String directory);

	String getFileData(String fileName) throws IOException;

    List getFileList(String directory);

    void parseData(String fileData, String fileName) throws IOException, JSONException, EncryptedDocumentException, InvalidFormatException;

    default String jsonToString(String json, String parsedData) throws IOException {

		JsonFactory factory = new JsonFactory();
		ObjectMapper mapper = new ObjectMapper(factory);

		JsonNode rootNode = mapper.readTree(json);

		Iterator<Map.Entry<String, JsonNode>> fieldsIterator = rootNode.fields();
		while (fieldsIterator.hasNext()) {
			Map.Entry<String,JsonNode> field = fieldsIterator.next();
			if(field.getValue().getClass().getName().equalsIgnoreCase("com.fasterxml.jackson.databind.node.ObjectNode")) {
				String parsedSubData = jsonToString(field.getValue().toString(), "");
				parsedData += parsedSubData;
			} else if(!(field.getKey().equals("ar") || field.getKey().equals("fr") || field.getKey().equals("cs") || field.getKey().equals("sv")
					|| field.getKey().equals("en") || field.getKey().equals("el") || field.getKey().equals("en-us"))){
//				parsedData += "\n" + field.getKey() + "," + field.getValue();
				parsedData += "\n" + field.getValue();
			}

		}
		return parsedData;
	}

    default void writeExcel(String[] data, String fileName) throws IOException, EncryptedDocumentException {

		File file = new File(fileName + ".xlsx");
		XSSFWorkbook translationSheet = null;
		XSSFSheet sheet = null;
		int sheetLastIndex = 0;
		if(!file.exists()) {
			translationSheet = new XSSFWorkbook();
			sheet = translationSheet.createSheet();
			for (String datum : data) {
				if(datum.trim().length() > 0) {
					Row row = sheet.createRow(sheetLastIndex++);
					row.createCell(0).setCellValue((String)datum.split(":")[0]);
					row.createCell(1).setCellValue((String)datum.split(":")[1]);
				}
			}

		}
		else {
			FileInputStream inputStream = new FileInputStream(file);
			translationSheet = new XSSFWorkbook(inputStream);
			sheet = translationSheet.getSheetAt(0);
			sheetLastIndex = sheet.getLastRowNum();
			for (String datum : data) {
				if(datum.toString().trim().length() > 0) {
					Row row = sheet.createRow(sheetLastIndex++);
					row.createCell(0).setCellValue((String)datum.split(":")[0]);
					row.createCell(1).setCellValue((String)datum.split(":")[1]);
				}
			}
			inputStream.close();
		}

		FileOutputStream outputStream = new FileOutputStream(file);
		translationSheet.write(outputStream);
		outputStream.close();
    }
}
