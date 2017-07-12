package de.ipatexi.apis.googleTextToSpeech;
import java.io.File;

public interface GoogleTextToSpeechObserver {

	/**
	 * Gets called when the first file of the request completed downloaded and got written to the disk.
	 * File Downloaded will also be called in this case afterwards. 
	 * @param f The file which was created
	 * @param id A unique id given to the request to connect successive fileDownloaded calls 
	 */
	public void firstFileDownloaded(File f, int id);
	
	/**
	 * Gets called when ever a file is downloaded and got written to the disk
	 * @param f  The file which was created
	 * @param id A unique id given to the request to connect successive fileDownloaded calls 
	 */
	public void fileDownloaded(File f, int id);
	
	/**
	 * Gets called when after the last fileDownloaded event was sent to the observer
	 * @param id A unique id given to the request to connect successive fileDownloaded calls 
	 */
	public void fileDownloadCompleted(int id);
	
}
