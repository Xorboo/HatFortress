package com.xorboo.hatfortress.loaders;

import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.util.debug.Debug;

import android.util.SparseArray;

import com.xorboo.hatfortress.MainActivity;

/**
 * Класс для загрузки текстур в атласы
 */
public class GfxAssets {
	
	public static BitmapTextureAtlas atlasPlayer;
	public static ITextureRegion trPlayer;
	public static ITextureRegion trEnemy;

	
	// Текстуры
	public static ITextureRegion trSplash;
	public static ITextureRegion trMenuBackground;
	public static ITextureRegion trMenuBackgroundWhite;

	public static ITextureRegion trMenuButtonNext;
	public static ITextureRegion trMenuButtonPrev;
	public static ITextureRegion trMenuButtonStartGame;
	public static ITextureRegion trMenuButtonWifiHost;
	public static ITextureRegion trMenuButtonWifiClient;
	public static ITextureRegion trMenuButtonPlayers;
	public static ITextureRegion trMenuButtonPlayer;
	public static ITextureRegion trMenuButtonGames;
	public static ITextureRegion trMenuButtonAdd;
	public static ITextureRegion trMenuButtonAch;
	public static SparseArray<ITextureRegion> trAvatars = new SparseArray<ITextureRegion>();
	
	public static ITextureRegion trHudCross;
	public static ITextureRegion trHudHeart;
	
	public static ITextureRegion trJoystickBody;
	public static ITextureRegion trJoystickKnob;
	
	public static BitmapTextureAtlas atlasGround;
	public static ITextureRegion trGround;
	public static ITextureRegion trDelete;
	public static ITextureRegion trGoals;
	public static ITextureRegion trInfo;
	public static ITextureRegion trMarker;
	public static ITextureRegion trOK;
	public static ITextureRegion trGame;
	
	public static BitmapTextureAtlas atlasBullets;
	public static ITextureRegion trBullet;
	public static ITextureRegion trGrenade;
	public static ITextureRegion trRocket;
	
	public static BuildableBitmapTextureAtlas animation;
	public static TiledTextureRegion waitAnim;

	public static ITextureRegion trWeaponButtonPistol;
	public static ITextureRegion trWeaponButtonSniper;

	public static ITextureRegion trWeaponButtonSmg;
	public static ITextureRegion trWeaponButtonShotgun;
	public static ITextureRegion trWeaponButtonRpg;
	public static ITextureRegion trWeaponButtonGrenade;

	public static ITextureRegion trWeaponButtonSmgSelected;
	public static ITextureRegion trWeaponButtonShotgunSelected;
	public static ITextureRegion trWeaponButtonRpgSelected;
	public static ITextureRegion trWeaponButtonGrenadeSelected;
	
	// Атласы
	public static BitmapTextureAtlas atlasSplash;
	public static BitmapTextureAtlas atlasManuBackground;
	public static BitmapTextureAtlas atlasMenuButtons;
	public static BitmapTextureAtlas atlasJoystick;
	public static BitmapTextureAtlas atlasWeaponButtons;

	static MainActivity main;
	
	// Установка параметров и загрузка сплеш-скрина
	public static void preloadGFX() {
		main = MainActivity._main;
		
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		loadSplash();
	}
	
	// Загрузка остальных текстур
	public static void loadGFX() {
		loadMenuBackground();
		loadButtons();
		loadJoystick();
		loadGround();
		loadAnimation();
		loadBullets();
		//loadInputBox();
		
		atlasPlayer = new BitmapTextureAtlas(main.getTextureManager(), 256, 256, TextureOptions.DEFAULT);
		trPlayer = 
				BitmapTextureAtlasTextureRegionFactory.createFromAsset(
						atlasPlayer,
						main,
						"player.png", 
						0, 0
				);

		trEnemy = 
				BitmapTextureAtlasTextureRegionFactory.createFromAsset(
						atlasPlayer,
						main,
						"enemy.png", 
						0, 128
				);
		atlasPlayer.load();	
		
	}
	
