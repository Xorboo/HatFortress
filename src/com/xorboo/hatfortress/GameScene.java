package com.xorboo.hatfortress;

import java.io.IOException;
import java.net.Socket;

import org.andengine.engine.Engine.EngineLock;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl.IOnScreenControlListener;
import org.andengine.engine.camera.hud.controls.DigitalOnScreenControl;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.extension.multiplayer.protocol.adt.message.server.IServerMessage;
import org.andengine.extension.multiplayer.protocol.client.IServerMessageHandler;
import org.andengine.extension.multiplayer.protocol.client.connector.ServerConnector;
import org.andengine.extension.multiplayer.protocol.client.connector.SocketConnectionServerConnector.ISocketConnectionServerConnectorListener;
import org.andengine.extension.multiplayer.protocol.server.connector.ClientConnector;
import org.andengine.extension.multiplayer.protocol.server.connector.SocketConnectionClientConnector.ISocketConnectionClientConnectorListener;
import org.andengine.extension.multiplayer.protocol.shared.SocketConnection;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.debug.Debug;

import android.opengl.GLES20;
import android.util.Log;
import android.util.SparseArray;

import com.xorboo.hatfortress.MainState.GameState;
import com.xorboo.hatfortress.game.BulletObject;
import com.xorboo.hatfortress.game.MapObject;
import com.xorboo.hatfortress.game.PlayerObject;
import com.xorboo.hatfortress.loaders.FontAssets;
import com.xorboo.hatfortress.loaders.GfxAssets;
import com.xorboo.hatfortress.loaders.MfxAssets;
import com.xorboo.hatfortress.messages.client.DBPlayerClientMessage;
import com.xorboo.hatfortress.messages.client.MovePlayerClientMessage;
import com.xorboo.hatfortress.messages.client.ShotPlayerClientMessage;
import com.xorboo.hatfortress.messages.client.StringClientMessage;
import com.xorboo.hatfortress.messages.client.WeaponPlayerClientMessage;
import com.xorboo.hatfortress.messages.client.connection.ConnectionCloseClientMessage;
import com.xorboo.hatfortress.messages.server.BulletServerMessage;
import com.xorboo.hatfortress.messages.server.BulletStatusServerMessage;
import com.xorboo.hatfortress.messages.server.DBPlayerServerMessage;
import com.xorboo.hatfortress.messages.server.DBStatisticsServerMessage;
import com.xorboo.hatfortress.messages.server.PlayerDeadServerMessage;
import com.xorboo.hatfortress.messages.server.PlayerParametersServerMessage;
import com.xorboo.hatfortress.messages.server.PlayerPositionServerMessage;
import com.xorboo.hatfortress.messages.server.PlayerQuitServerMessage;
import com.xorboo.hatfortress.messages.server.SetPlayerIDServerMessage;
import com.xorboo.hatfortress.messages.server.StringServerMessage;
import com.xorboo.hatfortress.messages.server.connection.ConnectionCloseServerMessage;
import com.xorboo.hatfortress.messages.server.connection.ConnectionEstablishedServerMessage;
import com.xorboo.hatfortress.messages.server.connection.ConnectionPingServerMessage;
import com.xorboo.hatfortress.messages.server.connection.ConnectionRejectedProtocolMissmatchServerMessage;
import com.xorboo.hatfortress.messages.server.connection.ServerMessageFlags;
import com.xorboo.hatfortress.utils.GameConstants;
import com.xorboo.hatfortress.utils.Logger;
import com.xorboo.hatfortress.utils.Toaster;
import com.xorboo.hatfortress.utils.Utils;
import com.xorboo.hatfortress.utils.Wifi;
import com.xorboo.hatfortress.utils.database.AchievementDB;
import com.xorboo.hatfortress.utils.database.GameDB;
import com.xorboo.hatfortress.utils.database.GamePlayerDB;
import com.xorboo.hatfortress.utils.database.MapDB;
import com.xorboo.hatfortress.utils.database.PlayerDB;

public class GameScene extends Scene/*CameraScene*/ implements IUpdateHandler, GameConstants {
	
	private final static String TAG = "Клиент";
	
	private GameServer mServer;
	public GameServerConnector mServerConnector;
	
	DigitalOnScreenControl mDigitalOnScreenControl;
	
	public static GameScene mScene;
	private HUD hud;
	private Text labelHP = null;
	private Text labelLives = null;
	private Text labelEnemyLives = null;
	private static boolean gameIsEnded = false;
	private SparseArray<PlayerObject> players = new SparseArray<PlayerObject>();
	public int playerID = -1;
	private PlayerDB playerDbInfo = MainActivity.dbWorker.getPlayer(PlayersScene.chosenID);

	private SparseArray<BulletObject> bullets = new SparseArray<BulletObject>();
	
	boolean gameStarted = false;

	float nextX = 0, nextY = 0;
	
	// Прошлые положения джойстиков
	float dirX = 0, dirY = 0, moveX = 0, moveY = 0;
	
	// Карта
	private MapObject map = null;
	
	// Кнопки оружия
	private Sprite btnGrenade;
	private Sprite btnRpg;
	private Sprite btnSmg;
	private Sprite btnShotgun;

	private Sprite btnGrenadeSelected;
	private Sprite btnRpgSelected;
	private Sprite btnSmgSelected;
	private Sprite btnShotgunSelected;
	
	private Sprite selected;
	private Sprite alterSelected;
	
