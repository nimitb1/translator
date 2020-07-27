package com.lang.translator.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author nimibans
 *
 */

@Service
@Slf4j
public class UIResourceCompile implements IUIResourceCompile {

	@Async
	public void initProcess(String uniqeuId, String directory){
		List<String> fileNames = getFileList(directory);
		try{
			fileNames.stream().forEach(file -> {
				try {
					parseData(getFileData(file), "");
				} catch (IOException e) {
					log.error("Error occurred while parsing the data for " + uniqeuId);
					e.printStackTrace();
				} catch (InvalidFormatException e) {
					log.error("Error occurred while parsing the data for " + uniqeuId);
					e.printStackTrace();
				}
			});
		} catch (Exception e){
			log.error("Error occurred while parsing the data for " + uniqeuId);
			e.printStackTrace();
		}
	}

	/**
	 * Method to get the data of the file.
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	@Override
	public String getFileData(String fileName) throws IOException {

		File file = new File(fileName);
		StringBuffer fileBuffer = new StringBuffer();

		BufferedReader bufferedReader = new BufferedReader(
				   new InputStreamReader(
		                      new FileInputStream(fileName), "UTF8"));

		String line = "";
		while((line = bufferedReader.readLine()) != null) {
			fileBuffer.append(line);
        }

		return fileBuffer.toString();

	}

	/**
	 * This method returns the list of files
	 * @param directory
	 * @return
	 */
	@Override
	public List<String> getFileList(String directory) {
		List<String> fileList;
		File folder = new File(directory);
		List<File> listOfFiles = Arrays.asList(folder.listFiles());
		fileList = listOfFiles.stream().filter(file -> file.isFile()).map(File::getName).collect(Collectors.toList());
		return fileList;
	}

	/**
	 * @TODO: Use the REGEX --> ([-"\w_"]*: [-+"\w_ "]*) --> check freeformatter.com for accuracy
	 * Method to parse the data read from the file
	 * @param fileData
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 * @throws InvalidFormatException
	 * @throws EncryptedDocumentException
	 */
	@Override
	public boolean parseData(String fileData, String fileName) throws IOException, JSONException, EncryptedDocumentException, InvalidFormatException {

		boolean success = true;

		//Sanitizing the file for the arguments
		int index = fileData.indexOf("function");
		while(fileData.charAt(index) != '(')
			index++;

		String args = "";
		index++;
		while(fileData.charAt(index) != ')') {
			args += fileData.charAt(index);
			index++;
		}

		String[] argArray = args.split(",");

		Arrays.sort(argArray);

		//Making json of the file
		index = fileData.indexOf("return");

		while(fileData.charAt(index) != '{') {
			index++;
		}

		//Fetch the remaining substring.
		String subjson = fileData.substring(index);

		//Remove the remaining non-required data for obtaining complete JSON
		index = subjson.indexOf(';');

		String comJSON = subjson.substring(0, index);

		if(args != "") {
			for (String arg : argArray) {
				comJSON = comJSON.replaceAll(arg.trim(), "0");
			}
		}

		comJSON = comJSON.replaceAll("\"(\\S+)\":", "$1:");
		/**
		 * : \"*\"(\S+)\":\"
		 * : \"$1\"
		 */
		String result = comJSON.replaceAll("(\\S+):", "\"$1\":");

		JSONObject json =  new JSONObject(comJSON);


		String parseData = jsonToString(json.toString(), "");
		String[] parseArray = parseData.split("\n");

		writeExcel(parseArray, "/home/nimibans/personal/test/MyFirstExcel.xlsx");

		return success;

	}

}




