package com.PortalCapital.NCAA.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Simulator {

	public static final double ROUND_2_SCORE = .01063;
	public static final double ROUND_3_SCORE = ROUND_2_SCORE + .02;
	public static final double ROUND_4_SCORE = ROUND_3_SCORE + .0375;
	public static final double ROUND_5_SCORE = ROUND_4_SCORE + .07;
	public static final double ROUND_6_SCORE = ROUND_5_SCORE + .13;
	public static final double ROUND_7_SCORE = ROUND_6_SCORE + .25;
	
	
	Map<Double, List<List<Team>>> storedResults;
	
	
	public void simulate(int count, double stDev){
		if(storedResults == null)
			storedResults = new HashMap<Double, List<List<Team>>>();
		Tournament t = new Tournament();
		List<List<Team>> theseResults = storedResults.get(stDev);
		if(theseResults == null){
			theseResults = new ArrayList<List<Team>>();
			storedResults.put(stDev, theseResults);
		}
		for(int i = 0; i < count; i++){
			theseResults.add(t.simulateTournament(stDev));
		}
	}
	
	public void clearResuts(){
		storedResults = null;
	}
	
	public Map<String, TeamStats> getTeamStats(double stDev){
		List<List<Team>> specificResults = storedResults.get(stDev);
		if(specificResults == null)
			return null;
		
		
		
		
		
		return null;
	}
	
	
	private Map<String, List<Double>> generateTeamResults(List<List<Team>> generalResults) {
		Map<String, List<Double>> teamResults = new HashMap<String, List<Double>>();
		for(List<Team> specificOutcome : generalResults){
			for(Team t : specificOutcome){
				List<Double> specificTeamFinishes = teamResults.get(t.name);
				if(specificTeamFinishes == null){
					specificTeamFinishes = new ArrayList<Double>();
					teamResults.put(t.name, specificTeamFinishes);
				}
				specificTeamFinishes.add(getScoreFromFinish(t.roundEliminated));
			}
		}
		
		return null;
	}
	
	private double getScoreFromFinish(int roundEliminated){
		switch(roundEliminated){
		case 2:
			return ROUND_2_SCORE;
		case 3:
			return ROUND_3_SCORE;
		case 4:
			return ROUND_4_SCORE;
		case 5:
			return ROUND_5_SCORE;
		case 6:
			return ROUND_6_SCORE;
		case 7:
			return ROUND_7_SCORE;
		default:
			return 0;
		}
	}
	
	
	public class TeamStats{
		public double mean;
		public double stDev;
		public Map<String, Double> covariances;
	}
	

}
