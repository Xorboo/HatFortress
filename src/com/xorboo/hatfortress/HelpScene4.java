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

public class HelpScene4 extends CameraScene{
	private static SpriteBackground background;
	public HelpScene4() {
		super(MainActivity.camera);
		
		createBackground();
		createButtons();	
		createText();
	}
	
	private void createText() {
		String txt = "Картинка игрока-сервера:";
		String txt2 = "Картинка игрока-клиента:";
		Text labelText = new Text(50, 30, FontAssets.droidFont, txt, new TextOptions(HorizontalAlign.LEFT), 
				MainActivity._main.getVertexBufferObjectManager());
		attachChild(labelText);
		Sprite me = new Sprite(380, 30,
				GfxAssets.trPlayer,
				MainActivity._main.getVertexBufferObjectManager());
		me.setScaleCenter(0, me.getHeight());
		me.setScale(1.3f);
		me.setVisible(true);
		attachChild(me);

		Text labelText2 = new Text(50, 130, FontAssets.droidFont, txt2, new TextOptions(HorizontalAlign.LEFT), 
				MainActivity._main.getVertexBufferObjectManager());
		attachChild(labelText2);
		Sprite enemy = new Sprite(380, 130,
				GfxAssets.trEnemy,
				MainActivity._main.getVertexBufferObjectManager());
		enemy.setScaleCenter(0, enemy.getHeight());
		enemy.setScale(1.3f);
		enemy.setVisible(true);
		attachChild(enemy);
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
				MainState.showScene(GameState.HELP3);
				return true;
			}
		};
		attachChild(btnExit);
		registerTouchArea(btnExit);
		
	}
	private void createBackground() {
		background = new SpriteBackground(
				new Sprite(0, 0, GfxAssets.trMenuBackgroundWhite, MainActivity._main.getVertexBufferObjectManager()));
		setBackground(background);
	}	
}
