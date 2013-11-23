package com.example.survivalgame;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.HorizontalAlign;


public class GameHUD extends HUD {

	Camera camera;

	public GameHUD(Camera camera, ResourcesManager resourcesManager,VertexBufferObjectManager vbom) {
		this.camera = camera;

		Text lifeText = new Text(5, 5, resourcesManager.font, "Life: 100%", new TextOptions(HorizontalAlign.LEFT), vbom);
		lifeText.setScale(0.5f);
		lifeText.setText("Life: 100%");
		lifeText.setPosition(-50, -10);
		attachChild(lifeText);

		Text menuText = new Text(390, 470, resourcesManager.font, "Inventory", new TextOptions(HorizontalAlign.LEFT), vbom) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionDown()) {
//					camera.setHUD(inventoryHud);
//					populateInventory();
//					movementOnScreenControl.setVisible(false);
//					movementOnScreenControl.setIgnoreUpdate(true);
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
//					speed = 1.2f;
					return true;
				} else if (pSceneTouchEvent.isActionUp()) {
//					speed = 1.0f;
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
//					bullet.setPosition(player.getX() + player.getWidth() / 2, player.getY() + player.getHeight() / 2);
//					bulletDirection = player.directionMove;
					return true;
				} else {
					return false;
				}
			}
		};
		attachChild(buttonB);

		buttonA.setScaleCenter(0, 0);
		buttonB.setScaleCenter(0, 0);
		buttonA.setScale(0.5f);
		buttonB.setScale(0.5f);

		attachChild(menuText);
		registerTouchArea(menuText);
		registerTouchArea(buttonA);
		registerTouchArea(buttonB);

		camera.setHUD(this);
	}
}
