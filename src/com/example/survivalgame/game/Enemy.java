package com.example.survivalgame.game;

import org.andengine.entity.shape.Shape;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
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

	Body bodyEnemy;

	FixtureDef enemyFixture;

	float maxDistance = 400;

	PhysicsWorld mWorld;

	public Enemy(float pX, float pY, VertexBufferObjectManager pVertexBufferObjectManager, GameScene gameScene, PhysicsWorld mWorld) {
		super(pX, pY, ResourcesManager.getInstance().enemies_region, pVertexBufferObjectManager);

		this.mGameScene = gameScene;
		setScale(0.75f);

		centerX = getX() + getWidth() / 2;
		centerY = getY() + getWidth() / 2;

		beforeMoveX = getX();
		beforeMoveY = getY();

		directionMove = Direction.NONE;

		enemyFixture = PhysicsFactory.createFixtureDef(10, 0.1f, 0.0f);
		this.mWorld = mWorld;

	}

	public void createEnemyBody() {
		bodyEnemy = PhysicsFactory.createBoxBody(mWorld, getX(),getY()-50,getWidth()*0.5f,getHeight()*0.7f, BodyType.DynamicBody, enemyFixture);
		bodyEnemy.setUserData("Enemy");

		mWorld.registerPhysicsConnector(new PhysicsConnector(this, bodyEnemy, true, false));
	}

	public void setRunningUp() {
		if (directionMove != Direction.UP || !isAnimationRunning()) {
			final long[] PLAYER_ANIMATE = new long[] { 200, 200, 200 };
			animate(PLAYER_ANIMATE, 36, 38, true);
			directionMove = Direction.UP;
			directionPointing = Direction.UP;
		}
	}

	public void setRunningDown() {
		if (directionMove != Direction.DOWN || !isAnimationRunning()) {
			final long[] PLAYER_ANIMATE = new long[] { 200, 200, 200 };
			animate(PLAYER_ANIMATE, 0, 2, true);
			directionMove = Direction.DOWN;
			directionPointing = Direction.DOWN;
		}
	}

	public void setRunningLeft() {
		if (directionMove != Direction.LEFT || !isAnimationRunning()) {
			final long[] PLAYER_ANIMATE = new long[] { 200, 200, 200 };
			animate(PLAYER_ANIMATE, 12, 14, true);
			directionMove = Direction.LEFT;
			directionPointing = Direction.LEFT;
		}
	}

	public void setRunningRight() {
		if (directionMove != Direction.RIGHT || !isAnimationRunning()) {
			final long[] PLAYER_ANIMATE = new long[] { 200, 200, 200 };
			animate(PLAYER_ANIMATE, 24, 26, true);
			directionMove = Direction.RIGHT;
			directionPointing = Direction.RIGHT;
		}
	}

	public void stopRunning() {
		bodyEnemy.setLinearVelocity(0, 0);
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


		setZIndex(10000-(int)getY());
		
		float distanceX = Math.abs(shape.getX() - getX());
		float distanceY = Math.abs(shape.getY() - getY());

		float distance = (float) (Math.pow(distanceX, 2) + Math.pow(distanceY, 2));

		distance = (float) Math.sqrt(distance);

		float speedX = 0;
		float speedY = 0;

		if (shape.getX() > getX()) {
			speedX = distanceX / distance;
		} else if (shape.getX() < getX()) {
			speedX = -distanceX / distance;
		}

		if (shape.getY() > getY()) {
			speedY = distanceY / distance;
		} else if (shape.getY() < getY()) {
			speedY = -distanceY / distance;
		}

		bodyEnemy.setLinearVelocity(speedX * speed, speedY * speed);

		if (shape.getX() >= getX()) {
			if (shape.getY() >= getY()) {
				if (distanceX > distanceY) {
					setRunningRight();
				} else {
					setRunningUp();
				}
			} else {
				if (distanceX > distanceY) {
					setRunningRight();
				} else {
					setRunningDown();
				}
			}
		} else {
			if (shape.getY() >= getY()) {
				if (distanceX > distanceY) {
					setRunningLeft();
				} else {
					setRunningUp();
				}
			} else {
				if (distanceX > distanceY) {
					setRunningLeft();
				} else {
					setRunningDown();
				}
			}
		}

		// checkIfHasToChase(shape);
		//
		// if (hasToChase) {
		// shapeX = shape.getX() + shape.getWidth() / 2;
		// shapeY = shape.getY() + shape.getHeight() / 2;
		//
		// centerX = getX() + getWidth() / 2;
		// centerY = getY() + getWidth() / 2;
		//
		//
		// }

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
			} else {
				if (distanceX > distanceY) {
					setRunningRight();
				} else {
					setRunningDown();
				}
			}
		} else {
			if (shapeY >= centerY) {
				if (distanceX > distanceY) {
					setRunningLeft();
				} else {
					setRunningUp();
				}
			} else {
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

			if (CollisionManager.getInstance().checkCollisionObstacles(this) != null) {
				setX(beforeMoveX);
			}

			if (shapeY > centerY) {
				setY(getY() + (distanceY / distance) * speed);
			} else if (shapeY < centerY) {
				setY(getY() - (distanceY / distance) * speed);
			}

			if (CollisionManager.getInstance().checkCollisionObstacles(this) != null) {
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
		final PhysicsConnector facePhysicsConnector = mWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(this);
		mWorld.unregisterPhysicsConnector(facePhysicsConnector);
		mWorld.destroyBody(facePhysicsConnector.getBody());
		detachSelf();
	}

}
