package control;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.http.HTTPException;

import model.Prediction;
import model.Predictions;

import org.json.JSONObject;
import org.json.XML;

public class PredictionsServlet extends HttpServlet {

	private Predictions predictions; //backend bean
	
	
		// this method is executed the first time the servlet is loaded to the container of the server
		// and here we simply set the context to the context of the servlet so that the objects will correctly read/write files and stuff
		@Override
		public void init(){
			predictions = new Predictions();
			predictions.setServletContext(this.getServletContext());
		}//end of init
	
	
		/**
		 *  if the request object contains an id property it sends (if it exists) the matching prediction 
		 *  an appropriate message
		 * @param request the HttpServletRequest object
		 * @param request the HttpServletResponse object 
		 */
		@Override
		public void doGet(HttpServletRequest request, HttpServletResponse response){
			String param = request.getParameter("id");
			Integer key = (param == null)?null: new Integer(param.trim());
			
			//next we check if the user of the service requested a json or xml response object
			boolean json= false;
			String accept = request.getHeader("accept");
			if(accept!=null && accept.contains("json")) json = true;
			
			// if there is no parameter with id then we assume that the user wants the full list
			if(key == null){
				ConcurrentMap<Integer,Predictions> map = predictions.getMap();
				//for readability they are stored in an array and get sorted
				Object[] list = map.values().toArray();
				Arrays.sort(list);
				String xml = predictions.toXML(list);
				sendResponse(response,xml,json);
			}else{
				Prediction pred = (Prediction) predictions.getMap().get(key);
				if(pred == null){
					String msg = key + " no prediction with such an id on the server!!!";
					sendResponse(response, predictions.toXML(msg), false);
				}else{
					sendResponse(response,predictions.toXML(pred),json);
				}//end if pred is not null, i.e. a object is found
			}//end if the key parameter is not null
		}//end of doGet
		

		
		@Override
		public void doPost(HttpServletRequest request, HttpServletResponse response){
			
			String who = request.getParameter("who");
			String what = request.getParameter("what");
			
			if(who!= null && what != null){
				Prediction p = new Prediction();
				p.setWho(who);
				p.setWhat(what);
				int id = predictions.addPrediction(p);
				String msg = "new prediction with "+ id + " created";
				sendResponse(response, predictions.toXML(msg), false);
				
			}else{
				throw new HTTPException(HttpServletResponse.SC_BAD_REQUEST);
			}//end if either who or what are null
			
			
		}//end of doPost
		
		
		
		/**
		 * has to update the matching contents with the ones contained in the request object
		 */
		@Override
		public void doPut(HttpServletRequest request, HttpServletResponse reponse){
				String key = null;
				String rest = null;
				boolean who = false;
				//the data passed by the user will be in the form of id=33#who=Homer Simpson
				try{
					BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
					String data = br.readLine();
					
					String[] args = data.split("#");
					String[] part1 = args[0].split("=");
					String[] part2 = args[1].split("=");
					key = part1[1];
					if(part2[0].contains("who")) who = true;
					rest = part2[1];
					
				}catch(Exception e){
					e.printStackTrace();
					throw new HTTPException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}//end of throwing the exception
				
				//if above we didn't find key then the data is malformed
				if(key == null){
					throw new HTTPException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}else{
					Prediction p =  (Prediction)predictions.getMap().get(new Integer(key.trim()));
					if(p== null){
						String msg = "the give key "+key+" was not found";
						sendResponse(reponse,predictions.toXML(msg),false);
					}else{
						if(who)p.setWho(rest);
						else{p.setWhat(rest);}
						String msg="prediction "+key+" was edited";
						sendResponse(reponse,predictions.toXML(msg),false);
					}//end if the key was found
				}//end if a key was give
		}//end of doPut
	
		
		
		/**
		 * 
		 * @param resonse the HttpServletResponse object
		 * @param payload the body of the message i.e. what the response should contain
		 * @param json a boolean idicating if we should convert to json before displaying or not
		 */
		private void sendResponse(HttpServletResponse response, String payload, boolean json){
			
			try{
					//check if we must convert to json
					if(json){
						JSONObject jobj = XML.toJSONObject(payload);
						payload = jobj.toString(3); // 3 is indention to look nice
					}
					OutputStream out = response.getOutputStream();
					out.write(payload.getBytes());
					out.flush();
				
				
			}catch(Exception e){
				e.printStackTrace();
			}
			
			
			
			
		}//end of sendResponse
	
	
	
	
}//end of class
