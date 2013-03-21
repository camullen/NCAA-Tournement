package com.PortalCapital.NCAA.model;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.PortalCapital.NCAA.model.Simulator.TeamStats;

public class ResultsOutputter {
	
	public static void writeResultsToFile(String filename, List<TeamStats> teamStatsList) throws FileNotFoundException{
		List<String> teamNames = new ArrayList<String>();
		for(TeamStats stats: teamStatsList){
			teamNames.add(stats.name);
		}
		PrintWriter writer = new PrintWriter(filename);
		writer.print("Name,Mean,StandardDeviation");
		for(String name : teamNames){
			writer.print("," + name);
		}
		writer.println();
		
		for(TeamStats stats: teamStatsList){
			writer.print(stats.name + ",");
			writer.print(stats.mean + ",");
			writer.print(stats.stDev);
			if(stats.covariances == null)
				throw new RuntimeException("Covariance map is null");
			for(String name : teamNames){
				Double covariance = stats.covariances.get(name); 
				writer.print(",");
				writer.print(covariance);
			}
			writer.println();
		}
	
		writer.flush();
		writer.close();
		
	}

}
