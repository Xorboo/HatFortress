package com.xorboo.hatfortress;

import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;

import android.view.KeyEvent;

import com.xorboo.hatfortress.utils.Toaster;

/**
 * Главная сцена
 */
public class MainState extends Scene {
	
	// Подгружаемые сцены
	public static MainMenuScene mainMenuScene = new MainMenuScene();
	public static PlayersScene playersScene = new PlayersScene();
	public static PlayerScene playerScene = new PlayerScene();
	public static GameDBScene gameDBScene = new GameDBScene();
	public static AchievementsDBScene achievementsDBScene = new AchievementsDBScene();
	public static TypeDBScene typeDBScene = new TypeDBScene();
	public static AvatarDBScene avatarDBScene = new AvatarDBScene();
	public static GamePlayerDBScene gpScene = new GamePlayerDBScene();
	public static GameScene gameScene = new GameScene();
	public static WifiMenuScene wifiMenuScene = new WifiMenuScene();
	public static WaitScene waitScene = new WaitScene();
	public static HelpScene1 helpScene1 = new HelpScene1();
	public static HelpScene2 helpScene2 = new HelpScene2();
	public static HelpScene3 helpScene3 = new HelpScene3();
	public static HelpScene4 helpScene4 = new HelpScene4();
	public static Creators creatorsScene = new Creators();
	
	// Состояние игры
	public static enum GameState {
		UNKNOWN,
        MENU,
        MENU_WIFI,
        MENU_PLAYERS,
        MENU_PLAYER,
        MENU_GAMES,
        MENU_ACHIEVEMENTS,
        MENU_TYPE,
        MENU_AVATAR,
        MENU_GPS,
        OPTIONS,
        GAME,
        WAIT,
        HELP1,
        HELP2,
        HELP3,
        HELP4,
        CREATORS
	}   
    public static GameState currentState = GameState.UNKNOWN;
	
	// Конструктор
	public MainState() {
		attachChild(mainMenuScene);
		attachChild(playersScene);
		attachChild(playerScene);
		attachChild(gameDBScene);
		attachChild(achievementsDBScene);
		attachChild(typeDBScene);
		attachChild(avatarDBScene);
		attachChild(gpScene);
		attachChild(wifiMenuScene);
		attachChild(waitScene);
		attachChild(gameScene);
		attachChild(helpScene1);
		attachChild(helpScene2);
		attachChild(helpScene3);
		attachChild(helpScene4);
		attachChild(creatorsScene);
		
		hideAllScenes();
		
		showScene(GameState.MENU);
	}
	
	// Рассылка тача
	public boolean onSceneTouchEvent(TouchEvent pSceneTouchEvent) {
		if (!pSceneTouchEvent.isActionDown() && currentState != GameState.GAME
				&& currentState != GameState.MENU_GAMES && currentState != GameState.MENU_PLAYERS) {
			return true;//super.onSceneTouchEvent(pSceneTouchEvent); 
		}
		switch (currentState) {
		case MENU:
			mainMenuScene.onSceneTouchEvent(pSceneTouchEvent);
			break;
		case MENU_WIFI:
			wifiMenuScene.onSceneTouchEvent(pSceneTouchEvent);
			break;
		case WAIT:
			waitScene.onSceneTouchEvent(pSceneTouchEvent);
			break;
		case MENU_PLAYERS:
			playersScene.onSceneTouchEvent(pSceneTouchEvent);
			break;
		case MENU_PLAYER:
			playerScene.onSceneTouchEvent(pSceneTouchEvent);
			break;
		case MENU_GAMES:
			gameDBScene.onSceneTouchEvent(pSceneTouchEvent);
			break;
		case MENU_ACHIEVEMENTS:
			achievementsDBScene.onSceneTouchEvent(pSceneTouchEvent);
			break;
		case MENU_TYPE:
			typeDBScene.onSceneTouchEvent(pSceneTouchEvent);
			break;
		case MENU_AVATAR:
			avatarDBScene.onSceneTouchEvent(pSceneTouchEvent);
			break;
		case MENU_GPS:
			gpScene.onSceneTouchEvent(pSceneTouchEvent);
			break;
		case GAME:
			gameScene.onSceneTouchEvent(pSceneTouchEvent);
			break;
		case HELP1:
			helpScene1.onSceneTouchEvent(pSceneTouchEvent);
			break;
		case HELP2:
			helpScene2.onSceneTouchEvent(pSceneTouchEvent);
			break;
		case HELP3:
			helpScene3.onSceneTouchEvent(pSceneTouchEvent);
			break;
		case HELP4:
			helpScene4.onSceneTouchEvent(pSceneTouchEvent);
			break;
		case CREATORS:
			creatorsScene.onSceneTouchEvent(pSceneTouchEvent);
			break;
		default:
			Toaster.send("MainState: unknown onSceneTouchEvent state");
			break;
		}
		return super.onSceneTouchEvent(pSceneTouchEvent);
	}

