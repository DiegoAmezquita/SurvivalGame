package com.example.survivalgame.game;

import java.util.ArrayList;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;

import android.util.Log;

import com.example.survivalgame.ResourcesManager;
import com.example.survivalgame.util.Popup;
import com.example.survivalgame.util.SpotLight;

public class GameHUD extends HUD {

	static final String TAG = "GameHUD";

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

	Rectangle blockScreen;

	ArrayList<ItemInventory> quickMenuItems;
	Rectangle quickMenuBackground;

	Path pathShow;
	Path pathHide;
	PathModifier showMenu;
	PathModifier hideMenu;
	Rectangle quickMenuButton;
	boolean quickMenuShowed = false;

	public GameHUD(Camera camera, VertexBufferObjectManager vbom, GameScene gameScene) {
		this.mCamera = camera;
		this.mGameScene = gameScene;
		this.vbom = vbom;

		resourcesManager = ResourcesManager.getInstance();

		arrayPopups = new ArrayList<Popup>();

		quickMenuItems = new ArrayList<ItemInventory>();

		nightRect = new Rectangle(400, 240, 800, 480, vbom);
		nightRect.setColor(Color.BLACK);
		nightRect.setShaderProgram(SpotLight.getInstance());
		attachChild(nightRect);

		blockScreen = new Rectangle(0, 0, 2000, 2000, vbom);
		blockScreen.setColor(Color.BLACK);
		blockScreen.setVisible(false);
		attachChild(blockScreen);

		lifeText = new Text(5, 5, resourcesManager.font, "Life: 100%", new TextOptions(HorizontalAlign.LEFT), vbom);
		lifeText.setScale(0.5f);
		lifeText.setText("Life: 100%");
		lifeText.setPosition(0 + lifeText.getWidth() * lifeText.getScaleX() / 2, 480 - lifeText.getHeight() / 2 * lifeText.getScaleY());
		attachChild(lifeText);

		timeDay = new Text(5, 5, resourcesManager.font, "Hour: 24", new TextOptions(HorizontalAlign.LEFT), vbom);
		timeDay.setScale(0.5f);
		timeDay.setText("Hour: 24");
		timeDay.setPosition(400, 480 - timeDay.getHeight() / 2 * timeDay.getScaleY());
		attachChild(timeDay);

		bulletCounter = new Text(5, 5, resourcesManager.font, "Bullet: 99", new TextOptions(HorizontalAlign.LEFT), vbom);
		bulletCounter.setScale(0.5f);
		bulletCounter.setText("Bullets: 99");
		bulletCounter.setPosition(800 - timeDay.getWidth() * 0.5f, 480 - bulletCounter.getHeight() / 2 * bulletCounter.getScaleY());

		attachChild(bulletCounter);

		Text menuText = new Text(400, 10, resourcesManager.font, "Inventory", new TextOptions(HorizontalAlign.LEFT), vbom) {
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
		menuText.setPosition(400, 0 + menuText.getHeight() / 2 * 0.7f);

		Sprite buttonA = new Sprite(670, 80, resourcesManager.mOnScreenButton, vbom) {
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

		Sprite buttonB = new Sprite(765, 120, resourcesManager.mOnScreenButton, vbom) {
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

		buttonC = new Sprite(765, 140 + buttonB.getHeight() / 2, resourcesManager.mOnScreenButton, vbom) {
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

		createQuickMenu();

	}

	public void createQuickMenu() {

		pathShow = new Path(2).to(780, 240).to(680, 240);
		pathHide = new Path(2).to(680, 240).to(780, 240);

		showMenu = new PathModifier(0.1f, pathShow);
		hideMenu = new PathModifier(0.1f, pathHide);

		quickMenuButton = new Rectangle(780, 240, 40, 60, vbom) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionDown()) {
					if (!quickMenuShowed) {
						quickMenuButton.clearEntityModifiers();
						quickMenuButton.registerEntityModifier(new PathModifier(0.1f, pathShow));
						quickMenuShowed = true;
					} else {
						quickMenuButton.clearEntityModifiers();
						quickMenuButton.registerEntityModifier(new PathModifier(0.1f, pathHide));
						quickMenuShowed = false;
					}
					return true;
				} else if (pSceneTouchEvent.isActionUp()) {
					return true;
				} else {
					return false;
				}
			}
		};
		attachChild(quickMenuButton);

		quickMenuBackground = new Rectangle(90, 30, 100, 240, vbom);
		quickMenuBackground.setColor(Color.RED);

		registerTouchArea(quickMenuButton);

		quickMenuButton.attachChild(quickMenuBackground);

	}

	public void createPopupConversation(float pX, float pY) {
		Popup popup = new Popup(pX, pY, vbom);
		attachChild(popup);
		popup.changeText("Game Over");
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

	public void addItemQuickMenu(final ItemInventory item) {
		if (quickMenuItems.size() < 4) {
			quickMenuItems.add(item);

			int posY = 200 - ((quickMenuItems.size() - 1) * 50);

			Sprite sprite = new Sprite(50, posY, item.mSpriteItem.getTextureRegion(), vbom) {
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
					if (pSceneTouchEvent.isActionUp()) {
						Log.v(TAG, "USE THIS QUICK ITEM " + item.name);
					}
					return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
				}
			};
			sprite.setScale(2.0f);

			quickMenuBackground.attachChild(sprite);
			registerTouchArea(sprite);
		}
	}
}
