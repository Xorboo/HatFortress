package com.xorboo.hatfortress.utils.database;

import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.util.HorizontalAlign;

import com.xorboo.hatfortress.MainActivity;
import com.xorboo.hatfortress.loaders.FontAssets;

public class AvatarDB {
	public int _id = 0;
	public String name = "";
	public int moneyReq = 0;
	
	public AvatarDB() {
		
	}
	
	public Text getText() {
		//Sprite player = new Sprite(0, 0, GfxAssets.trGround, MainActivity._main.getVertexBufferObjectManager());
		String text = _id + ") Имя: " + name + ";\n\t  Требуется денег: " + moneyReq;
		final Text label = new Text(0, 0, FontAssets.droidFont, text, new TextOptions(HorizontalAlign.LEFT), 
				MainActivity._main.getVertexBufferObjectManager());
		//player.attachChild(label);
		return label;
	}
}
