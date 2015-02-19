package com.xorboo.hatfortress.utils.database;

import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.util.HorizontalAlign;

import com.xorboo.hatfortress.MainActivity;
import com.xorboo.hatfortress.loaders.FontAssets;


public class PlayerInfoDB {
	public int _id = 0;
	public String name = "";
	public int exp = 0;
	public int avID = 0;
	public String type = "unknown";
	public int money = 0;
	public int deaths = 0;
	public int jumps = 0;
	public int shoots = 0;
	public int wins = 0;
	public int games = 0;
	
	public PlayerInfoDB() {
		
	}
	
	public Text getText() {
		//Sprite player = new Sprite(0, 0, GfxAssets.trGround, MainActivity._main.getVertexBufferObjectManager());
		String text = "Игрок: " + name + "    (" + _id + ")" +
				"\nОпыт: " + exp + "\nДенег: " + money +
				"\nСыграно игр: " + games + ";  \nПобед: " + wins + "; \t\tСмертей: " + deaths +
				"\ngТип игрока: " + type + "; \t\tАватар: " + avID +
				"\nПрыжков совершено: " + jumps + "\nВыстрелов совершено: " + shoots;
		final Text label = new Text(0, 0, FontAssets.droidFont, text, new TextOptions(HorizontalAlign.LEFT), 
				MainActivity._main.getVertexBufferObjectManager());
		//player.attachChild(label);
		return label;
	}
}
