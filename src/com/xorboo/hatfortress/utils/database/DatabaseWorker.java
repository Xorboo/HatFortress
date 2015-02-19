package com.xorboo.hatfortress.utils.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.xorboo.hatfortress.utils.Logger;
import com.xorboo.hatfortress.utils.Utils;

public class DatabaseWorker implements DBConstants {
	private static final String TAG = "БД";
	private SQLiteDatabase database = null;
	private DBHelper dbHelper = null;

	// Конструктор
	public DatabaseWorker(Context context) {
		dbHelper = new DBHelper(context);
	}

	// Открытие бд
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
		Logger.write(TAG, "База данных откыта");
		if (playersCount() == 0) {
			addPlayer("Summoner", "1");
		}
	}
	// Закрытие БД
	public void close() {
		dbHelper.close();
		Logger.write(TAG, "База данных закрыта");
	}
	
	// Получение списка игроков
	public ArrayList<PlayerDB> getAllPlayers() {
		ArrayList<PlayerDB> players = new ArrayList<PlayerDB>();
		Cursor cursor = database.query(TABLE_PLAYERS, null, null, null, null, null, PLAYERS_PASSWORD + " DESC");
		//DBHelper.logCursor(cursor);
		cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	      PlayerDB player = cursorToPlayer(cursor);
	      if (player == null) {
	    	  continue;
	      }
	      players.add(player);
	      cursor.moveToNext();
	    }
		return players;
	}

	// Получение игрока
	public PlayerDB getPlayer(int id) {
		String sqlQuery = "select * "
				+ "from " + TABLE_PLAYERS
				+ " where _id=? ";
		Cursor c = database.rawQuery(sqlQuery, new String[] { String.valueOf(id) });
		c.moveToFirst();
		return cursorToPlayer(c);
	}
	// Получение игрока
	public PlayerDB getPlayer(String login, String password) {
		String sqlQuery = "select * "
				+ "from " + TABLE_PLAYERS
				+ " where " + PLAYERS_NAME + "=? AND " + PLAYERS_PASSWORD + "=? ";
		Cursor c = database.rawQuery(sqlQuery, new String[] { login, password });
		c.moveToFirst();
		return cursorToPlayer(c);
	}
	
	// Получение всех игроков с данным ником
	public ArrayList<PlayerDB> getPlayers(String login) {
		ArrayList<PlayerDB> players = new ArrayList<PlayerDB>();
		String sqlQuery = "select * "
				+ "from " + TABLE_PLAYERS
				+ " where " + PLAYERS_NAME + "=? ";
		Cursor cursor = database.rawQuery(sqlQuery, new String[] { login });
		cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	      PlayerDB player = cursorToPlayer(cursor);
	      if (player == null) {
	    	  continue;
	      }
	      players.add(player);
	      cursor.moveToNext();
	    }
		return players;
	}
	
	// Изменение денег
	public void setPlayerMoney(int id, int money) {
		String strFilter = "_id=" + id;
		ContentValues args = new ContentValues();
		args.put(PLAYERS_MONEY, money);
		database.update(TABLE_PLAYERS, args, strFilter, null);
	}

	// Создание игрока
	public boolean addPlayer(String login, String password) {
		long res = -1;
		database.beginTransaction();
	    try {
			if (login == "" || password == "" || 
					getPlayer(login, password) != null) {
				return false;
			}
			ContentValues cv = new ContentValues();
			cv.put(PLAYERS_ID, Utils.getUniqueID());
			cv.put(PLAYERS_NAME, login);
			cv.put(PLAYERS_PASSWORD, password);
			res = database.insert(TABLE_PLAYERS, null, cv);
			database.setTransactionSuccessful();
	    } finally {
	    	database.endTransaction();
	    }
	    return res != -1;
	}

	// Обновление игрока
	public void updateOrAddPlayer(PlayerDB p) {
		database.beginTransaction();
	    try {
			String query = "INSERT OR REPLACE INTO " + TABLE_PLAYERS + 
					" (" + PLAYERS_ID + ", " + PLAYERS_NAME + ", " + PLAYERS_PASSWORD + ", " +
					PLAYERS_EXP + ", " + PLAYERS_MONEY + ", " + PLAYERS_TYPE + ", " + PLAYERS_AVATAR + ") " +
					" VALUES (" + p._id + ", \"" + p.name + "\", \"" + p.password + "\", " 
					 + p.exp + ", " + p.money + ", " + p.type + ", " + p.avID + ") ";
			database.execSQL(query);database.setTransactionSuccessful();
	    } finally {
	    	database.endTransaction();
	    }
	}
	
	// Удаление игрока
	public void delPlayer(int id) {
		database.beginTransaction();
	    try {
			// Из игр
			database.delete(TABLE_GP, GP_PLAYER_ID + " = ?", 
					new String[] { String.valueOf(id) });
			// Из ачивок
			// Из игроков
			database.delete(TABLE_PLAYERS, PLAYERS_ID + " = ?", 
					new String[] { String.valueOf(id) });
			if (playersCount() == 0) {
				addPlayer("Summoner", "1");
			}
			database.setTransactionSuccessful();
	    } finally {
	    	database.endTransaction();
	    }
	}

	// Количество игроков
	public int playersCount() {
		return (int)DatabaseUtils.queryNumEntries(database, DBHelper.TABLE_PLAYERS);
	}
	// Курсор в игрока
	private PlayerDB cursorToPlayer(Cursor cursor) {
		PlayerDB player = new PlayerDB();
		if (cursor.getCount() == 0) {
			return null;
		}
		player._id = cursor.getInt(0);
		player.name = cursor.getString(1);
		player.password = cursor.getString(2);
		player.exp = cursor.getInt(3);
		player.money = cursor.getInt(4);
		player.type = cursor.getInt(5);
		player.avID = cursor.getInt(6);
		return player;
	}


	// Получение информации по игроку
	public PlayerInfoDB getPlayerInfo(int id) {
		String sqlQuery = "select PL._id," 
				+ " PL." + PLAYERS_NAME + ","
				+ " PL." + PLAYERS_MONEY + ","
				+ " PL." + PLAYERS_EXP + ","				
				+ " PL." + PLAYERS_AVATAR + ","
				+ " TP." + TYPE_NAME + ","
				+ " sum(GP." + GP_DEATHS + ") as deaths,"
				+ " sum(GP." + GP_JUMPS + ") as jumps,"
				+ " sum(GP." + GP_SHOOTS + ") as shoots,"
				+ " sum(GP." + GP_WINNER + ") as wins,"
				+ " count(*) as games"
				+ " from " + TABLE_PLAYERS + " PL"
				+ " left outer join " + TABLE_GP + " GP on GP." + GP_PLAYER_ID + "=PL._id"
				+ " inner join " + TABLE_TYPE + " TP on TP." + TYPE_ID + "=PL." + PLAYERS_TYPE
				+ " where PL._id=?"
				+ " group by PL._id";
		Cursor c = database.rawQuery(sqlQuery, new String[] { String.valueOf(id) });
		c.moveToFirst();
		return cursorToPlayerInfo(c);
	}

	// Курсор в инфу игрока
	private PlayerInfoDB cursorToPlayerInfo(Cursor cursor) {
		PlayerInfoDB player = new PlayerInfoDB();
		if (cursor.getCount() == 0) {
			return null;
		}
		int i = 0;
		player._id = cursor.getInt(i++);
		player.name = cursor.getString(i++);
		player.money = cursor.getInt(i++);
		player.exp = cursor.getInt(i++);
		player.avID = cursor.getInt(i++);
		player.type = cursor.getString(i++);
		player.deaths = cursor.getInt(i++);
		player.jumps = cursor.getInt(i++);
		player.shoots = cursor.getInt(i++);
		player.wins = cursor.getInt(i++);
		player.games = cursor.getInt(i++);
		return player;
	}
	
	// Получение списка карт
	public ArrayList<MapDB> getAllMaps() {
		ArrayList<MapDB> maps = new ArrayList<MapDB>();
		Cursor cursor = database.query(TABLE_MAPS, null, null, null, null, null, null);
		//DBHelper.logCursor(cursor);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			MapDB map = cursorToMap(cursor);
		      if (map == null) {
		    	  continue;
		      }
			maps.add(map);
			cursor.moveToNext();
		}
		return maps;
	}

	// Получение карты
	public MapDB getMap(int id) {
		String sqlQuery = "select * " + "from " + TABLE_MAPS + " where _id=? ";
		Cursor c = database.rawQuery(sqlQuery, new String[] { String.valueOf(id) });
		c.moveToFirst();
		return cursorToMap(c);
	}

	// Добавление карты
	public int addMap(MapDB map) {
		ContentValues cv = new ContentValues();
		cv.put(MAPS_NAME, map.name);
		cv.put(MAPS_GRAVITY, map.gravity);
		cv.put(MAPS_SPEED, map.speed);
		cv.put(MAPS_JUMP, map.jump);
		return (int)database.insert(TABLE_MAPS, null, cv);
	}

	// Количество карт
	public int mapsCount() {
		return (int) DatabaseUtils.queryNumEntries(database, DBHelper.TABLE_MAPS);
	}

	// Курсор в карту
	private MapDB cursorToMap(Cursor cursor) {
		MapDB map = new MapDB();
		if (cursor.getCount() == 0) {
			return null;
		}
		map._id = cursor.getInt(0);
		map.name = cursor.getString(1);
		map.gravity = cursor.getInt(2);
		map.speed = cursor.getInt(3);
		map.jump = cursor.getInt(4);
		return map;
	}
	
	// Получение списка игр
	public ArrayList<GameDBInfo> getAllGames() {
		ArrayList<GameDBInfo> games = new ArrayList<GameDBInfo>();
		String sqlQuery = "select G._id, M." + MAPS_NAME + ", G." + GAMES_TIME + " "
				+ "from " + TABLE_GAMES + " as G "
				+ "inner join " + TABLE_MAPS + " as M "
				+ "on G." + GAMES_MAP + " = M." + MAPS_ID + " ";
		Cursor cursor = database.rawQuery(sqlQuery, new String[] { });
		//DBHelper.logCursor(cursor);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			GameDBInfo game = cursorToGameInfo(cursor);
		      if (game == null) {
		    	  continue;
		      }
			games.add(game);
			cursor.moveToNext();
		}
		return games;
	}

	// Получение списка игр
	public ArrayList<GameDBInfo> getGamesByPlayer(int pID) {
		ArrayList<GameDBInfo> games = new ArrayList<GameDBInfo>();
		String sqlQuery = "select G._id, M." + MAPS_NAME + ", G." + GAMES_TIME + " "
				+ "from " + TABLE_GAMES + " as G "
				+ "inner join " + TABLE_GP + " as GP "
				+ "on G._id = GP." + GP_GAME_ID + " " 
				+ "inner join " + TABLE_MAPS + " as M "
				+ "on G." + GAMES_MAP + " = M." + MAPS_ID + " " 
				+ "where GP." + GP_PLAYER_ID + " = ?";
		Cursor cursor = database.rawQuery(sqlQuery, new String[] { String.valueOf(pID) });
		//DBHelper.logCursor(cursor);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			GameDBInfo game = cursorToGameInfo(cursor);
		      if (game == null) {
		    	  continue;
		      }
			games.add(game);
			cursor.moveToNext();
		}
		return games;
	}
	
	// Получение игры
	public GameDB getGame(int id) {
		String sqlQuery = "select * " + "from " + TABLE_GAMES + " where _id=? ";
		Cursor c = database.rawQuery(sqlQuery, new String[] { String.valueOf(id) });
		c.moveToFirst();
		return cursorToGame(c);
	}

	// Добавление игры
	public int addGame(GameDB game) {
		ContentValues cv = new ContentValues();
		cv.put(GAMES_MAP, game.map);
		cv.put(GAMES_TIME, game.time);
		return (int)database.insert(TABLE_GAMES, null, cv);
	}
	
	// Удаление игры
	public void delGame(int id) {
		database.beginTransaction();
	    try {
			// Из игр игроков
			database.delete(TABLE_GP, GP_GAME_ID + " = ?", 
					new String[] { String.valueOf(id) });
			
			// Из игр
			database.delete(TABLE_GAMES, GAMES_ID + " = ?", 
					new String[] { String.valueOf(id) });
			database.setTransactionSuccessful();
	    } finally {
	    	database.endTransaction();
	    }
	}

	// Количество игр
	public int gamesCount() {
		return (int) DatabaseUtils.queryNumEntries(database, DBHelper.TABLE_GAMES);
	}

	// Курсор в игру
	private GameDB cursorToGame(Cursor cursor) {
		GameDB game = new GameDB();
		if (cursor.getCount() == 0) {
			return null;
		}
		game._id = cursor.getInt(0);
		game.map = cursor.getInt(1);
		game.time = cursor.getString(2);
		return game;
	}
	// Курсор в игру
	private GameDBInfo cursorToGameInfo(Cursor cursor) {
		GameDBInfo game = new GameDBInfo();
		if (cursor.getCount() == 0) {
			return null;
		}
		game._id = cursor.getInt(0);
		game.map = cursor.getString(1);
		game.time = cursor.getString(2);
		return game;
	}
	
	// Получение списка игр по игрокам
	public ArrayList<GamePlayerDB> getAllGP() {
		ArrayList<GamePlayerDB> games = new ArrayList<GamePlayerDB>();
		Cursor cursor = database.query(TABLE_GP, null, null, null, null, null, null);
		//DBHelper.logCursor(cursor);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			GamePlayerDB game = cursorToGP(cursor);
		      if (game == null) {
		    	  continue;
		      }
			games.add(game);
			cursor.moveToNext();
		}
		return games;
	}
	

	// Получение списка игр по игрокам
	public ArrayList<GamePlayerDB> getGPsByGID(int gID) {
		ArrayList<GamePlayerDB> games = new ArrayList<GamePlayerDB>();
		String sqlQuery = "select * " + "from " + TABLE_GP + " where " + GP_GAME_ID + "=? ";
		Cursor cursor = database.rawQuery(sqlQuery, new String[] { String.valueOf(gID) });
		//DBHelper.logCursor(cursor);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			GamePlayerDB game = cursorToGP(cursor);
		      if (game == null) {
		    	  continue;
		      }
			games.add(game);
			cursor.moveToNext();
		}
		return games;
	}
	
	// Получение игры по игрокам
	public GamePlayerDB getGP(int id) {
		String sqlQuery = "select * " + "from " + TABLE_GP + " where _id=? ";
		Cursor c = database.rawQuery(sqlQuery, new String[] { String.valueOf(id) });
		c.moveToFirst();
		return cursorToGP(c);
	}

	// Добавление игры по игрокам
	public int addGP(GamePlayerDB gp) {
		ContentValues cv = new ContentValues();
		cv.put(GP_GAME_ID, gp.gID);
		cv.put(GP_PLAYER_ID, gp.pID);
		cv.put(GP_KILLS, gp.kills);
		cv.put(GP_DEATHS, gp.deaths);
		cv.put(GP_SHOOTS, gp.shoots);
		cv.put(GP_JUMPS, gp.jumps);
		cv.put(GP_WINNER, gp.winner);
		return (int)database.insert(TABLE_GP, null, cv);
	}
	
	// Удаление игры по игрокам
	public void delGP(int id) {
		database.beginTransaction();
	    try {
			database.delete(TABLE_GP, GP_ID + " = ?", 
					new String[] { String.valueOf(id) });
				database.setTransactionSuccessful();
	    } finally {
	    	database.endTransaction();
	    }
	}

	// Количество игр по игрокам
	public int gpCount() {
		return (int) DatabaseUtils.queryNumEntries(database, DBHelper.TABLE_GP);
	}

	// Курсор в игру по игрокам
	private GamePlayerDB cursorToGP(Cursor cursor) {
		GamePlayerDB game = new GamePlayerDB();
		if (cursor.getCount() == 0) {
			return null;
		}
		game._id = cursor.getInt(0);
		game.gID = cursor.getInt(1);
		game.pID = cursor.getInt(2);
		game.kills = cursor.getInt(3);
		game.deaths = cursor.getInt(4);
		game.jumps = cursor.getInt(5);
		game.shoots = cursor.getInt(6);
		game.winner = cursor.getInt(7);
		return game;
	}
	
	// Получение списка достижений
	public ArrayList<AchievementDB> getAllAchievements() {
		ArrayList<AchievementDB> achs = new ArrayList<AchievementDB>();
		Cursor cursor = database.query(TABLE_ACHIEVEMENTS, null, null, null, null, null, null);
		//DBHelper.logCursor(cursor);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			AchievementDB ach = cursorToAchievement(cursor);
		      if (ach == null) {
		    	  continue;
		      }
		      achs.add(ach);
			cursor.moveToNext();
		}
		return achs;
	}

	// Получение списка достижений игрока
	public ArrayList<AchievementDB> getAchievementsByPlayer(int pID) {
		ArrayList<AchievementDB> achs = new ArrayList<AchievementDB>();
		String sqlQuery = "select A._id, A." + ACHIEVE_TITLE + ", A." + ACHIEVE_INFO
				+ " from " + TABLE_ACHIEVEMENTS + " as A"
				+ " inner join " + TABLE_AP + " as AP"
				+ " on A._id = AP." + AP_AID 
				+ " where AP." + AP_PID + " = ?";
		Cursor cursor = database.rawQuery(sqlQuery, new String[] { String.valueOf(pID) });
		//DBHelper.logCursor(cursor);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			AchievementDB ach = cursorToAchievement(cursor);
		      if (ach == null) {
		    	  continue;
		      }
		      achs.add(ach);
			cursor.moveToNext();
		}
		return achs;
	}

	// Получение достижения
	public AchievementDB getAchievementByID(int aID) {
		String sqlQuery = "select * "
				+ " from " + TABLE_ACHIEVEMENTS
				+ " where " + ACHIEVE_ID + " = ?";
		Cursor cursor = database.rawQuery(sqlQuery, new String[] { String.valueOf(aID) });
		//DBHelper.logCursor(cursor);
		cursor.moveToFirst();
		return cursorToAchievement(cursor);
	}
		
	// Установка достижения
	public boolean setAchievement(int pID, int aID) {
		String sqlQuery = "select * "
				+ " from " + TABLE_AP
				+ " where " + AP_AID + " = ? and " + AP_PID + " = ? ";
		Cursor cursor = database.rawQuery(sqlQuery, new String[] { String.valueOf(aID), String.valueOf(pID) });
		if (cursor.getCount() != 0) {
			return false;
		}
		
		ContentValues cv = new ContentValues();
		cv.put(AP_PID, pID);
		cv.put(AP_AID, aID);
		cv.put(AP_TIME, "? time ?");
		
		return (database.insert(TABLE_AP, null, cv) != -1);
	}
	// Курсор в игру по достижение
	private AchievementDB cursorToAchievement(Cursor cursor) {
		AchievementDB ach = new AchievementDB();
		if (cursor.getCount() == 0) {
			return null;
		}
		ach._id = cursor.getInt(0);
		ach.title = cursor.getString(1);
		ach.info = cursor.getString(2);
		return ach;
	}

	// Получение списка типов
	public ArrayList<TypeDB> getAllTypes() {
		ArrayList<TypeDB> achs = new ArrayList<TypeDB>();
		Cursor cursor = database.query(TABLE_TYPE, null, null, null, null, null, null);
		//DBHelper.logCursor(cursor);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			TypeDB ach = cursorToType(cursor);
		      if (ach == null) {
		    	  continue;
		      }
		      achs.add(ach);
			cursor.moveToNext();
		}
		return achs;
	}
	
	// Курсор в тип
	private TypeDB cursorToType(Cursor cursor) {
		TypeDB type = new TypeDB();
		if (cursor.getCount() == 0) {
			return null;
		}
		int i = 0;
		type._id = cursor.getInt(i++);
		type.name = cursor.getString(i++);
		type.speed = cursor.getFloat(i++);
		type.jump = cursor.getFloat(i++);
		type.health = cursor.getFloat(i++);
		type.expReq = cursor.getInt(i++);
		return type;
	}
	
	// Выбор типа игрока
	public boolean chooseType(int pID, int typeID) {
		boolean res = false;
		database.beginTransaction();
		try {
			PlayerDB player = getPlayer(pID);
			int currentExp = player.exp;
			String sqlQuery = "select count(*)" + " from " + TABLE_TYPE
					+ " where " + TYPE_EXPREQ + ">=? and" + " " + TYPE_ID
					+ "=?";
			Cursor cursor = database.rawQuery(
					sqlQuery,
					new String[] { String.valueOf(currentExp),
							String.valueOf(typeID) });
			cursor.moveToFirst();
			if (cursor.getCount() != 0) {
				player.type = typeID;
				updateOrAddPlayer(player);
				res = true;
			}
			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
		}
		return res;
	}

	// Получение списка аватар
	public ArrayList<AvatarDB> getAllAvatars() {
		ArrayList<AvatarDB> avas = new ArrayList<AvatarDB>();
		Cursor cursor = database.query(TABLE_AVATAR, null, null, null, null, null, null);
		//DBHelper.logCursor(cursor);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			AvatarDB ava = cursorToAvatar(cursor);
		      if (ava == null) {
		    	  continue;
		      }
		      avas.add(ava);
			cursor.moveToNext();
		}
		return avas;
	}
	
	// Курсор в тип
	private AvatarDB cursorToAvatar(Cursor cursor) {
		AvatarDB ava = new AvatarDB();
		if (cursor.getCount() == 0) {
			return null;
		}
		int i = 0;
		ava._id = cursor.getInt(i++);
		ava.name = cursor.getString(i++);
		ava.moneyReq = cursor.getInt(i++);
		return ava;
	}
	
	// Выбор аватара игрока
	public boolean chooseAvatar(int pID, int avaID) {
		boolean res = false;
		database.beginTransaction();
	    try {
			PlayerDB player = getPlayer(pID);
			int currentExp = player.exp;
			String sqlQuery = "select count(*)"
					+ " from " + TABLE_AVATAR
					+ " where " + AVATAR_MONEYREQ + ">=? and"
					+ " " + AVATAR_ID + "=?";
			Cursor cursor = database.rawQuery(sqlQuery, new String[] { String.valueOf(currentExp), String.valueOf(avaID) });
			cursor.moveToFirst();
			if (cursor.getCount() != 0) {
				player.avID = avaID;
				updateOrAddPlayer(player);
				res = true;
			}
			database.setTransactionSuccessful();
	    } finally {
	    	database.endTransaction();
	    }
	    return res;
	}
}
