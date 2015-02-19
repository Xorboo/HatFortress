package com.xorboo.hatfortress;

import java.util.ArrayList;

import org.andengine.entity.Entity;
import org.andengine.entity.scene.CameraScene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;

import com.xorboo.hatfortress.MainState.GameState;
import com.xorboo.hatfortress.loaders.GfxAssets;
import com.xorboo.hatfortress.loaders.MfxAssets;
import com.xorboo.hatfortress.utils.database.AchievementDB;

/**
 * Сцена ачивок
 */
public class AchievementsDBScene extends CameraScene {
	private static SpriteBackground background;
	
	private Entity group = new Entity(0, 0);
	//public static int chosenID;
	public static int pID = -1;
	public static ArrayList<AchievementDB> Achievements;
	
	public AchievementsDBScene() {
		super(MainActivity.camera);
		
		createBackground();
		createButtons();	
		
		drawAchievements();
	}
	
	// Включить сцену
	public void show() {
		setVisible(true);
		setIgnoreUpdate(false);
		
		MfxAssets.playMusic();
		drawAchievements();
	}
	// Выключить сцену
	public void hide() {
		setVisible(false);
		setIgnoreUpdate(true);
	}

	// Создание обоев
	private void createBackground()
	{
		background = new SpriteBackground(
				new Sprite(0, 0, GfxAssets.trMenuBackgroundWhite, MainActivity._main.getVertexBufferObjectManager()));
		setBackground(background);
	}
	
	// Создание кнопок
	private void createButtons() 
	{
		// Кнопка выхода
		Sprite btnExit = new Sprite(30, MainActivity.camera.getHeight() - 130, GfxAssets.trMenuButtonPrev,
				MainActivity._main.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				MainState.showScene(GameState.MENU_PLAYER);
				return true;
			}
		};
		attachChild(btnExit);
		registerTouchArea(btnExit);
	}
	
	// Отображаем игроков
	private void drawAchievements() {
		detachChild(group);
		group = new Entity(0, 0);
		attachChild(group);
		
		final int dx = 10;
		final int dy = 80;
		final float step = 90;
		if (pID == -1) {
			Achievements = MainActivity.dbWorker.getAllAchievements();
		}
		else {
			Achievements = MainActivity.dbWorker.getAchievementsByPlayer(pID);
		}
		
		for (int i = 0; i < Achievements.size(); i++) {
			AchievementDB Achievement = Achievements.get(i);
			Text text = Achievement.getText();
			text.setPosition(50, step * i + dy);
			group.attachChild(text);
			
			Sprite sprite = new Sprite(dx, step * i + dy, GfxAssets.trGround, MainActivity._main.getVertexBufferObjectManager());
		}
	}
}
