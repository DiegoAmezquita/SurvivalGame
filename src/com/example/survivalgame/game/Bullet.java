package com.example.survivalgame.game;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.graphics.PointF;
import android.util.Log;

import com.example.survivalgame.util.Util.Direction;

public class Bullet extends Rectangle {

	private boolean free;

	private float speed = 15f;

	Direction directionMove;

	PointF initialPosition;
	
	public enum kindOfBullet{
		PISTOL,SHOTGUN,SNIPER
	}

	public Bullet(float pX, float pY, VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, 5, 5, pVertexBufferObjectManager);
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

		initialPosition = new PointF(pX, pY);

		switch (directionMove) {
		case UP:
		case DOWN:
			setWidth(2);
			setHeight(5);
			break;
		case RIGHT:
		case LEFT:
			setWidth(5);
			setHeight(2);
			break;
		default:
			break;
		}
	}

	public void updatePosition() {
		if (directionMove != null)
			switch (directionMove) {
			case UP:
				setY(getY() + speed);
				break;
			case DOWN:
				setY(getY() - speed);
				break;
			case RIGHT:
				setX(getX() + speed);
				break;
			case LEFT:
				setX(getX() - speed);
				break;
			default:
				break;
			}

		if (getY() < initialPosition.y-500 || getY() > initialPosition.y+500 || getX() < initialPosition.x-500 || getX() > initialPosition.x+500) {
			Log.e("GAME", "Bullet free");
			release();
			getParent().detachChild(this);
		} else if (CollisionManager.getInstance().checkCollisionObstacles(this) != null || CollisionManager.getInstance().checkCollisionEnemy(this)) {

			Log.e("GAME", "Bullet free");
			release();
			getParent().detachChild(this);
		}

	}

}
