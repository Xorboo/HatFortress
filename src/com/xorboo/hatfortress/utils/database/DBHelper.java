package com.xorboo.hatfortress.utils.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.xorboo.hatfortress.utils.Logger;
import com.xorboo.hatfortress.utils.Utils;

public class DBHelper extends SQLiteOpenHelper implements DBConstants {
	private static final String TAG = "БД";
	

	private static final String CREATE_PLAYERS = "create table "
		      + TABLE_PLAYERS + "(" 
		      + PLAYERS_ID + " integer primary key, "
		      + PLAYERS_NAME + " text not null default \'Summoner\', "
		      + PLAYERS_PASSWORD + " text not null default 1, "
		      + PLAYERS_EXP + " integer not null default 0, "
		      + PLAYERS_MONEY + " integer not null default 10, "
		      + PLAYERS_TYPE + " integer not null default 1, "
		      + PLAYERS_AVATAR + " integer not null default 1, "
		      + "FOREIGN KEY(" + PLAYERS_TYPE + ") REFERENCES " + TABLE_TYPE + "(" + TYPE_ID + ") ON DELETE CASCADE, "
		      + "FOREIGN KEY(" + PLAYERS_AVATAR + ") REFERENCES " + TABLE_AVATAR + "(" + AVATAR_ID + ") ON DELETE CASCADE "
		      + ")";
	private static final String CREATE_MAPS = "create table "
		      + TABLE_MAPS + "(" 
		      + MAPS_ID + " integer primary key autoincrement, "
		      + MAPS_NAME + " text not null default \'testMap\', "
		      + MAPS_GRAVITY + " integer not null default 50, "
		      + MAPS_SPEED + " integer not null default 20, "
		      + MAPS_JUMP + " integer not null default 30 "
		      + ")";
	private static final String CREATE_GAMES = "create table "
		      + TABLE_GAMES + "(" 
		      + GAMES_ID + " integer primary key autoincrement, "
		      + GAMES_MAP + " text not null, "
		      + GAMES_TIME + " text not null, "
		      + "FOREIGN KEY(" + GAMES_MAP + ") REFERENCES " + TABLE_MAPS + "(" + MAPS_ID + ") ON DELETE CASCADE "
		      + ")";
	private static final String CREATE_GP = "create table "
		      + TABLE_GP + "(" 
		      + GP_ID + " integer primary key, "
		      + GP_GAME_ID + " integer not null, "
		      + GP_PLAYER_ID + " integer not null, "
		      + GP_KILLS + " integer not null, "
		      + GP_DEATHS + " integer not null default 0, "
		      + GP_JUMPS + " integer not null default 0, "
		      + GP_SHOOTS + " integer not null default 0, "
		      + GP_WINNER + " integer not null default 0, "
		      + "FOREIGN KEY(" + GP_GAME_ID + ") REFERENCES " + TABLE_GAMES + "(" + GAMES_ID + ") ON DELETE CASCADE, "
		      + "FOREIGN KEY(" + GP_PLAYER_ID + ") REFERENCES " + TABLE_PLAYERS + "(" + PLAYERS_ID + ") ON DELETE CASCADE "
		      + ")";
	
	private static final String CREATE_TYPE = "create table "
		      + TABLE_TYPE + "(" 
		      + TYPE_ID + " integer primary key, "
		      + TYPE_NAME + " text not null, "
		      + TYPE_SPEED + " real not null default 1, "
		      + TYPE_JUMP + " real not null default 1, "
		      + TYPE_HEALTH + " real not null default 0, "
		      + TYPE_EXPREQ + " integer not null default 0 "
		      + ")";
	
	private static final String CREATE_AVATAR = "create table "
		      + TABLE_AVATAR + "(" 
		      + AVATAR_ID + " integer primary key, "
		      + AVATAR_NAME + " text not null, "
		      + AVATAR_MONEYREQ + " integer not null default 0 "
		      + ")";
	
	private static final String CREATE_ACHIEVEMENTS = "create table "
		      + TABLE_ACHIEVEMENTS + "(" 
		      + ACHIEVE_ID + " integer primary key, "
			  + ACHIEVE_TITLE + " text not null, "
			  + ACHIEVE_INFO + " text not null "
		      + ")";
	
	private static final String CREATE_AP = "create table "
		      + TABLE_AP + "(" 
		      + AP_ID + " integer primary key, "
			  + AP_PID + " integer not null, "
			  + AP_AID + " integer not null, "
			  + AP_TIME + " text not null, "
			  + "FOREIGN KEY(" + AP_PID + ") REFERENCES " + TABLE_PLAYERS + "(" + PLAYERS_ID + ") ON DELETE CASCADE, "
			  + "FOREIGN KEY(" + AP_AID + ") REFERENCES " + TABLE_ACHIEVEMENTS + "(" + ACHIEVE_ID + ") ON DELETE CASCADE "
		      + ")";
		
