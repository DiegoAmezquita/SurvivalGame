package com.example.survivalgame.game;

import java.util.ArrayList;

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
import com.example.survivalgame.util.Popup;
import com.example.survivalgame.util.SpotLight;

public class GameHUD extends HUD {

	Camera mCamera;
	GameScene mGameScene;

	Sprite buttonC;

	Rectangle nightRect;

	Text timeDay;

	Text bulletCounter;
	
	Text lifeText;

	VertexBufferObjectManager vbom;

	ResourcesManager resourcesManager;

	ArrayList<Popup> arrayPopups;

	public GameHUD(Camera camera, VertexBufferObjectManager vbom, GameScene gameScene) {
		this.mCamera = camera;
		this.mGameScene = gameScene;
		this.vbom = vbom;

		resourcesManager = ResourcesManager.getInstance();

		arrayPopups = new ArrayList<Popup>();

//		nightRect = new Rectangle(0, 0, 800, 480, vbom);
//		nightRect.setColor(Color.BLACK);
//		nightRect.setShaderProgram(SpotLight.getInstance());
//		attachChild(nightRect);

		lifeText = new Text(5, 5, resourcesManager.font, "Life: 100%", new TextOptions(HorizontalAlign.LEFT), vbom);
		lifeText.setScale(0.5f);
		lifeText.setText("Life: 100%");
		lifeText.setPosition(-50, -10);
		attachChild(lifeText);

		timeDay = new Text(5, 5, resourcesManager.font, "Hour: 24", new TextOptions(HorizontalAlign.LEFT), vbom);
		timeDay.setScale(0.5f);
		timeDay.setText("Hour: 24");
		timeDay.setPosition(400 - timeDay.getWidth() / 2, -10);
		attachChild(timeDay);

		bulletCounter = new Text(5, 5, resourcesManager.font, "Bullet: 99", new TextOptions(HorizontalAlign.LEFT), vbom);
		bulletCounter.setScale(0.5f);
		bulletCounter.setText("Bullets: 99");
		bulletCounter.setPosition(800 - timeDay.getWidth(), -10);

		attachChild(bulletCounter);

		Text menuText = new Text(390, 470, resourcesManager.font, "Inventory", new TextOptions(HorizontalAlign.LEFT), vbom) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionDown()) {
					mCamera.setHUD(mGameScene.inventoryHUD);
					mGameScene.inventoryHUD.populateInventory();
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

	public void createPopupConversation(float pX, float pY) {
		Popup popup = new Popup(pX, pY, vbom);
		attachChild(popup);
		popup.changeText("Game Over");
		popup.setPosition(pX-popup.getWidth()/2, pY-popup.getHeightScaled());
		registerTouchArea(popup);

		arrayPopups.add(popup);
	}

	public void removePopup(int ID) {
		for (Popup popup : arrayPopups) {
			if (popup.getID() == ID) {
				detachChild(popup);
				unregisterTouchArea(popup);
				arrayPopups.remove(popup);
				break;
			}
		}
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