	public GameScene() {
		super();
		setBackgroundEnabled(false);
		initHUD();
		mScene = this;	
		
		IUpdateHandler update = new IUpdateHandler() {

			@Override
			public void onUpdate(float pSecondsElapsed) {
				// TODO Auto-generated method stub
				if (!gameStarted) {
					return;
				}
				PlayerObject player = players.get(playerID);
				//float smooth = 2;
				//player.setX(player.getX() + (nextX - player.getX()) / smooth);
				//player.setY(player.getY() + (nextY - player.getY()) / smooth);
				player.setPosition(nextX, nextY);
				MainActivity.camera.setCenter(player.getX(), player.getY());
				
				// Вращаем гранаты
				/*for (int i = 0; i < bullets.size(); i++) {
					BulletObject bullet = bullets.valueAt(i);
					if (bullet == null) {
						Log.e(TAG, "ERROR: update - null bullet, index: " + i);
						continue;
					}
					if (bullet.weaponID == WEAPON_GRENADE) {
						bullet.setRotationCenter(bullet.getX(), bullet.getY());
						bullet.setRotation(bullet.getRotation() + 4);
					}
				}*/
			}

			@Override
			public void reset() {
				// TODO Auto-generated method stub				
			}
			
		};
		MainActivity._main.getEngine().registerUpdateHandler(update);
	}

	// Включить сцену
	public void show() {
		gameIsEnded = false;
		for (int i = 0; i < players.size(); i++) {
			int key = players.keyAt(i);
			removePlayer(key);
		}
		for (int i = 0; i < bullets.size(); i++) {
			int key = bullets.keyAt(i);
			deleteBullet(key);
		}
		
		MfxAssets.stopMusic();
		setVisible(true);
		setIgnoreUpdate(false);

		MainActivity.camera.setHUD(hud);
		hud.setVisible(true);
		hud.setIgnoreUpdate(false);
	}

	// Выключить сцену
	public void hide() {
		for (int i = 0; i < players.size(); i++) {
			int key = players.keyAt(i);
			removePlayer(key);
		}
		
		if (isVisible() && !isIgnoreUpdate()) {
			setVisible(false);
			setIgnoreUpdate(true);
			
			MainActivity.camera.setHUD(null);
			hud.setVisible(false);
			hud.setIgnoreUpdate(true);
			
			finish();
		}
	}
		
