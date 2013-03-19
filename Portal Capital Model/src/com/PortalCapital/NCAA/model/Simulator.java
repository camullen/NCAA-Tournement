package com.PortalCapital.NCAA.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.descriptive.MultivariateSummaryStatistics;

public class Simulator {

	public static final double ROUND_2_SCORE = .01063;
	public static final double ROUND_3_SCORE = ROUND_2_SCORE + .02;
	public static final double ROUND_4_SCORE = ROUND_3_SCORE + .0375;
	public static final double ROUND_5_SCORE = ROUND_4_SCORE + .07;
	public static final double ROUND_6_SCORE = ROUND_5_SCORE + .13;
	public static final double ROUND_7_SCORE = ROUND_6_SCORE + .25;
	
	public static final String TOURNAMENT_STRUCTURE_FILE = "TournamentStructure.csv";
	public static final String TEAM_FILE = "teams.csv";
	
	
	Map<Double, List<List<Team>>> storedResults;
	
	
	public void simulate(int count, double stDev){
		if(storedResults == null)
			storedResults = new HashMap<Double, List<List<Team>>>();
		Tournament tourney = new Tournament();
		try {
			tourney.loadTournamentStructure(TOURNAMENT_STRUCTURE_FILE);
			tourney.loadTournamentTeams(TEAM_FILE);
		} catch (IOException e) {
			throw new RuntimeException("Unable to load tournament files");
		}
		
		List<List<Team>> theseResults = storedResults.get(stDev);
		if(theseResults == null){
			theseResults = new ArrayList<List<Team>>();
			storedResults.put(stDev, theseResults);
		}
		for(int i = 0; i < count; i++){
			List<Team> thisIterationTeamList = tourney.simulateTournament(stDev);
			theseResults.add(thisIterationTeamList);
		}
	}
	
	public void clearResuts(){
		storedResults = null;
	}
	
	
	
	
	
	public List<TeamStats> getTeamStats(double stDev){
		List<List<Team>> specificResults = storedResults.get(stDev);
		if(specificResults == null)
			return null;
		Map<String, List<Double>> teamResultsMap = generateTeamResults(specificResults);
		MultivariateSummaryStatistics statsObj = new MultivariateSummaryStatistics(teamResultsMap.size(), true);
		List<String> teamNameList = getTeamList(specificResults);
		addAllTeamsToStatsObj(teamResultsMap, statsObj, teamNameList);
		return getTeamStatsList(statsObj, teamNameList);
	}
	
	
	
	
private List<TeamStats> getTeamStatsList(
			MultivariateSummaryStatistics statsObj, List<String> teamNameList) {
		List<TeamStats> teamStatsList = new ArrayList<TeamStats>();
		double[] meanList = statsObj.getMean();
		double[] stDevList = statsObj.getStandardDeviation();
		RealMatrix covarianceMatrix = statsObj.getCovariance();
		for(int i = 0; i < teamNameList.size(); i++){
			TeamStats stats = new TeamStats();
			stats.mean = meanList[i];
			stats.stDev = stDevList[i];
			stats.name = teamNameList.get(i);
			Map<String, Double> covarianceMap = new HashMap<String, Double>();
			for(int j = 0; j < teamNameList.size(); j++){
				double covariance = covarianceMatrix.getEntry(i, j);
				String teamName = teamNameList.get(j);
				covarianceMap.put(teamName, covariance);
			}
			teamStatsList.add(stats);
		}
	
		return teamStatsList;
	}

private void addAllTeamsToStatsObj( Map<String, List<Double>> teamResultsMap,
			MultivariateSummaryStatistics statsObj, List<String> teamNameList) {
		int numIterations = teamResultsMap.values().iterator().next().size();
		for(List<Double> theseResults : teamResultsMap.values()){
			if(theseResults.size() != numIterations)
				throw new RuntimeException("Not all teams have same # of observations");
		}
		int numTeams = teamNameList.size();
		
		for(int i = 0; i < numIterations; i++){
			double thisIterationsResults[] = new double[numTeams];
			for(int j = 0; j < numTeams; j++){
				String teamName = teamNameList.get(j);
				double result = teamResultsMap.get(teamName).get(i);
				thisIterationsResults[j] = result;
			}
			statsObj.addValue(thisIterationsResults);
		}
	}


	
	
	private List<String> getTeamList(List<List<Team>> generalResults){
		if(generalResults == null || generalResults.size() < 1)
			throw new RuntimeException("Could not process general results");
		List<Team> teamList = generalResults.get(0);
		List<String> teamNameList = new ArrayList<String>();
		for(Team t : teamList){
			teamNameList.add(t.name);
		}
		return teamNameList;
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
		
		return teamResults;
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
		public String name;
		public double mean;
		public double stDev;
		public Map<String, Double> covariances;
	}
	

}
