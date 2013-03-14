/**
 * 
 */
package com.PortalCapital.NCAA.model;

import org.apache.commons.math3.distribution.NormalDistribution;


/**
 * @author Cameron Mullen
 *
 */
public class GamePlayer {
	/*
	 * Will need to incorporate point spreads and outcomes into this one
	 */
	
	private double standardDeviation;
	
	public GamePlayer(double standardDeviation){
		
		this.standardDeviation = standardDeviation;
	}
	
	
	
	/*
	 * Returns true if team A beats Team B; returns false if Team B beats team A
	 */
	public boolean playGame(Team a, Team b){
		
		//Should incorporate point spread and historical results preferentially
		
		return sagarinPlayGame(a, b);
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
	
	
	

}
