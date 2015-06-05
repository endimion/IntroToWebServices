package model;

import java.io.Serializable;

public class Prediction  implements Serializable,  Comparable<Prediction>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4321280759421113270L;
	private String who;
	private String what;
	private int id;
	
	public Prediction(){}
	
	public void setWho(String who){this.who = who;}
	public String  getWho(){return this.who;}
	
	public void setWhat(String what){this.what =what;}
	public String getWhat(){return this.what;}
	
	public void setId(int id){this.id = id;}
	public  int getId(){return this.id;}
	

	
	
	/**
	 *  overwrites the compareTo method of the Comperable Interface
	 *  this method returns true if the id of this object is bigger then that of the given object
	 *  0 if they are to be considered equal
	 *  and less than zero if the id of the other is bigger. In general such methods are:
	 *  A returned negative integer signals that the current object precedes the other object.
	 * A returned positive integer signals that the current object succeeds the other object.
	 * A returned zero signals that the two objects are to be treated as equals with respect to sorting.
	 */
	public int compareTo(Prediction other){
		return this.id - other.id;
	}
	
	
	
	
}//end of class
