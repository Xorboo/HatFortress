package com.xorboo.hatfortress;

import java.io.IOException;
import java.util.ArrayList;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.extension.multiplayer.protocol.adt.message.IMessage;
import org.andengine.extension.multiplayer.protocol.adt.message.client.IClientMessage;
import org.andengine.extension.multiplayer.protocol.server.IClientMessageHandler;
import org.andengine.extension.multiplayer.protocol.server.SocketServer;
import org.andengine.extension.multiplayer.protocol.server.SocketServer.ISocketServerListener.DefaultSocketServerListener;
import org.andengine.extension.multiplayer.protocol.server.connector.ClientConnector;
import org.andengine.extension.multiplayer.protocol.server.connector.SocketConnectionClientConnector;
import org.andengine.extension.multiplayer.protocol.server.connector.SocketConnectionClientConnector.ISocketConnectionClientConnectorListener;
import org.andengine.extension.multiplayer.protocol.shared.SocketConnection;
import org.andengine.extension.multiplayer.protocol.util.MessagePool;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.debug.Debug;

import android.util.Log;
import android.util.SparseArray;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.xorboo.hatfortress.game.BulletObject;
import com.xorboo.hatfortress.game.LootDataObject;
import com.xorboo.hatfortress.game.MapObject;
import com.xorboo.hatfortress.game.PlayerObject;
import com.xorboo.hatfortress.loaders.GfxAssets;
import com.xorboo.hatfortress.messages.client.DBPlayerClientMessage;
import com.xorboo.hatfortress.messages.client.MovePlayerClientMessage;
import com.xorboo.hatfortress.messages.client.ShotPlayerClientMessage;
import com.xorboo.hatfortress.messages.client.StringClientMessage;
import com.xorboo.hatfortress.messages.client.WeaponPlayerClientMessage;
import com.xorboo.hatfortress.messages.client.connection.ClientMessageFlags;
import com.xorboo.hatfortress.messages.client.connection.ConnectionCloseClientMessage;
import com.xorboo.hatfortress.messages.client.connection.ConnectionEstablishClientMessage;
import com.xorboo.hatfortress.messages.client.connection.ConnectionPingClientMessage;
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
import com.xorboo.hatfortress.messages.server.connection.ConnectionEstablishedServerMessage;
import com.xorboo.hatfortress.messages.server.connection.ConnectionPingServerMessage;
import com.xorboo.hatfortress.messages.server.connection.ConnectionRejectedProtocolMissmatchServerMessage;
import com.xorboo.hatfortress.messages.server.connection.ServerMessageFlags;
import com.xorboo.hatfortress.utils.GameConstants;
import com.xorboo.hatfortress.utils.Logger;
import com.xorboo.hatfortress.utils.Toaster;
import com.xorboo.hatfortress.utils.Utils;
import com.xorboo.hatfortress.utils.Wifi;
import com.xorboo.hatfortress.utils.database.GameDB;
import com.xorboo.hatfortress.utils.database.GamePlayerDB;
import com.xorboo.hatfortress.utils.database.MapDB;
import com.xorboo.hatfortress.utils.database.PlayerDB;
import com.xorboo.hatfortress.utils.database.TypeDB;

