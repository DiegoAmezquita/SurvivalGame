package com.example.survivalgame.game;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

import com.example.survivalgame.ResourcesManager;

public class ItemInventory extends Rectangle {

	String name;
	int quantity;

	Text textQuantity;

	public ItemInventory(float pX, float pY, String name, ITextureRegion pTextureRegion, ResourcesManager resourcesManager, VertexBufferObjectManager vbom) {

		super(pX, pY, 100, 32, vbom);
		setColor(Color.BLACK);
		Sprite spriteItem = new Sprite(0, 0, pTextureRegion, vbom);
		if (name == null) {
			name = "Null";
		}
		this.name = name;
		this.quantity = 0;

		textQuantity = new Text(15, -15, resourcesManager.font, "x99", new TextOptions(HorizontalAlign.LEFT), vbom);
		textQuantity.setScale(0.5f);

		attachChild(spriteItem);
		attachChild(textQuantity);

	}

	public void increaseQuantity() {
		quantity += 1;
		textQuantity.setText("x" + quantity);
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
		textQuantity.setText("x" + quantity);
	}

}
