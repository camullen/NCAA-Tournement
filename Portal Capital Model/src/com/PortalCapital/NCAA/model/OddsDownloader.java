package com.PortalCapital.NCAA.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class OddsDownloader {
	private static final String ODDS_SENTINEL = "<br />";
	private static final String TEAM_NAMES_FILE = "usaTodayNames.csv";
	
	
	Set<String> allTeams;
	
	public OddsDownloader() {
		allTeams = new HashSet<String>();
	}
	
	public Map<String, Double> getOdds(File savedHTML, String baseUrl) throws IOException{
		Document doc = Jsoup.parse(savedHTML, null, baseUrl);
		return getOdds(doc);
	}
	
	
	public Map<String, Double> getOdds(String url) throws IOException{
		Document doc = Jsoup.connect(url).get();
		return getOdds(doc);
	}
	
	public Set<String> getAllTeams(){
		Set<String> storedNames = setFromCSV(TEAM_NAMES_FILE);
		if(storedNames != null){
			allTeams.addAll(storedNames);
		}
		setToCSV(TEAM_NAMES_FILE, allTeams);
		return allTeams;
	}
	
	
	private Map<String, Double> getOdds(Document doc){
		Map<String, Double> oddsMap = new HashMap<String, Double>();
		Elements allOddsTables = getNCAAOddsTables(doc);
		for(Element oddTable : allOddsTables){
			Elements teamSub = oddTable.select(".sdi-datahead-sub-nb");
			Elements oddsDetail = oddTable.select(".sdi-datacell");
			if(teamSub.size() > 0 ){
				Elements teams = teamSub.get(0).select(".small-copy-sans");
				if(teams.size() == 2){
					String awayTeam = teams.get(0).html();
					String homeTeam = teams.get(1).html();
					allTeams.add(awayTeam);
					allTeams.add(homeTeam);
					List<Double> odds = getOddsFromOddsDetailElements(oddsDetail);
					Double averageOdd = getAverageOdds(odds);
					if(averageOdd != null){
						oddsMap.put(awayTeam + homeTeam, averageOdd);
						oddsMap.put(homeTeam + awayTeam, -averageOdd);
						System.out.println(awayTeam + " vs. " + homeTeam + " Average Line = " + averageOdd);
					}
						
				} else {
					throw new RuntimeException("Expected 2 teams in team box. Actual = " + teams.size());
				}
			} else {
				throw new RuntimeException("Could not find the team box");
			}
		}
		return oddsMap;
	}
	
	private Elements getNCAAOddsTables(Document doc){
		Elements allOddsTables = doc.select(".sdi-data-wide, .sdi-so-title");
		Elements ncaaOddsTables = new Elements();
		boolean withinNCAASectionOfDocument = false;
		for(Element e : allOddsTables){
			if(e.className().equals("sdi-so-title")){
				Elements title = e.select("span");
				if(title.size() != 0){
					String sectionTitle = title.get(0).html();
					if(sectionTitle.contains("College Basketball")){
						withinNCAASectionOfDocument = true;
					} else {
						withinNCAASectionOfDocument = false;
					}
				}
			} else {
				if(withinNCAASectionOfDocument){
					ncaaOddsTables.add(e);
				}
			}
		}
		return ncaaOddsTables;
	}
	
	private Double getAverageOdds(List<Double> allOdds){
		if(allOdds == null || allOdds.size() == 0) return null;
		if(allOdds.size() == 1) return allOdds.get(0);
		DescriptiveStatistics stats = new DescriptiveStatistics();
		for(Double d : allOdds.subList(1, allOdds.size()))
			stats.addValue(d);
		return stats.getMean();
	}

	
	
	private List<Double> getOddsFromOddsDetailElements(Elements oddsDetail) {
		List<Double> odds = new ArrayList<Double>();
		for(Element elem : oddsDetail){
			String rawOddsData = elem.html();
			int findIndex = rawOddsData.indexOf(ODDS_SENTINEL);
			if(findIndex == -1)
				throw new RuntimeException("Data Cell not properly formatted");
			int startIndex = findIndex + ODDS_SENTINEL.length();
			int endIndex = rawOddsData.indexOf(" ", startIndex);
			String formattedOddsData;
			if(endIndex != -1)
				formattedOddsData = rawOddsData.substring(startIndex, endIndex);
			else
				formattedOddsData = rawOddsData.substring(startIndex);
			Double d; 
			try {
				d = Double.parseDouble(formattedOddsData);
				odds.add(d);
			} catch (NumberFormatException e){
				
			}
		}
		return odds;
	}
	
	
	private Set<String> setFromCSV(String filename){
		try {
			Set<String> readSet = new HashSet<String>();
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			while(reader.ready()){
				readSet.add(reader.readLine());
			}
			reader.close();
			return readSet;
		} catch (FileNotFoundException e) {;
			return null;
		} catch (IOException e) {
			return null;
		}
		
	}
	
	private void setToCSV(String filename, Set<String> names){
		try {
			PrintWriter writer = new PrintWriter(filename);
			for(String s : names){
				writer.println(s);
			}
			writer.flush();
			writer.close();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	

}
