package com.xorboo.hatfortress;

import java.util.ArrayList;

import org.andengine.entity.scene.CameraScene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.HorizontalAlign;

import com.xorboo.hatfortress.MainState.GameState;
import com.xorboo.hatfortress.loaders.FontAssets;
import com.xorboo.hatfortress.loaders.GfxAssets;
import com.xorboo.hatfortress.loaders.MfxAssets;

public class HelpScene3 extends CameraScene{
	private static SpriteBackground background;
	public HelpScene3() {
		super(MainActivity.camera);
		
		createBackground();
		createButtons();
		createText();
	}
	
	private void createText() {
		String txt = "��������� ������� ����� � ������� ���� �����������. \n" +
					 "����� ��������� ������� ������ � ����� �� ��������� \n" +
					 "����������� � �������. ������ ��������� �������� � \n" +
					 "��������� �����������. ������ ������ ������ ���������\n" +
					 "������� ������. ������� � ������ ������� ���� ��" +
					 "�������, ������� � �������� - ������ �� ����.";
		Text labelText = new Text(50, 30, FontAssets.droidFont, txt, new TextOptions(HorizontalAlign.LEFT), 
				MainActivity._main.getVertexBufferObjectManager());
		attachChild(labelText);
	}
	
	// �������� �����
	public void show() {
		setVisible(true);
		setIgnoreUpdate(false);
		
		MfxAssets.playMusic();
	}
	// ��������� �����
	public void hide() {
		setVisible(false);
		setIgnoreUpdate(true);
	}

	
	private void createButtons() {
		// ������ ������
		Sprite btnExit = new Sprite(30, MainActivity.camera.getHeight() - 150, GfxAssets.trMenuButtonPrev,
				MainActivity._main.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				MainState.showScene(GameState.HELP2);
				return true;
			}
		};
		attachChild(btnExit);
		registerTouchArea(btnExit);
		


		// ������ ������
		Sprite btnNext = new Sprite(MainActivity.camera.getWidth() - 150, MainActivity.camera.getHeight() - 150, GfxAssets.trMenuButtonNext,
				MainActivity._main.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				MainState.showScene(GameState.HELP4);
				return true;
			}
		};
		attachChild(btnNext);
		registerTouchArea(btnNext);
		
		
	}
	private void createBackground() {
		background = new SpriteBackground(
				new Sprite(0, 0, GfxAssets.trMenuBackgroundWhite, MainActivity._main.getVertexBufferObjectManager()));
		setBackground(background);
	}	
}
