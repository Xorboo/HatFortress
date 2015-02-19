package com.xorboo.hatfortress.utils.database;

import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.util.HorizontalAlign;

import com.xorboo.hatfortress.MainActivity;
import com.xorboo.hatfortress.loaders.FontAssets;

public class GamePlayerDB {
	public int _id = 0;
	public int pID = 0;
	public int gID = 0;
	public int kills = 0;
	public int deaths = 0;
	public int jumps = 0;
	public int shoots = 0;
	public int winner = 0;
	
	public GamePlayerDB() {
	}
	
	public Text getText() {
		//Sprite player = new Sprite(0, 0, GfxAssets.trGround, MainActivity._main.getVertexBufferObjectManager());
		String text = _id + ") id: " + pID + 
				"\n Убийств: " + kills + "; \tСмертей: " + deaths + ";\n Прыжков: " + jumps + "; \tВыстрелов: " + shoots;
		final Text label = new Text(0, 0, FontAssets.droidFont, text, new TextOptions(HorizontalAlign.LEFT), 
				MainActivity._main.getVertexBufferObjectManager());
		//player.attachChild(label);
		return label;
	}
	
}
