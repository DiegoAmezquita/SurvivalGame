package com.example.survivalgame.game;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.util.Log;

import com.example.survivalgame.util.Util.Direction;

public class Bullet extends Rectangle {

	private boolean free;

	Direction directionMove;

	public Bullet(float pX, float pY, VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, 10, 10, pVertexBufferObjectManager);
		free = true;
	}

	public boolean isFree() {
		return free;
	}

	public void setBusy() {
		free = false;
	}

	public void release() {
		free = true;
	}

	public void setPosAndDir(float pX, float pY, Direction direction) {
		this.directionMove = direction;

		setPosition(pX, pY);

		switch (directionMove) {
		case UP:
		case DOWN:
			setWidth(5);
			setHeight(10);
			break;
		case RIGHT:
		case LEFT:
			setWidth(10);
			setHeight(5);
			break;
		default:
			break;
		}
	}

	public void updatePosition() {
		if (directionMove != null)
			switch (directionMove) {
			case UP:
				setY(getY() - 10);
				break;
			case DOWN:
				setY(getY() + 10);
				break;
			case RIGHT:
				setX(getX() + 10);
				break;
			case LEFT:
				setX(getX() - 10);
				break;
			default:
				break;
			}

		if (getY() < -2000 || getY() > 2000 || getX() < -2000 || getX() > 2000) {
			Log.e("GAME", "Bullet free");
			release();
			getParent().detachChild(this);
		} else if (CollisionManager.getInstance().checkCollisionObstacles(this)||CollisionManager.getInstance().checkCollisionEnemy(this)) {

			Log.e("GAME", "Bullet free");
			release();
			getParent().detachChild(this);
		}

	}

}
