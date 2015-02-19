package com.xorboo.hatfortress;

import java.util.ArrayList;

import org.andengine.entity.Entity;
import org.andengine.entity.scene.CameraScene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.color.Color;

import com.xorboo.hatfortress.MainState.GameState;
import com.xorboo.hatfortress.loaders.GfxAssets;
import com.xorboo.hatfortress.loaders.MfxAssets;
import com.xorboo.hatfortress.utils.Toaster;
import com.xorboo.hatfortress.utils.database.AvatarDB;
import com.xorboo.hatfortress.utils.database.PlayerDB;

/**
 * Сцена типов игрока
 */
public class AvatarDBScene extends CameraScene {
	private static SpriteBackground background;
	private ArrayList<AvatarDB> avas;
	private Entity group = new Entity(0, 0);
	
	public AvatarDBScene() {
		super(MainActivity.camera);
		
		createBackground();
		createButtons();	
		
		drawAvatars();
	}
	
	// Включить сцену
	public void show() {
		setVisible(true);
		setIgnoreUpdate(false);
		
		MfxAssets.playMusic();
		drawAvatars();
	}
	// Выключить сцену
	public void hide() {
		setVisible(false);
		setIgnoreUpdate(true);
	}

	// Создание обоев
	private void createBackground()
	{
		background = new SpriteBackground(
				new Sprite(0, 0, GfxAssets.trMenuBackgroundWhite, MainActivity._main.getVertexBufferObjectManager()));
		setBackground(background);
	}
	
	// Создание кнопок
	private void createButtons() 
	{
		// Кнопка выхода
		Sprite btnExit = new Sprite(30, MainActivity.camera.getHeight() - 130, GfxAssets.trMenuButtonPrev,
				MainActivity._main.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				MainState.showScene(GameState.MENU_PLAYER);
				return true;
			}
		};
		attachChild(btnExit);
		registerTouchArea(btnExit);
	}
	
	// Отображаем игроков
	private void drawAvatars() {
		detachChild(group);
		
		group = new Entity(0, 0);
		attachChild(group);

		PlayerDB player = MainActivity.dbWorker.getPlayer(PlayersScene.chosenID);
		if (player == null) {
			return;
		}
		avas = MainActivity.dbWorker.getAllAvatars();
		
		final int dy = 80;
		final float step = 90;
		
		for (int i = 0; i < avas.size(); i++) {
			AvatarDB ava = avas.get(i);
			Text text = ava.getText();
			text.setPosition(50, step * i + dy);
			group.attachChild(text);
			Sprite avSprite = new Sprite(530, step * i + dy, GfxAssets.trAvatars.get(ava._id), 
					MainActivity._main.getVertexBufferObjectManager());
			avSprite.setScaleCenter(0, 0);
			avSprite.setScale(1.4f);
			group.attachChild(avSprite);
			if (player.money >= ava.moneyReq) {
				Sprite sprite = new Sprite(730, step * i + dy, 
						player.avID == ava._id ? GfxAssets.trOK : GfxAssets.trInfo, 
								MainActivity._main.getVertexBufferObjectManager()) {
					@Override
					public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
							float pTouchAreaLocalX, float pTouchAreaLocalY) {
						AvatarDBScene.this.chooseAvatar((int)((this.getY() - dy) / step));
						return true;
					}
				};
				if (player != null) {
					if (player.avID == ava._id) {
						sprite.setColor(Color.RED);
					}
				}
					
				group.attachChild(sprite);
				registerTouchArea(sprite);	
			}
		}
	}
	
	// Выбор типа игрока
	private void chooseAvatar(int index) {
		if (MainActivity.dbWorker.chooseAvatar(PlayersScene.chosenID, avas.get(index)._id)) {
			Toaster.send("Аватара \"" + avas.get(index).name + "\" выбрана");
			MainState.showScene(GameState.MENU_PLAYER);
		}
		else {
			Toaster.send("Невозможно выбрать данную аватару");
		}
	}
}