	private static void loadGround(){
		atlasGround = new BitmapTextureAtlas(main.getTextureManager(), 512, 512, TextureOptions.DEFAULT);
		trGround = 
				BitmapTextureAtlasTextureRegionFactory.createFromAsset(
						atlasGround,
						main,
						"tiles/ground.png", 
						0, 0
				);
		trDelete = 
				BitmapTextureAtlasTextureRegionFactory.createFromAsset(
						atlasGround,
						main,
						"buttons/delete.png", 
						55, 0
				);
		trGoals = 
				BitmapTextureAtlasTextureRegionFactory.createFromAsset(
						atlasGround,
						main,
						"buttons/goals.png", 
						110, 0
				);
		trInfo = 
				BitmapTextureAtlasTextureRegionFactory.createFromAsset(
						atlasGround,
						main,
						"buttons/info.png", 
						165, 0
				);
		trMarker = 
				BitmapTextureAtlasTextureRegionFactory.createFromAsset(
						atlasGround,
						main,
						"buttons/marker.png", 
						220, 0
				);
		trOK = 
				BitmapTextureAtlasTextureRegionFactory.createFromAsset(
						atlasGround,
						main,
						"buttons/ok.png", 
						275, 0
				);
		trGame = 
				BitmapTextureAtlasTextureRegionFactory.createFromAsset(
						atlasGround,
						main,
						"buttons/game.png", 
						335, 0
				);
		atlasGround.load();	
	}
	