	private static final String TRIGGER_GAME = "CREATE TRIGGER gameTime AFTER INSERT ON "
			+ TABLE_GAMES
			+ " BEGIN "
			+ " UPDATE " + TABLE_GAMES + " SET " + GAMES_TIME + " = DATETIME('NOW')  WHERE rowid = new.rowid;"
			+ " END;";
	private static final String TRIGGER_ACH = "CREATE TRIGGER achievementTime AFTER INSERT ON "
			+ TABLE_AP
			+ " BEGIN "
			+ " UPDATE " + TABLE_AP + " SET " + AP_TIME + " = DATETIME('NOW')  WHERE rowid = new.rowid;"
			+ " END;";

	private static final String DATABASE_NAME = "hatFortress.db";
	private static final int DATABASE_VERSION = 32;
	
	// Конструктор
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	// Создание БД
	@Override
	public void onCreate(SQLiteDatabase db) {
		createEverything(db);
		
		ContentValues cv = new ContentValues();
		
		// Заполняем игроков
		cv.clear();
		cv.put(PLAYERS_ID, Utils.getUniqueID());
		db.insert(TABLE_PLAYERS, null, cv);
		
		// Заполняем карты
		cv.clear();
		cv.put(MAPS_NAME, "testMap");
		db.insert(TABLE_MAPS, null, cv);

		cv.clear();
		cv.put(MAPS_NAME, "Box and Pyramid");
		db.insert(TABLE_MAPS, null, cv);
		
		// Заполняем тип
		cv.clear();
		cv.put(TYPE_NAME, "Standart");
		cv.put(TYPE_EXPREQ, "0");
		cv.put(TYPE_SPEED, "1");
		cv.put(TYPE_JUMP, "1");
		cv.put(TYPE_HEALTH, "1");
		db.insert(TABLE_TYPE, null, cv);

		cv.clear();
		cv.put(TYPE_NAME, "Scout");
		cv.put(TYPE_EXPREQ, "20");
		cv.put(TYPE_SPEED, "1.3");
		cv.put(TYPE_JUMP, "1.5");
		cv.put(TYPE_HEALTH, "0.8");
		db.insert(TABLE_TYPE, null, cv);
		
		cv.clear();
		cv.put(TYPE_NAME, "Heavy");
		cv.put(TYPE_EXPREQ, "30");
		cv.put(TYPE_SPEED, "0.8");
		cv.put(TYPE_JUMP, "0.7");
		cv.put(TYPE_HEALTH, "1.6");
		db.insert(TABLE_TYPE, null, cv);
		
		cv.clear();
		cv.put(TYPE_NAME, "Ninja");
		cv.put(TYPE_EXPREQ, "1000");
		cv.put(TYPE_SPEED, "2.5");
		cv.put(TYPE_JUMP, "1.7");
		cv.put(TYPE_HEALTH, "1.6");
		db.insert(TABLE_TYPE, null, cv);
		
		// Заполняем ачивки
		cv.clear();
		cv.put(ACHIEVE_TITLE, "Неудачник");
		cv.put(ACHIEVE_INFO, "Закончи игру с 0 убийств");
		db.insert(TABLE_ACHIEVEMENTS, null, cv);

		cv.clear();
		cv.put(ACHIEVE_TITLE, "Ноулайфер");
		cv.put(ACHIEVE_INFO, "Сыграй 100 игр");
		db.insert(TABLE_ACHIEVEMENTS, null, cv);

		cv.clear();
		cv.put(ACHIEVE_TITLE, "Про");
		cv.put(ACHIEVE_INFO, "Закончи игру с 0 смертей");
		db.insert(TABLE_ACHIEVEMENTS, null, cv);
		
		// Заполняем аватары
		cv.clear();
		cv.put(AVATAR_MONEYREQ, "0");
		cv.put(AVATAR_NAME, "Стандарт");
		db.insert(TABLE_AVATAR, null, cv);
		cv.clear();
		cv.put(AVATAR_MONEYREQ, "20");
		cv.put(AVATAR_NAME, "Боец");
		db.insert(TABLE_AVATAR, null, cv);
		cv.clear();
		cv.put(AVATAR_MONEYREQ, "1000");
		cv.put(AVATAR_NAME, "Ниндзя");
		db.insert(TABLE_AVATAR, null, cv);
	}

	// Апгрейд БД
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { 
		Log.w(TAG, "Upgrading database from version " + oldVersion + 
				" to " + newVersion + ", which will destroy all old data");
		
		dropEverything(db);
	    onCreate(db);
	}

	// Удаляем все
	void dropEverything(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYERS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MAPS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_GP);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAMES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TYPE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_AVATAR);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACHIEVEMENTS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_AP);

		db.execSQL("DROP TRIGGER IF EXISTS " + "gameTime");
		db.execSQL("DROP TRIGGER IF EXISTS " + "achievementTime");
	}
	
	// Создаем все
	void createEverything(SQLiteDatabase db) {
		db.execSQL(CREATE_PLAYERS);
		db.execSQL(CREATE_MAPS);
		db.execSQL(CREATE_GP);
		db.execSQL(CREATE_GAMES);
		db.execSQL(CREATE_TYPE);
		db.execSQL(CREATE_AVATAR);
		db.execSQL(CREATE_ACHIEVEMENTS);
		db.execSQL(CREATE_AP);
		
		db.execSQL(TRIGGER_GAME);
		db.execSQL(TRIGGER_ACH);
		
		Logger.write(TAG, "База данных создана");
	}
}
