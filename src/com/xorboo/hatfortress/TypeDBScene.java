package com.xorboo.hatfortress;

import java.util.ArrayList;

import org.andengine.entity.Entity;
import org.andengine.entity.scene.CameraScene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.color.Color;

import com.badlogic.gdx.physics.box2d.Manifold.ManifoldType;
import com.xorboo.hatfortress.MainState.GameState;
import com.xorboo.hatfortress.loaders.GfxAssets;
import com.xorboo.hatfortress.loaders.MfxAssets;
import com.xorboo.hatfortress.utils.Toaster;
import com.xorboo.hatfortress.utils.database.AchievementDB;
import com.xorboo.hatfortress.utils.database.PlayerDB;
import com.xorboo.hatfortress.utils.database.TypeDB;

/**
 * Сцена типов игрока
 */
public class TypeDBScene extends CameraScene {
	private static SpriteBackground background;
	private ArrayList<TypeDB> types;
	private Entity group = new Entity(0, 0);
	
	public TypeDBScene() {
		super(MainActivity.camera);
		
		createBackground();
		createButtons();	
		
		drawTypes();
	}
	
	// Включить сцену
	public void show() {
		setVisible(true);
		setIgnoreUpdate(false);
		
		MfxAssets.playMusic();
		drawTypes();
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
				MainState.showScene(GameState.MENU);
				return true;
			}
		};
		attachChild(btnExit);
		registerTouchArea(btnExit);
	}
	
	// Отображаем игроков
	private void drawTypes() {
		detachChild(group);
		
		group = new Entity(0, 0);
		attachChild(group);

		PlayerDB player = MainActivity.dbWorker.getPlayer(PlayersScene.chosenID);
		types = MainActivity.dbWorker.getAllTypes();
		
		final int dy = 20;
		final float step = 90;
		
		for (int i = 0; i < types.size(); i++) {
			TypeDB type = types.get(i);
			Text text = type.getText();
			text.setPosition(50, step * i + dy);
			group.attachChild(text);
			
			if (player.exp >= type.expReq) {
				Sprite sprite = new Sprite(730, step * i + dy, 
						player != null && player.type == type._id ? GfxAssets.trOK : GfxAssets.trInfo, 
								MainActivity._main.getVertexBufferObjectManager()) {
					@Override
					public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
							float pTouchAreaLocalX, float pTouchAreaLocalY) {
						TypeDBScene.this.chooseType((int)((this.getY() - dy) / step));
						return true;
					}
				};
				if (player != null) {
					if (player.type == type._id) {
						sprite.setColor(Color.RED);
					}
				}
					
				group.attachChild(sprite);
				registerTouchArea(sprite);	
			}
		}
	}
	
	// Выбор типа игрока
	private void chooseType(int index) {
		if (MainActivity.dbWorker.chooseType(PlayersScene.chosenID, types.get(index)._id)) {
			Toaster.send("Тип \"" + types.get(index).name + "\" выбран");
			MainState.showScene(GameState.MENU_PLAYER);
		}
		else {
			Toaster.send("Невозможно выбрать данный тип");
		}
	}
}
