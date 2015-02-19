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
import com.xorboo.hatfortress.utils.database.PlayerDB;

/**
 * Сцена главного меню
 */
public class MainMenuScene extends CameraScene {
	private static SpriteBackground background;
	
	public static MainMenuScene mScene;
	
	public MainMenuScene() {
		super(MainActivity.camera);
		
		createBackground();
		createButtons();		
		
		mScene = this;
	}
	
	// Включить сцену
	public void show() {
		setVisible(true);
		setIgnoreUpdate(false);

        Wifi.setWifiAPOFF(MainActivity.getContext());
        Wifi.setWifiOFF(MainActivity.getContext());
        		
		MfxAssets.playMusic();
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
				new Sprite(0, 0, GfxAssets.trMenuBackground, MainActivity._main.getVertexBufferObjectManager()));
		setBackground(background);
	}

	@SuppressWarnings("deprecation")
	private void showLoginDialog() {
		Runnable run = new Runnable() {
			
			@Override
			public void run() {
				MainActivity._main.showDialog(MainActivity.DIALOG_ENTER_LOGIN);
			}
		};
		
		MainActivity._main.runOnUiThread(run);
	}
	
	// Логин
	public void tryLogin(String login, String password) {
		PlayerDB player = MainActivity.dbWorker.getPlayer(login, password);
		if (password.compareTo("") == 0 || login.compareTo("") == 0 || player == null) {
			Logger.write("Приложение", "Ошибка: введен неправильный логин:пароль - " + login + ":" + password);
			Toaster.send("Введен неверный логин или пароль");
			MainState.showScene(GameState.MENU);
		}
		else {
			PlayersScene.chosenID = player._id;
			Logger.write("Приложение", "Выбран игрок " + player.name + " (id = " + player._id + ")");
			Toaster.send("Игрок " + login + " выбран");
			MainState.showScene(GameState.MENU_WIFI);
		}
	}
	
	// Создание кнопок
	private void createButtons() 
	{
		// Кнопка старта игры
		Sprite btnStartGame = new Sprite(30, 20,
				GfxAssets.trMenuButtonStartGame,
				MainActivity._main.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				//MainMenuScene.this.showLoginDialog();
				MainState.showScene(GameState.MENU_WIFI);
				return true;
			}
		};
		attachChild(btnStartGame);
		registerTouchArea(btnStartGame);

		// Кнопка игроков
		Sprite btnPlayers = new Sprite(350, 20, 
				GfxAssets.trMenuButtonPlayers,
				MainActivity._main.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				MainState.showScene(GameState.MENU_PLAYERS);
				return true;
			}
		};
		attachChild(btnPlayers);
		registerTouchArea(btnPlayers);

		// Кнопка аккаунта
		Sprite btnPlayer = new Sprite(350, 100, 
				GfxAssets.trMenuButtonPlayer,
				MainActivity._main.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				MainState.showScene(GameState.MENU_PLAYER);
				return true;
			}
		};
		attachChild(btnPlayer);
		registerTouchArea(btnPlayer);

		// Кнопка достижений
		Sprite btnAchievements = new Sprite(350, 180, 
				GfxAssets.trMenuButtonAch,
				MainActivity._main.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				AchievementsDBScene.pID = -1;
				MainState.showScene(GameState.MENU_ACHIEVEMENTS);
				return true;
			}
		};
		attachChild(btnAchievements);
		registerTouchArea(btnAchievements);

		// Кнопка игр
		Sprite btnGames = new Sprite(350, 260, 
				GfxAssets.trMenuButtonGames,
				MainActivity._main.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				GameDBScene.pID = -1;
				MainState.showScene(GameState.MENU_GAMES);
				return true;
			}
		};
		attachChild(btnGames);
		registerTouchArea(btnGames);

		// Кнопка выхода
		Sprite btnExit = new Sprite(30, MainActivity.camera.getHeight() - 130, GfxAssets.trMenuButtonPrev,
				MainActivity._main.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				MainActivity.closeGame();
				return true;
			}
		};
		attachChild(btnExit);
		registerTouchArea(btnExit);
	}
}
