package com.example.survivalgame.game;

import java.util.ArrayList;

import org.andengine.engine.Engine;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXLoader;
import org.andengine.extension.tmx.TMXProperties;
import org.andengine.extension.tmx.TMXProperty;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.extension.tmx.TMXTileProperty;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.extension.tmx.TMXLoader.ITMXTilePropertiesListener;
import org.andengine.extension.tmx.util.exception.TMXLoadException;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import android.app.Activity;
import android.graphics.PointF;
import android.util.Log;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.example.survivalgame.TextureGameManager;
import com.example.survivalgame.util.Util;

public class MapGame {

	CollisionManager collisionManager;
	TextureGameManager textureManager;
	VertexBufferObjectManager vbom;

	private TMXTiledMap mTMXTiledMap;
	PhysicsWorld mWorld;

	public MapGame(String path, Activity activity, Engine engine, VertexBufferObjectManager vbom, PhysicsWorld mWorld) {
		collisionManager = CollisionManager.getInstance();
		textureManager = TextureGameManager.getInstance();
		this.vbom = vbom;
		this.mWorld = mWorld;
		loadMap(path, activity, engine);
	}

	public void loadMap(String path, Activity activity, Engine engine) {
		try {
			Util.enemiesSpawn = new ArrayList<PointF>();

			final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
			final TMXLoader tmxLoader = new TMXLoader(activity.getAssets(), engine.getTextureManager(), TextureOptions.BILINEAR_PREMULTIPLYALPHA, vbom, new ITMXTilePropertiesListener() {

				@Override
				public void onTMXTileWithPropertiesCreated(TMXTiledMap pTMXTiledMap, TMXLayer pTMXLayer, TMXTile pTMXTile, TMXProperties<TMXTileProperty> pTMXTileProperties) {

					if (!pTMXTileProperties.isEmpty()) {
						String nameProperty = pTMXTileProperties.get(0).getName();
						textureManager.setTexture(nameProperty, pTMXTile.getTextureRegion());
						Sprite item = new Sprite(pTMXLayer.getTileX(pTMXTile.getTileColumn()) + pTMXTile.getTileWidth() / 2,
								(pTMXLayer.getTileY(pTMXTile.getTileRow()) + pTMXTile.getTileHeight() / 2), pTMXTile.getTextureRegion(), vbom);
						item.setUserData(nameProperty);
						if (pTMXTileProperties.containsTMXProperty("tree", "true") || pTMXTileProperties.containsTMXProperty("house", "true")
								|| pTMXTileProperties.containsTMXProperty("water", "true") || pTMXTileProperties.containsTMXProperty("obstacle", "true")) {
							mWorld.registerPhysicsConnector(new PhysicsConnector(item, PhysicsFactory.createBoxBody(mWorld, item, BodyType.KinematicBody, wallFixtureDef), true, true));

							collisionManager.addObstacle(item);
						} else if (pTMXTileProperties.containsTMXProperty("door", "true")) {
							Log.v("GAME", "Door Finded");

							item.setY(item.getY() + 5);
							Body doorBody = PhysicsFactory.createBoxBody(mWorld, item, BodyType.StaticBody, wallFixtureDef);
							doorBody.setUserData("door-");

							for (TMXProperty property : pTMXTileProperties) {
								if (!property.getName().equals("door")) {
									doorBody.setUserData("door-" + property.getName());
								}
							}

							mWorld.registerPhysicsConnector(new PhysicsConnector(item, doorBody, true, true));
							collisionManager.addDoor(item);

						} else if (pTMXTileProperties.containsTMXProperty("EnemySpawn", "true")) {
							Util.enemiesSpawn.add(new PointF(item.getX(), item.getY()));
						} else if (pTMXTileProperties.containsTMXProperty("PlayerSpawn", "true")) {
							Util.playerSpawn = new PointF(item.getX(), item.getY());
						} else {
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

}
