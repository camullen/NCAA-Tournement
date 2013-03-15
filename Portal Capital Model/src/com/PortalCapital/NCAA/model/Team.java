/**
 * 
 */
package com.PortalCapital.NCAA.model;

/**
 * @author Cameron Mullen
 *
 */
public class Team {

	public String name;
	public int id;
	public String sagarinName;
	public double sagarinRating;
	public int roundEliminated;
	
	
	
	
	public Team(){
		
	}
	
	public Team(Team other){
		name = other.name;
		id = other.id;
		sagarinName = other.sagarinName;
		sagarinRating = other.sagarinRating;
		roundEliminated = other.roundEliminated;
	}
	
	
	
	
	@Override
	public String toString(){
		String returnString = id + " - " + name;
		return returnString;
	}
	
	
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Team)) {
			return false;
		}
		Team other = (Team) obj;
		if (id != other.id) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}
	
	
	
	
}
