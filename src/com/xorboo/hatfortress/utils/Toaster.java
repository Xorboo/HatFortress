package com.xorboo.hatfortress.utils;

import android.widget.Toast;

import com.xorboo.hatfortress.MainActivity;

public class Toaster {
	public static void send(final String pMessage) {
		MainActivity._main.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(MainActivity._main, pMessage, Toast.LENGTH_SHORT).show();
			}
		});
	}
	public static void sendLong(final String pMessage) {
		MainActivity._main.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(MainActivity._main, pMessage, Toast.LENGTH_LONG).show();
			}
		});
	}
}
