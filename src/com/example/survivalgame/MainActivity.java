package com.example.survivalgame;

import org.andengine.engine.FixedStepEngine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.shader.ShaderProgramManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.color.Color;

import com.example.survivalgame.util.SpotLight;

import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.Log;

public class MainActivity extends SimpleBaseGameActivity implements IOnSceneTouchListener {

	private Camera mCamera;
	private EngineOptions mEngineOptions;

	public static int mWidth = 0;
	public static int mHeight = 0;
	public static float mCentreX = 0;
	public static float mCentreY = 0;
	public static float mRadius = 0;
	private static Font mFont;
	
	public static float borderValue = 1f;

	@Override
	public EngineOptions onCreateEngineOptions() {

		final DisplayMetrics displayMetrics = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		mWidth = displayMetrics.widthPixels;
		mHeight = displayMetrics.heightPixels;
		mCentreX = mWidth / 2;
		mCentreY = mHeight / 2;

		Log.w("" + mWidth, "" + mHeight);
		
		Log.w("" + mCentreX, "" + mCentreY);

		mCamera = new Camera(0, 0, mWidth, mHeight);
		mEngineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(mWidth, mHeight), mCamera);
		mEngineOptions.getRenderOptions().setDithering(true);

		return mEngineOptions;
	}

	@Override
	public org.andengine.engine.Engine onCreateEngine(EngineOptions pEngineOptions) {
		return new FixedStepEngine(mEngineOptions, 60);
	}

	@Override
	protected void onCreateResources() {

		this.getEngine().registerUpdateHandler(new FPSLogger());

		ShaderProgramManager shaderProgramManager = this.getShaderProgramManager();
		shaderProgramManager.loadShaderProgram(SpotLight.getInstance());

		mFont = FontFactory.create(this.getFontManager(), this.getTextureManager(), 1024, 1024, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), (int) (mHeight / 5), true,
				Color.WHITE.getABGRPackedInt());
		mFont.load();

	}

	@Override
	protected Scene onCreateScene() {

		Scene scene = new Scene();
		scene.setBackground(new Background(1.0f, 0.0f, 0.0f));

		mRadius = mWidth / 8;

		Text text = new Text(mWidth / 2, mHeight / 2, mFont, "SPOTLIGHT", this.getVertexBufferObjectManager());
		scene.attachChild(text);

		Rectangle rec = new Rectangle(0, 0, 1794, 1080, this.getVertexBufferObjectManager());
		rec.setColor(Color.BLACK);
		rec.setShaderProgram(SpotLight.getInstance());
		scene.attachChild(rec);
		

		scene.setOnSceneTouchListener(this);
		return scene;

	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {

		mCentreX = pSceneTouchEvent.getX();
		mCentreY = mHeight-pSceneTouchEvent.getY();
		
		
		Log.v("GAME",mCentreY+"");

		return true;
	}

}