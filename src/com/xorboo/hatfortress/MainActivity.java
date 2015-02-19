package com.xorboo.hatfortress;

import java.io.IOException;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.SmoothCamera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.opengl.util.GLState;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.debug.Debug;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.xorboo.hatfortress.MainState.GameState;
import com.xorboo.hatfortress.loaders.FontAssets;
import com.xorboo.hatfortress.loaders.GfxAssets;
import com.xorboo.hatfortress.loaders.MfxAssets;
import com.xorboo.hatfortress.messages.client.connection.ConnectionPingClientMessage;
import com.xorboo.hatfortress.utils.GameConstants;
import com.xorboo.hatfortress.utils.Logger;
import com.xorboo.hatfortress.utils.Toaster;
import com.xorboo.hatfortress.utils.Wifi;
import com.xorboo.hatfortress.utils.database.DatabaseWorker;
// TODO ���������� ������ ��� � MenuExample
public class MainActivity extends BaseGameActivity implements GameConstants {

	public static String TAG = "MainActivity";
    public static SmoothCamera camera = null;
    public static DatabaseWorker dbWorker = null;
    public static String login = null;
    public static String password = null;

    // ������� �����
    boolean gameLoaded = false;
    private Scene splashScene;
    private MainState mainScene;
    
    // ������� (��������)
    private static final float splashTime = 2.0f;
    private Sprite splash;
    
    // ������ �� ����
    public static MainActivity _main;
    
    // �������� �� �������� wifi
    public static boolean isWifiServer = false;
    
    // �� ����������� ����
    public static final int DIALOG_QUIT = 1;
    public static final int  DIALOG_ENTER_LOGIN = DIALOG_QUIT + 1;
    public static final int DIALOG_ENTER_PASSWORD = DIALOG_ENTER_LOGIN + 1;
    public static final int  DIALOG_ENTER_CREATE_LOGIN = DIALOG_ENTER_PASSWORD + 1;
    public static final int DIALOG_ENTER_CREATE_PASSWORD = DIALOG_ENTER_CREATE_LOGIN + 1;
    public static final int DIALOG_ENTER_COMMAND = DIALOG_ENTER_CREATE_PASSWORD + 1;
    public static final int DIALOG_ENTER_REMOVE_PASSWORD = DIALOG_ENTER_COMMAND + 1;
    
    // ���� ����� ����� ������
    public static GameState nextWindow = GameState.MENU;
    
