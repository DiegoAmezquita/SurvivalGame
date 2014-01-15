package com.example.survivalgame;


import org.andengine.engine.Engine;
import org.andengine.engine.camera.SmoothCamera;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import android.graphics.Color;

/**
 * @author Mateusz Mysliwiec
 * @author www.matim-dev.com
 * @version 1.0
 */
public class ResourcesManager {
	// ---------------------------------------------
	// VARIABLES
	// ---------------------------------------------

	private static final ResourcesManager INSTANCE = new ResourcesManager();

	public Engine engine;
	public GameActivity activity;
	public SmoothCamera camera;
	public VertexBufferObjectManager vbom;

	// ---------------------------------------------
	// TEXTURES & TEXTURE REGIONS
	// ---------------------------------------------

	public ITextureRegion splash_region;
	private BitmapTextureAtlas splashTextureAtlas;

	public ITextureRegion menu_background_region;
	public ITextureRegion play_region;
	public ITextureRegion options_region;

	public ITextureRegion light_region;

	public ITexture mBuildingTexture;

	private BuildableBitmapTextureAtlas menuTextureAtlas;

	// ---------------------------------------------
	// GAME TEXTURES & TEXTURE REGIONS
	// ---------------------------------------------

	// Game Texture
	public BuildableBitmapTextureAtlas gameTextureAtlas;

	public ITiledTextureRegion player_region;

	public ITiledTextureRegion enemies_region;

	public ITiledTextureRegion explosion_region;

	public ITextureRegion mOnScreenControlBaseTextureRegion;
	public ITextureRegion mOnScreenControlKnobTextureRegion;

	public ITextureRegion mOnScreenButton;

	
	public ITextureRegion mBuildingFront1;
	public ITextureRegion mBuildingFront2;
	public ITextureRegion mBuildingFront3;
	public ITextureRegion mBuildingFront4;
	public ITextureRegion mBuildingFront5;
	public ITextureRegion mBuildingFront6;
	public ITextureRegion mBuildingFront7;
	public ITextureRegion mBuildingFront8;
	public ITextureRegion mBuildingFront9;
	
	
	public ITextureRegion mCar;

	public ITextureRegion mFirstAid;

	// ---------------------------------------------
	// FONT
	// ---------------------------------------------

	public Font font;

	// ---------------------------------------------
	// CLASS LOGIC
	// ---------------------------------------------

	public void loadMenuResources() {
		loadMenuGraphics();
		loadMenuAudio();
		loadMenuFonts();
	}

	public void loadGameResources() {
		loadGameGraphics();
		loadGameFonts();
		loadGameAudio();
	}

	public void unloadMenuTextures() {
		menuTextureAtlas.unload();
	}

	public void loadMenuTextures() {
		menuTextureAtlas.load();
	}

	private void loadMenuGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");
		menuTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
		menu_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "menu_background.png");
		play_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "play.png");
		options_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "options.png");

		menuTextureAtlas.addEmptyTextureAtlasSource(0, 0, 1024, 1024);

		try {
			this.menuTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.menuTextureAtlas.load();
		} catch (final TextureAtlasBuilderException e) {
			Debug.e(e);
		}
	}

	private void loadMenuFonts() {
		FontFactory.setAssetBasePath("font/");
		final ITexture mainFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 512, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

		font = FontFactory.createStrokeFromAsset(activity.getFontManager(), mainFontTexture, activity.getAssets(), "font.ttf", 50, true, Color.WHITE, 2, Color.BLACK);
		font.load();
	}

	private void loadMenuAudio() {

	}

	private void loadGameGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");
		gameTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 2048, 2048, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

		gameTextureAtlas.clearTextureAtlasSources();
		gameTextureAtlas.addEmptyTextureAtlasSource(0, 0, 1024, 1024);

		player_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "player.png", 4, 4);

		enemies_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "zombies.png", 12, 8);

		explosion_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "explosion.png", 3, 4);

		mOnScreenControlBaseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "onscreen_control_base.png");
		mOnScreenControlKnobTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "onscreen_control_knob.png");

		mOnScreenButton = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "button.png");
		mBuildingFront1 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "building.png");
		mBuildingFront2 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "building2.png");
		mBuildingFront3 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "building3.png");
		mBuildingFront4 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "building4.png");
		mBuildingFront5 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "building5.png");
		mBuildingFront6 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "building6.png");
		mBuildingFront7 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "building7.png");
		mBuildingFront8 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "building8.png");
		mBuildingFront9 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "building9.png");
		
		mCar = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "car.png");
		

		light_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "light.png");

//		try {
//			mBuildingTexture = new AssetBitmapTexture(activity.getTextureManager(), activity.getAssets(), "gfx/game/building.png", TextureOptions.BILINEAR_PREMULTIPLYALPHA);
//			mBuildingFront = TextureRegionFactory.extractFromTexture(mBuildingTexture);
//			light_region = TextureRegionFactory.extractFromTexture(this.lightTest);
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}

		mFirstAid = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "first_aid.png");
		//

		try {
			this.gameTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(10, 10, 10));
			this.gameTextureAtlas.load();
		} catch (final TextureAtlasBuilderException e) {
			Debug.e(e);
		}

	}

	private void loadGameFonts() {

	}

	private void loadGameAudio() {

	}

	public void unloadGameTextures() {
		// TODO (Since we did not create any textures for game scene yet)
	}

	public void loadSplashScreen() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		splashTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 1024, 512, TextureOptions.BILINEAR);
		splash_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas, activity, "splash.png", 0, 0);
		splashTextureAtlas.load();
	}

	public void unloadSplashScreen() {
		splashTextureAtlas.unload();
		splash_region = null;
	}

	/**
	 * @param engine
	 * @param activity
	 * @param camera
	 * @param vbom
	 * <br>
	 * <br>
	 *            We use this method at beginning of game loading, to prepare
	 *            Resources Manager properly, setting all needed parameters, so
	 *            we can latter access them from different classes (eg. scenes)
	 */
	public static void prepareManager(Engine engine, GameActivity activity, SmoothCamera camera, VertexBufferObjectManager vbom) {
		getInstance().engine = engine;
		getInstance().activity = activity;
		getInstance().camera = camera;
		getInstance().vbom = vbom;
	}

	// ---------------------------------------------
	// GETTERS AND SETTERS
	// ---------------------------------------------

	public static ResourcesManager getInstance() {
		return INSTANCE;
	}
}