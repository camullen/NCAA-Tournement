/**
 * 
 */
package com.PortalCapital.NCAA.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * @author Cameron Mullen
 *
 */
public class Tournament {
	public static final int TEAMS_PER_REGION = 16;
	public static final int TOURNAMENT_ROUNDS = 6;
	public static final int TEAM_FILE_COLS = 6;
	
	public Map<Integer, List<Set<Integer>>> structure;
	public HashMap<Integer, Team> masterTeamMap;
	private HashMap<Integer, Team> tempTeamMap;
	
	private boolean structureLoaded;
	private boolean teamsLoaded;
	private GamePlayer player;
	
	public Tournament(){
		structureLoaded = false;
		teamsLoaded = false;
		structure = new HashMap<Integer, List<Set<Integer>>>();
		masterTeamMap = new HashMap<Integer, Team>();
		player = new GamePlayer();
	}
	
	
	public List<Team> simulateTournament(double standardDeviation){
		if(!structureLoaded){
			System.err.println("Error: Tournament Structure Not Loaded");
			assert(false);
		}
		
		if(!teamsLoaded){
			System.err.println("Error: Teams Not Properly Loaded");
			assert(false);
		}
		cloneTeamMap();
		Set<Team> teamsAlive = new HashSet<Team>(tempTeamMap.values());
		List<Team> teamResults = new ArrayList<Team>(tempTeamMap.values());
		
		
		
		player.setStandardDeviation(standardDeviation);
		simulatePlayInGames(tempTeamMap, teamsAlive, player, teamResults);
		
		
		for(int i = 0; i < TOURNAMENT_ROUNDS; i++){
			Set<Team> teamsPlayed = new HashSet<Team>();
			for(Team t : tempTeamMap.values()){
				doTeamRound(t, i, teamsAlive, teamResults, player, teamsPlayed);
			}
		}
		
		if(teamsAlive.size() != 1) {
			throw new RuntimeException("Error: More than one team remaining after simulation");
		}
		removeTeam(teamsAlive.iterator().next(), teamsAlive, teamResults, TOURNAMENT_ROUNDS + 1);
		return teamResults;
	}
	
	
	private void cloneTeamMap(){
		tempTeamMap = new HashMap<Integer, Team>();
		for(Team t : masterTeamMap.values()){
			tempTeamMap.put(t.id, t.clone());
		}
	}
	
	
	
	/**
	 * @param tempTeamMap 
	 * @param teamsAlive
	 * @param teamResults
	 */
	private void simulatePlayInGames(HashMap<Integer, Team> tempTeamMap, Set<Team> teamsAlive, GamePlayer player, List<Team> teamResults) {
		for(Team t: new HashSet<Team>(tempTeamMap.values())){
			if(t.id > 1000 && t.id < 2000){
				Team opponent = tempTeamMap.get(t.id + 1000);
				if(opponent == null)
					throw new RuntimeException("Could not find other play in team");
				boolean result = player.playGame(t, opponent);
				if(result){
					removeTeam(opponent, teamsAlive, teamResults, 0);
					t.id -= 1000 * (t.id / 1000);
					tempTeamMap.put(t.id, t);
				} else {
					removeTeam(t, teamsAlive, teamResults, 0);
					opponent.id -= 1000 * (opponent.id / 1000);
					tempTeamMap.put(opponent.id, opponent);
				}
			}
		}
		
	}


	/**
	 * @param thisTeam
	 * @param i
	 * @param teamsAlive
	 * @param teamResults
	 * @param player
	 */
	private void doTeamRound(Team thisTeam, int round, Set<Team> teamsAlive,
			List<Team> teamResults, GamePlayer player, Set<Team> teamsPlayed) {
		if(!teamsAlive.contains(thisTeam)) return;
		if(teamsPlayed.contains(thisTeam)) return;
		Team opponent = getOpponent(thisTeam, round, teamsAlive);
		boolean result = player.playGame(thisTeam, opponent);
		teamsPlayed.add(thisTeam);
		teamsPlayed.add(opponent);
		if(result){
			removeTeam(opponent, teamsAlive, teamResults, round + 1);
		} else {
			removeTeam(thisTeam, teamsAlive, teamResults, round + 1);
		}
	}


	/**
	 * @param opponent
	 * @param teamsAlive
	 * @param teamResults
	 * @param round
	 */
	private void removeTeam(Team opponent, Set<Team> teamsAlive,
			List<Team> teamResults, int roundEliminated) {
		Team opponentCopy = new Team(opponent);
		opponentCopy.roundEliminated = roundEliminated;
		teamResults.add(opponentCopy);
		teamsAlive.remove(opponent);
	}