	// Удаление пули
	private void deleteBullet(int key) {
		BulletObject bullet = bullets.get(key);
		
		if (bullet == null) {
			Log.e(TAG, "ERROR: msgBullet Delete-  null, id: " + key);
			return;
		}
		final EngineLock engineLock = MainActivity._main.getEngine().getEngineLock();
		engineLock.lock();
		/* Now it is save to remove the entity! */
		detachChild(bullet);
		bullet.dispose();
		bullet = null;	
		engineLock.unlock();
		
		bullets.remove(key);
	}
	// Отправка команды серверу
	public void sendCommand(String command) {
		StringClientMessage message = new StringClientMessage(command);
		try {
			GameScene.this.mServerConnector
					.sendClientMessage(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Создать карту
	public void initMap(int id) {
		if (map != null) {
			final EngineLock engineLock = MainActivity._main.getEngine().getEngineLock();
			engineLock.lock();
			detachChild(map.groundBatch);
			map = null;
			engineLock.unlock();
		}
		
		MapDB mapDB = MainActivity.dbWorker.getMap(id);
		if (mapDB == null) {
			Logger.write("Клиент", "Ошибка: Получен несуществующий id карты от сервера: " + id);
			Toaster.send("Карта не найдена, отключение...");
			finish();
			return;
		}
		map = new MapObject(mapDB);
		map.attachMapToScene(this);
	}
	
	// Посылаем инфу из БД
	public void sendDbInfo() {
		playerDbInfo = MainActivity.dbWorker.getPlayer(PlayersScene.chosenID);
		
		PlayerDB pDB = MainActivity.dbWorker.getPlayer(PlayersScene.chosenID);
		pDB.password = "";
		DBPlayerClientMessage message = new DBPlayerClientMessage(pDB, playerID);
		try {
			GameScene.this.mServerConnector
					.sendClientMessage(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// Добавить игрока
	private void addPlayer(int key)
	{
		if (players.get(key) != null) {
			Log.e(TAG, "Ошибка: addPlayer, игрок с таким key уже существует, key: " + key);
			return;
		}
		PlayerObject player;
		if (MainActivity.isWifiServer){
			if (key == playerID)
				player = new PlayerObject(-1, -1, GfxAssets.trPlayer);
			else
				player = new PlayerObject(-1, -1, GfxAssets.trEnemy);
		} else {
			if (key == playerID)
				player = new PlayerObject(-1, -1, GfxAssets.trEnemy);
			else
				player = new PlayerObject(-1, -1, GfxAssets.trPlayer);
		}
		players.append(key, player);
		attachChild(player);
	}
	
	// Удалить игрока
	private void removePlayer(int id)
	{
		if (id == playerID) {
			Log.e(TAG, "Trying to remove us? key: " + id);
			//Toaster.send("Trying to remove us! WTF?! id: " + id);
			return;
		}
		
		PlayerObject deletePlayer = players.get(id);
		if (deletePlayer == null) {
			Log.w(TAG, "Удаление null игрока, id: " + id);
			return;
		}
		final EngineLock engineLock = MainActivity._main.getEngine().getEngineLock();
		engineLock.lock();

		/* Now it is save to remove the entity! */
		detachChild(deletePlayer);
		deletePlayer.dispose();
		deletePlayer = null;

		engineLock.unlock();
		players.remove(id);
		if (playerID != id) {
			Toaster.send("Игрок отключен: " + id);
		}
	}
	
	// Создаем HUD
	private void initHUD() {
		hud = new HUD();
		initJoysticks(hud);
		initWeapons(hud);
		hud.setVisible(false);
		MainActivity.camera.setHUD(hud);
		hud.setTouchAreaBindingOnActionDownEnabled(true);
		hud.setTouchAreaBindingOnActionMoveEnabled(true);
		
		initHP();

		labelLives = new Text(70, 30, FontAssets.droidFont, "x " + LIVES, new TextOptions(HorizontalAlign.LEFT), 
				MainActivity._main.getVertexBufferObjectManager());
		labelEnemyLives = new Text(CAMERA_WIDTH - 50, 30, FontAssets.droidFont, "x " + LIVES, new TextOptions(HorizontalAlign.LEFT), 
				MainActivity._main.getVertexBufferObjectManager());
		hud.attachChild(labelLives);
		hud.attachChild(labelEnemyLives);
	}
	
	// Создание ХП
	private void initHP() {
		int x = 20;
		int y = 130;
		Sprite cross = new Sprite(x, y,
				GfxAssets.trHudCross,
				MainActivity._main.getVertexBufferObjectManager());
		cross.setScaleCenter(0, cross.getHeight());
		cross.setScale(1.3f);
		cross.setVisible(true);
		hud.attachChild(cross);
		hud.registerTouchArea(cross);

		Sprite heart = new Sprite(x, 30,
				GfxAssets.trHudHeart,
				MainActivity._main.getVertexBufferObjectManager());
		heart.setScaleCenter(0, heart.getHeight());
		heart.setScale(1.3f);
		heart.setVisible(true);
		hud.attachChild(heart);
		hud.registerTouchArea(heart);
		

		Sprite heart2 = new Sprite(CAMERA_WIDTH - 100, 30,
				GfxAssets.trHudHeart,
				MainActivity._main.getVertexBufferObjectManager());
		heart2.setScaleCenter(0, heart2.getHeight());
		heart2.setScale(1.3f);
		heart2.setVisible(true);
		hud.attachChild(heart2);
		hud.registerTouchArea(heart2);
		
		labelHP = new Text(x + 38, y + 11, FontAssets.hpFont, "" + PlayerObject.HEALTH_MAX, 
				new TextOptions(HorizontalAlign.LEFT), MainActivity._main.getVertexBufferObjectManager());
		hud.attachChild(labelHP);
	}
	
	private void showHP(int hp) {
		String txt = "" + hp;
		labelHP.setText(txt);
	}
	

	private void showlives(int lives) {
		String txt = "x " + lives;
		labelLives.setText(txt);
	}
	
	private void showEnemylives(int lives) {
		String txt = "x " + lives;
		labelEnemyLives.setText(txt);
	}
	// Создание джойстиков
	private void initJoysticks(HUD hud) {
		float padding = 30; // отступ от краев
		float scale = 1.6f;
		float x = padding; 
		float y = CAMERA_HEIGHT - padding - GfxAssets.trJoystickBody.getHeight();
		this.mDigitalOnScreenControl = new DigitalOnScreenControl(x, y,
				MainActivity.camera, GfxAssets.trJoystickBody,
				GfxAssets.trJoystickKnob, 0.1f,
				MainActivity._main.getVertexBufferObjectManager(),
				new IOnScreenControlListener() {
					@Override
					public void onControlChange(
							final BaseOnScreenControl pBaseOnScreenControl,
							final float pValueX, final float pValueY) {
						if (moveX != pValueX || moveY != pValueY) {
							// Посылаем сообщение серверу
							if (!GameScene.this.gameStarted || !GameScene.this.mServerConnector.getConnection().isAlive()) {
								return;
							}				
							MovePlayerClientMessage movePlayerClientMessage = new MovePlayerClientMessage(
									playerID, Math.round(pValueX), pValueY < -0.5f);
							try {
								GameScene.this.mServerConnector
										.sendClientMessage(movePlayerClientMessage);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}			
						moveX = pValueX;
						moveY = pValueY;
					}
				});
		this.mDigitalOnScreenControl.getControlBase().setBlendFunction(
				GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		this.mDigitalOnScreenControl.getControlBase().setAlpha(0.5f);
		this.mDigitalOnScreenControl.getControlBase().setScaleCenter(0, 128);
		this.mDigitalOnScreenControl.getControlBase().setScale(scale);
		this.mDigitalOnScreenControl.getControlKnob().setScale(scale);
		this.mDigitalOnScreenControl.refreshControlKnobPosition();
		this.mDigitalOnScreenControl.setAllowDiagonal(true);
		hud.setChildScene(this.mDigitalOnScreenControl);
		
		// Второй джойстик
		x = CAMERA_WIDTH - padding - GfxAssets.trJoystickBody.getWidth();
		y = CAMERA_HEIGHT - padding - GfxAssets.trJoystickBody.getHeight();
		final AnalogOnScreenControl rotationOnScreenControl = new AnalogOnScreenControl(
				x, y, 
				MainActivity.camera, GfxAssets.trJoystickBody,
				GfxAssets.trJoystickKnob, 0.05f,
				MainActivity._main.getVertexBufferObjectManager(),
				new IAnalogOnScreenControlListener() {
			@Override
			public void onControlChange(final BaseOnScreenControl pBaseOnScreenControl, final float pValueX, final float pValueY) {
				if (pValueX != dirX || pValueY != dirY) {
					float dist = pValueX * pValueX + pValueY * pValueY;
					if (dist == 0 || dist >= 1 - 0.1) {
						if (!GameScene.this.gameStarted || !GameScene.this.mServerConnector.getConnection().isAlive()) {
							return;
						}
						ShotPlayerClientMessage shotMessage = new ShotPlayerClientMessage(playerID, pValueX, pValueY);
						try {
							GameScene.this.mServerConnector
									.sendClientMessage(shotMessage);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				dirX = pValueX;
				dirY = pValueY;
			}

			@Override
			public void onControlClick(final AnalogOnScreenControl pAnalogOnScreenControl) {
				/* Nothing. */
			}
		});
		rotationOnScreenControl.getControlBase().setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		rotationOnScreenControl.getControlBase().setAlpha(0.5f);
		rotationOnScreenControl.getControlBase().setScaleCenter(128, 128);
		rotationOnScreenControl.getControlBase().setScale(scale);
		rotationOnScreenControl.getControlKnob().setScale(scale);

		this.mDigitalOnScreenControl.setChildScene(rotationOnScreenControl);
	}
	
	// Установка оружия
	private void setWeapon(int id) {
		PlayerObject player =  players.get(playerID);
		if (player == null) {
			Log.e(TAG, "ERROR: setWeapon - null player, key: " + id);
			return;
		}
		
		player.weaponID = id;
		
		if (!GameScene.this.gameStarted || !GameScene.this.mServerConnector.getConnection().isAlive()) {
			return;
		}
		WeaponPlayerClientMessage weaponMessage = new WeaponPlayerClientMessage(playerID, id);
		try {
			GameScene.this.mServerConnector.sendClientMessage(weaponMessage);
		} catch (IOException e) {
			Log.e(TAG, "Ошибка: setWeapon - невозможно отправить сообщение, key: " + id);
			e.printStackTrace();
		}
	}
	
	// Создание кнопок выбора оружия
	private void initWeapons(HUD hud) {
		float y = 20;
		float dX = 105 + 30;
		float x = 20;
		x += dX;
		
		// Шотган
		btnShotgun = new Sprite(x, y,
				GfxAssets.trWeaponButtonShotgun,
				MainActivity._main.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				setWeapon(WEAPON_SHOTGUN);
				setWeaponIcon(btnShotgunSelected, btnShotgun);
				return true;
			}
		};
		btnShotgun.setVisible(false);
		hud.attachChild(btnShotgun);
		hud.registerTouchArea(btnShotgun);
		
		btnShotgunSelected = new Sprite(x, y,
				GfxAssets.trWeaponButtonShotgunSelected,
				MainActivity._main.getVertexBufferObjectManager());
		hud.attachChild(btnShotgunSelected);
		hud.registerTouchArea(btnShotgunSelected);
		x += dX;
		
		// SMG
		btnSmg = new Sprite(x, y,
				GfxAssets.trWeaponButtonSmg,
				MainActivity._main.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				setWeapon(WEAPON_SMG);
				setWeaponIcon(btnSmgSelected, btnSmg);
				return true;
			}
		};
		hud.attachChild(btnSmg);
		hud.registerTouchArea(btnSmg);
		btnSmgSelected = new Sprite(x, y,
				GfxAssets.trWeaponButtonSmgSelected,
				MainActivity._main.getVertexBufferObjectManager());
		btnSmgSelected.setVisible(false);
		hud.attachChild(btnSmgSelected);
		hud.registerTouchArea(btnSmgSelected);
		x += dX;
		
		// RPG
		btnRpg = new Sprite(x, y,
				GfxAssets.trWeaponButtonRpg,
				MainActivity._main.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				setWeapon(WEAPON_ROCKET);
				setWeaponIcon(btnRpgSelected, btnRpg);
				return true;
			}
		};
		hud.attachChild(btnRpg);
		hud.registerTouchArea(btnRpg);
		btnRpgSelected = new Sprite(x, y,
				GfxAssets.trWeaponButtonRpgSelected,
				MainActivity._main.getVertexBufferObjectManager());
		btnRpgSelected.setVisible(false);
		hud.attachChild(btnRpgSelected);
		hud.registerTouchArea(btnRpgSelected);
		x += dX;
		
		// Граната
		btnGrenade = new Sprite(x, y,
				GfxAssets.trWeaponButtonGrenade,
				MainActivity._main.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				setWeapon(WEAPON_GRENADE);
				setWeaponIcon(btnGrenadeSelected, btnGrenade);
				return true;
			}
		};
		hud.attachChild(btnGrenade);
		hud.registerTouchArea(btnGrenade);
		btnGrenadeSelected = new Sprite(x, y,
				GfxAssets.trWeaponButtonGrenadeSelected,
				MainActivity._main.getVertexBufferObjectManager());
		btnGrenadeSelected.setVisible(false);
		hud.attachChild(btnGrenadeSelected);
		hud.registerTouchArea(btnGrenadeSelected);
		x += dX;
		selected = btnShotgunSelected;
		alterSelected = btnShotgun;
	}
	
	protected void setWeaponIcon(Sprite _btn, Sprite _oldBtn) {
		selected.setVisible(false);
		alterSelected.setVisible(true);
		_btn.setVisible(true);
		_oldBtn.setVisible(false);
		selected = _btn;
		alterSelected = _oldBtn;
	}

	// Создание сервера
	public void initServer() {
		if (!MainActivity.isWifiServer){
			mScene.mServer = new GameServer(new ClientConnectorListener());
	
			mScene.mServer.start();
	
			MainActivity._main.getEngine().registerUpdateHandler(mScene.mServer);
			
			try {
				Thread.sleep(1000);
			} catch (final Throwable t) {
				Debug.e(t);
			}
			Logger.write("Сервер", "Инициализирован");
		}
	}

	// Создание клиента
	public void initClient() {
		String serverIP;
		if (MainActivity.isWifiServer) {
			serverIP = Wifi.getIP();
		}
		else{
			serverIP = "127.0.0.1";
		}
		try {
			this.mServerConnector = new GameServerConnector(serverIP, new ServerConnectorListener());

			this.mServerConnector.getConnection().start();
		} catch (final Throwable t) {
			Debug.e(t);
			Toaster.send("Не удалось подключиться к серверу!");
			Logger.write("Клиент", "Ошибка: превышено время ожидания подключения к серверу");
			WaitScene.wifiOff();
			MainState.showScene(GameState.MENU);
			return;
		}
		Logger.write("Клиент", "Инициализирован. IP: "+ serverIP);
	}
	
	// Завершение матча
	public void finish() {
		// Если открыта другая сцена - выходим
		/*if (!isVisible() && isIgnoreUpdate()) {
			return;
		}*/
		// Если запущена игра - закрываем сокеты
		if (gameStarted) {
			gameStarted = false;
			playerID = -1;
			
			showlives(LIVES);
			showEnemylives(LIVES);
			showHP(PlayerObject.HEALTH_MAX);
			
			// Если создан сервер (и мы и есть сервер)
			if (this.mServer != null && !MainActivity.isWifiServer) {
				MainActivity._main.getEngine().unregisterUpdateHandler(this.mServer);
				if (GameServer.gameIsFinished) {
					Logger.write("Сервер", "Завершение работы...");
				}
				else {
					//Logger.write("Сервер", "Ошибка: неожиданное завершение работы сервера");
					GameServer.gameIsFinished = true;
				}
				/*try {
					mServer.sendBroadcastServerMessage(new ConnectionCloseServerMessage());
				} catch (final IOException e) {
					Debug.e(e);
				}
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				this.mServer.terminate();
				this.mServer = null;
			}

			// Если создан клиент (и мы и есть клиент)
			if (this.mServerConnector != null && MainActivity.isWifiServer) {
				if (GameScene.this.mServerConnector.getConnection().isAlive()) {
					ConnectionCloseClientMessage connectionCloseClientMessage = new ConnectionCloseClientMessage(playerID);
					try {
						GameScene.this.mServerConnector.sendClientMessage(connectionCloseClientMessage);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
					Logger.write("Клиент", "Отключен");
				}
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (this.mServerConnector != null) {
					this.mServerConnector.terminate();
				}
				this.mServerConnector = null;
			}
		}
	}
	
	// Класс, получающий сообщения от сервера
	class GameServerConnector extends ServerConnector<SocketConnection>
			implements GameConstants, ServerMessageFlags {
		public GameServerConnector(
				final String pServerIP,
				final ISocketConnectionServerConnectorListener pSocketConnectionServerConnectorListener)
				throws IOException {
			super(new SocketConnection(new Socket(pServerIP, Wifi.port)),
					pSocketConnectionServerConnectorListener);

			// Выключение сервера
			this.registerServerMessage(FLAG_MESSAGE_SERVER_CONNECTION_CLOSE,
					ConnectionCloseServerMessage.class,
					new IServerMessageHandler<SocketConnection>() {
						@Override
						public void onHandleMessage(
								final ServerConnector<SocketConnection> pServerConnector,
								final IServerMessage pServerMessage)
								throws IOException {
							if (!MainActivity.isWifiServer) {
								return;
							}
							GameScene.this.finish();
						}
					});

			// Установка соединения с сервером
			this.registerServerMessage(
					FLAG_MESSAGE_SERVER_CONNECTION_ESTABLISHED,
					ConnectionEstablishedServerMessage.class,
					new IServerMessageHandler<SocketConnection>() {
						@Override
						public void onHandleMessage(
								final ServerConnector<SocketConnection> pServerConnector,
								final IServerMessage pServerMessage)
								throws IOException {
							Debug.d("Клиент: Подключение установлено.");
						}
					});

			// Неправильный протокол
			this.registerServerMessage(
					FLAG_MESSAGE_SERVER_CONNECTION_REJECTED_PROTOCOL_MISSMATCH,
					ConnectionRejectedProtocolMissmatchServerMessage.class,
					new IServerMessageHandler<SocketConnection>() {
						@Override
						public void onHandleMessage(
								final ServerConnector<SocketConnection> pServerConnector,
								final IServerMessage pServerMessage)
								throws IOException {
							final ConnectionRejectedProtocolMissmatchServerMessage connectionRejectedProtocolMissmatchServerMessage = (ConnectionRejectedProtocolMissmatchServerMessage) pServerMessage;
							Logger.write("GAME_SCENE", "Ошибка: Несоответствие протоколов: " + connectionRejectedProtocolMissmatchServerMessage.getProtocolVersion());
							// Завершаем работу
							GameScene.this.finish();
						}
					});

			// Пинг
			this.registerServerMessage(FLAG_MESSAGE_SERVER_CONNECTION_PING,
					ConnectionPingServerMessage.class,
					new IServerMessageHandler<SocketConnection>() {
						@Override
						public void onHandleMessage(
								final ServerConnector<SocketConnection> pServerConnector,
								final IServerMessage pServerMessage)
								throws IOException {
							final ConnectionPingServerMessage connectionGameServerMessage = (ConnectionPingServerMessage) pServerMessage;
							final long roundtripMilliseconds = System
									.currentTimeMillis()
									- connectionGameServerMessage
											.getTimestamp();
							Debug.v("Ping: " + roundtripMilliseconds / 2 + "ms");
							Toaster.send("Ping: " + roundtripMilliseconds / 2 + "ms");
						}
					});

			// Установка ID игрока
			this.registerServerMessage(FLAG_MESSAGE_SERVER_SET_PLAYERID,
					SetPlayerIDServerMessage.class,
					new IServerMessageHandler<SocketConnection>() {
						@Override
						public void onHandleMessage(
								final ServerConnector<SocketConnection> pServerConnector,
								final IServerMessage pServerMessage)
								throws IOException {
							final SetPlayerIDServerMessage setPlayerIDServerMessage = (SetPlayerIDServerMessage) pServerMessage;
							int previousID = playerID;
							// устанавливаем ID игрока							
							playerID = setPlayerIDServerMessage.playerID;							
							GameScene.this.addPlayer(playerID);
							GameScene.this.removePlayer(previousID);
							
							initMap(setPlayerIDServerMessage.mapID);
							sendDbInfo();
							gameStarted = true;
							Toaster.send("ID = " + playerID);
						}
					});
			
			// Выход игрока
			this.registerServerMessage(FLAG_MESSAGE_SERVER_PLAYER_QUIT,
					PlayerQuitServerMessage.class,
					new IServerMessageHandler<SocketConnection>() {
						@Override
						public void onHandleMessage(
								final ServerConnector<SocketConnection> pServerConnector,
								final IServerMessage pServerMessage)
								throws IOException {
							final PlayerQuitServerMessage playerQuitServerMessage = (PlayerQuitServerMessage) pServerMessage;
							// TODO устанавливаем ID игрока							
							int quitID = playerQuitServerMessage.playerID;
							GameScene.this.removePlayer(quitID);
							if (!gameIsEnded) {
								Toaster.send("Клиент: игрок вышел: " + quitID);
								//Logger.write("Клиент", "игрок вышел: " + quitID);
							}
						}
					});

			// Обновление статуса игрока
			this.registerServerMessage(FLAG_MESSAGE_SERVER_PLAYER_MOVE,
					PlayerPositionServerMessage.class,
					new IServerMessageHandler<SocketConnection>() {
						@Override
						public void onHandleMessage(
								final ServerConnector<SocketConnection> pServerConnector,
								final IServerMessage pServerMessage)
								throws IOException {
							final PlayerPositionServerMessage playerStatusServerMessage = (PlayerPositionServerMessage) pServerMessage;
							int id = playerStatusServerMessage.playerID;
							float x = playerStatusServerMessage.x * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
							float y = playerStatusServerMessage.y * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
							
							PlayerObject player = players.get(id);
							if (player == null && !gameIsEnded ) {
								GameScene.this.addPlayer(id);
								Toaster.send("Клиент, новый игрок: " + id);
								//Logger.write("Клиент", "новый игрок: " + id);
							}
							
							if (id != playerID) {
								if (players.get(id) == null) {
									return;
								}
								players.get(id).setPosition(x, y);
							}
							else {
								GameScene.this.nextX = x;
								GameScene.this.nextY = y;
							}	
						}
					});

			// Сообщение, что игрок убит
			this.registerServerMessage(FLAG_MESSAGE_SERVER_PLAYER_DEAD,
					PlayerDeadServerMessage.class,
					new IServerMessageHandler<SocketConnection>() {
						@Override
						public void onHandleMessage(
								final ServerConnector<SocketConnection> pServerConnector,
								final IServerMessage pServerMessage)
								throws IOException {
							final PlayerDeadServerMessage playerStatusDeadMessage = (PlayerDeadServerMessage) pServerMessage;
							int pID = playerStatusDeadMessage.playerID;
							int kID = playerStatusDeadMessage.killerID;
							int type = playerStatusDeadMessage.bulletType;

							PlayerObject player = players.get(pID);
							PlayerObject killer = players.get(kID);
							
							if (player == null || killer == null || player.dbInfo == null || killer.dbInfo == null) {
								Log.e(TAG, "Error: message server dead, null, ids: " + pID + " " + kID);
								return;
							}
							String pName = player.dbInfo.name;
							String kName = killer.dbInfo.name;
							String wName = Utils.getWeaponName(type);
							if (gameIsEnded) {
								return;
							}
							if (type != WALL) {
								if (pID == kID) {
									Toaster.send(pName + " убил себя с помощью: " + wName + ". \nНеудачник.");									
								}
								else {
									Toaster.send(kName + "  убил " + pName +
										" \n(с: "+ wName + ")");
								}
							}
							else {
								if (pID == kID) {
									Toaster.send(pName + " убил себя :(");									
								}
								else {
									Toaster.send(pName + " убил себя об #" + kName);
								}
							}
						}
					});
			
			// Обновление статуса пуля
			this.registerServerMessage(FLAG_MESSAGE_SERVER_STATUS_BULLET,
					BulletStatusServerMessage.class,
					new IServerMessageHandler<SocketConnection>() {
						@Override
						public void onHandleMessage(
								final ServerConnector<SocketConnection> pServerConnector,
								final IServerMessage pServerMessage)
								throws IOException {
							final BulletStatusServerMessage bulletStatusServerMessage = (BulletStatusServerMessage) pServerMessage;
							BulletObject bullet = bullets.get(bulletStatusServerMessage.key);
							if (bullet != null) {
								bullet.setX(bulletStatusServerMessage.posX * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
								bullet.setY(bulletStatusServerMessage.posY * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
							}
						}
					});
			
			// Ответ на команду серверу
			this.registerServerMessage(FLAG_MESSAGE_SERVER_STRING,
					StringServerMessage.class,
					new IServerMessageHandler<SocketConnection>() {
						@Override
						public void onHandleMessage(
								final ServerConnector<SocketConnection> pServerConnector,
								final IServerMessage pServerMessage)
								throws IOException {
							final StringServerMessage message = (StringServerMessage) pServerMessage;
							String answer = message.str;
							Logger.write(TAG, "Ответ сервера: " + answer);
							Toaster.sendLong(answer);
						}
					});

			// Обновление хп, брони и оружия игрока
			this.registerServerMessage(FLAG_MESSAGE_SERVER_PLAYER_PARAMETERS,
					PlayerParametersServerMessage.class,
					new IServerMessageHandler<SocketConnection>() {
						@Override
						public void onHandleMessage(
								final ServerConnector<SocketConnection> pServerConnector,
								final IServerMessage pServerMessage)
								throws IOException {
							final PlayerParametersServerMessage playerParametersServerMessage = (PlayerParametersServerMessage) pServerMessage;

							PlayerObject player = players.get(playerParametersServerMessage.playerID);
							if (player == null) {
								Log.e(TAG, "ERROR: Message - playerParamters - null player, id: " + playerID);
								return;
							}
							player.health = playerParametersServerMessage.hp;
							player.armor = playerParametersServerMessage.armor;
							player.weaponID = playerParametersServerMessage.weapon;
							if (playerID == playerParametersServerMessage.playerID) {
								showHP(playerParametersServerMessage.hp);
								showlives(playerParametersServerMessage.lives);
							} else {
								showEnemylives(playerParametersServerMessage.lives);
								if (playerParametersServerMessage.lives < 1){
									//Toaster.send("You win");
								}
							}
						}
					});

			// Получение инфы об игроке из БД
			this.registerServerMessage(FLAG_MESSAGE_SERVER_DB_INFO,
					DBPlayerServerMessage.class,
					new IServerMessageHandler<SocketConnection>() {
						@Override
						public void onHandleMessage(
								final ServerConnector<SocketConnection> pServerConnector,
								final IServerMessage pServerMessage)
								throws IOException {
							final DBPlayerServerMessage message = (DBPlayerServerMessage) pServerMessage;

							Log.w(TAG, "ADD DBINFO: pID: " + message.pID);
							PlayerObject player = players.get(message.pID);
							if (player == null) {
								GameScene.this.addPlayer(message.pID);
								player = players.get(message.pID);
								Log.w(TAG, "ERROR: Message - playerDBinfo - null player, id: " + message.pID);
								return;
							}
							if (message._id == playerDbInfo._id) {
								message.dbInfo.password = playerDbInfo.password;
							}
							Log.w(TAG, "ADD DBINFO: pID: " + message.pID + " || id: " + message.dbInfo._id +
									", name: " + message.dbInfo.name);
							player.dbInfo = message.dbInfo;
							player.mSprite.attachChild(new Text(-20, -20, FontAssets.droidFont, player.dbInfo.name, 
									MainActivity._main.getVertexBufferObjectManager()));
							MainActivity.dbWorker.updateOrAddPlayer(message.dbInfo);
						}
					});

			// Получение итоговой статистики для БД
			this.registerServerMessage(FLAG_MESSAGE_SERVER_DB_STATISTICS,
					DBStatisticsServerMessage.class,
					new IServerMessageHandler<SocketConnection>() {
						@Override
						public void onHandleMessage(
								final ServerConnector<SocketConnection> pServerConnector,
								final IServerMessage pServerMessage)
								throws IOException {
							final DBStatisticsServerMessage message = (DBStatisticsServerMessage) pServerMessage;

							if (!gameIsEnded) {
								gameIsEnded = true;
								
								for (int i = 0; i < message.playerDBs.size(); i++) {
									PlayerDB pDB = message.playerDBs.get(i);
									if (pDB._id == playerDbInfo._id) {
										pDB.password = playerDbInfo.password;
									}
									MainActivity.dbWorker.updateOrAddPlayer(pDB);								
								}

								GameDB game = message.game;
								int gameIndex = MainActivity.dbWorker.addGame(game);
								
								GamePlayerDB playerStats = null;
								int winnerID = -1;
								for (int i = 0; i < message.gpDBs.size(); i++) {								
									GamePlayerDB gpDB = message.gpDBs.get(i);
									gpDB.gID = gameIndex;
									MainActivity.dbWorker.addGP(gpDB);
									if (gpDB.winner == 1) {
										winnerID = gpDB.pID;
									}
									
									if (gpDB.pID == playerDbInfo._id) {
										playerStats = gpDB;
									}
								}
								
								for (int i = 0; i < message.playerDBs.size(); i++) {
									PlayerDB pDB = message.playerDBs.get(i);
									if (pDB._id == winnerID) {
										Toaster.send("Игра окончена, победитель: " + pDB.name);
										Logger.write("Клиент", "Игра окончена, победитель: " + pDB.name);
									}
								}
								
								GameScene.this.checkAchievements(playerStats);
								MainState.showScene(GameState.MENU);
							}
						}
					});
			
			// Добавление/удаление пули
			this.registerServerMessage(FLAG_MESSAGE_SERVER_BULLET,
					BulletServerMessage.class,
					new IServerMessageHandler<SocketConnection>() {
						@Override
						public void onHandleMessage(
								final ServerConnector<SocketConnection> pServerConnector,
								final IServerMessage pServerMessage)
								throws IOException {
							final BulletServerMessage bulletMessage = (BulletServerMessage) pServerMessage;
							if (bulletMessage.added) {
								// Добавляем пулю
								float posX = bulletMessage.posX / TILE_SIZE;
								float posY = bulletMessage.posY / TILE_SIZE;
								float dirX = bulletMessage.dirX;
								float dirY = bulletMessage.dirY;
								int weapon = bulletMessage.type;
								int key = bulletMessage.key;

								BulletObject bullet = new BulletObject(posX, posY, dirX, dirY, weapon);
								bullets.append(key, bullet);
								attachChild(bullet);
							}
							else {
								// Удаляем пулю
								GameScene.this.deleteBullet(bulletMessage.key);
							}
						}
					});
		}
	}


	// Проверка получения новых достижений
	private void checkAchievements(GamePlayerDB stats) {
		if (stats == null) {
			Logger.write(TAG, "Ошибка: checkAchievements null");
			return;
		}
		
		// Неудачник
		if (stats.kills == 0) {
			if (MainActivity.dbWorker.setAchievement(playerDbInfo._id, 1)) {
				AchievementDB ach = MainActivity.dbWorker.getAchievementByID(1);
				if (ach != null)
					Toaster.send("Достижение: \'" + ach.title + "\' заработано");
			}
		}
		
		// Ноулайфер
		if (MainActivity.dbWorker.getGamesByPlayer(playerDbInfo._id).size() >= 100) {
			if (MainActivity.dbWorker.setAchievement(playerDbInfo._id, 2)) {
				AchievementDB ach = MainActivity.dbWorker.getAchievementByID(2);
				if (ach != null)
					Toaster.send("Достижение: \'" + ach.title + "\' заработано");
			}
		}
		
		// Про
		if (stats.deaths == 0) {
			if (MainActivity.dbWorker.setAchievement(playerDbInfo._id, 3)) {
				AchievementDB ach = MainActivity.dbWorker.getAchievementByID(3);
				if (ach != null)
					Toaster.send("Достижение: \'" + ach.title + "\' заработано");
			}
		}
	}
	
	// Прослушивальщик клиента
	private class ServerConnectorListener implements ISocketConnectionServerConnectorListener {
		@Override
		public void onStarted(final ServerConnector<SocketConnection> pServerConnector) {
			Toaster.send("Клиент: Подключен к серверу.");
			Logger.write("Клиент", "Подключен к серверу");
		}

		@Override
		public void onTerminated(final ServerConnector<SocketConnection> pServerConnector) {
			Toaster.send("Клиент: Отключен от сервера.");
			if (GameScene.gameIsEnded) {
				Logger.write("Клиент", "Отключен от сервера.");
			}
			else {
				Logger.write("Клиент", "Ошибка: неожиданное отключение сервера");
				GameServer.gameIsFinished = true;
				GameScene.this.mServer = null;
			}
			MainState.showScene(GameState.MENU);
		}
	}

	// Прослушивальщик сервера
	private class ClientConnectorListener implements ISocketConnectionClientConnectorListener {
		@Override
		public void onStarted(final ClientConnector<SocketConnection> pClientConnector) {
			Toaster.send("Сервер: Клиент подключен: " + pClientConnector.getConnection().getSocket().getInetAddress().getHostAddress());
			Logger.write("Сервер", "Клиент подключен: "+ pClientConnector.getConnection().getSocket().getInetAddress().getHostAddress());
		}

		@Override
		public void onTerminated(final ClientConnector<SocketConnection> pClientConnector) {
			Toaster.send("Сервер: Клиент отключен: " + pClientConnector.getConnection().getSocket().getInetAddress().getHostAddress());
			if (GameServer.gameIsFinished) {
				/*
				Logger.write("Сервер", "Клиент отключен: "+ 
						pClientConnector.getConnection().getSocket().getInetAddress().getHostAddress());
						*/
			}
			else {
				Logger.write("Сервер", "Ошибка: неожиданное отключение клиента: "+ 
						pClientConnector.getConnection().getSocket().getInetAddress().getHostAddress());				
			}
			if (GameScene.this.mServer == null) {
				return;
			}
			GameScene.this.mServer.removePlayer(pClientConnector.getConnection().getSocket().getInetAddress().getHostAddress());
		}
	}
}
