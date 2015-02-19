package com.xorboo.hatfortress.utils.database;

import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.util.HorizontalAlign;

import com.xorboo.hatfortress.MainActivity;
import com.xorboo.hatfortress.loaders.FontAssets;

public class TypeDB {
	public int _id = 0;
	public String name = "";
	public int expReq = 0;
	public float jump = 0;
	public float speed = 0;
	public float health = 0;
	
	public TypeDB() {
		
	}
	
	public Text getText() {
		//Sprite player = new Sprite(0, 0, GfxAssets.trGround, MainActivity._main.getVertexBufferObjectManager());
		String text = "Тип: " + name + ";   Требуется опыта: " + expReq +
				"\nПрыжок: " + jump + ";   Скорость: " + speed + ";   Здоровье: " + health;
		final Text label = new Text(0, 0, FontAssets.droidFont, text, new TextOptions(HorizontalAlign.LEFT), 
				MainActivity._main.getVertexBufferObjectManager());
		//player.attachChild(label);
		return label;
	}
}
