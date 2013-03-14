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
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
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
	public static final int TEAM_FILE_COLS = 4;
	
	public Map<Integer, List<Set<Integer>>> structure;
	public HashMap<Integer, Team> teamMap;
	
	private boolean structureLoaded;
	private boolean teamsLoaded;
	
	public Tournament(){
		structureLoaded = false;
		teamsLoaded = false;
		structure = new HashMap<Integer, List<Set<Integer>>>();
		teamMap = new HashMap<Integer, Team>();
	}
	
	
	public List<Team> simulateTournament(double standardDeviation){
		if(!(structureLoaded && teamsLoaded)) return null;
		List<Team> teamsAlive = new LinkedList<Team>(teamMap.values());
		List<Team> teamResults = new ArrayList<Team>();
		
		GamePlayer player = new GamePlayer(standardDeviation);
		ListIterator<Team> it = teamsAlive.listIterator();
		
		for(int i = 0; i < TOURNAMENT_ROUNDS; i++){
			while(it.hasNext()){
				Team thisTeam = it.next();
				doTeamRound(thisTeam, i, teamsAlive, teamResults, player);
			}
			it = teamsAlive.listIterator();
		}
		
		if(teamsAlive.size() != 1) return null;
		removeTeam(teamsAlive.iterator().next(), teamsAlive, teamResults, TOURNAMENT_ROUNDS + 1);
		return teamResults;
	}
	
	
	/**
	 * @param thisTeam
	 * @param i
	 * @param teamsAlive
	 * @param teamResults
	 * @param player
	 */
	private void doTeamRound(Team thisTeam, int round, List<Team> teamsAlive,
			List<Team> teamResults, GamePlayer player) {
		Team opponent = getOpponent(thisTeam, round, teamsAlive);
		boolean result = player.playGame(thisTeam, opponent);
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
	private void removeTeam(Team opponent, List<Team> teamsAlive,
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
	private Team getOpponent(Team thisTeam, int round, List<Team> teamsAlive) {
		Set<Integer> potentialOpponentsOrig = structure.get(thisTeam.id).get(round);
		Set<Team> potentialOpponents = new HashSet<Team>();
		for(Integer i : potentialOpponentsOrig){
			potentialOpponents.add(teamMap.get(i));
		}
		potentialOpponents.retainAll(teamsAlive);
		if(potentialOpponents.size() != 1) return null;
		return potentialOpponents.iterator().next();
	}


	public void loadTournamentStructure(String filename) throws IOException{
		BufferedReader structureReader;
		structureReader = new BufferedReader(new FileReader(filename));
		while(structureReader.ready()){
			String fileLine = structureReader.readLine();
			addStringToStructure(fileLine);
		}
		structureLoaded = true;
	}
	
	public void loadTournamentTeams(String filename) throws IOException{
		BufferedReader teamReader;
		teamReader = new BufferedReader(new FileReader(filename));
		while(teamReader.ready()){
			String fileLine = teamReader.readLine();
			addStringToTeams(fileLine);
		}
		teamsLoaded = true;
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
		if(allTokens.size() != TEAM_FILE_COLS || allTokens.get(0).contains("Name")) return;
		Team thisTeam = new Team();
		thisTeam.name = allTokens.get(0);
		thisTeam.id = Integer.parseInt(allTokens.get(1));
		thisTeam.sagarinName = allTokens.get(2);
		thisTeam.sagarinRating = Double.parseDouble(allTokens.get(3));
		teamMap.put(thisTeam.id, thisTeam);
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
		
		if(allTokens.size() != 69 || allTokens.get(0).contains("Team")) return;
		
		List<Set<Integer>> teamSchedule = new ArrayList<Set<Integer>>();
		
		int index = 0;
		int teamsInRound = 1;
		int teamID;
		try {
			teamID = Integer.parseInt(allTokens.get(0));
		} catch (NumberFormatException e){
			return;
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
					return;
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
