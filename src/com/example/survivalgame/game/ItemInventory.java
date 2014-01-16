package com.example.survivalgame.game;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;

import com.example.survivalgame.ResourcesManager;

public class ItemInventory extends Rectangle {

	static final String TAG = "ItemInventory";

	String name;
	int quantity;

	Text textQuantity;

	Sprite mSpriteItem;

	public enum Attribute {
		SPEED, LIFE, MUNITION
	}

	int amount = 0;

	Attribute attribute;

	GameScene mGameScene;

	public ItemInventory(float pX, float pY, String name, ITextureRegion pTextureRegion, Attribute attribute, ResourcesManager resourcesManager, VertexBufferObjectManager vbom, GameScene gameScene) {

		super(pX, pY, 50, 50, vbom);
		// setColor(Color.YELLOW);
		// setColor(181.0f / 255.0f, 167.0f / 255.0f, 167.0f / 255.0f);
		setColor(181.0f / 255.0f, 167.0f / 255.0f, 167.0f / 255.0f);
		mSpriteItem = new Sprite(25, 25, pTextureRegion, vbom);
		if (name == null) {
			name = "Null";
		}

		mGameScene = gameScene;

		mSpriteItem.setScale(2f);
		this.name = name;
		this.quantity = 0;
		this.attribute = attribute;

		textQuantity = new Text(45, 16, resourcesManager.font, "x99", new TextOptions(HorizontalAlign.LEFT), vbom);
		textQuantity.setScale(0.5f);

		attachChild(mSpriteItem);
		// attachChild(textQuantity);

	}

	public void setSelected(boolean selected) {
		if (selected) {
			setColor(Color.YELLOW);
		} else {
			setColor(181.0f / 255.0f, 167.0f / 255.0f, 167.0f / 255.0f);
		}
	}

	public void increaseQuantity() {
		quantity += 1;
		textQuantity.setText("x" + quantity);
	}

	public void decreaseQuantity() {
		quantity--;
		mGameScene.inventoryPlayer.inventory.put(name, quantity);
		mGameScene.inventoryHUD.quantityTextSelected.setText("Cant:" + quantity);
		mGameScene.inventoryHUD.populateInventory();

	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
		textQuantity.setText("x" + quantity);
	}

	public void useItem() {
		if (quantity > 0) {
			decreaseQuantity();
			switch (attribute) {
			case SPEED:
				mGameScene.addLife(20);
				break;

			default:
				break;
			}
		}
	}

}
