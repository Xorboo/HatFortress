package com.xorboo.hatfortress.game;

import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.opengl.texture.region.ITextureRegion;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.xorboo.hatfortress.GameServer;
import com.xorboo.hatfortress.loaders.GfxAssets;

public class BulletObject extends GameObject{
	private static final FixtureDef BULLET_FIXTUREDEF = PhysicsFactory.createFixtureDef(1, 0, 0, 
			true, CATEGORYBIT_BULLET, MASKBITS_BULLET, (short)0);
	private static final FixtureDef BULLET_R_FIXTUREDEF = PhysicsFactory.createFixtureDef(1, 0, 0, 
			true, CATEGORYBIT_BULLET, MASKBITS_BULLET, (short)0);
	private static final FixtureDef BULLET_G_FIXTUREDEF = PhysicsFactory.createFixtureDef(1, 0.3f, 0, 
			true, CATEGORYBIT_BULLET, MASKBITS_BULLET, (short)0);
	// Скорость
	public int weaponID; 
	public Body body;
	public int playerID;
	public int ID;
	public int power;
	public int range;
	public int speed;
	public float speedX;
	public float speedY;
	
	public BulletObject(ITextureRegion region, PlayerObject player, float dirX, float dirY, int _id, int pID){
		super((int)player.body.getPosition().x, (int)player.body.getPosition().y, region);
		ID = _id;
		weaponID = player.weaponID;
		playerID = pID;
		
		// Настройки для пули
		float mass = 0.05f;
		speed = 60;
		range = 1;
		power = 5;		
		LootDataObject userData = new LootDataObject(ID, BULLET_BULLET);
		FixtureDef fd = BULLET_FIXTUREDEF;
		BodyType bt = BodyType.DynamicBody;
		
		switch (weaponID){
		case WEAPON_PISTOL:
			power = 10;			
			break;
		case WEAPON_SHOTGUN:
			power = 6;
			break;
		case WEAPON_SNIPER:
			power = 60;
			break;
		case WEAPON_SMG:
			power = 8;
			break;
		case WEAPON_ROCKET:
			userData.Type = BULLET_ROCKET;
			fd = BULLET_R_FIXTUREDEF;
			speed = 35;
			range = 200;
			power = 70;
			mass = 10;
			break;
		case WEAPON_GRENADE:
			userData.Type = BULLET_GRENADE;
			fd = BULLET_G_FIXTUREDEF;
			speed = 25;
			range = 200;
			power = 80;
			mass = 1;
			break;
		default:
			break;
		}
		//fd.isSensor = true;	
		body = PhysicsFactory.createBoxBody(GameServer.mPhysicsWorld,
				(int)(player.body.getPosition().x * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT), 
				(int)(player.body.getPosition().y * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT), 
				2, 2, bt, fd);
		body.setLinearVelocity(dirX * speed, dirY * speed);
		MassData massInfo = body.getMassData();
		massInfo.mass = mass;
		body.setMassData(massInfo);
		body.setUserData(userData);
		body.setBullet(false);
		//body.setBullet(true);
	}
	
	public BulletObject(float posX, float posY, float dirX, float dirY, int weaponType){
		super((int)posX, (int)posY, (weaponType == WEAPON_GRENADE) ? GfxAssets.trGrenade : 
			(weaponType == WEAPON_ROCKET) ? GfxAssets.trRocket : GfxAssets.trBullet);
		weaponID = weaponType;
		speed = 80;
		switch (weaponID){
		case WEAPON_PISTOL:
			break;
		case WEAPON_SHOTGUN:
			break;
		case WEAPON_SNIPER:
			break;
		case WEAPON_ROCKET:
			speed = 40;
			break;
		case WEAPON_GRENADE:
			speed = 20;
			break;
		default:
			break;
		}
		speedX = speed * dirX;
		speedY = speed * dirY;
	}
}
