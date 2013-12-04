package com.example.survivalgame.util;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

import android.util.Log;

import com.example.survivalgame.ResourcesManager;

public class Popup extends Rectangle {

	Text popupText;

	public Popup(float pX, float pY, VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, 100, 50, pVertexBufferObjectManager);

		setColor(Color.WHITE);
		popupText = new Text(5, 5, ResourcesManager.getInstance().font, "Hola soy maxito lero lero", new TextOptions(HorizontalAlign.LEFT), pVertexBufferObjectManager) {
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				Log.v("GAME", "Touch Popup");
				return true;
			};
		};
		popupText.setScale(0.5f);
		popupText.setColor(Color.RED);
		popupText.setText("Popup Text");
		popupText.setPosition(getWidth() / 2 - popupText.getWidth() / 2, getHeight() / 2 - popupText.getHeight() / 2);
		attachChild(popupText);
	}

	public void changeText(String text) {
		popupText.setText(text);
		setWidth(popupText.getWidthScaled() + 10);
		popupText.setPosition(getWidth() / 2 - popupText.getWidth() / 2, getHeight() / 2 - popupText.getHeight() / 2);
	}

	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		Log.v("GAME", "Touch Popup");
		if (pSceneTouchEvent.isActionUp()) {
			getParent().detachChild(this);
		}
		return true;
	}

}
