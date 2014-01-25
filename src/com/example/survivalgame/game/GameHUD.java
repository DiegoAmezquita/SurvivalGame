package com.example.survivalgame.game;

import java.util.ArrayList;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.entity.primitive.Line;
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

public class GameHUD extends HUD {

	static final String TAG = "GameHUD";

	Camera mCamera;
	GameScene mGameScene;

	Sprite buttonC;

	Rectangle nightRect;

	Text timeDay;

	Text bulletCounter;

	Text lifeText;

	Text infoText;

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

	Rectangle backgroundItemsBox;
	Rectangle closeButton;

	Rectangle backgroundOpaque;

	public GameHUD(Camera camera, VertexBufferObjectManager vbom, GameScene gameScene) {
		this.mCamera = camera;
		this.mGameScene = gameScene;
		this.vbom = vbom;

		resourcesManager = ResourcesManager.getInstance();

		arrayPopups = new ArrayList<Popup>();

		quickMenuItems = new ArrayList<ItemInventory>();

		// nightRect = new Rectangle(400, 240, 800, 480, vbom);
		// nightRect.setColor(Color.BLACK);
		// nightRect.setShaderProgram(SpotLight.getInstance());
		// attachChild(nightRect);

		blockScreen = new Rectangle(0, 0, 2000, 2000, vbom);
		blockScreen.setColor(Color.BLACK);
		blockScreen.setVisible(false);
		attachChild(blockScreen);

		backgroundOpaque = new Rectangle(400, 240, 800, 480, vbom);
		backgroundOpaque.setColor(Color.BLACK);
		backgroundOpaque.setAlpha(0.7f);
		backgroundOpaque.setVisible(false);
		attachChild(backgroundOpaque);

		lifeText = new Text(5, 5, resourcesManager.font, "Vida: 100%", new TextOptions(HorizontalAlign.LEFT), vbom);
		lifeText.setScale(0.7f);
		lifeText.setText("Vida: 100%");
		lifeText.setPosition(0 + lifeText.getWidth() * lifeText.getScaleX() / 2, 480 - lifeText.getHeight() / 2 * lifeText.getScaleY());
		attachChild(lifeText);

		timeDay = new Text(5, 5, resourcesManager.font, "Hour: 24", new TextOptions(HorizontalAlign.LEFT), vbom);
		timeDay.setScale(0.5f);
		timeDay.setText("Hour: 24");
		timeDay.setPosition(400, 480 - timeDay.getHeight() / 2 * timeDay.getScaleY());
		// attachChild(timeDay);

		bulletCounter = new Text(5, 5, resourcesManager.font, "Balas: 99", new TextOptions(HorizontalAlign.LEFT), vbom);
		bulletCounter.setScale(0.5f);
		bulletCounter.setText("Balas: 99");
		bulletCounter.setPosition(800 - timeDay.getWidth() * 0.5f, 480 - bulletCounter.getHeight() / 2 * bulletCounter.getScaleY());

		attachChild(bulletCounter);

		Text menuText = new Text(400, 10, resourcesManager.font, "Inventario", new TextOptions(HorizontalAlign.LEFT), vbom) {
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
		createInfoText();

		createUIBox();
	}

	public void createInfoText() {
		infoText = new Text(400, 400, resourcesManager.font, "TEST", 255, vbom);
		infoText.setVisible(false);
		attachChild(infoText);
	}

	public void createUIBox() {
		backgroundItemsBox = new Rectangle(400, 240, 200, 100, vbom);
		backgroundItemsBox.setColor(Color.RED);
		backgroundItemsBox.setVisible(false);
		attachChild(backgroundItemsBox);

		Line topLine = new Line(0, 100, 200, 100, vbom);
		Line bottomLine = new Line(0, 0, 200, 0, vbom);
		Line leftLine = new Line(0, 0, 0, 100, vbom);
		Line rightLine = new Line(200, 0, 200, 100, vbom);

		topLine.setLineWidth(3);
		bottomLine.setLineWidth(3);
		leftLine.setLineWidth(3);
		rightLine.setLineWidth(3);

		backgroundItemsBox.attachChild(topLine);
		backgroundItemsBox.attachChild(bottomLine);
		backgroundItemsBox.attachChild(leftLine);
		backgroundItemsBox.attachChild(rightLine);

		closeButton = new Rectangle(100, -70, 100, 40, vbom) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionUp()) {
					hideBoxItems();
				}
				return false;
			}
		};
		closeButton.setColor(Color.WHITE);
		backgroundItemsBox.attachChild(closeButton);

	}

	public void populateItemBox(ArrayList<Sprite> items) {
		backgroundItemsBox.setVisible(true);
		backgroundOpaque.setVisible(true);
		int pX = 25;
		int pY = 75;

		for (int i = 0; i < backgroundItemsBox.getChildCount(); i++) {
			if (!backgroundItemsBox.getChildByIndex(i).getClass().equals(Line.class) && backgroundItemsBox.getChildByIndex(i) != closeButton) {
				unregisterTouchArea(backgroundItemsBox.getChildByIndex(i));
				backgroundItemsBox.getChildByIndex(i).detachSelf();
			}
		}

		for (int i = 0; i < items.size(); i++) {
			registerTouchArea(items.get(i));
			items.get(i).setPosition(pX, pY);
			items.get(i).setScale(2);
			backgroundItemsBox.attachChild(items.get(i));
			pX += 50;
			if (i == 3) {
				pY -= 50;
				pX = 25;
			}
		}

		registerTouchArea(closeButton);
	}

	public void removeItemBox(Sprite item) {
		for (int i = 0; i < backgroundItemsBox.getChildCount(); i++) {
			if (backgroundItemsBox.getChildByIndex(i) == item) {
				item.detachSelf();
				unregisterTouchArea(item);
				mGameScene.itemToPick = item;
				mGameScene.pickItem();
			}
		}
	}

	public void hideBoxItems() {
		backgroundItemsBox.setVisible(false);
		backgroundOpaque.setVisible(false);
		for (int i = 0; i < backgroundItemsBox.getChildCount(); i++) {
			if (!backgroundItemsBox.getChildByIndex(i).getClass().equals(Line.class)) {
				// unregisterTouchArea(backgroundItemsBox.getChildByIndex(i));
			}
		}
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

	public void createPopupConversation() {
		Popup popup = new Popup(vbom);
		attachChild(popup);
		popup.changeText("Fin del juego");
		registerTouchArea(popup);

		arrayPopups.add(popup);
	}

	public void createPopupInformation(String message, int seconds) {
		Popup popup = new Popup(vbom);
		attachChild(popup);
		popup.changeText(message);
		popup.timerDestroyPopup(seconds);
		// arrayPopups.add(popup);
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
		if (quickMenuItems.size() < 4 && !checkAlreadyQuickMenu(item)) {
			quickMenuItems.add(item);

			int posY = 200 - ((quickMenuItems.size() - 1) * 50);

			Sprite sprite = new Sprite(50, posY, item.mSpriteItem.getTextureRegion(), vbom) {
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
					if (pSceneTouchEvent.isActionUp()) {
						item.useItem();
					}
					return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
				}
			};
			sprite.setScale(2.0f);

			quickMenuBackground.attachChild(sprite);
			registerTouchArea(sprite);
		}
	}

	public boolean checkAlreadyQuickMenu(ItemInventory item) {
		for (int i = 0; i < quickMenuItems.size(); i++) {
			if (item.name.equals(quickMenuItems.get(i).name)) {
				return true;
			}
		}
		return false;
	}
}
