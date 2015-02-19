package com.xorboo.hatfortress.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Calendar;

import android.content.Context;
import android.util.Log;

import com.xorboo.hatfortress.MainActivity;

public class Logger {
	static String path = "logs.txt";
	public static void write(String tag, String text) {
		Log.d("LOGGER", tag + ": " + text);
		writeToFile(tag, text);
	}
	
	public static String getTime() {
		Calendar now = Calendar.getInstance();
		String time = now.get(Calendar.HOUR_OF_DAY) + ":" + 
			now.get(Calendar.MINUTE) + ":" +
			now.get(Calendar.SECOND);
		return time;
	}
	
	public static void showAll() {
		String str="";
		StringBuffer buf = new StringBuffer();			
		FileInputStream fIn;
		try {
			fIn = MainActivity.getContext().openFileInput(path);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fIn));
			if (fIn!=null) {	
				while ((str = reader.readLine()) != null) {	
					buf.append(str + "\n" );
				}		
			}		
			fIn.close();
			Log.d("LOGGER", buf.toString());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	private static void writeToFile(String tag, String text) {
		Calendar now = Calendar.getInstance();
		String time = "[" + now.get(Calendar.HOUR_OF_DAY) + ":" + 
			now.get(Calendar.MINUTE) + ":" +
			now.get(Calendar.SECOND) + "]";
		String message = time + " " + tag + ": " + text + '\n';
		if(message != null) {
			//String full_path = Environment.getExternalStorageDirectory().getAbsolutePath();
			//full_path = full_path + "/" + path;
			try { 
					// Пишет в папку приложения в root/data/data/com.xorboo.hatfortress/files, работает всегда
				Context context = MainActivity._main.getApplicationContext();
				FileOutputStream fOut = context.openFileOutput(path, Context.MODE_APPEND);
				OutputStreamWriter osw = new OutputStreamWriter(fOut);  
				osw.write(message);
				osw.flush();
				osw.close();
					// Запись на sdcard средствами джавы, не работает с подключенным к компу телефоном
	            /*BufferedWriter fbw = new BufferedWriter(new FileWriter(full_path,true));
	            fbw.write(message);
	            fbw.newLine();
	            fbw.close();*/
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
