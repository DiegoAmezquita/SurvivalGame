package com.example.survivalgame.game;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

import com.example.survivalgame.ResourcesManager;
import com.example.survivalgame.util.SpotLight;

public class GameHUD extends HUD {

	Camera mCamera;
	GameScene mGameScene;

	Sprite buttonC;

	public GameHUD(Camera camera, ResourcesManager resourcesManager, VertexBufferObjectManager vbom, GameScene gameScene) {
		this.mCamera = camera;
		this.mGameScene = gameScene;

		// Rectangle rec = new Rectangle(0, 0, 800, 480, vbom);
		// rec.setColor(Color.BLACK);
		// rec.setShaderProgram(SpotLight.getInstance());
		// attachChild(rec);

		Text lifeText = new Text(5, 5, resourcesManager.font, "Life: 100%", new TextOptions(HorizontalAlign.LEFT), vbom);
		lifeText.setScale(0.5f);
		lifeText.setText("Life: 100%");
		lifeText.setPosition(-50, -10);
		attachChild(lifeText);

		Text menuText = new Text(390, 470, resourcesManager.font, "Inventory", new TextOptions(HorizontalAlign.LEFT), vbom) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionDown()) {
					mCamera.setHUD(mGameScene.inventoryHud);
					mGameScene.populateInventory();
					mGameScene.movementOnScreenControl.setVisible(false);
					mGameScene.movementOnScreenControl.setIgnoreUpdate(true);
					return true;
				} else {
					return false;
				}
			}
		};

		menuText.setScale(0.7f);
		menuText.setPosition(400 - menuText.getWidth() / 2, 480 - menuText.getHeight());

		Sprite buttonA = new Sprite(630, 390, resourcesManager.mOnScreenButton, vbom) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionDown()) {
					mGameScene.actionButtonA();
					return true;
				} else if (pSceneTouchEvent.isActionUp()) {
					mGameScene.releaseButtonA();
					return true;
				} else {
					return false;
				}
			}
		};
		attachChild(buttonA);

		Sprite buttonB = new Sprite(715, 350, resourcesManager.mOnScreenButton, vbom) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionDown()) {
					mGameScene.actionButtonB();
					return true;
				} else if (pSceneTouchEvent.isActionUp()) {
					mGameScene.releaseButtonB();
					return true;
				} else {
					return false;
				}
			}
		};
		attachChild(buttonB);

		buttonC = new Sprite(715, 350 - 20 - buttonB.getHeight() / 2, resourcesManager.mOnScreenButton, vbom) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionDown()) {
					mGameScene.actionButtonC();
					return true;
				} else if (pSceneTouchEvent.isActionUp()) {
					mGameScene.releaseButtonC();
					return true;
				} else {
					return false;
				}
			}
		};
		attachChild(buttonC);

		buttonA.setScaleCenter(0, 0);
		buttonB.setScaleCenter(0, 0);
		buttonC.setScaleCenter(0, 0);
		buttonA.setScale(0.5f);
		buttonB.setScale(0.5f);
		buttonC.setScale(0.5f);

		attachChild(menuText);
		registerTouchArea(menuText);
		registerTouchArea(buttonA);
		registerTouchArea(buttonB);
		registerTouchArea(buttonC);

		camera.setHUD(this);

		hideButtonC();
	}

	public void showButtonC() {
		if (!buttonC.isVisible()) {
			buttonC.setVisible(true);
			registerTouchArea(buttonC);
		}
	}

	public void hideButtonC() {
		if (buttonC.isVisible()) {
			buttonC.setVisible(false);
			unregisterTouchArea(buttonC);
		}
	}
}
