package com.xorboo.hatfortress.loaders;

import java.io.IOException;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;

import android.media.MediaPlayer;
import android.util.Log;

import com.xorboo.hatfortress.MainActivity;
import com.xorboo.hatfortress.utils.GameConstants;

public class MfxAssets {
	static MainActivity main;
	
	// Фоновая музыка
	private static Music music = null;
	static int songNumber = -1;
	
	private static String [] musicFiles= {
		"Drunken Pipe Bomb (Meet the Demoman).mp3",
		"Faster Than a Speeding Bullet (Meet the Scout).mp3",
		"Haunted Fortress 2.mp3",
		"Intruder Alert (Meet the Spy).mp3",
		"More Gun (Meet the Engineer).mp3",
		/*"Petite Chou-Fleur (Meet the Spy).mp3",
		"Playing With Danger.mp3",
		"Right Behind you (Meet the Spy).mp3",
		"Rocket Jump Waltz.mp3",
		"Team Fortress 2.mp3",
		"The Art of War.mp3"*/	
	};
	
	public static void loadMfx(){
		MusicFactory.setAssetBasePath("mfx/");
		main = MainActivity._main;
	}
	
	private static void loadSong(){
		try{
			// Боимся утечек памяти
			if (music!=null){
				music.stop();
				music.release();
				music = null;
			}
			String nameString = getRandomSong();
			music = MusicFactory.createMusicFromAsset(main.getMusicManager(), main, nameString);
			music.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
				{
					public void onCompletion(MediaPlayer mp){
						loadSong();
						music.play();
					}
				});
		} catch (IOException e){
			Log.e("Music Loader", "Can't open music file ._.");
			loadSong();
			music.play();
		}
	}
	
	private static String getRandomSong(){
		int k = songNumber;
		while (k == songNumber){
			k = GameConstants.rand.nextInt(MfxAssets.musicFiles.length);
		}
		songNumber = k;
		return musicFiles[songNumber];
	}
	
	public static void playMusic(){
		if (music == null || !music.isPlaying()){
			loadSong();
			music.play();
		}
	}
	
	public static void stopMusic(){
		if (music != null && music.isPlaying()){
			music.stop();
			music.release();
			music = null;
		}
	}
}
