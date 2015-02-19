package com.xorboo.hatfortress;

import java.util.ArrayList;
import java.util.Random;

import org.andengine.entity.Entity;
import org.andengine.entity.scene.CameraScene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.color.Color;

import com.xorboo.hatfortress.MainState.GameState;
import com.xorboo.hatfortress.loaders.GfxAssets;
import com.xorboo.hatfortress.loaders.MfxAssets;
import com.xorboo.hatfortress.utils.Logger;
import com.xorboo.hatfortress.utils.Toaster;
import com.xorboo.hatfortress.utils.database.PlayerDB;
import com.xorboo.hatfortress.utils.database.PlayerInfoDB;

/**
 * Сцена главного меню
 */
public class PlayerScene extends CameraScene {
	private static SpriteBackground background;
	
	private Entity group = new Entity(0, 0);
	public static PlayerScene mScene;
	public static PlayerInfoDB player;
	
	public PlayerScene() {
		super(MainActivity.camera);
		
		createBackground();
		createButtons();	
		
		drawPlayers();
		mScene = this;
	}
	
	// Включить сцену
	public void show() {
		setVisible(true);
		setIgnoreUpdate(false);
		
		MfxAssets.playMusic();
		drawPlayers();
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
				MainState.showScene(GameState.MENU);
				return true;
			}
		};
		attachChild(btnExit);
		registerTouchArea(btnExit);
	}
	
	// Отображаем игроков
	private void drawPlayers() {
		detachChild(group);
		
		if (PlayersScene.chosenID == -1) {
			return;
		}
		group = new Entity(0, 0);
		attachChild(group);
		
		player = MainActivity.dbWorker.getPlayerInfo(PlayersScene.chosenID);
		
		Text text = player.getText();
		text.setScaleCenter(0, 0);
		text.setScale(1.3f);
		text.setPosition(20, 30);
		group.attachChild(text);


		// Ачивки
		Sprite sprite = new Sprite(230, 380, GfxAssets.trGoals, MainActivity._main.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				PlayerScene.this.showAchievements();
				return true;
			}
		};
		sprite.setScale(1.5f);
		group.attachChild(sprite);
		registerTouchArea(sprite);

		// Выбор типа игрока
		Sprite type = new Sprite(360, 380, GfxAssets.trRocket, MainActivity._main.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				MainState.showScene(GameState.MENU_TYPE);
				return true;
			}
		};
		type.setScale(1.6f);
		group.attachChild(type);
		registerTouchArea(type);
		
		// Игры игрока
		Sprite spriteGames = new Sprite(500, 380, GfxAssets.trGame, MainActivity._main.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				GameDBScene.pID = PlayersScene.chosenID;
				MainState.showScene(GameState.MENU_GAMES);
				return true;
			}
		};
		spriteGames.setScale(1.8f);
		group.attachChild(spriteGames);
		registerTouchArea(spriteGames);		
		
		// Аватара
		Sprite avSprite = new Sprite(640, 380, GfxAssets.trAvatars.get(player.avID), 
				MainActivity._main.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				MainState.showScene(GameState.MENU_AVATAR);
				return true;
			}
		};
		avSprite.setScale(1.8f);
		group.attachChild(avSprite);
		registerTouchArea(avSprite);		
	}

	// Открытие окна достижений игрока
	private void showAchievements() {
		AchievementsDBScene.pID = PlayersScene.chosenID;
		MainState.showScene(GameState.MENU_ACHIEVEMENTS);
	}
}
