package com.xorboo.hatfortress.game;

import org.andengine.entity.Entity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;

import com.xorboo.hatfortress.MainActivity;
import com.xorboo.hatfortress.utils.GameConstants;

public class GameObject extends Entity implements GameConstants {
	public static final byte TYPE_PLAYER = 0;
	public static final byte TYPE_HEALTH_SMALL = TYPE_PLAYER + 1;
	
	public Sprite mSprite;

	public GameObject(final int posX, final int posY, final ITextureRegion region) {
		int x = posX * (int) TILE_SIZE;
		int y = posY * (int) TILE_SIZE;
		mSprite = new Sprite(x, y, region,
				MainActivity._main.getVertexBufferObjectManager());
		attachChild(mSprite);
	}

	// Получение координат объекта
	public float getX() {
		return mSprite.getX();
	}
	
	public float getY() {
		return mSprite.getY();
	}
	
	// Установка координат объекта
	public void setPosition(float x, float y) {
		mSprite.setPosition(x, y);
	}
	public void setX(float x) {
		mSprite.setX(x);
	}
	public void setY(float y) {
		mSprite.setY(y);
	}
	
	protected Sprite getSprite() {
		return mSprite;
	}
}
