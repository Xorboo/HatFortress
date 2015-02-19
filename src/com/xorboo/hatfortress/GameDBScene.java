package com.xorboo.hatfortress;

import java.util.ArrayList;

import org.andengine.entity.Entity;
import org.andengine.entity.scene.CameraScene;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;

import android.view.MotionEvent;

import com.xorboo.hatfortress.MainState.GameState;
import com.xorboo.hatfortress.loaders.GfxAssets;
import com.xorboo.hatfortress.loaders.MfxAssets;
import com.xorboo.hatfortress.utils.database.GameDB;
import com.xorboo.hatfortress.utils.database.GameDBInfo;

/**
 * Сцена главного меню
 */
public class GameDBScene extends CameraScene {
	private static Sprite background;
	
	private Entity group = new Entity(0, 0);
	//public static int chosenID;
	public static int pID = -1;
	public static ArrayList<GameDBInfo> games;

	static float prevY = 0;
	
	public GameDBScene() {
		super(MainActivity.camera);
		
		createBackground();
		createButtons();	
		
		drawGames();
		
		setOnSceneTouchListener(new IOnSceneTouchListener() {
			
			@Override
			public boolean onSceneTouchEvent(Scene pScene, TouchEvent event) {
				if (group == null) {
					return true;
				}
				int myEventAction = event.getAction(); 

				float X = event.getX();
				float Y = event.getY();

				switch (myEventAction) {
					case MotionEvent.ACTION_DOWN:
						break;
					case MotionEvent.ACTION_MOVE:
						float newY = group.getY() + Y - prevY;
						group.setPosition(group.getX(), newY);
						break;
					case MotionEvent.ACTION_UP:
						break;
				}
				prevY = Y;
				return true;
			}
		});
		registerTouchArea(background);
	}
	
	// Включить сцену
	public void show() {
		setVisible(true);
		setIgnoreUpdate(false);
		
		MfxAssets.playMusic();
		drawGames();
	}
	// Выключить сцену
	public void hide() {
		setVisible(false);
		setIgnoreUpdate(true);
	}

	// Создание обоев
	private void createBackground()
	{
		background = 
				new Sprite(0, 0, GfxAssets.trMenuBackgroundWhite, MainActivity._main.getVertexBufferObjectManager());
		attachChild(background);
		registerTouchArea(background);
		//setBackground(background);
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
	private void drawGames() {
		detachChild(group);
		group = new Entity(0, 0);
		attachChild(group);
		
		final int dx = 20;
		final int dy = 80;
		final float step = 90;
		if (pID == -1) {
			games = MainActivity.dbWorker.getAllGames();
		}
		else {
			games = MainActivity.dbWorker.getGamesByPlayer(pID);
		}
		
		for (int i = games.size() - 1; i >= 0; i--) {
			GameDBInfo game = games.get(i);
			Text text = game.getText();
			text.setPosition(dx + 55, step * i + dy);
			group.attachChild(text);
			
			Sprite sprite = new Sprite(dx, step * i + dy, GfxAssets.trInfo, MainActivity._main.getVertexBufferObjectManager()) {
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
						float pTouchAreaLocalX, float pTouchAreaLocalY) {
					GameDBScene.this.chooseGame((int)((this.getY() - dy) / step));
					return true;
				}
			};
			group.attachChild(sprite);
			registerTouchArea(sprite);
			
			Sprite sprite2 = new Sprite(800 - dx - sprite.getWidth(), step * i + dy, GfxAssets.trDelete, MainActivity._main.getVertexBufferObjectManager()) {
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
						float pTouchAreaLocalX, float pTouchAreaLocalY) {
					if (pSceneTouchEvent.isActionDown()) {
						GameDBScene.this.delGame((int)((this.getY() - dy) / step));
					}
					return true;
				}
			};
			group.attachChild(sprite2);
			registerTouchArea(sprite2);
			
		}
	}

	// Выбор игрока
	private void chooseGame(int index) {
		if (index < 0 || index >= games.size()) {
			return;
		}
		GameDBInfo game = games.get(index);
		GamePlayerDBScene.gID = game._id;
		MainState.showScene(GameState.MENU_GPS);
	}
	

	// Выбор игрока
	private void delGame(int index) {
		if (index < 0 || index >= games.size()) {
			return;
		}
		GameDBInfo game = games.get(index);
		MainActivity.dbWorker.delGame(game._id);
		drawGames();
	}
}
