package com.example.survivalgame.util;

import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;

import android.graphics.PointF;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class MoveTask {

	Body body;
	PointF positionToMove;

	public MoveTask(Body body, PointF positionToMove) {
		this.body = body;
		this.positionToMove = positionToMove;
	}

	public void move() {
		float angle = body.getAngle(); // keeps the body angle
		Vector2 v2 = Vector2Pool.obtain(positionToMove.x / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, positionToMove.y / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
		
//		Log.v("BUILDING",v2.x+" --|-- "+v2.y);
		
		body.setTransform(v2, angle);
		Vector2Pool.recycle(v2);
	}

}
