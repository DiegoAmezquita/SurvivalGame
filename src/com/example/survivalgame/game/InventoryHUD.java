package com.example.survivalgame.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;

import com.example.survivalgame.ResourcesManager;
import com.example.survivalgame.TextureGameManager;

public class InventoryHUD extends HUD {

	ResourcesManager resourcesManager;

	VertexBufferObjectManager vbom;
	GameScene mGameScene;
	Camera mCamera;

	ItemInventory mItemSelected;

	Rectangle mSpriteButtonUse;

	int mPosInventoryItemY = 100;

	ArrayList<ItemInventory> itemsAlreadyLoaded;

	InventoryPlayer inventoryPlayer;

	public InventoryHUD(Camera camera, GameScene gameScene, VertexBufferObjectManager vbom) {

		this.mCamera = camera;
		this.mGameScene = gameScene;
		this.vbom = vbom;
		resourcesManager = ResourcesManager.getInstance();

		itemsAlreadyLoaded = new ArrayList<ItemInventory>();

		inventoryPlayer = InventoryPlayer.getInstance();

		createInventory();

	}

	public void createInventory() {

		Rectangle background = new Rectangle(0, 0, 800, 480, vbom);
		background.setColor(Color.BLACK);
		attachChild(background);

		Rectangle menuBorder = new Rectangle(0, 0, 500, 300, vbom);
		menuBorder.setPosition(400 - menuBorder.getWidth() / 2, 240 - menuBorder.getHeight() / 2);
		attachChild(menuBorder);

		Rectangle menuBackground = new Rectangle(0, 0, 490, 290, vbom);
		menuBackground.setColor(181.0f / 255.0f, 167.0f / 255.0f, 167.0f / 255.0f);
		menuBackground.setPosition(400 - menuBackground.getWidth() / 2, 240 - menuBackground.getHeight() / 2);
		attachChild(menuBackground);

		Text closeText = new Text(390, 470, resourcesManager.font, "X", new TextOptions(HorizontalAlign.LEFT), vbom) {
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
		closeText.setPosition(menuBackground.getX() + menuBackground.getWidth() - 20 - closeText.getWidth() / 2, 140 - closeText.getHeight());

		attachChild(closeText);

	}

	public void populateInventory() {
		Iterator<Map.Entry<String, Integer>> entries = inventoryPlayer.inventory.entrySet().iterator();

		while (entries.hasNext()) {
			Map.Entry<String, Integer> entry = entries.next();

			int position = checkAlreadyLoaded(entry.getKey());
			if (position == -1) {
				ItemInventory itemTest = new ItemInventory(180, 0, entry.getKey(), TextureGameManager.getInstance().getTexture(entry.getKey()), resourcesManager, vbom) {
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

	public void addItemInventory(ItemInventory item) {
		item.setY(mPosInventoryItemY);
		attachChild(item);
		mPosInventoryItemY += 40;
	}

	public void setItemSelected(ItemInventory item) {
		if (mItemSelected != null) {
			mItemSelected.mSpriteItem.detachSelf();
		}

		mItemSelected = item;

		mItemSelected.mSpriteItem = new Sprite(400, 180, mItemSelected.mSpriteItem.getTextureRegion(), vbom);

		mItemSelected.mSpriteItem.setScale(3f);

		mItemSelected.mSpriteItem.setX(500 - mItemSelected.mSpriteItem.getWidth() / 2);

		attachChild(mItemSelected.mSpriteItem);

		if (mSpriteButtonUse == null) {
			mSpriteButtonUse = new Rectangle(450, 260, 100, 50, vbom);
			Text useText = new Text(0, 0, resourcesManager.font, "USE", new TextOptions(HorizontalAlign.CENTER), vbom) {
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
					if (pSceneTouchEvent.isActionUp()) {
						mGameScene.useItem(mItemSelected);
					}
					return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
				}
			};
			useText.setScale(0.7f);
			useText.setPosition(50 - useText.getWidth() / 2, 25 - useText.getHeight() / 2);
			useText.setColor(Color.RED);
			mSpriteButtonUse.attachChild(useText);
			attachChild(mSpriteButtonUse);
		}

	}
}
