package com.xorboo.hatfortress;

import java.util.ArrayList;

import org.andengine.entity.Entity;
import org.andengine.entity.scene.CameraScene;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;

import android.view.MotionEvent;

import com.xorboo.hatfortress.MainState.GameState;
import com.xorboo.hatfortress.loaders.GfxAssets;
import com.xorboo.hatfortress.loaders.MfxAssets;
import com.xorboo.hatfortress.utils.Logger;
import com.xorboo.hatfortress.utils.Toaster;
import com.xorboo.hatfortress.utils.database.PlayerDB;

/**
 * ����� �������� ����
 */
public class PlayersScene extends CameraScene {
	private static Sprite background;
	
	private Entity group = new Entity(0, 0);
	public static int chosenID = -1;
	public static int deleteIndex = -1;
	public static int clickIndex = -1;
	public static PlayersScene mScene;
	public static ArrayList<PlayerDB> players;

	static float prevY = 0;
	
	public PlayersScene() {
		super(MainActivity.camera);
		
		createBackground();
		createButtons();	
		
		ArrayList<PlayerDB> players = MainActivity.dbWorker.getAllPlayers();

		if (players.size() != 0) {
			for (int i = 0; i < players.size(); i++) {
				if (players.get(i).password.compareTo("") != 0) {
					chosenID = players.get(i)._id;
					break;
				}
			}
		}
		if (chosenID == -1) {
			MainActivity.dbWorker.addPlayer("Summoner", "1");
			chosenID = MainActivity.dbWorker.getPlayer("Summoner", "1")._id;
		}
		
		drawPlayers();
		mScene = this;
		
		setOnSceneTouchListener(new IOnSceneTouchListener() {			
			@Override
			public boolean onSceneTouchEvent(Scene pScene, TouchEvent event) {
				if (group == null) {
					return true;
				}
				int myEventAction = event.getAction(); 
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
	
	// �������� �����
	public void show() {
		setVisible(true);
		setIgnoreUpdate(false);
		
		MfxAssets.playMusic();
		drawPlayers();
	}
	// ��������� �����
	public void hide() {
		setVisible(false);
		setIgnoreUpdate(true);
	}

	// �������� �����
	private void createBackground()
	{
		background = 
				new Sprite(0, 0, GfxAssets.trMenuBackgroundWhite, MainActivity._main.getVertexBufferObjectManager());
		attachChild(background);
		registerTouchArea(background);
	}
	
	// �������� ������
	private void createButtons() 
	{
		// �������� ������
		Sprite btnAdd = new Sprite(30, 20,
				GfxAssets.trMenuButtonAdd,
				MainActivity._main.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				Runnable run = new Runnable() {
					
					@SuppressWarnings("deprecation")
					@Override
					public void run() {
						MainActivity._main.showDialog(MainActivity.DIALOG_ENTER_CREATE_LOGIN);
					}
				};
				
				MainActivity._main.runOnUiThread(run);
				return true;
			}
		};
		attachChild(btnAdd);
		registerTouchArea(btnAdd);

		// ������ ������
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
	
	// ���������� �������
	private void drawPlayers() {
		detachChild(group);
		group = new Entity(0, 0);
		attachChild(group);
		
		final int dx = 20;
		final int dy = 80;
		final float step = 100;
		players = MainActivity.dbWorker.getAllPlayers();
		
		for (int i = 0; i < players.size(); i++) {	
			PlayerDB player = players.get(i);
			
			Sprite sprite;
			if (player.password.compareTo("") != 0) {
				sprite = new Sprite(dx, step * i + dy, chosenID == player._id ? GfxAssets.trMarker : GfxAssets.trInfo, 
					MainActivity._main.getVertexBufferObjectManager()) {
					@Override
					public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
							float pTouchAreaLocalX, float pTouchAreaLocalY) {
						PlayersScene.this.choosePlayer((int)((this.getY() - dy) / step));
						return true;
					}
				};
				sprite.setScaleCenter(0, 0);
				sprite.setScale(1.7f);
				group.attachChild(sprite);
				registerTouchArea(sprite);
			}

			sprite = new Sprite(400, step * i + dy, GfxAssets.trAvatars.get(player.avID), 
					MainActivity._main.getVertexBufferObjectManager());
			sprite.setScaleCenter(0, 0);
			sprite.setScale(1.4f);
			group.attachChild(sprite);
			
			if (player.password.compareTo("") != 0) {
				sprite = new Sprite(800 - dx - sprite.getWidth(), step * i + dy, GfxAssets.trDelete, MainActivity._main.getVertexBufferObjectManager()) {
					@Override
					public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
							float pTouchAreaLocalX, float pTouchAreaLocalY) {
						if (pSceneTouchEvent.isActionDown()) {
							PlayersScene.this.delPlayer((int)((this.getY() - dy) / step));
						}
						return true;
					}
				};
				group.attachChild(sprite);
				registerTouchArea(sprite);
			}

			sprite = new Sprite(800 - 2 * (dx + 10 + sprite.getWidth()), step * i + dy, GfxAssets.trGame, MainActivity._main.getVertexBufferObjectManager()) {
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
						float pTouchAreaLocalX, float pTouchAreaLocalY) {
					PlayersScene.this.getPlayerGames((int)((this.getY() - dy) / step));
					return true;
				}
			};
			sprite.setScale(1.4f);
			group.attachChild(sprite);
			registerTouchArea(sprite);			

			Text text = player.getText();
			text.setPosition(sprite.getWidthScaled() + dx + 10, step * i + dy + 3);
			group.attachChild(text);
		}
	}

	// ����� ������
	private void choosePlayer(int index) {
		players = MainActivity.dbWorker.getAllPlayers();
		if (index < 0 || index >= players.size()) {
			return;
		}

		PlayerDB player = players.get(index);
		if (player._id == chosenID) {
			return;
		}
		if (player.password.compareTo("") == 0) {
			Toaster.send("������ ������� ������ ������");
			return;
		}
		clickIndex = index;
		MainActivity.nextWindow = GameState.MENU_PLAYERS;
		
		Runnable run = new Runnable() {		
			@Override
			public void run() {
				MainActivity._main.showDialog(MainActivity.DIALOG_ENTER_PASSWORD);
			}
		};
		
		MainActivity._main.runOnUiThread(run);
	}
	
	public void playerSelect(String password) {
		players = MainActivity.dbWorker.getAllPlayers();
		PlayerDB player = players.get(clickIndex);
		if (password.equals(player.password)) {
			Toaster.send("����� \"" + player.name + "\" ������");
			chosenID = player._id;
		}
		else {
			Toaster.send("�������� ������");
		}
		drawPlayers();
	}

	// ���� ������
	private void getPlayerGames(int index) {
		players = MainActivity.dbWorker.getAllPlayers();
		if (index < 0 || index >= players.size()) {
			return;
		}
		PlayerDB player = players.get(index);
		GameDBScene.pID = player._id;
		MainState.showScene(GameState.MENU_GAMES);
	}

	// �������� ������
	private void delPlayer(int index) {
		ArrayList<PlayerDB> players = MainActivity.dbWorker.getAllPlayers();
		if (index < 0 || index >= players.size()) {
			return;
		}
		
		PlayersScene.deleteIndex = index;
		Runnable run = new Runnable() {			
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				MainActivity._main.showDialog(MainActivity.DIALOG_ENTER_REMOVE_PASSWORD);
			}
		};
		
		MainActivity._main.runOnUiThread(run);
		drawPlayers();
	}

	// �������� ������ ����� ����� ������
	public void deletePlayer(String password) {
		PlayerDB player = players.get(PlayersScene.deleteIndex);
		if (player == null) {
			return;
		}
		if (player.password.compareTo(password) == 0) {
			MainActivity.dbWorker.delPlayer(player._id);
			Toaster.send("����� \"" + player.name + "\" ������");
		}
		else {
			Toaster.send("������: ������������ ������");
		}
		drawPlayers();
	}
	
	// �������� ������
	public void addPlayer(String login, String password) {
		
		if (MainActivity.dbWorker.addPlayer(login, password)) {
			Logger.write("����������", "������������ ������: \"" + login + "\", ������: \"" + password + "\"");
			Toaster.send("����� ������");
		}
		else {
			Toaster.send("������: �������� ����� ��� ������");
			Logger.write("����������", "������: ������� ������� ������������� ������������ \"" + login + "\", ������: \"" + password + "\"");
		}
		drawPlayers();
	}
}
