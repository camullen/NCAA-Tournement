package com.PortalCapital.NCAA.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class ActualsReader {

	public static final String DEFAULT_FILENAME = "TBD"; //Need to choose a file
	
	
	public static Map<String, GameResult> getResults(String filename) throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		Map<String, GameResult> resultsMap = new HashMap<String, GameResult>();
		reader.readLine(); //throw away the headers
		while(reader.ready()){
			String resultFileLine = reader.readLine();
			GameResult thisResult = getResultFromFileString(resultFileLine);
			resultsMap.put(thisResult.teamA + thisResult.teamB, thisResult);
			resultsMap.put(thisResult.teamB + thisResult.teamA, thisResult);
		}
		reader.close();
		return resultsMap;
	}
	
	
	private static GameResult getResultFromFileString(String resultFileLine) {
		StringTokenizer tok = new StringTokenizer(resultFileLine, ",");
		List<String> fileLineItems = new ArrayList<String>();
		while(tok.hasMoreTokens()){
			fileLineItems.add(tok.nextToken());
		}
		if(fileLineItems.size() != 4){
			throw new RuntimeException("Expected 4 columns in file. Actual: " + fileLineItems.size());
		}
		GameResult result = new GameResult();
		result.teamA = fileLineItems.get(0);
		result.aScore = Integer.parseInt(fileLineItems.get(1));
		result.teamB = fileLineItems.get(2);
		result.bScore = Integer.parseInt(fileLineItems.get(3));
		return result;
	}


	public static Map<String, GameResult> getResults(){
		try {
			return getResults(DEFAULT_FILENAME);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} 
	}
	
	
}
