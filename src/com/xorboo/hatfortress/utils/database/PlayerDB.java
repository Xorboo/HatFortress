package com.xorboo.hatfortress.utils.database;

import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.util.HorizontalAlign;

import com.xorboo.hatfortress.MainActivity;
import com.xorboo.hatfortress.loaders.FontAssets;


public class PlayerDB {
	public int _id = 0;
	public String name = "";
	public String password = "";
	public int exp = 0;
	public int money = 0;
	public int type = 0;
	public int avID = 0;
	
	public PlayerDB() {
		
	}
	
	public Text getText() {
		//Sprite player = new Sprite(0, 0, GfxAssets.trGround, MainActivity._main.getVertexBufferObjectManager());
		String text = "id: " + _id + "\nИмя: " + name + 
				"\nОпыт: " + exp + ";\t денег: " + money;
		final Text label = new Text(0, 0, FontAssets.droidFont, text, new TextOptions(HorizontalAlign.LEFT), 
				MainActivity._main.getVertexBufferObjectManager());
		//player.attachChild(label);
		return label;
	}
}
