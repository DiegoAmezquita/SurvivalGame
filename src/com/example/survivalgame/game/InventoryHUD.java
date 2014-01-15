package com.example.survivalgame.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;

import android.util.Log;

import com.example.survivalgame.ResourcesManager;
import com.example.survivalgame.TextureGameManager;
import com.example.survivalgame.game.ItemInventory.Attribute;

public class InventoryHUD extends HUD implements IOnSceneTouchListener {

	static final String TAG = "InventoryHUD";

	ResourcesManager resourcesManager;

	VertexBufferObjectManager vbom;
	GameScene mGameScene;
	Camera mCamera;

	ItemInventory mItemSelected;

	Rectangle mSpriteButtonUse;

	int mPosInventoryItemX = 210;
	int mPosInventoryItemY = 325;

	ArrayList<ItemInventory> itemsAlreadyLoaded;

	InventoryPlayer inventoryPlayer;

	Rectangle testPosition;

	float posXInit = 400;
	float posYInit = 240;

	public InventoryHUD(Camera camera, GameScene gameScene, VertexBufferObjectManager vbom) {

		this.mCamera = camera;
		this.mGameScene = gameScene;
		this.vbom = vbom;
		resourcesManager = ResourcesManager.getInstance();

		itemsAlreadyLoaded = new ArrayList<ItemInventory>();

		inventoryPlayer = InventoryPlayer.getInstance();

		createInventory();

		createGrid();

		testPosition = new Rectangle(400, 240, 10, 10, vbom);
		testPosition.setColor(Color.RED);
		attachChild(testPosition);

		setOnSceneTouchListener(this);
	}

