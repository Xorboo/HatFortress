package com.xorboo.hatfortress.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;

public class Utils implements GameConstants {
	private final static String TAG = "UTILS";
	
	public static float getBulletDelay(int weaponID) {
		float bulletTime = 0;
		switch (weaponID) {
			case WEAPON_PISTOL:
				bulletTime = DELAY_PISTOL;
				break;
			case WEAPON_SHOTGUN:
				bulletTime = DELAY_SHOTGUN;
				break;
			case WEAPON_SNIPER:
				bulletTime = DELAY_SNIPER;
				break;
			case WEAPON_ROCKET:
				bulletTime = DELAY_ROCKET;
				break;
			case WEAPON_GRENADE:
				bulletTime = DELAY_GRENADE;
				break;
			case WEAPON_SMG:
				bulletTime = DELAY_SMG;
				break;
			default:
				bulletTime = 1;
				Log.e(TAG, "ERROR: spawn bullets, weaponType: " + weaponID);
				break;
		}
		return bulletTime;
	}
	
	public static String getWeaponName(int weaponID) {
		String res = "";
		switch (weaponID) {
		case WEAPON_PISTOL:
			res = "Пистолет";
			break;
		case WEAPON_SHOTGUN:
			res = "Дробовик";
			break;
		case WEAPON_SNIPER:
			res = "Снайперка";
			break;
		case WEAPON_ROCKET:
			res = "Ракета";
			break;
		case WEAPON_GRENADE:
			res = "Граната";
			break;
		case WEAPON_SMG:
			res = "Пулемет";
			break;
		default:
			res = "unknown";
			break;
	}
		return res;
	}
	
	// Получить _id игрока из текущей даты
	public static int getUniqueID() {
		SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmmss");
		String currentDateAndTime = sdf.format(new Date());
		return Integer.parseInt(currentDateAndTime);
	}

	// Получить Время
	public static String getTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		String currentDateAndTime = sdf.format(new Date());
		return currentDateAndTime;
	}
}
