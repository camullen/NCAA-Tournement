package com.PortalCapital.NCAA.model;

public class GameResult{
	public String teamA;
	public String teamB;
	public int aScore;
	public int bScore;
	
	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + aScore;
		result = prime * result + bScore;
		result = prime * result + ((teamA == null) ? 0 : teamA.hashCode());
		result = prime * result + ((teamB == null) ? 0 : teamB.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof GameResult))
			return false;
		GameResult other = (GameResult) obj;
		if (aScore != other.aScore)
			return false;
		if (bScore != other.bScore)
			return false;
		if (teamA == null) {
			if (other.teamA != null)
				return false;
		} else if (!teamA.equals(other.teamA))
			return false;
		if (teamB == null) {
			if (other.teamB != null)
				return false;
		} else if (!teamB.equals(other.teamB))
			return false;
		return true;
	}
	
	
	
	
}
