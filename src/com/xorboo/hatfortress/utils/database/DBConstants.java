package com.xorboo.hatfortress.utils.database;

public interface DBConstants {

	// Игроки
	public static final String TABLE_PLAYERS = "tblPlayers";
	public static final String PLAYERS_ID = "_id";
	public static final String PLAYERS_NAME = "name";
	public static final String PLAYERS_PASSWORD = "password";
	public static final String PLAYERS_EXP = "experience";
	public static final String PLAYERS_MONEY = "money";
	public static final String PLAYERS_TYPE = "type";
	public static final String PLAYERS_AVATAR = "avatar";
	
	// Игры
	public static final String TABLE_GAMES = "tblGames";
	public static final String GAMES_ID = "_id";
	public static final String GAMES_TIME = "time";
	public static final String GAMES_MAP = "map";
	
	// Карта
	public static final String TABLE_MAPS = "tblMaps";
	public static final String MAPS_ID = "_id";
	public static final String MAPS_NAME = "name";
	public static final String MAPS_GRAVITY = "gravity";
	public static final String MAPS_SPEED = "speed";
	public static final String MAPS_JUMP = "jump";
	
	// Игроки в игру
	public static final String TABLE_GP = "tblGamePlayers";
	public static final String GP_ID = "_id";
	public static final String GP_PLAYER_ID = "pID";
	public static final String GP_GAME_ID = "gID";
	public static final String GP_KILLS = "kills";
	public static final String GP_DEATHS = "deaths";
	public static final String GP_JUMPS = "jumps";
	public static final String GP_SHOOTS = "shoots";
	public static final String GP_WINNER = "winner";

	// Тип игрока
	public static final String TABLE_TYPE = "tblTypes";
	public static final String TYPE_ID = "_id";
	public static final String TYPE_NAME = "name";
	public static final String TYPE_SPEED = "speed";
	public static final String TYPE_JUMP = "jump";
	public static final String TYPE_HEALTH = "health";
	public static final String TYPE_EXPREQ = "expRequired";

	// Аватар
	public static final String TABLE_AVATAR = "tblAvatar";
	public static final String AVATAR_ID = "_id";
	public static final String AVATAR_NAME = "name";
	public static final String AVATAR_MONEYREQ = "moneyRequired";
	
	// Достижения
	public static final String TABLE_ACHIEVEMENTS = "tblAchievements";
	public static final String ACHIEVE_ID = "_id";
	public static final String ACHIEVE_TITLE = "title";
	public static final String ACHIEVE_INFO = "info";
	
	// Достижения игрока
	public static final String TABLE_AP = "tblAchievementsPlayers";
	public static final String AP_ID = "_id";
	public static final String AP_PID = "pID";
	public static final String AP_AID = "aID";
	public static final String AP_TIME = "time";
	
}