	private static void loadBullets()
	{
		atlasBullets = new BitmapTextureAtlas(main.getTextureManager(), 256, 256, TextureOptions.REPEATING_BILINEAR_PREMULTIPLYALPHA);
		
		trBullet = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				atlasBullets,
				main,
				"bullets/Bullet.png", 
				0, 0
		);
		trGrenade = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				atlasBullets,
				main,
				"bullets/Grenade.png", 
				10, 10
		);
		trRocket = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				atlasBullets,
				main,
				"bullets/rocket.png", 
				100, 100
		);
		atlasBullets.load();	
	}
	
	private static void loadAnimation(){
		animation = new BuildableBitmapTextureAtlas(main.getTextureManager(), 1024, 1024, TextureOptions.NEAREST);
		
		waitAnim = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(animation, main, "waiting.png", 2, 3);
		try {
			animation.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 1));
			animation.load();
		} catch (TextureAtlasBuilderException e) {
			Debug.e(e);
		}
	}
	
	private static void loadSplash()
	{
		atlasSplash = new BitmapTextureAtlas(main.getTextureManager(), 1024, 1024, TextureOptions.REPEATING_BILINEAR_PREMULTIPLYALPHA);
		trSplash = 
				BitmapTextureAtlasTextureRegionFactory.createFromAsset(
						atlasSplash,
						main,
						"splash.png", 
						0, 0
				);
		atlasSplash.load();
	}
	
	private static void loadMenuBackground()
	{
		atlasManuBackground = new BitmapTextureAtlas(main.getTextureManager(), 1024, 1024, TextureOptions.REPEATING_BILINEAR_PREMULTIPLYALPHA);
		trMenuBackground = 
				BitmapTextureAtlasTextureRegionFactory.createFromAsset(
						atlasManuBackground,
						main,
						"menuBackground.png", 
						0, 0
				);
		trMenuBackgroundWhite = 
				BitmapTextureAtlasTextureRegionFactory.createFromAsset(
						atlasManuBackground,
						main,
						"menuBackgroundWhite.png", 
						0, 500
				);
		atlasManuBackground.load();
	}
		
	private static void loadButtons()
	{
		loadMenuButtons();
		loadWeaponButtons();
	}
	
	private static void loadMenuButtons()
	{
		atlasMenuButtons = new BitmapTextureAtlas(main.getTextureManager(), 1024, 1024, TextureOptions.REPEATING_BILINEAR_PREMULTIPLYALPHA);
		
		// 250x50
		int btnHeight = 65;
		int btnWidth = 255;
		trMenuButtonStartGame = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				atlasMenuButtons,
				main,
				"buttons/btnStartGame.png", 
				0, 0
		);
		trMenuButtonWifiHost = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				atlasMenuButtons,
				main,
				"buttons/btnCreateAP.png", 
				0, btnHeight + 1
		);
		trMenuButtonWifiClient = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				atlasMenuButtons,
				main,
				"buttons/btnConnectToAP.png", 
				0, 2 * (btnHeight + 1)
		);
		trMenuButtonPlayers = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				atlasMenuButtons,
				main,
				"buttons/btnPlayers.png", 
				0, 3 * (btnHeight + 1)
		);
		trMenuButtonPlayer = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				atlasMenuButtons,
				main,
				"buttons/btnPlayer.png", 
				0, 4 * (btnHeight + 1)
		);
		trMenuButtonGames = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				atlasMenuButtons,
				main,
				"buttons/btnGames.png", 
				0, 5 * (btnHeight + 1)
		);
		trMenuButtonAdd = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				atlasMenuButtons,
				main,
				"buttons/btnAdd.png", 
				0, 6 * (btnHeight + 1)
		);
		trMenuButtonAch = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				atlasMenuButtons,
				main,
				"buttons/btnAch.png", 
				0, 7 * (btnHeight + 1)
		);
		
		ITextureRegion ava;
		ava = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				atlasMenuButtons,
				main,
				"avatars/main.png", 
				0, 8 * (btnHeight + 1)
		);
		trAvatars.append(1, ava);
		ava = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				atlasMenuButtons,
				main,
				"avatars/fighter.png", 
				60, 8 * (btnHeight + 1)
		);
		trAvatars.append(2, ava);
		ava = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				atlasMenuButtons,
				main,
				"avatars/ninja.png", 
				120, 8 * (btnHeight + 1)
		);
		trAvatars.append(3, ava);
		
		// ~50x50
		trMenuButtonNext = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				atlasMenuButtons,
				main,
				"buttons/btnNext.png", 
				300, 0
		);
		trMenuButtonPrev = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				atlasMenuButtons,
				main,
				"buttons/btnPrev.png", 
				300, 125
		);

		trHudCross = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				atlasMenuButtons,
				main,
				"cross.png", 
				300, 250
		);
		trHudHeart = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				atlasMenuButtons,
				main,
				"heart.png", 
				300, 360
		);
		atlasMenuButtons.load();
	}
	
	private static void loadWeaponButtons()
	{
		atlasWeaponButtons = new BitmapTextureAtlas(main.getTextureManager(), 1024, 1024, TextureOptions.REPEATING_BILINEAR_PREMULTIPLYALPHA);
		
		// 105x68
		int btnHeight = 80;
		trWeaponButtonShotgun = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				atlasWeaponButtons,
				main,
				"buttons/weapons/shotgun.png", 
				0, 0
		);
		trWeaponButtonSmg = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				atlasWeaponButtons,
				main,
				"buttons/weapons/smg.png", 
				0, btnHeight + 1
		);
		trWeaponButtonRpg = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				atlasWeaponButtons,
				main,
				"buttons/weapons/rocket.png", 
				0, 2 * btnHeight + 1
		);
		trWeaponButtonGrenade = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				atlasWeaponButtons,
				main,
				"buttons/weapons/grenade.png", 
				0, 3 * btnHeight + 1
		);
		
		// Selected
		trWeaponButtonShotgunSelected = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				atlasWeaponButtons,
				main,
				"buttons/weapons/shotgun_selected.png", 
				0, 4 * btnHeight + 1
		);
		trWeaponButtonSmgSelected = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				atlasWeaponButtons,
				main,
				"buttons/weapons/smg_selected.png", 
				0, 5 * btnHeight + 1
		);
		trWeaponButtonRpgSelected = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				atlasWeaponButtons,
				main,
				"buttons/weapons/rocket_selected.png", 
				0, 6 * btnHeight + 1
		);
		trWeaponButtonGrenadeSelected = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				atlasWeaponButtons,
				main,
				"buttons/weapons/grenade_selected.png", 
				0, 7 * btnHeight + 1
		);
		atlasWeaponButtons.load();
	}

	private static void loadJoystick()
	{
		atlasJoystick = new BitmapTextureAtlas(main.getTextureManager(), 256, 256, TextureOptions.REPEATING_BILINEAR_PREMULTIPLYALPHA);
		trJoystickBody = 
				BitmapTextureAtlasTextureRegionFactory.createFromAsset(
						atlasJoystick,
						main,
						"joystick/base.png", 
						0, 0
				);
		trJoystickKnob = 
				BitmapTextureAtlasTextureRegionFactory.createFromAsset(
						atlasJoystick,
						main,
						"joystick/knob.png", 
						128, 0
				);
		atlasJoystick.load();
	}
}
