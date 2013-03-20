package com.PortalCapital.NCAA.model.test;

import java.io.FileNotFoundException;
import java.util.List;

import junit.framework.TestCase;

import com.PortalCapital.NCAA.model.ResultsOutputter;
import com.PortalCapital.NCAA.model.Simulator;
import com.PortalCapital.NCAA.model.Simulator.TeamStats;

public class SimulatorTest extends TestCase {
	
	public void testNoCrash(){
		Simulator sim = new Simulator();
		sim.simulate(100000, 9.5);
		List<TeamStats> teamStatsList = sim.getTeamStats(9.5);
		assertEquals(68, teamStatsList.size());
		try {
			ResultsOutputter.writeResultsToFile("testOutputs.csv", teamStatsList);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
