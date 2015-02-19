package com.xorboo.hatfortress;

import java.util.ArrayList;

import org.andengine.entity.scene.CameraScene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.HorizontalAlign;

import com.xorboo.hatfortress.MainState.GameState;
import com.xorboo.hatfortress.loaders.FontAssets;
import com.xorboo.hatfortress.loaders.GfxAssets;
import com.xorboo.hatfortress.loaders.MfxAssets;

public class HelpScene2 extends CameraScene{
	private static SpriteBackground background;
	public HelpScene2() {
		super(MainActivity.camera);
		
		createBackground();
		createButtons();	
		createText();
	}
	
	private void createText() {
		String txt = "При входе в игру есть 4 кнопки. Кнопка начала игры\n" +
					 "позволяет начать игру как сервер, либо как клиент \n" +
					 "по выбору игрока. Кнопки Игроки, Игры, Игры по игрокам\n" +
					 "показывают статистику по игрокам и проведенным ими\n"+
					 "играм.";
		Text labelText = new Text(50, 30, FontAssets.droidFont, txt, new TextOptions(HorizontalAlign.LEFT), 
				MainActivity._main.getVertexBufferObjectManager());
		attachChild(labelText);
	}
	
	// Включить сцену
	public void show() {
		setVisible(true);
		setIgnoreUpdate(false);
		
		MfxAssets.playMusic();
	}
	// Выключить сцену
	public void hide() {
		setVisible(false);
		setIgnoreUpdate(true);
	}

	
	private void createButtons() {

		// Кнопка выхода
		Sprite btnExit = new Sprite(30, MainActivity.camera.getHeight() - 150, GfxAssets.trMenuButtonPrev,
				MainActivity._main.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				MainState.showScene(GameState.HELP1);
				return true;
			}
		};
		attachChild(btnExit);
		registerTouchArea(btnExit);
		


		// Кнопка выхода
		Sprite btnNext = new Sprite(MainActivity.camera.getWidth() - 150, MainActivity.camera.getHeight() - 150, GfxAssets.trMenuButtonNext,
				MainActivity._main.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				MainState.showScene(GameState.HELP3);
				return true;
			}
		};
		attachChild(btnNext);
		registerTouchArea(btnNext);
				
	}
	private void createBackground() {
		background = new SpriteBackground(
				new Sprite(0, 0, GfxAssets.trMenuBackgroundWhite, MainActivity._main.getVertexBufferObjectManager()));
		setBackground(background);
	}	
}
