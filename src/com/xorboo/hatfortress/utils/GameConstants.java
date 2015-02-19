package com.xorboo.hatfortress.utils;

import java.util.Random;

public interface GameConstants {
	// ===========================================================
	// Final Fields
	// ===========================================================

	public static int GRAVITY = 50;
	
	public static final int LIVES = 3;
	
	public static final int FPS = 60;

	public static final int GAME_WIDTH = 800;
	public static final int GAME_WIDTH_HALF = GAME_WIDTH / 2;
	public static final int GAME_HEIGHT = 480;
	public static final int GAME_HEIGHT_HALF = GAME_HEIGHT / 2;

	public static final short PROTOCOL_VERSION = 1;
	
	public static final float TILE_SIZE = 32;
	public static final int TILE_NONE = 0;
	public static final int TILE_BRICK = 1;
	public static final int TILE_PLAYER_SPAWN = 4;
	public static final int TILE_HEALTH_BIG = 5;
	public static final int TILE_HEALTH_MEDIUM = 6;
	public static final int TILE_HEALTH_SMALL = 7;
	public static final int TILE_ARMOR_BIG = 8;
	public static final int TILE_ARMOR_MEDIUM = 9;

	/* The categories. */
	public static final short CATEGORYBIT_WALL = 1;
	public static final short CATEGORYBIT_PLAYER = 2;
	public static final short CATEGORYBIT_ITEM = 4;
	public static final short CATEGORYBIT_BULLET = 8;

	/* And what should collide with what. */
	public static final short MASKBITS_WALL = CATEGORYBIT_WALL + CATEGORYBIT_PLAYER + CATEGORYBIT_BULLET;
	public static final short MASKBITS_PLAYER = CATEGORYBIT_WALL + CATEGORYBIT_ITEM + CATEGORYBIT_BULLET;
	public static final short MASKBITS_ITEM = CATEGORYBIT_PLAYER;
	public static final short MASKBITS_BULLET = CATEGORYBIT_WALL + CATEGORYBIT_PLAYER;
	
	// Камера
	public final int CAMERA_WIDTH = GAME_WIDTH;
	public final int CAMERA_HEIGHT = GAME_HEIGHT;
	
	public final int WEAPON_PISTOL = 0;
	public final int WEAPON_SHOTGUN = WEAPON_PISTOL + 1;
	public final int WEAPON_SNIPER = WEAPON_SHOTGUN + 1;
	public final int WEAPON_ROCKET = WEAPON_SNIPER + 1;
	public final int WEAPON_GRENADE = WEAPON_ROCKET + 1;
	public final int WEAPON_SMG = WEAPON_GRENADE + 1;
	public final int WEAPON_MAX = WEAPON_SMG + 1;
	
	public final float DELAY_PISTOL = 0.3f;
	public final float DELAY_SHOTGUN = 0.8f;
	public final float DELAY_SNIPER = 1.5f;
	public final float DELAY_ROCKET = 1.5f;
	public final float DELAY_GRENADE = 2.5f;
	public final float DELAY_SMG = 0.10f;
	
	public final int BULLET_BULLET = 0;
	public final int BULLET_ROCKET = 1;
	public final int BULLET_GRENADE = 2;
	public final int PLAYER = 3;
	public final int HEALTH_BIG = 4;
	public final int HEALTH_MEDIUM = 5;
	public final int HEALTH_SMALL = 6;
	public final int ARMOR_PACK_BIG = 7;
	public final int ARMOR_PACK = 8;
	public final int WALL = 9;
	
	public final int PLAYER_BULLET = 0;
	public final int PLAYER_RANGE = 1;
	public final int PLAYER_H_A = 2;
	public final int WALL_BULLET = 3;
	public final int WALL_RANGE = 4;
	public final int UNKNOWN = 7;
	
	
	public static final Random rand = new Random();
}
