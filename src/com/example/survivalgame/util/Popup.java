package com.example.survivalgame.util;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;

import android.util.Log;

import com.example.survivalgame.ResourcesManager;

public class Popup extends Rectangle {

	Text popupText;

	int ID;

	Line lineTop;
	Line lineBottom;
	Line lineRight;
	Line lineLeft;

	public Popup(VertexBufferObjectManager pVertexBufferObjectManager) {
		super(400, 455, 100, 50, pVertexBufferObjectManager);

		setColor(87f / 255f, 130f / 255f, 192f / 255f);

		lineTop = new Line(0, getHeight(), getWidth(), getHeight(), pVertexBufferObjectManager);
		lineTop.setLineWidth(5);
		lineTop.setColor(Color.WHITE);

		lineBottom = new Line(0, 0, getWidth(), 0, pVertexBufferObjectManager);
		lineBottom.setLineWidth(5);
		lineBottom.setColor(Color.WHITE);

		lineLeft = new Line(0, 0, 0, getHeight(), pVertexBufferObjectManager);
		lineLeft.setLineWidth(5);
		lineLeft.setColor(Color.WHITE);

		lineRight = new Line(getWidth(), 0, getWidth(), getHeight(), pVertexBufferObjectManager);
		lineRight.setLineWidth(5);
		lineRight.setColor(Color.WHITE);

		attachChild(lineTop);
		attachChild(lineBottom);
		attachChild(lineLeft);
		attachChild(lineRight);

		popupText = new Text(5, 5, ResourcesManager.getInstance().font, "Hola soy maxito lero lero", new TextOptions(HorizontalAlign.LEFT), pVertexBufferObjectManager) {
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				Log.v("GAME", "Touch Popup");
				return true;
			};
		};
		popupText.setScale(0.7f);
		popupText.setColor(Color.WHITE);
		popupText.setText("Popup Text");
		popupText.setPosition(getWidth() / 2, getHeight() / 2);
		attachChild(popupText);
	}
	
	public void timerDestroyPopup(int seconds){
		registerUpdateHandler(new TimerHandler(seconds, true, new ITimerCallback() {
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				detachSelf();				
			}
		}));
	}

	public void changeText(String text) {
		popupText.setText(text);
		setWidth(popupText.getWidth() + 10);
		popupText.setPosition(getWidth() / 2, getHeight() / 2);
		lineTop.setPosition(0, getHeight(), getWidth(), getHeight());
		lineBottom.setPosition(0, 0, getWidth(), 0);
		lineRight.setPosition(getWidth(), 0, getWidth(), getHeight());
	}

	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		Log.v("GAME", "Touch Popup");
		if (pSceneTouchEvent.isActionUp()) {
//			detachSelf();
			return true;
		}
		return false;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

}
