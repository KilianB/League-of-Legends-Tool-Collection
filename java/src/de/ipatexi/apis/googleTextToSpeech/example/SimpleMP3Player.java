package de.ipatexi.apis.googleTextToSpeech.example;

import java.io.File;
import java.util.Vector;

import de.ipatexi.apis.googleTextToSpeech.GoogleTextToSpeechObserver;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class SimpleMP3Player implements GoogleTextToSpeechObserver {

	private MediaPlayer player;
	private float volume = 0.5f;
	private Vector<File> queuedFilesForPlayBack; // Thread save playlist
	boolean startedPlaying = false;
	boolean isPlaying = false;

	public SimpleMP3Player() {

		// For the sake of demonstration we create a simple media player using javafx.
		// If you run a proper javafx application those nasty workarounds are not
		// necessary

		// Hacky toolkit initialization
		new JFXPanel();

		// By calling the player we keep the fx application thread receiving updates
		new Thread(() -> {
			while (true) {
				try {
					Thread.sleep(10);
					if (player != null)
						player.getStatus();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void stop() {
		if (player != null) {
			player.stop();
		}
		isPlaying = false;
	}

	/**
	 * Volume of the lore player between 0 and 1
	 * 
	 * @param volume
	 */
	public void setVolume(float volume) {
		this.volume = volume;
		if (player != null)
			player.setVolume(volume);
	}

	/**
	 * Recursive play back of all files added to the playlist
	 */
	private void startPlayback() {
		System.out.println("Start playback: " + queuedFilesForPlayBack.size());

		// See if we got files to work with.
		if (!queuedFilesForPlayBack.isEmpty()) {
			String filePath = queuedFilesForPlayBack.remove(0).toURI().toString();
			player = new MediaPlayer(new Media(filePath));
			player.setVolume(volume);
			player.setOnEndOfMedia(new Runnable() {
				@Override
				public void run() {
					startPlayback();
				}
			});
			player.play();
		} else {
			// We are at the end of the playlist.
			isPlaying = false;
			// Discard all calls to the observer from past requests
			currentId = -1;
			// For the sake of this sample exit here
			System.exit(0);
		}
	}

	/**
	 * @return true if we currently play back a file
	 */
	public boolean isPlaying() {
		return isPlaying;
	}

	/*
	 * Id to match separate mp3 files which belong together. If this is connected to
	 * multiple conversions we ignore every other file which is not part of the one
	 * first recieved file.
	 */

	int currentId;
	File firstFile;

	@Override
	public void firstFileDownloaded(File f, int id) {
		// Start the playback
		isPlaying = true;
		currentId = id;
		queuedFilesForPlayBack = new Vector<File>();
		queuedFilesForPlayBack.addElement(f);
		firstFile = f;

		// Start a new thread to free the observer
		new Thread(() -> startPlayback()).start();
	}

	@Override
	public void fileDownloaded(File f, int id) {
		// We will only take care of the most current id and discard everything else for
		// now
		if (id == currentId) {
			if (firstFile != f) { // don't add the first file twice
				queuedFilesForPlayBack.add(f);
			}

		}
	}

	@Override
	public void fileDownloadCompleted(int id) {
		// We could merge the files if neccesarry
	}

}
