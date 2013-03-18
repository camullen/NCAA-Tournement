package com.PortalCapital.NCAA.model.test;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import com.PortalCapital.NCAA.model.OddsDownloader;

public class OddsDownloaderTest extends TestCase {

	public OddsDownloaderTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	
//	public void testNoCrash(){
//		
//		OddsDownloader odds = new OddsDownloader();
//		File downloadedFile = new File("USATodayOdds.html");
//		try {
//			odds.getOdds(downloadedFile, "http://sportsdirect.usatoday.com/odds/usatoday/odds.aspx");
//			odds.getAllTeams();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	public void testNoCrashOnline(){
		try {
			OddsDownloader odds = new OddsDownloader();
			odds.getOdds("http://sportsdirect.usatoday.com/odds/usatoday/odds.aspx");
			odds.getAllTeams();
		} catch (IOException e){
			e.printStackTrace();
		}
		
	}

}