	// Нажатие "back"
	@SuppressWarnings("deprecation")
	public void keyPressed(int keyCode, KeyEvent event) {
		switch (currentState) {
		case MENU:
			MainActivity._main.showDialog(MainActivity.DIALOG_QUIT);
			break;
		case WAIT:
			MainActivity._main.getEngine().unregisterUpdateHandler(WaitScene.update);	
			waitScene.wifiOff();
			showScene(GameState.MENU_WIFI);
			break;
		case MENU_WIFI:
		case MENU_PLAYERS:
		case MENU_PLAYER:
		case MENU_GPS:
		case GAME:
		case HELP1:
		case HELP2:
		case HELP3:
		case HELP4:
		case CREATORS:
			showScene(GameState.MENU);
			break;
		case MENU_ACHIEVEMENTS:
		case MENU_GAMES:
			showScene(GameState.MENU_PLAYER);
			break;
		case MENU_TYPE:
		case MENU_AVATAR:
			showScene(GameState.MENU_PLAYER);
			break;
		default:
			Toaster.send("MainState: unknown keyPressed back state");
			break;
		}
	}

	// Отображение сцены
	public static void showScene(GameState state) {
		hideAllScenes();
		currentState = state;
		switch (state) {
		case MENU:
			mainMenuScene.show();
			break;
		case MENU_WIFI:
			wifiMenuScene.show();
			break;
		case MENU_PLAYERS:
			playersScene.show();
			break;
		case MENU_PLAYER:
			playerScene.show();
			break;
		case MENU_GAMES:
			gameDBScene.show();
			break;
		case MENU_ACHIEVEMENTS:
			achievementsDBScene.show();
			break;
		case MENU_TYPE:
			typeDBScene.show();
			break;
		case MENU_AVATAR:
			avatarDBScene.show();
			break;
		case MENU_GPS:
			gpScene.show();
			break;
		case GAME:
			gameScene.show();
			break;
		case WAIT:
			waitScene.show();
			break;
		case HELP1:
			helpScene1.show();
			break;
		case HELP2:
			helpScene2.show();
			break;
		case HELP3:
			helpScene3.show();
			break;
		case HELP4:
			helpScene4.show();
			break;
		case CREATORS:
			creatorsScene.show();
			break;
		default:
			Toaster.send("MainState: unknown showScene state");
			currentState = GameState.MENU;
			mainMenuScene.show();
			break;
		}
	}
	
	// Спрятать все сцены
	private static void hideAllScenes() {
		mainMenuScene.hide();
		gameScene.hide();
		waitScene.hide();
		wifiMenuScene.hide();
		playersScene.hide();
		playerScene.hide();
		gameDBScene.hide();
		achievementsDBScene.hide();
		typeDBScene.hide();
		avatarDBScene.hide();
		gpScene.hide();
		helpScene1.hide();
		helpScene2.hide();
		helpScene3.hide();
		helpScene4.hide();
		creatorsScene.hide();
	}
}
