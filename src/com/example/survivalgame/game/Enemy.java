package com.example.survivalgame.game;

import org.andengine.entity.shape.RectangularShape;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;

import android.util.Log;

import com.example.survivalgame.ResourcesManager;
import com.example.survivalgame.util.Util.Direction;

public class Enemy extends AnimatedSprite {

	private float speed = 0.5f;

	private boolean free = true;

	private boolean hasToChase = false;

	private float shapeX;
	private float shapeY;

	private float centerX;
	private float centerY;

	private float beforeMoveX;
	private float beforeMoveY;

	Direction directionMove;

	Direction directionPointing;

	GameScene mGameScene;

	public Enemy(float pX, float pY, VertexBufferObjectManager pVertexBufferObjectManager, GameScene gameScene) {
		super(pX, pY, ResourcesManager.getInstance().enemies_region, pVertexBufferObjectManager);
		// setColor(Color.RED);

		this.mGameScene = gameScene;
		setScale(1.6f);

		centerX = getX() + getWidth() / 2;
		centerY = getY() + getWidth() / 2;

		beforeMoveX = getX();
		beforeMoveY = getY();

		setRunningDown();
	}

	public void setRunningUp() {
		if (directionMove != Direction.UP) {
			Log.v("GAME", "UP");
			final long[] PLAYER_ANIMATE = new long[] { 200, 200, 200 };
			animate(PLAYER_ANIMATE, 36, 38, true);
			directionMove = Direction.UP;
			directionPointing = Direction.UP;
		}
	}

	public void setRunningDown() {
		if (directionMove != Direction.DOWN) {
			Log.v("GAME", "DOWN");
			final long[] PLAYER_ANIMATE = new long[] { 200, 200, 200 };
			animate(PLAYER_ANIMATE, 0, 2, true);
			directionMove = Direction.DOWN;
			directionPointing = Direction.DOWN;
		}
	}

	public void setRunningLeft() {
		if (directionMove != Direction.LEFT) {
			Log.v("GAME", "LEFT");
			final long[] PLAYER_ANIMATE = new long[] { 200, 200, 200 };
			animate(PLAYER_ANIMATE, 12, 14, true);
			directionMove = Direction.LEFT;
			directionPointing = Direction.LEFT;
		}
	}

	public void setRunningRight() {
		if (directionMove != Direction.RIGHT) {
			Log.v("GAME", "RIGHT");
			final long[] PLAYER_ANIMATE = new long[] { 200, 200, 200 };
			animate(PLAYER_ANIMATE, 24, 26, true);
			directionMove = Direction.RIGHT;
			directionPointing = Direction.RIGHT;
		}
	}

	public void setRunningUpRight() {
		// if (directionMove != Direction.UPRIGHT) {
		// final long[] PLAYER_ANIMATE = new long[] { 200, 200, 200, 200};
		// animate(PLAYER_ANIMATE, 18, 23, true);
		// directionMove = Direction.UPRIGHT;
		// }
	}

	public void setRunningUpLeft() {
		// if (directionMove != Direction.UPLEFT) {
		// final long[] PLAYER_ANIMATE = new long[] { 200, 200, 200, 200};
		// animate(PLAYER_ANIMATE, 30, 35, true);
		// directionMove = Direction.UPLEFT;
		// }
	}

	public void setRunningDownRight() {
		// if (directionMove != Direction.DOWNRIGHT) {
		// final long[] PLAYER_ANIMATE = new long[] { 200, 200, 200, 200};
		// animate(PLAYER_ANIMATE, 6, 11, true);
		// directionMove = Direction.DOWNRIGHT;
		// }
	}

	public void setRunningDownLeft() {
		// if (directionMove != Direction.DOWNLEFT) {
		// final long[] PLAYER_ANIMATE = new long[] { 200, 200, 200, 200};
		// animate(PLAYER_ANIMATE, 42, 47, true);
		// directionMove = Direction.DOWNLEFT;
		// }
	}

	public void stopRunning() {
		// stopAnimation();
		// if (directionMove != null)
		// switch (directionMove) {
		// case UP:
		// setCurrentTileIndex(37);
		// break;
		// case DOWN:
		// setCurrentTileIndex(1);
		// break;
		// case RIGHT:
		// setCurrentTileIndex(25);
		// break;
		// case LEFT:
		// setCurrentTileIndex(13);
		// break;
		// case UPRIGHT:
		// setCurrentTileIndex(23);
		// break;
		// case UPLEFT:
		// setCurrentTileIndex(35);
		// break;
		// case DOWNRIGHT:
		// setCurrentTileIndex(11);
		// break;
		// case DOWNLEFT:
		// setCurrentTileIndex(47);
		// break;
		// default:
		// break;
		// }
		// directionMove = Direction.NONE;
	}

	public void chaseEntity(RectangularShape shape) {
		checkIfHasToChase(shape);
		if (hasToChase) {
			shapeX = shape.getX() + shape.getWidth() / 2;
			shapeY = shape.getY() + shape.getHeight() / 2;

			centerX = getX() + getWidth() / 2;
			centerY = getY() + getWidth() / 2;

			beforeMoveX = getX();
			beforeMoveY = getY();

			if (shapeX > centerX) {
				setX(getX() + speed);
				// setRunningRight();
			} else if (shapeX < centerX) {
				setX(getX() - speed);
				// setRunningLeft();
			}

			if (CollisionManager.getInstance().checkCollisionObstacles(this)) {
				setX(beforeMoveX);
			}

			if (shapeY > centerY) {
				setY(getY() + speed);
				// setRunningDown();
			} else if (shapeY < centerY) {
				setY(getY() - speed);
				// setRunningUp();
			}

			if (CollisionManager.getInstance().checkCollisionObstacles(this)) {
				setY(beforeMoveY);
			}
		} else {
			Log.v("GAME", "Se detiene");
			stopRunning();
		}
	}

	private void checkIfHasToChase(RectangularShape shape) {

		shapeX = shape.getX() + shape.getWidth() / 2;
		shapeY = shape.getY() + shape.getHeight() / 2;

		centerX = getX() + getWidth() / 2;
		centerY = getY() + getWidth() / 2;

		float distanceX = centerX - shapeX;
		float distanceY = centerY - shapeY;

		float distance = (float) (Math.pow(Math.abs(distanceX), 2) + Math.pow(Math.abs(distanceY), 2));

		distance = (float) Math.sqrt(distance);

		if (distanceX > 0) {
			if(distanceY>0){
				if(distanceX>distanceY){
					setRunningLeft();
				}else{
					setRunningUp();
				}
			}else{
				if(distanceX>(distanceY*-1)){
					setRunningLeft();
				}else{
					setRunningDown();
				}
			}
			
		} else {
			if(distanceY>0){
				if((distanceX*-1)>distanceY){
					setRunningRight();
				}else{
					setRunningUp();
				}
			}else{
				if((distanceX*-1)>(distanceY*-1)){
					setRunningRight();
				}else{
					setRunningDown();
				}
			}
		}

		if (distance < 200) {
			if (collidesWith(shape)) {
				mGameScene.damagePlayer();
			}
			hasToChase = true;
		} else {
			stopAnimation();
			hasToChase = false;
		}
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

}
