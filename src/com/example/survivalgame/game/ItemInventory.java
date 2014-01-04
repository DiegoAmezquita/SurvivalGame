package com.example.survivalgame.game;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.align.HorizontalAlign;

import com.example.survivalgame.ResourcesManager;

public class ItemInventory extends Rectangle {

	String name;
	int quantity;

	Text textQuantity;

	Sprite mSpriteItem;
	
	public enum Attribute{
		SPEED,LIFE,MUNITION
	}
	
	int amount = 0;
	
	
	Attribute attribute;
	
	
	GameScene mGameScene;

	public ItemInventory(float pX, float pY, String name, ITextureRegion pTextureRegion,Attribute attribute, ResourcesManager resourcesManager, VertexBufferObjectManager vbom,GameScene gameScene) {

		super(pX, pY, 100, 32, vbom);
		setColor(181.0f / 255.0f, 167.0f / 255.0f, 167.0f / 255.0f);
		mSpriteItem = new Sprite(pTextureRegion.getWidth(), pTextureRegion.getHeight(), pTextureRegion, vbom);
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
		attachChild(textQuantity);

	}

	public void increaseQuantity() {
		quantity += 1;
		textQuantity.setText("x" + quantity);
	}
	
	public void decreaseQuantity() {
		quantity -= 1;
		textQuantity.setText("x" + quantity);
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
		textQuantity.setText("x" + quantity);
	}
	
	public void useItem(){
		decreaseQuantity();
		
		switch (attribute) {
		case SPEED:
			
			break;

		default:
			break;
		}
	}

}
