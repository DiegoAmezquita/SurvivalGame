package com.example.survivalgame.game;

import org.andengine.engine.Engine;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXLoader;
import org.andengine.extension.tmx.TMXProperties;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.extension.tmx.TMXTileProperty;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.extension.tmx.TMXLoader.ITMXTilePropertiesListener;
import org.andengine.extension.tmx.util.exception.TMXLoadException;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import android.app.Activity;

import com.example.survivalgame.TextureGameManager;

public class MapGame {

	CollisionManager collisionManager;
	TextureGameManager textureManager;
	VertexBufferObjectManager vbom;

	private TMXTiledMap mTMXTiledMap;

	public MapGame(String path, Activity activity, Engine engine, VertexBufferObjectManager vbom) {
		collisionManager = CollisionManager.getInstance();
		textureManager = TextureGameManager.getInstance();
		this.vbom = vbom;
		loadMap(path, activity, engine);
	}

	public void loadMap(String path, Activity activity, Engine engine) {
		try {

			final TMXLoader tmxLoader = new TMXLoader(activity.getAssets(), engine.getTextureManager(), TextureOptions.BILINEAR_PREMULTIPLYALPHA, vbom,
					new ITMXTilePropertiesListener() {

						@Override
						public void onTMXTileWithPropertiesCreated(TMXTiledMap pTMXTiledMap, TMXLayer pTMXLayer, TMXTile pTMXTile, TMXProperties<TMXTileProperty> pTMXTileProperties) {

							if (!pTMXTileProperties.isEmpty()) {
								String nameProperty = pTMXTileProperties.get(0).getName();
								textureManager.setTexture(nameProperty, pTMXTile.getTextureRegion());
								Sprite item = new Sprite(pTMXTile.getTileX(), pTMXTile.getTileY(), pTMXTile.getTextureRegion(), vbom);
								item.setUserData(nameProperty);
								collisionManager.addItem(item);
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

	public int getWidth() {
		return mTMXTiledMap.getTMXLayers().get(0).getWidth();
	}

	public int getHeight() {
		return mTMXTiledMap.getTMXLayers().get(0).getHeight();
	}

}
