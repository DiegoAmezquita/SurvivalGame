package com.example.survivalgame.game;

import java.util.ArrayList;

import org.andengine.engine.Engine;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.shape.Shape;
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
import org.andengine.util.adt.color.Color;
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

	ArrayList<Sprite> items;
	ArrayList<Rectangle> obstacles;
	ArrayList<Rectangle> boxes;

	PointF playerSpawnEntrance;
	PointF exit;
	Sprite buildingFront;

	PhysicsWorld mWorld;

	String userData;

	float pX, pY;

	FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);

	public Building(String path, float pX, float pY, VertexBufferObjectManager vbom, AssetManager assetManager, Engine engine, PhysicsWorld mWorld) {
		this.vbom = vbom;
		this.assetManager = assetManager;
		this.engine = engine;
		this.path = path;
		this.mWorld = mWorld;
		collisionManager = CollisionManager.getInstance();
		textureManager = TextureGameManager.getInstance();

		items = new ArrayList<Sprite>();
		obstacles = new ArrayList<Rectangle>();
		boxes = new ArrayList<Rectangle>();
		this.pX = pX;
		this.pY = pY;

		loadTMX();
		// createBuildingFront();
		createWalls();
		loadObstacles();
		loadItems();
		loadBoxes();

	}

	public void loadTMX() {

		try {

			final TMXLoader tmxLoader = new TMXLoader(assetManager, engine.getTextureManager(), TextureOptions.BILINEAR_PREMULTIPLYALPHA, vbom, new ITMXTilePropertiesListener() {

				@Override
				public void onTMXTileWithPropertiesCreated(TMXTiledMap pTMXTiledMap, TMXLayer pTMXLayer, TMXTile pTMXTile, TMXProperties<TMXTileProperty> pTMXTileProperties) {

					for (int i = 0; i < pTMXTileProperties.size(); i++) {
						Log.v("PROPERTIES", pTMXTileProperties.get(i).getName() + " - " + pTMXTileProperties.get(i).getValue());
					}

					if (!pTMXTileProperties.isEmpty()) {
						String nameProperty = pTMXTileProperties.get(0).getName();
						if (nameProperty.equals("PlayerSpawnBuilding")) {
							playerSpawnEntrance = new PointF(pTMXLayer.getTileX(pTMXTile.getTileColumn()) + pTMXTile.getTileWidth() / 2,
									(pTMXLayer.getTileY(pTMXTile.getTileRow()) + pTMXTile.getTileHeight() / 2));
						} else if (nameProperty.equals("BuildingExit")) {
							Log.v("BUILDING", "SALIDA");
							int posX = pTMXLayer.getTileX(pTMXTile.getTileColumn()) + pTMXTile.getTileWidth() / 2;
							int posY = pTMXLayer.getTileY(pTMXTile.getTileRow()) + pTMXTile.getTileHeight() / 2;

							exit = new PointF(posX, posY);

						} else if (pTMXTileProperties.containsTMXProperty("obstacle", "true")) {

							float posX = pTMXLayer.getTileX(pTMXTile.getTileColumn()) + pTMXTile.getTileWidth() / 2;
							float posY = pTMXLayer.getTileY(pTMXTile.getTileRow()) + pTMXTile.getTileHeight() / 2;

							Rectangle rect = new Rectangle(posX, posY, 16, 16, vbom);
							obstacles.add(rect);

						} else if (pTMXLayer.getName().equals("Items")) {
							textureManager.setTexture(nameProperty, pTMXTile.getTextureRegion());
							Sprite item = new Sprite(pTMXLayer.getTileX(pTMXTile.getTileColumn()) + pTMXTile.getTileWidth() / 2,
									(pTMXLayer.getTileY(pTMXTile.getTileRow()) + pTMXTile.getTileHeight() / 2), pTMXTile.getTextureRegion(), vbom);
							item.setUserData(pTMXTileProperties.get(0).getName());
							items.add(item);
						}

						if (pTMXTileProperties.containsTMXProperty("box", "true")) {
							Log.v("BOXES", "CAJA");
							float posX = pTMXLayer.getTileX(pTMXTile.getTileColumn()) + pTMXTile.getTileWidth() / 2;
							float posY = pTMXLayer.getTileY(pTMXTile.getTileRow()) + pTMXTile.getTileHeight() / 2;

							Rectangle rect = new Rectangle(posX, posY, 17, 17, vbom);
							boxes.add(rect);

						}

					}
				}
			});
			this.mTMXTiledMap = tmxLoader.loadFromAsset(path);

		} catch (final TMXLoadException e) {
			Debug.e(e);
		}
		Rectangle exitDoor = new Rectangle(pX - getWidth() / 2 + exit.x, pY - getWidth() / 2 + exit.y - 8, 16, 8, vbom);

		Body exitBody = PhysicsFactory.createBoxBody(mWorld, exitDoor, BodyType.StaticBody, wallFixtureDef);
		exitBody.setUserData("exit");
		mWorld.registerPhysicsConnector(new PhysicsConnector(exitDoor, exitBody, true, true));

		for (int i = 0; i < getNumberLayers(); i++) {
			getLayer(i).setX(pX);
			getLayer(i).setY(pY);
		}
	}

	public void loadItems() {
		for (int i = 0; i < items.size(); i++) {
			getLayer(0).attachChild(items.get(i));
		}
	}

	public void loadObstacles() {
		for (int i = 0; i < obstacles.size(); i++) {
			Rectangle rect = obstacles.get(i);
			rect.setX((pX - getWidth() / 2) + rect.getX());
			rect.setY((pY - getHeight() / 2) + rect.getY());
			mWorld.registerPhysicsConnector(new PhysicsConnector(rect, PhysicsFactory.createBoxBody(mWorld, rect, BodyType.StaticBody, wallFixtureDef), false, false));
		}
	}

	public void loadBoxes() {
		Log.v("BOXES","CAPA "+getLayer(getNumberLayers()-1).getX());
		
		
		for (int i = 0; i < boxes.size(); i++) {
			Log.v("BOXES",boxes.get(i).getX()+" - "+boxes.get(i).getY());
			Rectangle rec = boxes.get(i);
			rec.setColor(Color.WHITE);
			getLayer(0).attachChild(rec);
		}
	}

	public void createWalls() {
		TMXLayer layer = getLayer(0);

		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);

		Rectangle topLimit = new Rectangle(layer.getX(), layer.getY() + layer.getHeight() / 2, layer.getWidth(), 2, vbom);
		Rectangle bottomLimit = new Rectangle(layer.getX(), layer.getY() - layer.getHeight() / 2, layer.getWidth(), 2, vbom);
		Rectangle leftLimit = new Rectangle(layer.getX() - layer.getWidth() / 2, layer.getY(), 2, layer.getHeight(), vbom);
		Rectangle rightLimit = new Rectangle(layer.getX() + layer.getWidth() / 2, layer.getY(), 2, layer.getHeight(), vbom);

		mWorld.registerPhysicsConnector(new PhysicsConnector(topLimit, PhysicsFactory.createBoxBody(mWorld, topLimit, BodyType.StaticBody, wallFixtureDef), true, true));
		mWorld.registerPhysicsConnector(new PhysicsConnector(bottomLimit, PhysicsFactory.createBoxBody(mWorld, bottomLimit, BodyType.StaticBody, wallFixtureDef), true, true));
		mWorld.registerPhysicsConnector(new PhysicsConnector(leftLimit, PhysicsFactory.createBoxBody(mWorld, leftLimit, BodyType.StaticBody, wallFixtureDef), true, true));
		mWorld.registerPhysicsConnector(new PhysicsConnector(rightLimit, PhysicsFactory.createBoxBody(mWorld, rightLimit, BodyType.StaticBody, wallFixtureDef), true, true));

		// layer.attachChild(topLimit);
		// layer.attachChild(bottomLimit);
		// layer.attachChild(leftLimit);
		// layer.attachChild(rightLimit);
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
