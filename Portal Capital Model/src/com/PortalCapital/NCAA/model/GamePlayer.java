/**
 * 
 */
package com.PortalCapital.NCAA.model;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.math3.distribution.NormalDistribution;


/**
 * @author Cameron Mullen
 *
 */
public class GamePlayer {
	/*
	 * Will need to incorporate point spreads and outcomes into this one
	 */
	
	public static final boolean USE_OFFLINE_FILE = false;
	
	public static final double DEFAULT_STANDARD_DEVIATION = 9.0;
	public static final String DEFAULT_ODDS_URL = "http://sportsdirect.usatoday.com/odds/usatoday/odds.aspx";
	public static final String DEBUG_ODDS_FILEPATH = "USATodayOdds.html";
	
	private static final OddsDownloader downloader = new OddsDownloader();
	private double standardDeviation;
	private Map<String, Double> oddsMap;
	private Map<String, GameResult> resultsMap;
	
	
	
	public GamePlayer(double standardDeviation, File savedHTML, String baseUrl){
		setStandardDeviation(standardDeviation);
		try {
			oddsMap = downloader.getOdds(savedHTML, baseUrl);
			setUpResultsMap();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public GamePlayer(double standardDeviation, String urlOrFile){
		setStandardDeviation(standardDeviation);
		try {
			if(USE_OFFLINE_FILE){
				oddsMap = downloader.getOdds(new File(urlOrFile), DEFAULT_ODDS_URL);
			} else {
				oddsMap = downloader.getOdds(urlOrFile);
			}
			setUpResultsMap();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public GamePlayer(double standardDeviation){
		this(standardDeviation, DEFAULT_ODDS_URL);
	}
	
	public GamePlayer(){
		this(DEFAULT_STANDARD_DEVIATION, DEFAULT_ODDS_URL);
	}
	
	
	public void setStandardDeviation(double standardDeviation){
		this.standardDeviation = standardDeviation;
	}
	
	/*
	 * Returns true if team A beats Team B; returns false if Team B beats team A
	 */
	public boolean playGame(Team a, Team b){
		boolean result;
		Boolean actualResult = checkActual(a, b);
		Boolean oddsResult = checkOdds(a, b);
		if(actualResult != null) {
			result = actualResult;
		} else if (oddsResult != null){
			result = oddsResult;
		} else {
			result = sagarinPlayGame(a, b);
		}
		/*
		 * For debugging only
		Team winner = result ? a : b;
		Team loser = result ? b : a;
		System.out.println(winner + " defeats " + loser);
		*/
		return result;
	}
	
	
	private Boolean checkOdds(Team a, Team b){
		String oddsSearch = a.usaTodayName + b.usaTodayName;
		Double oddsSpread = oddsMap.get(oddsSearch);
		if(oddsSpread == null) return null;
		double outcome = generateScoreDifferential(oddsSpread);
		return (outcome > 0);
	}
	
	private Boolean checkActual(Team one, Team two) {
		GameResult result = resultsMap.get(one.name + two.name);
		if(result == null) return null;
		int oneScore, twoScore;
		if(result.teamA.equals(one.name)){
			oneScore = result.aScore;
			twoScore = result.bScore;
		} else {
			oneScore = result.bScore;
			twoScore = result.aScore;
		}
		
		return (oneScore > twoScore);
		
	}

	private boolean sagarinPlayGame(Team a, Team b){
		double spread = a.sagarinRating - b.sagarinRating;
		double outcome = generateScoreDifferential(spread);
		return (outcome > 0);
	}
	
	
	private double generateScoreDifferential(double spread){
		NormalDistribution dist = new NormalDistribution(spread, standardDeviation);
		return dist.sample();
	}
	
	private void setUpResultsMap(){
		resultsMap = ActualsReader.getResults();
	}
	
	
	

}
