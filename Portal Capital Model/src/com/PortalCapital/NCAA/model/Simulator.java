package com.PortalCapital.NCAA.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Simulator {

	
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
	
	
	private Map<String, List<Integer>> generateTeamResults(List<List<Team>> generalResults) {
		Map<String, List<Integer>> teamResults = new HashMap<String, List<Integer>>();
		for(List<Team> specificOutcome : generalResults){
			for(Team t : specificOutcome){
				List<Integer> specificTeamFinishes = teamResults.get(t.name);
				if(specificTeamFinishes == null){
					specificTeamFinishes = new ArrayList<Integer>();
					teamResults.put(t.name, specificTeamFinishes);
				}
				specificTeamFinishes.add(t.roundEliminated);
			}
		}
		
		return null;
	}
	
	
	public class TeamStats{
		public double mean;
		public double stDev;
		public Map<String, Double> covariances;
	}
	

}
