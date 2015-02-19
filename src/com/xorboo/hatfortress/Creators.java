package com.xorboo.hatfortress;

import java.util.ArrayList;

import org.andengine.entity.scene.CameraScene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.util.HorizontalAlign;

import com.xorboo.hatfortress.loaders.FontAssets;
import com.xorboo.hatfortress.loaders.GfxAssets;
import com.xorboo.hatfortress.loaders.MfxAssets;
class Creators extends CameraScene{
	private static SpriteBackground background;
	public Creators() {
		super(MainActivity.camera);
		
		createBackground();
		createText();
	}
	
	private void createText() {
		String txt = "Создатель игры: \n" +
					 "Корепанов К.Е. Korepanov.k@gmail.com\n" +
					 "Курсовой проект по базам данных" +
					 "Москва 2012";
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

	private void createBackground() {
		background = new SpriteBackground(
				new Sprite(0, 0, GfxAssets.trMenuBackgroundWhite, MainActivity._main.getVertexBufferObjectManager()));
		setBackground(background);
	}	
}
