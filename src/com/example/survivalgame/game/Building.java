package com.example.survivalgame.game;

import org.andengine.engine.Engine;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXLoader;
import org.andengine.extension.tmx.TMXLoader.ITMXTilePropertiesListener;
import org.andengine.extension.tmx.TMXProperties;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.extension.tmx.TMXTileProperty;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.extension.tmx.util.exception.TMXLoadException;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.color.Color;
import org.andengine.util.debug.Debug;

import android.content.res.AssetManager;

import com.example.survivalgame.TextureGameManager;

public class Building {

	VertexBufferObjectManager vbom;

	private TMXTiledMap mTMXTiledMap;
	
	CollisionManager collisionManager;
	TextureGameManager textureManager;
	
	AssetManager assetManager;
	Engine engine;
	String path;
	
	
	Sprite buildingFront;
	

	public Building(String path, VertexBufferObjectManager vbom, AssetManager assetManager, Engine engine) {
		this.vbom = vbom;
		this.assetManager = assetManager;
		this.engine = engine;
		this.path = path;
		collisionManager = CollisionManager.getInstance();
		textureManager = TextureGameManager.getInstance();
		loadTMX();
//		createBuildingFront();
		createWalls();
	}

	public void loadTMX() {

		try {

			final TMXLoader tmxLoader = new TMXLoader(assetManager, engine.getTextureManager(), TextureOptions.BILINEAR_PREMULTIPLYALPHA, vbom, new ITMXTilePropertiesListener() {

				@Override
				public void onTMXTileWithPropertiesCreated(TMXTiledMap pTMXTiledMap, TMXLayer pTMXLayer, TMXTile pTMXTile, TMXProperties<TMXTileProperty> pTMXTileProperties) {

					if (!pTMXTileProperties.isEmpty()) {
						String nameProperty = pTMXTileProperties.get(0).getName();
						if (nameProperty.equals("teleport")) {
							Sprite teleport = new Sprite(pTMXTile.getTileColumn(), pTMXTile.getTileRow(), pTMXTile.getTextureRegion(), vbom);
							String value = pTMXTileProperties.get(0).getValue();
							textureManager.setTexture(nameProperty, pTMXTile.getTextureRegion());
							teleport.setUserData(nameProperty + "," + value.split("-")[0] + "," + value.split("-")[1]);
							collisionManager.addDoor(teleport);
						} else {
							textureManager.setTexture(nameProperty, pTMXTile.getTextureRegion());
							Sprite item = new Sprite(pTMXTile.getTileColumn(), pTMXTile.getTileRow(), pTMXTile.getTextureRegion(), vbom);
							item.setUserData(nameProperty);
							collisionManager.addItem(item);
						}
					}
				}
			});
			this.mTMXTiledMap = tmxLoader.loadFromAsset(path);

		} catch (final TMXLoadException e) {
			Debug.e(e);
		}
	}
	
	
	public void createBuildingFront(ITextureRegion textureFront){
		buildingFront = new Sprite(0, 0,textureFront, vbom);
		collisionManager.addObstacle(buildingFront);
		
		
		Rectangle door = new Rectangle(buildingFront.getWidth()/2,+10, 32, 32, vbom);
		door.setColor(Color.RED);
		float centerX = getLayer(0).getX();
		float centerY = getLayer(0).getY();
		door.setUserData("teleport,"+centerX+","+centerY);
		door.setVisible(false);
		collisionManager.addDoor(door);
		buildingFront.attachChild(door);
		
		Rectangle exit = new Rectangle(getLayer(0).getWidth()/2, 32, 32, 32, vbom);
		exit.setColor(Color.RED);
		exit.setUserData("exit");
		collisionManager.addDoor(exit);
		getLayer(0).attachChild(exit);
		
		
	}
	
	public void createWalls(){
		TMXLayer layer = getLayer(0);
		Rectangle topLimit = new Rectangle(0+layer.getWidth()/2, layer.getHeight()-30, layer.getWidth(), 2, vbom);
		Rectangle bottomLimit = new Rectangle(0+layer.getWidth()/2, 0, layer.getWidth(), 2, vbom);
		Rectangle leftLimit = new Rectangle(0, 0+layer.getHeight()/2, 2, layer.getHeight(), vbom);
		Rectangle rightLimit = new Rectangle(layer.getWidth(), 0+layer.getHeight()/2, 2, layer.getHeight(), vbom);

		collisionManager.addObstacle(topLimit);
		collisionManager.addObstacle(bottomLimit);
		collisionManager.addObstacle(leftLimit);
		collisionManager.addObstacle(rightLimit);
		
		layer.attachChild(topLimit);
		layer.attachChild(bottomLimit);
		layer.attachChild(leftLimit);
		layer.attachChild(rightLimit);
	}
	
	public TMXLayer getLayer(int index) {
		if (index < mTMXTiledMap.getTMXLayers().size()) {
			return mTMXTiledMap.getTMXLayers().get(index);
		} else {
			return null;
		}
	}

	public float getWidth() {
		return mTMXTiledMap.getTMXLayers().get(0).getWidth();
	}

	public float getHeight() {
		return mTMXTiledMap.getTMXLayers().get(0).getHeight();
	}

}
