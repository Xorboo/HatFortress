package com.xorboo.hatfortress;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.scene.CameraScene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;

import android.content.Context;
import android.util.Log;

import com.xorboo.hatfortress.MainState.GameState;
import com.xorboo.hatfortress.loaders.GfxAssets;
import com.xorboo.hatfortress.loaders.MfxAssets;
import com.xorboo.hatfortress.utils.Logger;
import com.xorboo.hatfortress.utils.Toaster;
import com.xorboo.hatfortress.utils.Wifi;

public class WaitScene extends CameraScene {
	private static SpriteBackground background;
	private static float WAIT_MAX = 40;
	private static float wait = 0;
	public static IUpdateHandler update;
	public WaitScene() {
		super(MainActivity.camera);
		
		createBackground();
		createButtons();
		createAnimation();
	}
	
	// Включить сцену
	public void show() {
		setVisible(true);
		setIgnoreUpdate(false);
		
		MfxAssets.playMusic();
		Wifi.createWifiAccessPoint(MainActivity.getContext());
		MainActivity.isWifiServer = true;
		Toaster.send("Wi-Fi точка создана");
		//Logger.write("WiFi", "Точка доступа создана");

		wait = 0;
		
		update = new IUpdateHandler() {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				wait += pSecondsElapsed;
				if (wait > WAIT_MAX) {
					Toaster.send("Не удалось подключиться к серверу!");
					Logger.write("Клиент", "Ошибка: превышено время ожидания подключения к серверу");
					wifiOff();
					MainState.showScene(GameState.MENU);
				}
				String ip;
				if ((ip = Wifi.getIP()) == "") {
					//Log.d("WIFI_MENU", "Wifi.getIP() failed, retry");
				} else {
					MainActivity._main.getEngine().unregisterUpdateHandler(this);
					Log.d("WIFI_MENU", "Wifi.getIP() success!, res: " + ip);
					GameScene.mScene.initClient();
					Toaster.send("Подключен клиент Wi-Fi: " + ip);
					Logger.write("WiFi", "Подключен клиент Wi-Fi: " + ip);
					MainState.showScene(GameState.GAME);
				}
			}

			@Override
			public void reset() {
				// TODO Auto-generated method stub				
			}
			
		};
		MainActivity._main.getEngine().registerUpdateHandler(update);
	}
	// Выключить сцену
	public void hide() {
		MainActivity._main.getEngine().unregisterUpdateHandler(update);	
		//wifiOff();
		setVisible(false);
		setIgnoreUpdate(true);
	}
	
	public static void wifiOff() {
		Context ctx = MainActivity.getContext();
		if (Wifi.isWifiAPOn(ctx)) {
			Wifi.setWifiAPOFF(ctx);
			//Logger.write("Wi-Fi", "Точка доступа отключена");
		}	
	}
	
	public void createAnimation(){
		final AnimatedSprite waitAnim = new AnimatedSprite(200, 200, GfxAssets.waitAnim, MainActivity._main.getVertexBufferObjectManager());
		waitAnim.animate(100);
		this.attachChild(waitAnim);
	}
	
	private void createButtons(){
		// Кнопка выхода
		Sprite btnPrev = new Sprite(30, MainActivity.camera.getHeight() - 130, GfxAssets.trMenuButtonPrev,
				MainActivity._main.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				MainActivity._main.getEngine().unregisterUpdateHandler(update);	
				wifiOff();
				MainState.showScene(GameState.MENU_WIFI);
				return true;
			}
		};
		attachChild(btnPrev);
		registerTouchArea(btnPrev);
	}
	
	// Создание обоев
	private void createBackground()
	{
		background = new SpriteBackground(
				new Sprite(0, 0, GfxAssets.trMenuBackground, MainActivity._main.getVertexBufferObjectManager()));
		setBackground(background);
	}
}

