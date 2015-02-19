package com.xorboo.hatfortress;

import org.andengine.entity.scene.CameraScene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;

import com.xorboo.hatfortress.MainState.GameState;
import com.xorboo.hatfortress.loaders.GfxAssets;
import com.xorboo.hatfortress.loaders.MfxAssets;
import com.xorboo.hatfortress.utils.Logger;
import com.xorboo.hatfortress.utils.Toaster;
import com.xorboo.hatfortress.utils.Wifi;

public class WifiMenuScene extends CameraScene {
	private static SpriteBackground background;
	
	public WifiMenuScene() {
		super(MainActivity.camera);
		
		createBackground();
		createButtons();		
	}
	
	// �������� �����
	public void show() {
		setVisible(true);
		setIgnoreUpdate(false);

        Wifi.setWifiAPOFF(MainActivity.getContext());
        Wifi.setWifiOFF(MainActivity.getContext());
        
		MfxAssets.playMusic();
	}
	// ��������� �����
	public void hide() {
		setVisible(false);
		setIgnoreUpdate(true);
	}
	
	// �������� �����
	private void createBackground()
	{
		background = new SpriteBackground(
				new Sprite(0, 0, GfxAssets.trMenuBackground, MainActivity._main.getVertexBufferObjectManager()));
		setBackground(background);
	}
	
	// �������� ������
	private void createButtons() 
	{
		// ���� ������
		Sprite btnHostWifi = new Sprite(30, 20,
				GfxAssets.trMenuButtonWifiHost,
				MainActivity._main.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				MainState.showScene(GameState.WAIT);
				return true;
			}
		};
		attachChild(btnHostWifi);
		registerTouchArea(btnHostWifi);

		// ������ ������
		Sprite btnClientWifi = new Sprite(30, 110,
				GfxAssets.trMenuButtonWifiClient,
				MainActivity._main.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (Wifi.connect(MainActivity.getContext())) {
					Toaster.send("wifi ������ ������");
					//Logger.write("WifiMenu", "wifi ������ ������");
				}
				else {
					//Toaster.send("WifiMenu: wifi CLIENT failed");
				}
				MainActivity.isWifiServer = false;
				GameScene.mScene.initServer();
				GameScene.mScene.initClient();
				//Toaster.send("WifiMenu: ������ ������");
				//Logger.write("WifiMenu", "������ ������");
				MainState.showScene(GameState.GAME);
				return true;
			}
		};
		attachChild(btnClientWifi);
		registerTouchArea(btnClientWifi);
		
		
		// ������ ������
		Sprite btnPrev = new Sprite(30, MainActivity.camera.getHeight() - 130, GfxAssets.trMenuButtonPrev,
				MainActivity._main.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				GameScene.mScene.finish();
				MainState.showScene(GameState.MENU);
				return true;
			}
		};
		attachChild(btnPrev);
		registerTouchArea(btnPrev);
	}

}
