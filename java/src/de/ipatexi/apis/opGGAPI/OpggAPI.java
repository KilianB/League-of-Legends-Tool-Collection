package de.ipatexi.apis.opGGAPI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.ipatexi.apis.RiotAPI.Enums.League;

/**
 * Minimal OpggAPI to retrive items
 * @author Kilian
 *
 */
public class OpggAPI {

	private static final String API_KEY = "";
	
	private static final String OP_GG_ENDPOINT = "http://api.champion.gg/";
	private static final String CHAMPION_ENDPOINT = "v2/champions/";	//Currently the new endpoint does not support tags and therefore can not be used
	private static final String CHAMPION_ENDPOINT_OLD = "champion/";
	
	/**
	 * For now the new api does not return the items therefore we have to work with the old version
	 * 
	 * (int championID, League elo){
	 * @param championID
	 * @param elo
	 */
	public int[] getMostPickedItems(String championName){
		
		int[] itemIds = new int[6];	
		
		String request;
		
		
		championName = championName.replace(" ","");// Lee Sin
		championName = championName.replace("'", ""); //Kha'Zix
		championName = championName.replace(".","");//  Dr.Mundo	
	
		request = OP_GG_ENDPOINT + CHAMPION_ENDPOINT_OLD + championName + "?api_key=" + API_KEY;
		

		//Object o = executeRequest(request);
		JSONArray value = (JSONArray) executeRequest(request);
		
		//Items
		JSONObject mostPickedRole = (JSONObject) value.get(0);
		
		JSONArray items = (JSONArray) ((JSONObject) ((JSONObject) mostPickedRole.get("items")).get("mostGames")).get("items");
		
		for(int i = 0; i < 6; i++){
			//for some reason sometimes it does not report any items back
			try{
			itemIds[i] = (int)((long) ((JSONObject)items.get(i)).get("id"));
			}
			catch(Exception e){
				itemIds[i] = -1;
			}
		}
		return itemIds;
	}
	
	
	private Object executeRequest(String requestURL){
		
		try {
			URL url = new URL(requestURL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
			int responseCode = connection.getResponseCode();
			if(responseCode == 429){ //rate limit exceeded
				
				System.out.println(connection.getHeaderFields().toString());
				
				String retry = connection.getHeaderField("Retry-After");
				long secondsUntilNextCall = 10; // default wait 10 seconds. Might apply if no rate limiting context is provided.
				if(retry != null){
					secondsUntilNextCall  = Long.parseLong(retry);
				}
				
				 
				System.out.println("Rate limit exceeded. Retry after: " + secondsUntilNextCall);
				
				try {
					Thread.sleep(secondsUntilNextCall*1000 + 500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return executeRequest(requestURL);
			}else if(responseCode == 404){ //Not found. exp if no ranked information is present. = Unranked
				return null;
			}	
			else{
			
				//System.out.println("connection: " + connection.getResponseCode() + " " + requestURL);
				BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
			
				try {
				
					return 	new JSONParser().parse(br);
					
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				connection.disconnect();
			}
			
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
		
	}
	
}
