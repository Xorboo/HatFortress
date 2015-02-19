package com.xorboo.hatfortress.loaders;

import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;

import com.xorboo.hatfortress.MainActivity;

/**
 * Класс для загрузки текстур в атласы
 */
public class FontAssets {

	public static Font utFont;
	public static Font droidFont;
	public static Font hpFont;

	static MainActivity main;
	
	// Загрузка шрифтов
	public static void loadFonts() {
		main = MainActivity._main;
		
		FontFactory.setAssetBasePath("font/");
		final ITexture fontTexture = new BitmapTextureAtlas(main.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
		//utFont = FontFactory.create(main.getFontManager(), main.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32);
		utFont = FontFactory.createFromAsset(main.getFontManager(), fontTexture, main.getAssets(), 
				"UnrealTournament.ttf", 48, true, android.graphics.Color.RED);
		utFont.load();

		final ITexture fontTexture3 = new BitmapTextureAtlas(main.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
		droidFont = FontFactory.createFromAsset(main.getFontManager(), fontTexture3, main.getAssets(), 
				"Droid.ttf", 24, true, android.graphics.Color.BLACK);
		droidFont.load();
		

		final ITexture fontTexture2 = new BitmapTextureAtlas(main.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
		hpFont = FontFactory.createFromAsset(main.getFontManager(), fontTexture2, main.getAssets(), 
				"Droid2.ttf", 44, true, android.graphics.Color.argb(255, 126, 111, 90));
		hpFont.load();
	}
}
	