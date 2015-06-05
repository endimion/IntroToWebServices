package model;

import java.beans.XMLEncoder;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletContext;

public class Predictions {

	//private Prediction[] predictions; // this is replaced by the concurrent map below
	
	private ServletContext cntx;
	private ConcurrentMap<Integer, Prediction> predictions; 	//this allows multiple threads to have
																																//	acess to the object and once a thread 
																																//updates it then the data become available
																																//to all other threads
	private AtomicInteger mapKey; 	// An int value that may be updated atomically. 
	
	
	/**
	 *  constructor
	 */
	public Predictions(){
		predictions = new ConcurrentHashMap<Integer,Prediction>();
		mapKey = new AtomicInteger();
	}//end of constructor
	
	public void setServletContext(ServletContext cntx){this.cntx = cntx;}
	public ServletContext getServletContext(){return this.cntx;}
	
	/* these methods are replaced by the setMap and getMap respectively!!!!
	 * 
	 * 
	   public void setPredictions(String ps){} // no-op
		public String getPredictions(){
			
				if(getServletContext()== null ) return null;
				else{
					if(predictions == null) populate();
					return toXML();	
				}
		} //end of getPredictions
	 */
	
	public void setMap(ConcurrentMap<String,Prediction> predictions){
		//no-op
	}
	
	/**
	 * 
	 * @return the ConcurrentMap of predictions
	 * if the context of the servlet is not set then null is retunred (because we cannot read from the file)
	 * also if the size of the predictions map is < 1 then we first populate the map
	 * and then return itub
	 */
	public ConcurrentMap getMap(){
		if(getServletContext() == null) return null;
		else{
			if(predictions.size() < 1) populate();
			return this.predictions;
		}
	}//end of getMAP
	
	/**
	 * 
	 * @param p a new prediction object to be added to the Predictions concurrentMap
	 * @return the id the new prediction got
	 */
	public int addPrediction(Prediction p){
		int id = mapKey.incrementAndGet();
		p.setId(id);
		predictions.put(id,p);
		return id;
	}//end of addPrediction
	
	
	
	/**
	 * reads the contents of the file predictions.db
	 * and then stores then as prediction objects to the predictions array
	 */
	public void populate(){
		String filename = "/WEB-INF/data/predictions.db";
		
		InputStream in = cntx.getResourceAsStream(filename); 
		if(in != null){
			try{
				InputStreamReader isr = new InputStreamReader(in);
				BufferedReader reader = new BufferedReader(isr);
				//predictions = new Prediction[n];
				String line = null;
				int i =0;
				
				while( (line = reader.readLine())!= null  ){
					String[] parts =line.split("!");
					Prediction p = new Prediction();
					p.setWho(parts[0]);
					p.setWhat(parts[1]);
					
					addPrediction(p);
					i++;
				}
			}catch(IOException e){e.printStackTrace();}
		}//end of if in is not null
		
	}//end of populate
	
	
	/**
	 * 
	 * @return an XML representation of the contents of the 
	 * predictions array!!!
	 */
	public String toXML(Object pred){
		String xml = null;
		try{
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			XMLEncoder encoder = new XMLEncoder(out); //serialize to XML!!!!
			encoder.writeObject(pred);
			encoder.close();
			
			xml = out.toString();
		}catch(Exception e){e.printStackTrace();}
		
		return xml;
	}//end of to XML
	
	
	
	
	
	
	
}//end of class