	/**
	 * @param thisTeam
	 * @param round
	 * @return
	 */
	private Team getOpponent(Team thisTeam, int round, Set<Team> teamsAlive) {
		Set<Integer> potentialOpponentsOrig = structure.get(thisTeam.id).get(round);
		Set<Team> potentialOpponents = new HashSet<Team>();
		for(Integer i : potentialOpponentsOrig){
			potentialOpponents.add(tempTeamMap.get(i));
		}
		potentialOpponents.retainAll(teamsAlive);
		if(potentialOpponents.size() != 1) {
			throw new RuntimeException("Error: Multiple teams available to play");

		}
		return potentialOpponents.iterator().next();
	}


	public void loadTournamentStructure(String filename) throws IOException{
		BufferedReader structureReader;
		structureReader = new BufferedReader(new FileReader(filename));
		while(structureReader.ready()){
			String fileLine = structureReader.readLine();
			if(!fileLine.equals(""))
				addStringToStructure(fileLine);
		}
		structureLoaded = true;
		structureReader.close();
	}
	
	public void loadTournamentTeams(String filename) throws IOException{
		BufferedReader teamReader;
		teamReader = new BufferedReader(new FileReader(filename));
		while(teamReader.ready()){
			String fileLine = teamReader.readLine();
			addStringToTeams(fileLine);
		}
		teamsLoaded = true;
		teamReader.close();
	}
	
	

	/**
	 * @param fileLine
	 */
	private void addStringToTeams(String fileLine) {
		List<String> allTokens = new ArrayList<String>();
		StringTokenizer tok = new StringTokenizer(fileLine, ",");
		while(tok.hasMoreTokens()){
			allTokens.add(tok.nextToken());
		}
		
		if(allTokens.size() > 0 && allTokens.get(0).contains("Name")) return;
		if(allTokens.size() != TEAM_FILE_COLS){
			throw new RuntimeException("Unable to process team file. Expected " + TEAM_FILE_COLS + ". Was: " + allTokens.size());
		}
		
		Team thisTeam = new Team();
		thisTeam.name = allTokens.get(0);
		thisTeam.id = Integer.parseInt(allTokens.get(1));
		thisTeam.sagarinName = allTokens.get(2);
		thisTeam.sagarinRating = Double.parseDouble(allTokens.get(3));
		thisTeam.usaTodayName = allTokens.get(4);
		thisTeam.roundEliminated = Integer.parseInt(allTokens.get(5));
		masterTeamMap.put(thisTeam.id, thisTeam);
	}


	/**
	 * @param fileLine
	 */
	private void addStringToStructure(String fileLine) {
		List<String> allTokens = new ArrayList<String>();
		StringTokenizer tok = new StringTokenizer(fileLine, ",");
		while(tok.hasMoreTokens()){
			allTokens.add(tok.nextToken());
		}
		
		if(allTokens.size() > 0 && allTokens.get(0).contains("Team")){
			return;
		}
		
		if(allTokens.size() != 69) {
			throw new RuntimeException("Error: expected 69 columns in file. # of columns found: " + allTokens.size());
		}
		
		List<Set<Integer>> teamSchedule = new ArrayList<Set<Integer>>();
		
		int index = 0;
		int teamsInRound = 1;
		int teamID;
		try {
			teamID = Integer.parseInt(allTokens.get(0));
		} catch (NumberFormatException e){
			throw new RuntimeException("Error: unparsable line from file - could not get team ID: number format exception");
		}
		
		for(int i = 0; i < TOURNAMENT_ROUNDS; i++){
			index++;
			Set<Integer> teamsThisRound = new HashSet<Integer>();
			
			
			/*
			 * Add a team from the string to the set for this particular
			 * round. Advances the index by one
			 */
			for(int j = 0; j < teamsInRound; j++){
				String intString = allTokens.get(index);
				int intFromString;
				try{
					intFromString = Integer.parseInt(intString);
				} catch (NumberFormatException e){
					throw new RuntimeException("Error: unparsable line from file - could not process team schedule");
				}
				teamsThisRound.add(intFromString);
				index++;
			}
			
			teamSchedule.add(teamsThisRound);
			teamsInRound *= 2; //potential teams per round double every round
		}
		structure.put(teamID, teamSchedule);
	}
	
	
	
	
}
