/**
 * 
 */
package com.PortalCapital.NCAA.model.test;

import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

import com.PortalCapital.NCAA.model.Team;
import com.PortalCapital.NCAA.model.Tournament;



/**
 * @author Cameron Mullen
 *
 */
public class TournamentTest extends TestCase {

	public static final String STRUCTURE_FILE = "TournamentStructure.csv";
	public static final String TEAM_FILE = "testTeams.csv";
	
	
	/**
	 * @param name
	 */
	public TournamentTest(String name) {
		super(name);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	
	public void testTournamentStructure(){
		
		Tournament t = new Tournament();
		try {
			t.loadTournamentStructure(STRUCTURE_FILE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertEquals(64, t.structure.size());
		assertEquals(6, t.structure.get(101).size());
		assertEquals(1, t.structure.get(101).get(0).size());
		assertEquals(2, t.structure.get(101).get(1).size());
		assertEquals(4, t.structure.get(101).get(2).size());
		assertEquals(8, t.structure.get(101).get(3).size());
		assertEquals(16, t.structure.get(101).get(4).size());
		assertEquals(32, t.structure.get(101).get(5).size());
	}
	
	
	public void testInputTeams(){
		Tournament t = new Tournament();
		
		try {
			t.loadTournamentTeams(TEAM_FILE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Team kansas = t.teamMap.get(302);
		assertNotNull(kansas);
		assertEquals("Kansas", kansas.name);
	}
	
	public void testNoCrash(){
		Tournament t = new Tournament();
		try {
			t.loadTournamentStructure(STRUCTURE_FILE);
			t.loadTournamentTeams(TEAM_FILE);
			List<Team> results = t.simulateTournament(9.0);
			assertNotNull(results);
			for(Team thisTeam : results){
				System.out.println(thisTeam + " eliminated in round " + thisTeam.roundEliminated);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	

}
