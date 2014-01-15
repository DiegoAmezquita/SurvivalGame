package com.example.survivalgame.game;

import org.andengine.engine.Engine;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
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
import org.andengine.util.debug.Debug;

import android.content.res.AssetManager;
import android.graphics.PointF;
import android.util.Log;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.example.survivalgame.TextureGameManager;

public class Building {

	VertexBufferObjectManager vbom;

	private TMXTiledMap mTMXTiledMap;

	CollisionManager collisionManager;
	TextureGameManager textureManager;

	AssetManager assetManager;
	Engine engine;
	String path;

	PointF playerSpawnEntrance;

	Sprite buildingFront;
	
	PhysicsWorld mWorld;
	
	String userData;

	public Building(String path, VertexBufferObjectManager vbom, AssetManager assetManager, Engine engine, PhysicsWorld mWorld) {
		this.vbom = vbom;
		this.assetManager = assetManager;
		this.engine = engine;
		this.path = path;
		this.mWorld = mWorld;
		collisionManager = CollisionManager.getInstance();
		textureManager = TextureGameManager.getInstance();
		loadTMX();
		// createBuildingFront();
		createWalls();
	}

	public void loadTMX() {

		try {
			final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
			final TMXLoader tmxLoader = new TMXLoader(assetManager, engine.getTextureManager(), TextureOptions.BILINEAR_PREMULTIPLYALPHA, vbom, new ITMXTilePropertiesListener() {

				@Override
				public void onTMXTileWithPropertiesCreated(TMXTiledMap pTMXTiledMap, TMXLayer pTMXLayer, TMXTile pTMXTile, TMXProperties<TMXTileProperty> pTMXTileProperties) {

					if (!pTMXTileProperties.isEmpty()) {
						String nameProperty = pTMXTileProperties.get(0).getName();
						if (nameProperty.equals("PlayerSpawnBuilding")) {
							playerSpawnEntrance = new PointF(pTMXLayer.getTileX(pTMXTile.getTileColumn()) + pTMXTile.getTileWidth() / 2,
									(pTMXLayer.getTileY(pTMXTile.getTileRow()) + pTMXTile.getTileHeight() / 2));
						} else if (nameProperty.equals("BuildingExit")) {
							Log.v("BUILDING","SALIDA");
							Sprite item = new Sprite(pTMXLayer.getTileX(pTMXTile.getTileColumn()) + pTMXTile.getTileWidth() / 2,
									(pTMXLayer.getTileY(pTMXTile.getTileRow()) + pTMXTile.getTileHeight() / 2), pTMXTile.getTextureRegion(), vbom);
							item.setX(item.getX() - 572);
							item.setY(item.getY() - 10);
							Body exitBody = PhysicsFactory.createBoxBody(mWorld, item, BodyType.StaticBody, wallFixtureDef);
							exitBody.setUserData("exit");

							mWorld.registerPhysicsConnector(new PhysicsConnector(item, exitBody, true, true));
						}

//						if (nameProperty.equals("teleport")) {
//							Sprite teleport = new Sprite(pTMXTile.getTileColumn(), pTMXTile.getTileRow(), pTMXTile.getTextureRegion(), vbom);
//							String value = pTMXTileProperties.get(0).getValue();
//							textureManager.setTexture(nameProperty, pTMXTile.getTextureRegion());
//							teleport.setUserData(nameProperty + "," + value.split("-")[0] + "," + value.split("-")[1]);
//							// collisionManager.addDoor(teleport);
//						} else {
//							textureManager.setTexture(nameProperty, pTMXTile.getTextureRegion());
//							Sprite item = new Sprite(pTMXTile.getTileColumn(), pTMXTile.getTileRow(), pTMXTile.getTextureRegion(), vbom);
//							item.setUserData(nameProperty);
//							collisionManager.addItem(item);
//						}
					}
				}
			});
			this.mTMXTiledMap = tmxLoader.loadFromAsset(path);

		} catch (final TMXLoadException e) {
			Debug.e(e);
		}
	}

	public void createBuildingFront(ITextureRegion textureFront) {
		// buildingFront = new Sprite(0, 0,textureFront, vbom);
		// collisionManager.addObstacle(buildingFront);
		//
		//
		// Rectangle door = new Rectangle(buildingFront.getWidth()/2,+10, 32,
		// 32, vbom);
		// door.setColor(Color.RED);
		// float centerX = getLayer(0).getX();
		// float centerY = getLayer(0).getY();
		// door.setUserData("teleport,"+centerX+","+centerY);
		// door.setVisible(false);
		// collisionManager.addDoor(door);
		// buildingFront.attachChild(door);
		//
		// Rectangle exit = new Rectangle(getLayer(0).getWidth()/2, 32, 32, 32,
		// vbom);
		// exit.setColor(Color.RED);
		// exit.setUserData("exit");
		// collisionManager.addDoor(exit);
		// getLayer(0).attachChild(exit);

	}

	public void createWalls() {
		TMXLayer layer = getLayer(0);
		
		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
		
		Rectangle topLimit = new Rectangle(-500 + layer.getWidth() / 2, layer.getHeight(), layer.getWidth(), 2, vbom);
		Rectangle bottomLimit = new Rectangle(-572 + layer.getWidth() / 2, 0, layer.getWidth(), 2, vbom);
		Rectangle leftLimit = new Rectangle(-572, 0 + layer.getHeight() / 2, 2, layer.getHeight(), vbom);
		Rectangle rightLimit = new Rectangle(-572+layer.getWidth(), 0 + layer.getHeight() / 2, 2, layer.getHeight(), vbom);


		mWorld.registerPhysicsConnector(new PhysicsConnector(topLimit, PhysicsFactory.createBoxBody(mWorld, topLimit, BodyType.StaticBody, wallFixtureDef), true, true));
		mWorld.registerPhysicsConnector(new PhysicsConnector(bottomLimit, PhysicsFactory.createBoxBody(mWorld, bottomLimit, BodyType.StaticBody, wallFixtureDef), true, true));
		mWorld.registerPhysicsConnector(new PhysicsConnector(leftLimit, PhysicsFactory.createBoxBody(mWorld, leftLimit, BodyType.StaticBody, wallFixtureDef), true, true));
		mWorld.registerPhysicsConnector(new PhysicsConnector(rightLimit, PhysicsFactory.createBoxBody(mWorld, rightLimit, BodyType.StaticBody, wallFixtureDef), true, true));
		

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

	public int getNumberLayers() {
		return mTMXTiledMap.getTMXLayers().size();
	}

	public float getWidth() {
		return mTMXTiledMap.getTMXLayers().get(0).getWidth();
	}

	public float getHeight() {
		return mTMXTiledMap.getTMXLayers().get(0).getHeight();
	}

	
	public void setUserData(String userData) {
		this.userData = userData;
	}
	
	public String getUserData() {
		return userData;
	}
}
