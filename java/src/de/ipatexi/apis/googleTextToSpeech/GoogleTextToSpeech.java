package de.ipatexi.apis.googleTextToSpeech;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;


/**
 * This class allows to convert long strings of text into .mp3 files in real time.
 * If you want to convert larger volumes of data please consider using the official cloud spech api https://cloud.google.com/speech/
 * @author Kilian
 */
public class GoogleTextToSpeech {

	private final String GOOGLE_URL = "https://translate.google.com/translate_tts?ie=UTF-8&client=tw-ob&q=__TEXT__&tl=";
	private final String defaultLanguage = "en-gb";  //all allowed language codes can be found here https://cloud.google.com/speech/docs/languages
	
	/* The string length limit for every request. Increasing this will result in increased performance and fewer generated mp3 files. 
	 * In turn this will result in the first file needing longer to convert. The Google api only allows a limited request length it 
	 * allows which lays somewhere between 200 - 300 characters per request. If exceeded goofle just cuts of the request or simply
	 * sends a 420 header back. If not needed leave it as it is as it has the potential to break the request!. 
	 */
	private final int characterLimit = 200;
	private String outFilePath;
	
	/**
	 * @param outFilePath The directory where the .mp3 files will be stored
	 */
	public GoogleTextToSpeech(String outFilePath){
		this.outFilePath = outFilePath;
	}
	
	/**
	 * Converts the following text to spoken mp3 files.
	 * @param text The text which will be send to the tts service
	 * @param outputFilePrefix The name of the resulting mp3 files
	 * 			Naming convention for example Apple:
	 * 				Apple0.mp3
	 * 				Apple1.mp3
	 * 				Apple2-mp3
	 * 				...
	 * @param observer
	 * 			Observer which will be notified once files are ready to be played.
	 * 			Can be null if not needed
	 */
	public void convertText(String text ,String outputFilePrefix, GoogleTextToSpeechObserver observer){
		convertText(text,defaultLanguage,outputFilePrefix,observer);
	}
	
	/**
	 * Converts the following text to spoken mp3 files.
	 * @param text The text which will be send to the tts service
	 * @param language the language pack used by google to speak the sentence
	 * @param outputFilePrefix The name of the resulting mp3 files
	 * 			Naming convention for example Apple:
	 * 				Apple0.mp3
	 * 				Apple1.mp3
	 * 				Apple2-mp3
	 * 				...
	 * @param observer
	 * 			Observer which will be notified once files are ready to be played.
	 * 			Can be null if not needed
	 */
	public void convertText(String text,String language,String outputFilePrefix, GoogleTextToSpeechObserver observer){
		
		/*Set an id to this request. In case of multiple requests the observers can distinguish the files based on the id.
			if a file is converted twice it will have the same id connected to it */
		int id = text.hashCode();
		
		
		System.out.println("text: " + text);
		
		//Prepare the text and split it up into suitable chunks
		
		String sentences[] = text.split("\\.");
		ArrayList<String> requestStrings = new ArrayList<String>();
		
		String tempRequest = "";
		for(int i = 0; i < sentences.length; i++){
			
			if( (tempRequest.length() + sentences[i].length()) > characterLimit){;
				//If it's not empty add it to the queue. This can happen at the very beginning.
				if(!tempRequest.isEmpty())
					requestStrings.add(tempRequest); //Add the old request to the queue
				tempRequest = sentences[i] +"."; //And fill the current line to the next reuqest.
			}else{
				tempRequest += sentences[i] + ".";
			}
			
			//If the current sentence is to long, we need to cut it somewhere after a word.
			while(tempRequest.length() > characterLimit){
				
				int wordEndIndex = tempRequest.lastIndexOf(" ",characterLimit);
				String subsentence = tempRequest.substring(0, wordEndIndex);
				requestStrings.add(subsentence);
				tempRequest = tempRequest.substring(wordEndIndex);
			}
		}
		
		//and at the very end simply add the remainder to the request
		if(tempRequest.length() > 0){
			requestStrings.add(tempRequest);
		}
	
		//Download the files
		
		try{
		//Build request
			
			//We could multi thread this but time wise this should not be needed and a more complex file handling will be needed at the player part
			
			final int entriesCount = requestStrings.size();
			for(int i = 0; i < entriesCount; i++){
				String query = 	URLEncoder.encode(requestStrings.get(i), "UTF-8");
				String request = GOOGLE_URL.replace("__TEXT__", query) + language;
			
				URLConnection conn = new URL(request).openConnection();
				conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
				InputStream is = conn.getInputStream();
				
				//TODO we might need to ad a small sleep for google to be able to process the files
				
				File outputFile = new File(outFilePath + outputFilePrefix+i+".mp3");
				OutputStream outstream = new FileOutputStream(outputFile);
				byte[] buffer = new byte[4096];
				int len;
				while ((len = is.read(buffer)) > 0) {
					outstream.write(buffer, 0, len);
				}
				outstream.close();
				
				//Notify observers
				if(observer != null){
					if(i == 0){
						observer.firstFileDownloaded(outputFile,id);
					}
					observer.fileDownloaded(outputFile,id);
				}
			}
		
			observer.fileDownloadCompleted(id);
		
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Merge the separate file into one huge mp3 file. Merging files isn't as simple as concatenating files as
	 * we need to take care of the header fields. You might be able to find libraries which are able to do this
	 * for the sake of keeping this repository minimal this function is not included.
	 */
	public void mergeFiles(String targetName,File... filesToMerge){
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	
}
