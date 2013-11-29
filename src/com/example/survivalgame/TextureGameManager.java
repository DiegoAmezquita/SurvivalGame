package com.example.survivalgame;

import java.util.HashMap;

import org.andengine.opengl.texture.region.ITextureRegion;

public class TextureGameManager {

	private HashMap<String, ITextureRegion> textures;
	public static TextureGameManager instance;

	private TextureGameManager() {
		textures = new HashMap<String, ITextureRegion>();
	}

	public void setTexture(String name, ITextureRegion iTextureRegion) {
		textures.put(name, iTextureRegion);
	}

	public ITextureRegion getTexture(String name) {
		return textures.get(name);
	}

	public static TextureGameManager getInstance() {
		if (instance == null) {
			instance = new TextureGameManager();
		}
		return instance;
	}

}
