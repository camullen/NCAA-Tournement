package com.PortalCapital.NCAA.model.test;

import java.io.IOException;
import java.util.Map;

import junit.framework.TestCase;

import com.PortalCapital.NCAA.model.ActualsReader;
import com.PortalCapital.NCAA.model.GameResult;

public class ActualsReaderTest extends TestCase {
	
	public static final String TEST_FILE = "testResults.csv";
	
	public void testBasicFunctionality(){
		try {
			Map<String, GameResult> results = ActualsReader.getResults(TEST_FILE);
			GameResult firstFind = results.get("StanfordCalifornia");
			GameResult secondFind = results.get("CaliforniaStanford");
			
			
			assertNotNull(firstFind);
			assertNotNull(secondFind);
			assertEquals(firstFind, secondFind);
			
			assertEquals("California", firstFind.teamA);
			assertEquals("Stanford", firstFind.teamB);
			
			assertEquals(3, firstFind.aScore);
			assertEquals(21, firstFind.bScore);
			
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
