package com.xorboo.hatfortress;

import java.util.ArrayList;

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
import com.xorboo.hatfortress.utils.database.GamePlayerDB;

public class GamePlayerDBScene extends CameraScene {
	private static SpriteBackground background;
	
	private Entity group = new Entity(0, 0);
	public static int gID = -1;
	public static ArrayList<GamePlayerDB> gamePlayers;
	
	public GamePlayerDBScene() {
		super(MainActivity.camera);
		
		createBackground();
		createButtons();	
		
		gamePlayers = MainActivity.dbWorker.getAllGP();

		/*if (gamePlayers.size() != 0) {
			chosenID = gamePlayers.get(0)._id;
		}*/
		
		drawGames();
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
	private void drawGames() {
		detachChild(group);
		group = new Entity(0, 0);
		attachChild(group);
		
		final int dx = 20;
		final int dy = 25;
		final float step = 100;
		if (gID == -1) {
			gamePlayers = MainActivity.dbWorker.getAllGP();
		}
		else {
			gamePlayers = MainActivity.dbWorker.getGPsByGID(gID);			
		}
		
		for (int i = 0; i < gamePlayers.size(); i++) {
			GamePlayerDB game = gamePlayers.get(i);
			Text text = game.getText();
			text.setPosition(dx + 60, step * i + dy);
			group.attachChild(text);
			
			// Победитель
			if (game.winner == 1) {
				Sprite sprite = new Sprite(dx, step * i + dy, GfxAssets.trGoals, MainActivity._main.getVertexBufferObjectManager());
				group.attachChild(sprite);
			}
		}
	}
}