public class GameServer extends SocketServer<SocketConnectionClientConnector> 
implements IUpdateHandler, GameConstants, ClientMessageFlags, ServerMessageFlags, ContactListener {
	
	private static final String TAG = "Сервер";
	
	// Физика
	public static PhysicsWorld mPhysicsWorld;
	private static final FixtureDef PLAYER_FIXTUREDEF = PhysicsFactory.createFixtureDef(1, 0, 0, false, CATEGORYBIT_PLAYER, MASKBITS_PLAYER, (short)0);
	private static final FixtureDef TILE_FIXTUREDEF = PhysicsFactory.createFixtureDef(1, 0, 0, false, CATEGORYBIT_WALL, MASKBITS_WALL, (short)0);
	
	private final MessagePool<IMessage> mMessagePool = new MessagePool<IMessage>();

	public static boolean gameIsFinished = false;
	// Карта
	MapObject map = null;
	// Игра в БД
	private GameDB gameDB = new GameDB();
	// Скорость игроков
	private int speed = 20;
	private int jump = 30;

	private SparseArray<PlayerObject> players = new SparseArray<PlayerObject>();
	private int playerIndex = 0;
	private SparseArray<BulletObject> bullets = new SparseArray<BulletObject>();
	private int bulletIndex = 0;
	private ArrayList<Body> delList = new ArrayList<Body>();
	
	private ArrayList<Vector2> spawnPoints = new ArrayList<Vector2>();
	
	private ArrayList<TypeDB> types;
	public GameServer(final ISocketConnectionClientConnectorListener pSocketConnectionClientConnectorListener) {
		super(Wifi.port, pSocketConnectionClientConnectorListener, new DefaultSocketServerListener<SocketConnectionClientConnector>());
		
		PLAYER_FIXTUREDEF.isSensor = false;
		TILE_FIXTUREDEF.isSensor = false;
		types = MainActivity.dbWorker.getAllTypes();
		this.initPhysicsWorld();
		
		this.initMessagePool();

	}
	
	// Заполнение типов сообщений
	private void initMessagePool() {
		this.mMessagePool.registerMessage(FLAG_MESSAGE_SERVER_CONNECTION_ESTABLISHED, PlayerPositionServerMessage.class);
		this.mMessagePool.registerMessage(FLAG_MESSAGE_SERVER_CONNECTION_CLOSE, PlayerPositionServerMessage.class);

		this.mMessagePool.registerMessage(FLAG_MESSAGE_SERVER_STATUS_BULLET, BulletStatusServerMessage.class);
		this.mMessagePool.registerMessage(FLAG_MESSAGE_SERVER_PLAYER_MOVE, PlayerPositionServerMessage.class);
		this.mMessagePool.registerMessage(FLAG_MESSAGE_SERVER_PLAYER_QUIT, PlayerQuitServerMessage.class);
		this.mMessagePool.registerMessage(FLAG_MESSAGE_SERVER_SET_PLAYERID, SetPlayerIDServerMessage.class);
		this.mMessagePool.registerMessage(FLAG_MESSAGE_SERVER_BULLET, BulletServerMessage.class);
		this.mMessagePool.registerMessage(FLAG_MESSAGE_SERVER_PLAYER_DEAD, PlayerDeadServerMessage.class);
		this.mMessagePool.registerMessage(FLAG_MESSAGE_SERVER_PLAYER_PARAMETERS, PlayerParametersServerMessage.class);
		this.mMessagePool.registerMessage(FLAG_MESSAGE_SERVER_DB_INFO, DBPlayerServerMessage.class);		
		this.mMessagePool.registerMessage(FLAG_MESSAGE_SERVER_DB_STATISTICS, DBStatisticsServerMessage.class);		
		this.mMessagePool.registerMessage(FLAG_MESSAGE_SERVER_STRING, StringServerMessage.class);				
	}
	
	// Создание физического мира
	private void initPhysicsWorld() {
		GameServer.mPhysicsWorld = new FixedStepPhysicsWorld(FPS, 2, new Vector2(0, GRAVITY), false, 2, 1);
		GameServer.mPhysicsWorld.setContactListener(this);	
		
		createMap();
	}
	
	// Создание карты
	private void createMap() {
		//TILE_FIXTUREDEF.isSensor = true;
		gameIsFinished = false;
		spawnPoints.clear();
		map = new MapObject(getRandomMap());
		if (map == null) {
			Logger.write(TAG, "Ошибка: не найдено подходящей карты, выход...");
			GameScene.mScene.finish();
			return;
		}
		for (int i = 0; i < map.mapHeight; i++) {
			for (int j = 0; j < map.mapWidth; j++) {
				switch (map.map[i][j]) {
				case TILE_BRICK:
					Body bo = PhysicsFactory.createBoxBody(GameServer.mPhysicsWorld, TILE_SIZE * j, TILE_SIZE * i,
							TILE_SIZE, TILE_SIZE, BodyType.StaticBody, TILE_FIXTUREDEF);
					bo.setUserData(new LootDataObject(i*1000 + j, WALL));					
					break;
				case TILE_PLAYER_SPAWN:
					spawnPoints.add(new Vector2(j * TILE_SIZE, i * TILE_SIZE));
					break;
				default:
					break;
				}
			}
		}
		
		// Дефолтный спавн в центре карты
		if (spawnPoints.size() == 0) {
			spawnPoints.add(new Vector2(map.mapWidth / 2 * TILE_SIZE, 
					map.mapHeight / 2 * TILE_SIZE));
		}
		Logger.write("Сервер", "Карта (id = " + map.mapDB._id + ") создана.");
		
		gameDB.map = map.mapDB._id;
		gameDB.time = Utils.getTime();
		
		mPhysicsWorld.setGravity(new Vector2(0, map.mapDB.gravity));
		speed = map.mapDB.speed;
		jump = map.mapDB.jump;
	}
	
	// Получение случайно карты из базы
	private MapDB getRandomMap() {
		ArrayList<MapDB> maps = MainActivity.dbWorker.getAllMaps();
		int size = maps.size();
		if (size == 0) {
			return null;
		}
		int n = rand.nextInt(size);
		return maps.get(n);
	}
	
	// Кадр
	@Override
	public void onUpdate(float pSecondsElapsed) {
		GameServer.mPhysicsWorld.onUpdate(pSecondsElapsed);
		
		PlayerObject player = null;
		
		// Убираем гравитацию у пуль
		for (int i = 0; i < bullets.size(); i++) {
			BulletObject bullet = bullets.valueAt(i);
			if (bullet.weaponID == WEAPON_GRENADE) {
				continue;
			}
			bullet.body.applyForce(new Vector2(0,-(0.98f * mPhysicsWorld.getGravity().y) * bullet.body.getMass()), new Vector2(bullet.body.getWorldCenter()));
		}
		
		// Убиваем упавших вниз
		killFallenPlayers();
		// Удаляем улетевшие пули
		killFallenBullets();
		
		// Спавним пули
		for (int i = 0; i < players.size(); i++) {
			int key = players.keyAt(i);
			player = players.get(key);
			
			if (player.time > 0) {
				player.time -= pSecondsElapsed;
			}
			
			// Новая пуля
			if (player.dirX != 0 && player.dirY != 0 && player.time <= 0) {
				createShot(key);
				player.stats.shoots++;
			}
		}

		// Двигаем игроков
		for (int i = 0; i < players.size(); i++) {
			player = players.get(players.keyAt(i));
			Vector2 v = player.body.getLinearVelocity();
			switch (player.status) {
			case PlayerObject.STATUS_MOVE_LEFT:
				v.x = -speed * types.get(player.dbInfo.type - 1).speed;
				break;
			case PlayerObject.STATUS_STAND:
				v.x = 0;
				break;
			case PlayerObject.STATUS_MOVE_RIGHT:
				v.x = speed * types.get(player.dbInfo.type - 1).speed;
				break;
			}
			if (player.isJumping) {
				if (Math.abs(v.y) < 0.001f) {
					v.y = -jump * types.get(player.dbInfo.type - 1).jump;
					player.stats.jumps++;
				}
			}
			player.body.setLinearVelocity(v);
		}
		
		// Удаляем тела какие нужно
		for (int i = 0; i < delList.size(); i++) {
			GameServer.mPhysicsWorld.destroyBody(delList.get(i));
		}
		delList.clear();
		
		// Обновляем игроков
		for (int i = 0; i < players.size(); i++) {
			int key = players.keyAt(i);
			player = players.get(key);
			// Возрождаем игроков
			if (player.status == PlayerObject.STATUS_DEAD) {
				Vector2 spawn = chooseSpawnPoint();
				player.revive(spawn.x, spawn.y, types.get(player.dbInfo.type - 1).health);
				sendPlayerParameters(key);
			}
			final PlayerPositionServerMessage statusMessage = (PlayerPositionServerMessage) this.mMessagePool
					.obtainMessage(FLAG_MESSAGE_SERVER_PLAYER_MOVE);
			statusMessage.setPlayerStatus(key, player.body.getPosition().x, player.body.getPosition().y);
			try {
				this.sendBroadcastServerMessage(statusMessage);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.mMessagePool.recycleMessage(statusMessage);
		}
		
		// Обновляем пули
		for (int i = 0; i < bullets.size(); i++) {
			int key = bullets.keyAt(i);
			BulletObject bul = bullets.get(key);
			final BulletStatusServerMessage statusMessage = (BulletStatusServerMessage) this.mMessagePool
					.obtainMessage(FLAG_MESSAGE_SERVER_STATUS_BULLET);
			statusMessage.set(key, bul.body.getPosition().x,
					bul.body.getPosition().y);
			try {
				this.sendBroadcastServerMessage(statusMessage);
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.mMessagePool.recycleMessage(statusMessage);
		}
	}

	@Override
	public void reset() {
		// Ничего
	}

	// Убиваем упавших вниз
	private void killFallenPlayers() {
		float bottom = (map.mapHeight + 50) * TILE_SIZE / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		for (int i = 0; i < players.size(); i++) {
			int key = players.keyAt(i);
			PlayerObject player = players.get(key);
			if (player.body.getPosition().y > bottom) {
				killPlayer(key, WALL, key);
			}
		}
	}
	
	// Удаляем улетевшие пули
	private void killFallenBullets() {
		float bottom =	(map.mapHeight + 50) * TILE_SIZE / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		float top =		(0 - 50) * TILE_SIZE / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		float left =	(0 - 50) * TILE_SIZE / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		float right =	(map.mapWidth + 50) * TILE_SIZE / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		for (int i = 0; i < bullets.size(); i++) {
			int key = bullets.keyAt(i);
			BulletObject bullet = bullets.get(key);
			float x = bullet.body.getPosition().x;
			float y = bullet.body.getPosition().y;
			if (x < left || x > right || y < top || y > bottom) {
				removeBullet(key);
				Log.d(TAG, "Удалена пуля: " + key);
			}
		}
	}
	
	// Выбор точки спавна
	Vector2 chooseSpawnPoint() {
		if (spawnPoints.size() == 0) {
			Log.e(TAG, "ERROR: choose spawn, spawns count == 0");
			return new Vector2(0, 0);
		}
		int spawnN = rand.nextInt(spawnPoints.size());
		return spawnPoints.get(spawnN);
	}
	
	// Добавление игрока
	private void addPlayer(SocketConnectionClientConnector clientConnector) {
		ITextureRegion playerTR = GfxAssets.trPlayer;		
		PlayerObject newPlayer = new PlayerObject(0, 0, playerTR);
		newPlayer.IP = clientConnector.getConnection().getSocket().getInetAddress().getHostAddress();
		newPlayer.connector = clientConnector;
		
		PLAYER_FIXTUREDEF.isSensor = false;
		final Body playerBody = PhysicsFactory.createBoxBody(GameServer.mPhysicsWorld, 100, 100, 
				playerTR.getWidth(), playerTR.getHeight(),
				BodyType.DynamicBody, PLAYER_FIXTUREDEF);
		playerBody.setUserData(new LootDataObject(playerIndex, PLAYER));	
		playerBody.setFixedRotation(true);		
		newPlayer.body = playerBody;
		
		players.append(playerIndex, newPlayer);	
		try {
			clientConnector.sendServerMessage(new SetPlayerIDServerMessage(playerIndex, map.mapDB._id));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		playerIndex++;		
	}
	
	// Удаление игрока
	private void removePlayer(int key) {
		PlayerObject player = players.get(key);
		if (player == null) {
			Log.e(TAG, "ERROR: removePlayer, key (null): " + key);
			return;
		}		

		GameServer.mPhysicsWorld.destroyBody(player.body);		
		players.remove(key);
		
		// Посылаем сообщение остальным
		try {
			this.sendBroadcastServerMessage(new PlayerQuitServerMessage(key));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// Удаление по IP
	public void removePlayer(String ip) {
		for (int i = 0; i < players.size(); i++) {
			int key = players.keyAt(i);
			if (players.get(key).IP.compareTo(ip) == 0) {
				removePlayer(key);
				break;
			}
		}
	}
	
	// Создание пуль выстрела игрока
	private void createShot(int pID) {
		PlayerObject player = players.get(pID);
		if (player == null) {
			Log.e(TAG, "ERROR: createShot - null player, key: " + pID);
			return;
		}
		
		float bulletTime = Utils.getBulletDelay(player.weaponID);
		player.time = bulletTime;
		
		if (player.weaponID != WEAPON_SHOTGUN) {
			createBullet(player, player.dirX, player.dirY);
		}
		else {
			float dirX = player.dirX, dirY = player.dirY;
			int dAngle = 10;
			int bulletsCount = 15;
			for (int i = 0; i < bulletsCount; i++) {
				float angle = -dAngle + rand.nextInt(2 * dAngle + 1); // [-20..20]
				angle *= Math.PI / 180.0f;
				dirX = (float) (player.dirX * Math.cos(angle) - player.dirY * Math.sin(angle));
				dirY = (float) (player.dirX * Math.sin(angle) + player.dirY * Math.cos(angle));
				BulletObject bullet = createBullet(player, dirX, dirY);
				float pow = 10;
				float change = (8 + rand.nextInt(5 * (int)pow) / pow) / 10.0f;
				float speed = bullet.speed * change;
				bullet.body.setLinearVelocity(dirX * speed, dirY * speed);
			}
		}
	}
	
	// Создание одной пули
	private BulletObject createBullet(PlayerObject player, float dirX, float dirY) {
		int playerID = players.keyAt(players.indexOfValue(player));
		// Создаем пулю
		BulletObject bullet = new BulletObject(GfxAssets.trMenuButtonNext, player, 
				dirX, dirY, GameServer.this.bulletIndex, playerID);
		GameServer.this.bullets.append(GameServer.this.bulletIndex, bullet);
		
		// Посылаем сообщение о пуле
		final BulletServerMessage bulletMessage = (BulletServerMessage) GameServer.this.mMessagePool
				.obtainMessage(FLAG_MESSAGE_SERVER_BULLET);
		bulletMessage.set(GameServer.this.bulletIndex, 
				dirX, dirY, 
				bullet.body.getPosition().x, bullet.body.getPosition().y,
				bullet.weaponID, true);
		Log.d(TAG, "Новая пуля, id: " + GameServer.this.bulletIndex);
		GameServer.this.bulletIndex++;
		try {
			GameServer.this.sendBroadcastServerMessage(bulletMessage);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		GameServer.this.mMessagePool.recycleMessage(bulletMessage);
		return bullet;
	}
	
	// Подключение нового клиента
	@Override
	protected SocketConnectionClientConnector newClientConnector(final SocketConnection pSocketConnection) throws IOException {
		final SocketConnectionClientConnector clientConnector = new SocketConnectionClientConnector(pSocketConnection);

		// Движение игрока
		clientConnector.registerClientMessage(FLAG_MESSAGE_CLIENT_MOVE_PLAYER,
				MovePlayerClientMessage.class,
				new IClientMessageHandler<SocketConnection>() {
					@Override
					public void onHandleMessage(
							final ClientConnector<SocketConnection> pClientConnector,
							final IClientMessage pClientMessage)
							throws IOException {
						final MovePlayerClientMessage movePlayerClientMessage = (MovePlayerClientMessage) pClientMessage;
						// TODO Перемещаем игрока
						int id = movePlayerClientMessage.playerID;
						int dir = movePlayerClientMessage.direction;
						boolean isJumping = movePlayerClientMessage.jump;

						PlayerObject player = players.get(id);

						if (player == null) {
							Log.e(TAG, "PlayerMove - player == null OMBWTF");
							return;
						}
						switch (dir) {
						case -1:
							player.status = PlayerObject.STATUS_MOVE_LEFT;
							break;
						case 0:
							player.status = PlayerObject.STATUS_STAND;
							break;
						case 1:
							player.status = PlayerObject.STATUS_MOVE_RIGHT;
							break;
						}
						player.isJumping = isJumping;
					}
				});

		// Выбор оружия
		clientConnector.registerClientMessage(FLAG_MESSAGE_CLIENT_CHOOSE_WEAPON, WeaponPlayerClientMessage.class,
				new IClientMessageHandler<SocketConnection>() {
			@Override
			public void onHandleMessage(final ClientConnector<SocketConnection> pClientConnector, final IClientMessage pClientMessage) throws IOException {
				final WeaponPlayerClientMessage weaponMessage = (WeaponPlayerClientMessage) pClientMessage;
				int playerID = weaponMessage.playerID;
				int weaponID = weaponMessage.weaponID;
				players.get(playerID).weaponID = weaponID;
				// TODO сообщать другим, что игрок выбрал оружие
			}
		});

		// Выстрел
		clientConnector.registerClientMessage(FLAG_MESSAGE_CLIENT_SHOT_PLAYER, ShotPlayerClientMessage.class,
				new IClientMessageHandler<SocketConnection>() {
			@Override
			public void onHandleMessage(final ClientConnector<SocketConnection> pClientConnector, final IClientMessage pClientMessage) throws IOException {
				final ShotPlayerClientMessage shotPlayerClientMessage = (ShotPlayerClientMessage) pClientMessage;
				int playerID = shotPlayerClientMessage.playerID;
				float dirX = shotPlayerClientMessage.dirX;
				float dirY = shotPlayerClientMessage.dirY;
				
				PlayerObject player = players.get(playerID);
				player.dirX = dirX;
				player.dirY = dirY;
			}
		});
		
		// Отключение клиента
		clientConnector.registerClientMessage(FLAG_MESSAGE_CLIENT_CONNECTION_CLOSE, ConnectionCloseClientMessage.class, new IClientMessageHandler<SocketConnection>() {
			@Override
			public void onHandleMessage(final ClientConnector<SocketConnection> pClientConnector, final IClientMessage pClientMessage) throws IOException {
				ConnectionCloseClientMessage message = (ConnectionCloseClientMessage) pClientMessage;
				// TODO передавать инфу об игре и прочее
				GameServer.this.removePlayer(message.playerID);
				pClientConnector.terminate();
			}			
		});

		// Подключение клиента
		clientConnector.registerClientMessage(FLAG_MESSAGE_CLIENT_CONNECTION_ESTABLISH, ConnectionEstablishClientMessage.class, new IClientMessageHandler<SocketConnection>() {
			@Override
			public void onHandleMessage(final ClientConnector<SocketConnection> pClientConnector, final IClientMessage pClientMessage) throws IOException {
				final ConnectionEstablishClientMessage connectionEstablishClientMessage = (ConnectionEstablishClientMessage) pClientMessage;
				if(connectionEstablishClientMessage.getProtocolVersion() == GameConstants.PROTOCOL_VERSION) {
					final ConnectionEstablishedServerMessage connectionEstablishedServerMessage = (ConnectionEstablishedServerMessage) GameServer.this.mMessagePool.obtainMessage(FLAG_MESSAGE_SERVER_CONNECTION_ESTABLISHED);
					try {
						pClientConnector.sendServerMessage(connectionEstablishedServerMessage);
					} catch (IOException e) {
						Debug.e(e);
					}
					GameServer.this.mMessagePool.recycleMessage(connectionEstablishedServerMessage);
				} else {
					final ConnectionRejectedProtocolMissmatchServerMessage connectionRejectedProtocolMissmatchServerMessage = (ConnectionRejectedProtocolMissmatchServerMessage) GameServer.this.mMessagePool.obtainMessage(FLAG_MESSAGE_SERVER_CONNECTION_REJECTED_PROTOCOL_MISSMATCH);
					connectionRejectedProtocolMissmatchServerMessage.setProtocolVersion(GameConstants.PROTOCOL_VERSION);
					try {
						pClientConnector.sendServerMessage(connectionRejectedProtocolMissmatchServerMessage);
					} catch (IOException e) {
						Debug.e(e);
					}
					GameServer.this.mMessagePool.recycleMessage(connectionRejectedProtocolMissmatchServerMessage);
				}
			}
		});

		// Пинг
		clientConnector.registerClientMessage(FLAG_MESSAGE_CLIENT_CONNECTION_PING, ConnectionPingClientMessage.class, new IClientMessageHandler<SocketConnection>() {
			@Override
			public void onHandleMessage(final ClientConnector<SocketConnection> pClientConnector, final IClientMessage pClientMessage) throws IOException {
				final ConnectionPingServerMessage connectionPingServerMessage = (ConnectionPingServerMessage) GameServer.this.mMessagePool.obtainMessage(FLAG_MESSAGE_SERVER_CONNECTION_PING);
				try {
					pClientConnector.sendServerMessage(connectionPingServerMessage);
				} catch (IOException e) {
					Debug.e(e);
				}
				GameServer.this.mMessagePool.recycleMessage(connectionPingServerMessage);
			}
		});

		// Инфа из БД об игроке
		clientConnector.registerClientMessage(FLAG_MESSAGE_CLIENT_DB_INFO, DBPlayerClientMessage.class, new IClientMessageHandler<SocketConnection>() {
			@Override
			public void onHandleMessage(final ClientConnector<SocketConnection> pClientConnector, final IClientMessage pClientMessage) throws IOException {
				final DBPlayerClientMessage message = (DBPlayerClientMessage) pClientMessage;
				PlayerObject player = players.get(message.pID);
				if (player == null) {
					Log.e(TAG, "ERROR: playerDBinfo - null player, id: " + message.pID);
					return;
				}
				PlayerDB dbInfo = message.dbInfo;
				player.dbInfo = dbInfo;
				player.stats.pID = dbInfo._id;
				sendPlayerDbInfo();
			}
		});

		// Команда серверу
		clientConnector.registerClientMessage(FLAG_MESSAGE_CLIENT_STRING, StringClientMessage.class, new IClientMessageHandler<SocketConnection>() {
			@Override
			public void onHandleMessage(final ClientConnector<SocketConnection> pClientConnector, final IClientMessage pClientMessage) throws IOException {
				final StringClientMessage message = (StringClientMessage) pClientMessage;
				String com = message.str;
				String answer = "";
				if (com.compareTo("help") == 0) {
					answer = "Доступные команды: help, time, \nplayers, playersIP";
				}
				else if (com.compareTo("players") == 0) {
					answer = "Игроки: ";
					for (int i = 0; i < players.size(); i++) {
						if (players.valueAt(i).dbInfo == null) {
							continue;
						}
						answer += players.valueAt(i).dbInfo.name + "; ";
					}
				}
				else if (com.compareTo("playersIP") == 0) {
					answer = "IP игроков: ";
					for (int i = 0; i < players.size(); i++) {
						answer += players.valueAt(i).connector.getConnection().getSocket().getInetAddress().getHostAddress() + "; ";
					}
				}
				else if (com.compareTo("time") == 0) {
					answer = Logger.getTime();
				}
				else {
					answer = "Ошибка: команда не распознана";
					Logger.write(TAG, "Ошибка: клиент прислал недопустимую команду: \"" + com + "\"");
				}
				
				final StringServerMessage answerMessage = (StringServerMessage) GameServer.this.mMessagePool.obtainMessage(FLAG_MESSAGE_SERVER_STRING);
				answerMessage.str = answer;
				try {
					pClientConnector.sendServerMessage(answerMessage);
				} catch (IOException e) {
					Debug.e(e);
				}
				GameServer.this.mMessagePool.recycleMessage(answerMessage);
			}
		});

		GameServer.this.addPlayer(clientConnector);
		return clientConnector;
	}

	@Override
	public void beginContact(Contact contact) {
		if (contact == null || contact.getFixtureA() == null || contact.getFixtureB() == null || 
				contact.getFixtureA().getBody().getUserData() == null ||
						contact.getFixtureB().getBody().getUserData() == null) {
			return;
		}
		final LootDataObject userDataA = (LootDataObject)contact.getFixtureA().getBody().getUserData();
		final LootDataObject userDataB = (LootDataObject)contact.getFixtureB().getBody().getUserData();
		
		// Игрок со стеной
		if (userDataA.Type == WALL && userDataB.Type == PLAYER ||
			userDataB.Type == WALL && userDataA.Type == PLAYER) {
			return;
		}
		
		// passive - Игрок или стена
		if (userDataA.Type == WALL || userDataA.Type == PLAYER){
			parseCollisions(userDataB, userDataA);
		}
		else{
			parseCollisions(userDataA, userDataB);
		}
	}

	// Парсинг коллизии
	private void parseCollisions(LootDataObject active, LootDataObject passive) {
		int type = UNKNOWN;
		if (passive.Type == WALL)
		{
			if (active.Type == BULLET_BULLET)
			{
				type = WALL_BULLET;
			}
			else if (active.Type == BULLET_GRENADE || active.Type == BULLET_ROCKET)
			{
				type = WALL_RANGE;
			}
		}
		else if (passive.Type == PLAYER)
		{
			if (active.Type == BULLET_BULLET)
			{
				type = PLAYER_BULLET;
			}
			else if (active.Type == BULLET_GRENADE || active.Type == BULLET_ROCKET)
			{
				type = PLAYER_RANGE;
			}
			else if (active.Type == ARMOR_PACK || active.Type == HEALTH_BIG || active.Type == HEALTH_MEDIUM || active.Type == HEALTH_SMALL)
			{
				type = PLAYER_H_A;
			}
		}
		makeCollision(type, active, passive);
	}
	
	// Обработка коллизии
	private void makeCollision(int type, LootDataObject active, LootDataObject passive)
	{
		BulletObject bullet = null;
		switch (type) {
		case WALL_BULLET:
			break;
		// Взрыв гранаты/ракеты
		case PLAYER_RANGE:
		case WALL_RANGE:
			bullet = bullets.get(active.ID);
			if (bullet == null) {
				Log.e(TAG, "Collision bullet == null OMGWTF");
				return; // TODO wtf тут за баг?
			}
			Log.d(TAG, "Коллизии: pID|wall: " + passive.ID + "; bul_pID: " + bullet.playerID);
			if (passive.Type == PLAYER) {
				if (bullet.playerID == passive.ID) {
					return;
				}
			}
			int power = bullet.power;
			float range = bullet.range / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
			float bulletX = bullet.body.getPosition().x;
			float bulletY = bullet.body.getPosition().y;
			range *= range;
			for (int i = 0; i < players.size(); i++) {
				int key = players.keyAt(i);
				PlayerObject player = players.get(key);
				float x = player.body.getPosition().x;
				float y = player.body.getPosition().y;
				// TODO посчитать пересечения
				float distance = (x - bulletX) * (x - bulletX) + (y - bulletY) * (y - bulletY);
				if (distance >= range) {
					continue;
				}
				int damage = (int) ((1.0f - distance / range) * power);
				player.recieveHit(damage);
				sendPlayerParameters(key);
			}
			break;
		// Пуля в игрока
		case PLAYER_BULLET:
			bullet = bullets.get(active.ID);
			if (bullet == null) {
				Log.e(TAG, "Collision bullet == null OMGWTF");
				return; // TODO wtf тут за баг?
			}
			if (passive.Type == PLAYER) {
				if (bullet.playerID == passive.ID) {
					return;
				}
			}
			PlayerObject p = players.get(passive.ID);
			if (p.status != PlayerObject.STATUS_DEAD) {
				p.recieveHit(bullet.power);
				sendPlayerParameters(passive.ID);
			}
			break;
		// В хп или армор
		case PLAYER_H_A:
			PlayerObject player = players.get(passive.ID);
			switch (active.Type){
			case ARMOR_PACK_BIG:
				player.changeArmor(100);
				break;
			case ARMOR_PACK:
				player.changeArmor(40);
				break;
			case HEALTH_BIG:
				player.increaseHealth(100, false);
				break;
			case HEALTH_MEDIUM:
				player.increaseHealth(50, false);
				break;
			case HEALTH_SMALL:
				player.increaseHealth(5, true);
				break;
			default:
				break;
			}
			sendPlayerParameters(passive.ID);
		default:
			Log.e(TAG, "ERROR: Collisions - undefined: " + type);
			break;
		}
		
		// "Убиваем" умерших
		for (int i = 0; i < players.size(); i++) {
			int key = players.keyAt(i);
			PlayerObject player = players.get(key);
			if (player.status == PlayerObject.STATUS_DEAD) {
				Log.d(TAG, "мертв: " + i);
				BulletObject killBullet = bullets.get(active.ID);
				if (killBullet == null) {
					Log.e(TAG, "ERROR: makeCollision (kill players) - null bullet" +
							", key: " + active.ID);
					continue;
				}
				killPlayer(key, killBullet.weaponID, killBullet.playerID);
			}
		}
		
		// Удаляем пулю
		if (active != null && (	active.Type == BULLET_BULLET || 
								active.Type == BULLET_GRENADE || 
								active.Type == BULLET_ROCKET)) {
			removeBullet(active.ID);
		}
	}
	

	// Удаляем пулю, посылаем сообщение
	private void removeBullet(int id) {
		//Logger.write(TAG, "Delete bullet, id: " + id);
		if (bullets.get(id) == null) {
			Log.e(TAG, "ERROR: remove bullet - null bullet, key: " + id);
			return;
		}
		delList.add(bullets.get(id).body);	
		bullets.remove(id);
		
		final BulletServerMessage statusMessage = (BulletServerMessage) GameServer.this.mMessagePool
				.obtainMessage(FLAG_MESSAGE_SERVER_BULLET);
		statusMessage.set(id, 0, 0, 0, 0, 0, false);
		try {
			GameServer.this.sendBroadcastServerMessage(statusMessage);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		GameServer.this.mMessagePool.recycleMessage(statusMessage);
	}
	
	// Убиваем игрока
	private void killPlayer(int pID, int type, int killer){
		if (GameServer.gameIsFinished) {
			return;
		}
		Logger.write(TAG, "Игрок #" + pID + " мертв. Осталось жизней:" + (LIVES - players.get(pID).stats.deaths - 1));
		PlayerObject player = players.get(pID);
		if (player == null) {
			return;
		}
		player.recieveHit(10000);
		player.status = PlayerObject.STATUS_DEAD;
		player.stats.deaths++;
		
		// Статистика
		PlayerObject enemy = players.get(killer);
		if (type != WALL)
			enemy.stats.kills++;
		/*if (killer != pID) {
			enemy.stats.kills++;
		}
		else {
			enemy.stats.kills--;
		}*/
		if (player.stats.deaths >= LIVES) {
			// Конец игры
			endGameStatistics();
		}
		
		final PlayerDeadServerMessage message = (PlayerDeadServerMessage) GameServer.this.mMessagePool
				.obtainMessage(FLAG_MESSAGE_SERVER_PLAYER_DEAD);
		message.set(pID, killer, type);
		try {
			GameServer.this.sendBroadcastServerMessage(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		GameServer.this.mMessagePool.recycleMessage(message);
	}
	
	// Посылаем параметры игрока
	private void sendPlayerParameters(int pID) {
		PlayerObject player = players.get(pID);
		if (player == null) {
			Log.e(TAG, "ERROR: sendPlayerParameters - null player, id: " + pID);
			return;
		}
		int hp = player.health;
		int armor = player.armor;
		int weapon = player.weaponID;
		int lives = LIVES - player.stats.deaths;
		
		final PlayerParametersServerMessage message = (PlayerParametersServerMessage) GameServer.this.mMessagePool
				.obtainMessage(FLAG_MESSAGE_SERVER_PLAYER_PARAMETERS);
		message.set(pID, hp, armor, weapon, lives);
		try {
			GameServer.this.sendBroadcastServerMessage(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		GameServer.this.mMessagePool.recycleMessage(message);
	}
	
	// Посылаем инфу БД игрока
	private void sendPlayerDbInfo() {
		for (int i =0; i < players.size(); i++) {
			int key = players.keyAt(i);
			PlayerObject player = players.get(key);
			PlayerDB dbInfo = player.dbInfo;
			final DBPlayerServerMessage message = (DBPlayerServerMessage) GameServer.this.mMessagePool
					.obtainMessage(FLAG_MESSAGE_SERVER_DB_INFO);
			message.set(dbInfo, key);
			try {
				GameServer.this.sendBroadcastServerMessage(message);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	// Собираем итоговую статистику
	private void endGameStatistics() {
		if (gameIsFinished) {
			return;
		}
		gameIsFinished = true;
		Logger.write(TAG, "Игра закончена - отправка статистики");
		for (int i = 0; i < players.size(); i++) {								
			PlayerObject player = players.valueAt(i);
			if (player.stats.deaths < LIVES) {
				Logger.write(TAG, "Победитель: " + player.dbInfo.name);
				break;
			}
		}
		
		// Повышаем уровень и деньги игрокам
		for (int i = 0; i < players.size(); i++) {
			PlayerObject player = players.valueAt(i);
			player.dbInfo.exp += player.stats.kills * 10 * players.size();
			player.dbInfo.money += Math.max(0, (player.stats.kills - player.stats.deaths) * 10);
			if (player.stats.deaths != LIVES || players.size() == 1) {
				player.stats.winner = 1;
			}
		}
		
		// Нужно разослать все player.dbInfo, player.stats, а также gameDB		
		final DBStatisticsServerMessage message = (DBStatisticsServerMessage) GameServer.this.mMessagePool
				.obtainMessage(FLAG_MESSAGE_SERVER_DB_STATISTICS);
		message.set(gameDB, players);
		try {
			GameServer.this.sendBroadcastServerMessage(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void endContact(Contact contact) {
		// Ничего не делаем
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// Ничего не делаем
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// Ничего не делаем
	}

}
