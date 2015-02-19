package com.xorboo.hatfortress.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.batch.SpriteBatch;

import android.content.res.AssetManager;

import com.xorboo.hatfortress.MainActivity;
import com.xorboo.hatfortress.loaders.GfxAssets;
import com.xorboo.hatfortress.utils.GameConstants;
import com.xorboo.hatfortress.utils.database.MapDB;

public class MapObject implements GameConstants {	
	public static int [][] testMap =
				  {{0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
				  {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
				  {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
				  {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,1},
				  {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1},
				  {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,1},
				  {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,1},
				  {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,1},
				  {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1},
				  {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,1},
				  {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
				  {1,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
				  {1,0,0,0,0,0,1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
				  {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}};
	public static AssetManager assetManager = MainActivity._main.getAssets();
	
	public int [][] map;
	public int mapWidth = 0;
	public int mapHeight = 0;
	public MapDB mapDB = null;
	public String mapFileName = "";
	public final SpriteBatch groundBatch = new SpriteBatch(GfxAssets.atlasGround, 4000, 
			MainActivity._main.getVertexBufferObjectManager());

	public MapObject(MapDB m) {
		loadMap(m);
	}
	
	private void loadMap(MapDB m) {
		mapDB = m;
		mapFileName = "maps/" + mapDB.name + ".map";		
		// Грузим карту
		readMapFromFile(mapFileName);
	}

	// Загрузка карты из файла
	private void readMapFromFile(String fileName) {
		InputStream inputStream;
		try {
			inputStream = assetManager.open(fileName);
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String line;
			ArrayList<int[]> m = new ArrayList<int[]>();
			while ((line = reader.readLine()) != null) {
				String[] tiles = line.split(" ");
				int[] tile = new int[tiles.length];
				for (int i = 0; i < tiles.length; i++) {
					tile[i] = Integer.parseInt(tiles[i]);
				}
				m.add(tile);
			}
			reader.close();
			map = new int[m.size()][];
			for (int i = 0; i < m.size(); i++) {
				map[i] = m.get(i);
			}
			mapHeight = map.length;
			mapWidth = map[0].length;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Рисуем карту на сцену
	public void attachMapToScene(Scene scene) {
		for (int i = 0; i < mapHeight; i++) {
			for (int j = 0; j < mapWidth; j++) {
				switch (map[i][j]) {
				case 1:
					/*groundBatch.drawWithoutChecks(GfxAssets.trGround, 
							TILE_SIZE * j + TILE_SIZE / 2, TILE_SIZE * i + TILE_SIZE / 2, TILE_SIZE, TILE_SIZE,
							1f, 1f, 1f, 1f
							);*/
					Sprite tile = new Sprite((float) TILE_SIZE * j,
							(float) TILE_SIZE * i, GfxAssets.trGround,
							MainActivity._main.getVertexBufferObjectManager());
					tile.setScale(TILE_SIZE / tile.getWidth());
					groundBatch.drawWithoutChecks(tile);
					//scene.attachChild(tile);
					break;
				default:
					break;
				}
			}
		}
		groundBatch.submit();
		scene.attachChild(groundBatch);
		
		//Sprite s = new Sprite(0, 0, GfxAssets.trMap, MainActivity._main.getVertexBufferObjectManager());
		//scene.attachChild(s);
	}
	public int GetXY(int x, int y) {
		return map[x][y];
	}
}