	public void createInventory() {

		Rectangle background = new Rectangle(400, 240, 800, 480, vbom);
		background.setColor(Color.BLACK);
		attachChild(background);

		Rectangle menuBorder = new Rectangle(400, 240, 500, 300, vbom);
		attachChild(menuBorder);

		Rectangle menuBackground = new Rectangle(400, 240, 490, 290, vbom);
		menuBackground.setColor(181.0f / 255.0f, 167.0f / 255.0f, 167.0f / 255.0f);
		attachChild(menuBackground);

		Text closeText = new Text(390, 500, resourcesManager.font, "X", new TextOptions(HorizontalAlign.LEFT), vbom) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionDown()) {
					mCamera.setHUD(mGameScene.gameHUD);
					mGameScene.movementOnScreenControl.setVisible(true);
					mGameScene.movementOnScreenControl.setIgnoreUpdate(false);
				}
				return true;
			}
		};

		registerTouchArea(closeText);
		closeText.setScale(0.7f);
		closeText.setPosition(menuBackground.getX() + menuBackground.getWidth() / 2 - 10 - closeText.getWidth() / 2, 340 + closeText.getHeight() / 2);

		attachChild(closeText);

	}

	public void populateInventory() {
		Iterator<Map.Entry<String, Integer>> entries = inventoryPlayer.inventory.entrySet().iterator();

		while (entries.hasNext()) {
			Map.Entry<String, Integer> entry = entries.next();

			int position = checkAlreadyLoaded(entry.getKey());
			if (position == -1) {
				ItemInventory itemTest = new ItemInventory(220, 0, entry.getKey(), TextureGameManager.getInstance().getTexture(entry.getKey()), Attribute.SPEED, resourcesManager, vbom, mGameScene) {
					@Override
					public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
						if (pSceneTouchEvent.isActionUp()) {
							setItemSelected(this);
						}
						return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
					}
				};
				itemTest.setQuantity(entry.getValue());
				registerTouchArea(itemTest);
				itemsAlreadyLoaded.add(itemTest);
				addItemInventory(itemTest);

			} else {
				ItemInventory itemTempo = itemsAlreadyLoaded.get(position);
				itemTempo.setQuantity(entry.getValue());
				itemsAlreadyLoaded.set(position, itemTempo);
			}

		}
	}

	public int checkAlreadyLoaded(String key) {
		for (int i = 0; i < itemsAlreadyLoaded.size(); i++) {
			if (itemsAlreadyLoaded.get(i).name.equals(key)) {
				return i;
			}
		}
		return -1;
	}

	public void createGrid() {

		int sizeCell = 60;
		int rowCount = 4;
		int columnCount = 4;

		for (int i = 0; i < rowCount + 1; i++) {
			Line line = new Line(180, 355 - sizeCell * i, 180 + sizeCell * rowCount - 1, 355 - sizeCell * i, vbom);
			line.setLineWidth(5);
			attachChild(line);
		}

		for (int i = 0; i < columnCount + 1; i++) {
			Line line = new Line(180 + sizeCell * i, 115, 180 + sizeCell * i, 115 + sizeCell * columnCount - 1, vbom);
			line.setLineWidth(5);
			attachChild(line);
		}

		// for (int i = 0; i < rowCount; i++) {
		// for (int j = 0; j < columnCount; j++) {
		// Rectangle object = new Rectangle(180+sizeCell/2 + sizeCell * j,
		// 355-sizeCell/2 - sizeCell * i, sizeCell/2, sizeCell/2, vbom);
		// object.setColor(Color.RED);
		// attachChild(object);
		// }
		// }

	}

	public void addItemInventory(ItemInventory item) {
		item.setX(mPosInventoryItemX);
		item.setY(mPosInventoryItemY);
		attachChild(item);
		mPosInventoryItemX += 60;
		mPosInventoryItemY -= 60;
	}

	public void setItemSelected(ItemInventory item) {
		if (mItemSelected != null) {
			mItemSelected.mSpriteItem.detachSelf();
		}

		mItemSelected = item;

		mItemSelected.mSpriteItem = new Sprite(520, 310, mItemSelected.mSpriteItem.getTextureRegion(), vbom);

		mItemSelected.mSpriteItem.setScale(3f);

		attachChild(mItemSelected.mSpriteItem);

		if (mSpriteButtonUse == null) {
			mSpriteButtonUse = new Rectangle(455, 150, 50, 50, vbom) {
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
					if (pSceneTouchEvent.isActionUp()) {
						mGameScene.useItem(mItemSelected);
						Log.v(TAG, "Item Added");
					}
					return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
				}
			};
			;
			mSpriteButtonUse.setColor(Color.GREEN);
			registerTouchArea(mSpriteButtonUse);
			attachChild(mSpriteButtonUse);

			Rectangle addQuickMenu = new Rectangle(520, 150, 50, 50, vbom) {
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
					if (pSceneTouchEvent.isActionUp()) {
						mGameScene.addItemQuickMenu(mItemSelected);
					}
					return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
				}
			};
			addQuickMenu.setColor(Color.YELLOW);
			registerTouchArea(addQuickMenu);
			attachChild(addQuickMenu);

			Rectangle dropItem = new Rectangle(585, 150, 50, 50, vbom) {
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
					if (pSceneTouchEvent.isActionUp()) {
						mGameScene.addItemQuickMenu(mItemSelected);
					}
					return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
				}
			};
			dropItem.setColor(Color.RED);
			registerTouchArea(dropItem);
			attachChild(dropItem);

		}

	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		if (pSceneTouchEvent.isActionDown()) {
			posXInit = pSceneTouchEvent.getX();
			posYInit = pSceneTouchEvent.getY();
		} else if (pSceneTouchEvent.isActionMove()) {
			float newX = pSceneTouchEvent.getX();
			float newY = pSceneTouchEvent.getY();

			testPosition.setPosition(testPosition.getX() + (newX - posXInit), testPosition.getY() + (newY - posYInit));
			posXInit = newX;
			posYInit = newY;

			Log.v(TAG, "Position X " + testPosition.getX() + "  Y " + testPosition.getY());
		}
		return false;
	}
}
