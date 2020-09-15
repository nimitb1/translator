package com.lang.translator.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.EncryptedDocumentException;
import org.json.JSONException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author nimibans
 *
 */

@Service
@Slf4j
public class UIResourceCompile implements IUIResourceCompile {

	private static String baseFilePath;

	@Override
	public void setBaseFilePath(String baseFilePath) {
		UIResourceCompile.baseFilePath = baseFilePath;
	}

	@Async
	public void initProcess(String uniqeuId, String directory){
		directory = directory.substring(0, directory.lastIndexOf('.'));
		List<String> fileNames = getFileList(directory);
		try{
			String finalDirectory = directory;
			fileNames.stream().forEach(file -> {
				parseData(getFileData(finalDirectory + "/" + file), uniqeuId);
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
	public String getFileData(String fileName) {

		File file = new File(fileName);
		StringBuffer fileBuffer = new StringBuffer();

		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(
					   new InputStreamReader(
								  new FileInputStream(fileName), "UTF8"));
			String line = "";
			while((line = bufferedReader.readLine()) != null) {
				fileBuffer.append(line);
			}
		} catch (IOException e) {
			log.error("Error encountered while reading the file {}", fileName);
			e.printStackTrace();
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
	 * @throws IOException
	 * @throws JSONException
	 * @throws EncryptedDocumentException
	 */
	@Override
	public void parseData(String fileData, String fileName) {
		String regex = "([-\"\\'\\w_\"]*: [-+\"\\w_ ,/.\"\']*[\"\'])";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(fileData);
		List<String> parsedData = new ArrayList<>();
		while(m.find()) {
			parsedData.add(m.group());
		}
		Map<String, String> dataMap = new HashMap<>();
		List<String> filterData = parsedData.stream()
				.filter(data -> (!data.split(":")[1].equals("true")
						|| !data.split(":")[1].equals("false")
						|| !data.split(":")[1].trim().isEmpty())).collect(Collectors.toList());
		String[] parseArray = new String[filterData.size()];
		parseArray = filterData.toArray(parseArray);
		try {
			writeExcel(parseArray, baseFilePath + "/" + fileName);
		} catch (IOException e) {
			log.error("Error occurred while writing the data of file: {}", fileName);
			e.printStackTrace();
		}
	}

}