    // ��������� ����� ������
	@Override
	public EngineOptions onCreateEngineOptions() {
		_main = this;
		//camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		camera = new SmoothCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT, CAMERA_WIDTH * 2, CAMERA_HEIGHT * 2, 0);
        EngineOptions engineOptions = 
        		new EngineOptions(
        				true, 
        				ScreenOrientation.LANDSCAPE_FIXED, 
        				new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), 
        				camera);
        engineOptions.getAudioOptions().setNeedsMusic(true);
        engineOptions.getTouchOptions().setNeedsMultiTouch(true);
        return engineOptions;
	}

	// �������������� �������� �������� (��� ����������� �����)
	@Override
	public void onCreateResources(
			OnCreateResourcesCallback pOnCreateResourcesCallback)
			throws Exception {
		GfxAssets.preloadGFX();
		
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	// ������ ����� ��������
	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)
			throws Exception {
		this.mEngine.registerUpdateHandler(new FPSLogger());
		this.mEngine.registerUpdateHandler(MainActivity.camera);
		initSplashScene();
        pOnCreateSceneCallback.onCreateSceneFinished(this.splashScene);		
	}
	
	// �������� ���� �����
	@Override
	public boolean onCreateOptionsMenu(final Menu pMenu) {
		pMenu.add(Menu.NONE, 0, Menu.NONE, "���� �������");
		pMenu.add(Menu.NONE, 1, Menu.NONE, "�������");
		pMenu.add(Menu.NONE, 2, Menu.NONE, "���������");
		pMenu.add(Menu.NONE, 3, Menu.NONE, "�������");
		pMenu.add(Menu.NONE, 4, Menu.NONE, "���");
		return super.onCreateOptionsMenu(pMenu);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onMenuItemSelected(final int pFeatureId, final MenuItem pItem) {
		switch (pItem.getItemId()) {
		case 0:
			if (MainState.currentState == GameState.GAME
					&& GameScene.mScene.mServerConnector != null
					&& GameScene.mScene.gameStarted) {
				try {
					final ConnectionPingClientMessage connectionPingClientMessage = new ConnectionPingClientMessage(); // TODO
																														// Pooling
					connectionPingClientMessage.setTimestamp(System
							.currentTimeMillis());
					GameScene.mScene.mServerConnector
							.sendClientMessage(connectionPingClientMessage);
				} catch (final IOException e) {
					Debug.e(e);
				}
			}
			return true;
		case 1:
			MainState.showScene(GameState.HELP1);
			return true;
		case 2:
			MainState.showScene(GameState.CREATORS);
			return true;
		case 3:
			if (MainState.currentState == GameState.GAME) {
				MainActivity.this.showDialog(DIALOG_ENTER_COMMAND);
			}
			else {
				Toaster.send("������ ������� ������� ��� ����������� � �������");
				Logger.write("����������", "������: ������� ������� ������� ��� ����������� � �������");
			}
			return true;
		case 4:
			Logger.showAll();
			return true;
		default:
				return super.onMenuItemSelected(pFeatureId, pItem);
		}
	}
	// ������ ��������
	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
		 mEngine.registerUpdateHandler(new TimerHandler(splashTime, new ITimerCallback() {
			public void onTimePassed(final TimerHandler pTimerHandler) {
				mEngine.unregisterUpdateHandler(pTimerHandler);
				loadResources();
				loadScenes();
				splash.detachSelf();
				gameLoaded();
			}
		 }));
		  
		 pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
	
	// ��������� �������� ����
	protected void gameLoaded() {
		//Logger.write("����������", "���� ��������");
		gameLoaded = true;
        mEngine.setScene(mainScene);
        Wifi.setWifiAPOFF(getContext());
        Wifi.setWifiOFF(getContext());
    }
	
	// �������� ��������
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
	     {   
			if (!gameLoaded) return true;
			if (mainScene != null) {
				mainScene.keyPressed(keyCode, event);
				return true;
			}
	     }
	     return super.onKeyDown(keyCode, event);
    }
	
	// �������� ����� ��������
	private void initSplashScene() {
		splashScene = new Scene();
		splash = new Sprite(0, 0, GfxAssets.trSplash, mEngine.getVertexBufferObjectManager()) {
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera) {
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		};

		splash.setPosition((CAMERA_WIDTH - splash.getWidth()) * 0.5f,
						   (CAMERA_HEIGHT - splash.getHeight()) * 0.5f);
		splashScene.attachChild(splash);
	}

	// �������� ��������
	public void loadResources() 
	{
		/**
		 * ��������� ������� �����
		 */
		GfxAssets.loadGFX();
		MfxAssets.loadMfx();
		FontAssets.loadFonts();
		loadDatabase();
	}
	
	// �������� ��
	private void loadDatabase() {
		dbWorker = new DatabaseWorker(getContext());
		dbWorker.open();
	}
	
	// �������� ����
	private void loadScenes()
	{
		// load your game here, you scenes
		mainScene = new MainState();
		mainScene.setBackground(new Background(0.09804f, 0.6274f, 0.8784f));
	}

	// ����������� ����
	@SuppressWarnings("deprecation")
	@Override
	protected Dialog onCreateDialog(final int pID) {
		switch (pID) {
			case DIALOG_QUIT:
				return new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setTitle("�����? ._.")
				.setCancelable(true)
				.setPositiveButton("�� :<", new OnClickListener() {
					@Override
					public void onClick(final DialogInterface pDialog, final int pWhich) {
						MainActivity.closeGame();
					}
				})
				.setNegativeButton("��� :3", null)
				.create();
				
			case DIALOG_ENTER_LOGIN:
				final EditText login = new EditText(this);
				return new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setTitle("������� ����� ...")
				.setCancelable(false)
				.setView(login)
				.setPositiveButton("������", new OnClickListener() {
					@Override
					public void onClick(final DialogInterface pDialog, final int pWhich) {
						MainActivity.login = login.getText().toString();
						MainActivity.this.showDialog(DIALOG_ENTER_PASSWORD);
					}
				})
				.setNegativeButton(android.R.string.cancel, new OnClickListener() {
					@Override
					public void onClick(final DialogInterface pDialog, final int pWhich) {
						MainActivity.login = null;
						MainActivity.password = null;
						MainState.showScene(GameState.MENU);
					}
				})
				.create();
				
			case DIALOG_ENTER_PASSWORD:
				final EditText password = new EditText(this);
				password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
				return new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setTitle("������� ������ ...")
				.setCancelable(false)
				.setView(password)
				.setPositiveButton("�����", new OnClickListener() {
					@Override
					public void onClick(final DialogInterface pDialog, final int pWhich) {
						MainActivity.password = password.getText().toString();
						switch (nextWindow) {
							case MENU:
								MainMenuScene.mScene.tryLogin(MainActivity.login, MainActivity.password);
								password.setText("");
								break;
							case MENU_PLAYERS:
								PlayersScene.mScene.playerSelect(MainActivity.password);
								break;
							default:
								Log.e(TAG, "DIALOG_ENTER_PASSWORD - ����������� ��������� ����: " + nextWindow);
								break;
						}
					}
				})
				.setNegativeButton(android.R.string.cancel, new OnClickListener() {
					@Override
					public void onClick(final DialogInterface pDialog, final int pWhich) {
						MainActivity.login = "";
						MainActivity.password = "";
						switch (nextWindow) {
							case MENU:
								MainState.showScene(GameState.MENU);
								password.setText("");
								break;
							case MENU_PLAYERS:
								PlayersScene.mScene.playerSelect("");
								break;
							default:
								Log.e(TAG, "DIALOG_ENTER_PASSWORD - ����������� ��������� ����: " + nextWindow);
								break;
						}						
						password.setText("");
					}
				})
				.create();
			case DIALOG_ENTER_CREATE_LOGIN:
				final EditText login1 = new EditText(this);
				return new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setTitle("������� ����� ...")
				.setCancelable(false)
				.setView(login1)
				.setPositiveButton("������", new OnClickListener() {
					@Override
					public void onClick(final DialogInterface pDialog, final int pWhich) {
						MainActivity.login = login1.getText().toString();
						MainActivity.this.showDialog(DIALOG_ENTER_CREATE_PASSWORD);
					}
				})
				.setNegativeButton(android.R.string.cancel, new OnClickListener() {
					@Override
					public void onClick(final DialogInterface pDialog, final int pWhich) {
						MainActivity.login = "";
						MainActivity.password = "";
						MainState.showScene(GameState.MENU);
					}
				})
				.create();
				
			case DIALOG_ENTER_CREATE_PASSWORD:
				final EditText password1 = new EditText(this);
				password1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
				return new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setTitle("������� ������ ...")
				.setCancelable(false)
				.setView(password1)
				.setPositiveButton("�������", new OnClickListener() {
					@Override
					public void onClick(final DialogInterface pDialog, final int pWhich) {
						MainActivity.password = password1.getText().toString();
						PlayersScene.mScene.addPlayer(MainActivity.login, MainActivity.password);
						password1.setText("");
					}
				})
				.setNegativeButton(android.R.string.cancel, new OnClickListener() {
					@Override
					public void onClick(final DialogInterface pDialog, final int pWhich) {
						MainActivity.login = "";
						MainActivity.password = "";
						password1.setText("");
						MainState.showScene(GameState.MENU);
					}
				})
				.create();
				
			case DIALOG_ENTER_COMMAND:
				final EditText command = new EditText(this);
				return new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setTitle("������� ������� ...")
				.setCancelable(false)
				.setView(command)
				.setPositiveButton("���������", new OnClickListener() {
					@Override
					public void onClick(final DialogInterface pDialog, final int pWhich) {
						GameScene.mScene.sendCommand(command.getText().toString());
						command.setText("");
					}
				})
				.setNegativeButton(android.R.string.cancel, new OnClickListener() {
					@Override
					public void onClick(final DialogInterface pDialog, final int pWhich) {
						command.setText("");
						MainState.showScene(GameState.MENU);
					}
				})
				.create();
				
			case DIALOG_ENTER_REMOVE_PASSWORD:
				final EditText password2 = new EditText(this);
				password2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
				return new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setTitle("������� ������ ...")
				.setCancelable(false)
				.setView(password2)
				.setPositiveButton("�������", new OnClickListener() {
					@Override
					public void onClick(final DialogInterface pDialog, final int pWhich) {
						MainActivity.password = password2.getText().toString();
						PlayersScene.mScene.deletePlayer(MainActivity.password);
						password2.setText("");
					}
				})
				.setNegativeButton(android.R.string.cancel, new OnClickListener() {
					@Override
					public void onClick(final DialogInterface pDialog, final int pWhich) {
						MainActivity.login = "";
						MainActivity.password = "";
						password2.setText("");
						MainState.showScene(GameState.MENU_PLAYERS);
					}
				})
				.create();
			default:
				return super.onCreateDialog(pID);
		}
	}
	
	public static Context getContext()
	{
		return _main.getApplicationContext();
	}
	
	public static void closeGame() {
		Wifi.setWifiAPOFF(getContext());
		Wifi.setWifiOFF(getContext());
		System.exit(0);
	}
}
