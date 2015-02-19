package com.xorboo.hatfortress.game;

import org.andengine.extension.multiplayer.protocol.server.connector.SocketConnectionClientConnector;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.opengl.texture.region.ITextureRegion;

import com.badlogic.gdx.physics.box2d.Body;
import com.xorboo.hatfortress.utils.database.GamePlayerDB;
import com.xorboo.hatfortress.utils.database.PlayerDB;

public class PlayerObject extends GameObject {

	public static final int HEALTH_MAX = 100;
	public static final int HEALTH_BONUS_MAX = 25;
	public static final int ARMOR_MAX = 100;
	public static final float VELOCITY_MAX = 10;

	public static final short STATUS_DEAD = 0;
	public static final short STATUS_STAND = STATUS_DEAD + 1;
	public static final short STATUS_MOVE_RIGHT = STATUS_STAND + 1;
	public static final short STATUS_MOVE_LEFT = STATUS_MOVE_RIGHT + 1;
	
	// ���� �� ��
	public PlayerDB dbInfo;
	public GamePlayerDB stats = new GamePlayerDB();
	
	// ��, �����
	public int health = HEALTH_MAX;
	public int bonusHealth = 0;
	public int armor = 0;
	// ��������
	public float velocityX = 0;
	public float velocityY = 0;
	// ���� �������� (0 - ������, ������ �������)
	public float angle = 0;
	// ��������� �� � �������
	public boolean isJumping = false;
	// ������
	public short status = STATUS_STAND;
	// IP
	public String IP = "";
	public SocketConnectionClientConnector connector = null;
	
	// ���� ��� ������
	public Body body;

	// ������, �������
	public int weaponID = -1;
	public int[] ammo = new int[WEAPON_MAX];
	public float time = 0;
	public float dirX = 0;
	public float dirY = 0;

	// �����������
	public PlayerObject(int posX, int posY, ITextureRegion region) {
		super(posX, posY, region);
		weaponID = WEAPON_PISTOL;
		setDefaultAmmo();
	}

	// ����������� ���������� ��������
	public void setDefaultAmmo() {
		ammo[WEAPON_PISTOL] = Integer.MAX_VALUE;
		ammo[WEAPON_SHOTGUN] = 500;
		ammo[WEAPON_SNIPER] = 150;
		ammo[WEAPON_ROCKET] = 100;
		ammo[WEAPON_GRENADE] = 100;
		ammo[WEAPON_SMG] = 2000;
	}
	
	// ��������� ������
	public void revive(float x, float y, float kHealth) {
		if (status == STATUS_DEAD) {
			health = (int) (HEALTH_MAX * kHealth);
			status = STATUS_STAND;
			
			body.setTransform(x / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 
					y / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, body.getAngle());
		}
	}
	// ��������� �����
	public void recieveHit(int power)
	{
		// ����������� ������� ����� ���������� �������
		float k = 0.3f;
			
		// ��������� ���������
		if (armor >= power / 2) {
			armor -= power / 2;
			int decPower = (int)(power * k); // ����������� �� ����� ����
			changeHealth(-decPower);
		}
		// �� ���������
		else {
			// ������� �������� �����
			int dArmor = armor / 2;
			armor = 0;
			power -= dArmor; // �� �������� ������ ����
			int decPower = (int)(dArmor * k); // ����������� �� ����� ����
			power += decPower;
			
			changeHealth(-power);
		}
	}

	// ���������� ��
	public void increaseHealth(int hp, boolean isBonus) {
		changeHealth(Math.abs(hp), isBonus);
	}
	
	// �������� ��������
	private void changeHealth(int hp, boolean isBonus) {
		if (status == STATUS_DEAD) {
			return;
		}
		
		if (hp > 0) {
			health += hp;
			if (health > 100 * HEALTH_MAX) {
				if (isBonus) {
					bonusHealth += health - HEALTH_MAX;
					bonusHealth = Math.min(HEALTH_BONUS_MAX, bonusHealth);
				}
				health = HEALTH_MAX;
			}
		}
		else {
			hp *= -1;
			bonusHealth -= hp;
			if (bonusHealth < 0) {
				health += bonusHealth;
				bonusHealth = 0;
				if (health <= 0) {
					status = STATUS_DEAD;
					health = 0;
				}
			}
		}
	}
	private void changeHealth(int hp) {
		changeHealth(hp, false);
	}

	// �������� �����
	public void changeArmor(int val) {
		armor = Math.max(0, Math.min(armor + val, ARMOR_MAX));
	}

	// �������� ��������
	public void setVelocity(float val) {
		velocityX = Math.max(0, Math.min(val, VELOCITY_MAX));
	}
}
