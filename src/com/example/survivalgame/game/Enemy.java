package com.example.survivalgame.game;

import org.andengine.entity.shape.Shape;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.vbo.VertexBufferObjectManager;


import com.example.survivalgame.ResourcesManager;
import com.example.survivalgame.util.Util.Direction;

public class Enemy extends AnimatedSprite {

	private float speed = 0.3f;

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
	
	float maxDistance = 400;

	public Enemy(float pX, float pY, VertexBufferObjectManager pVertexBufferObjectManager, GameScene gameScene) {
		super(pX, pY, ResourcesManager.getInstance().enemies_region, pVertexBufferObjectManager);

		this.mGameScene = gameScene;
		setScale(0.75f);

		centerX = getX() + getWidth() / 2;
		centerY = getY() + getWidth() / 2;

		beforeMoveX = getX();
		beforeMoveY = getY();

		directionMove = Direction.NONE;

	}

	public void setRunningUp() {
		if (directionMove != Direction.UP||!isAnimationRunning()) {
			final long[] PLAYER_ANIMATE = new long[] { 200, 200, 200 };
			animate(PLAYER_ANIMATE, 36, 38, true);
			directionMove = Direction.UP;
			directionPointing = Direction.UP;
		}
	}

	public void setRunningDown() {
		if (directionMove != Direction.DOWN||!isAnimationRunning()) {
			final long[] PLAYER_ANIMATE = new long[] { 200, 200, 200 };
			animate(PLAYER_ANIMATE, 0, 2, true);
			directionMove = Direction.DOWN;
			directionPointing = Direction.DOWN;
		}
	}

	public void setRunningLeft() {
		if (directionMove != Direction.LEFT||!isAnimationRunning()) {
			final long[] PLAYER_ANIMATE = new long[] { 200, 200, 200 };
			animate(PLAYER_ANIMATE, 12, 14, true);
			directionMove = Direction.LEFT;
			directionPointing = Direction.LEFT;
		}
	}

	public void setRunningRight() {
		if (directionMove != Direction.RIGHT||!isAnimationRunning()) {
			final long[] PLAYER_ANIMATE = new long[] { 200, 200, 200 };
			animate(PLAYER_ANIMATE, 24, 26, true);
			directionMove = Direction.RIGHT;
			directionPointing = Direction.RIGHT;
		}
	}
	
	
	

	public void stopRunning() {
		stopAnimation();
		if (directionMove != null)
			switch (directionMove) {
			case UP:
				setCurrentTileIndex(37);
				break;
			case DOWN:
				setCurrentTileIndex(1);
				break;
			case RIGHT:
				setCurrentTileIndex(25);
				break;
			case LEFT:
				setCurrentTileIndex(13);
				break;
			case UPRIGHT:
				setCurrentTileIndex(23);
				break;
			case UPLEFT:
				setCurrentTileIndex(35);
				break;
			case DOWNRIGHT:
				setCurrentTileIndex(11);
				break;
			case DOWNLEFT:
				setCurrentTileIndex(47);
				break;
			default:
				break;
			}
		directionMove = Direction.NONE;
	}

	public void chaseEntity(Shape shape) {
		checkIfHasToChase(shape);

		if (hasToChase) {
			shapeX = shape.getX() + shape.getWidth() / 2;
			shapeY = shape.getY() + shape.getHeight() / 2;

			centerX = getX() + getWidth() / 2;
			centerY = getY() + getWidth() / 2;

			
		}

	}

	private void checkIfHasToChase(Shape shape) {

		shapeX = shape.getX() + shape.getWidth() / 2;
		shapeY = shape.getY() + shape.getHeight() / 2;

		centerX = getX() + getWidth() / 2;
		centerY = getY() + getWidth() / 2;

		float distanceX = Math.abs(centerX - shapeX);
		float distanceY = Math.abs(centerY - shapeY);

		float distance = (float) (Math.pow(distanceX, 2) + Math.pow(distanceY, 2));

		distance = (float) Math.sqrt(distance);

		if (shapeX >= centerX) {
			if (shapeY >= centerY) {
				if (distanceX > distanceY) {
					setRunningRight();
				} else {
					setRunningUp();
				}
			}else{
				if (distanceX > distanceY) {
					setRunningRight();
				} else {
					setRunningDown();
				}
			}
		}else{
			if (shapeY >= centerY) {
				if (distanceX > distanceY) {
					setRunningLeft();
				} else {
					setRunningUp();
				}
			}else{
				if (distanceX > distanceY) {
					setRunningLeft();
				} else {
					setRunningDown();
				}
			}
		}

		if (distance < maxDistance) {
			beforeMoveX = getX();
			beforeMoveY = getY();

			if (shapeX > centerX) {
				setX(getX() + (distanceX / distance) * speed);
			} else if (shapeX < centerX) {
				setX(getX() - (distanceX / distance) * speed);
			}

			if (CollisionManager.getInstance().checkCollisionObstacles(this)!=null) {
				setX(beforeMoveX);
			}

			if (shapeY > centerY) {
				setY(getY() + (distanceY / distance) * speed);
			} else if (shapeY < centerY) {
				setY(getY() - (distanceY / distance) * speed);
			}

			if (CollisionManager.getInstance().checkCollisionObstacles(this)!=null) {
				setY(beforeMoveY);
			}

			setZIndex((int) getY());

			if (collidesWith(shape)) {
				mGameScene.damagePlayer();
			}
			hasToChase = true;
			setZIndex(10000 - (int) getY());
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
