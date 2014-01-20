package com.example.survivalgame.game;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.color.Color;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.example.survivalgame.ResourcesManager;
import com.example.survivalgame.util.Util.Direction;

/**
 * @author Mateusz Mysliwiec
 * @author www.matim-dev.com
 * @version 1.0
 */
public abstract class Player extends AnimatedSprite {
	// ---------------------------------------------
	// VARIABLES
	// ---------------------------------------------

	// private boolean canRun = false;

	private int footContacts = 0;

	Rectangle feet;

	Direction directionMove;

	Direction directionPointing;

	AnimatedSprite shadow;

	FixtureDef playerFixtureDef;

	Body body;

	float speed = 1f;

	long speedFrame = 200;
	long speedAttackFrame = 50;

	LoopEntityModifier swordMovement;

	boolean attacking;

	boolean running;

	Rectangle sword;

	// ---------------------------------------------
	// CONSTRUCTOR
	// ---------------------------------------------

	public Player(float pX, float pY, VertexBufferObjectManager vbom, Camera camera, PhysicsWorld mWorld) {
		super(pX, pY, ResourcesManager.getInstance().player_region, vbom);
		// setScale(2);

		playerFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);

		feet = new Rectangle(this.getWidth() / 2, this.getHeight() / 3 - (this.getHeight() / 6), this.getWidth() - 10, this.getHeight() / 3, vbom);
		attachChild(feet);
		feet.setVisible(false);
		camera.setChaseEntity(this);
		directionPointing = Direction.RIGHT;

		shadow = new AnimatedSprite(pX, pY, ResourcesManager.getInstance().player_region, vbom);
		shadow.setScale(1.5f);
		shadow.setColor(Color.BLACK);
		shadow.setFlippedVertical(true);
		shadow.setVisible(false);

		// body = PhysicsFactory.createBoxBody(mWorld, this,
		// BodyType.DynamicBody, playerFixtureDef);

		body = PhysicsFactory.createBoxBody(mWorld, getX(), getY(), getWidth() / 3, getHeight()/2, BodyType.DynamicBody, playerFixtureDef);

		body.setUserData("Player");
		mWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, true));

		attacking = false;
		running = false;

		createSword(vbom);
	}

	public void createSword(VertexBufferObjectManager vbom) {
		sword = new Rectangle(getWidth()/2+(getWidth()/6)+6, getHeight() / 2.5f, 12, 2, vbom);
		sword.setVisible(false);
		attachChild(sword);
	}

	// ---------------------------------------------
	// CLASS LOGIC
	// ---------------------------------------------

	public void setRunningUp() {
		if (directionMove != Direction.UP && !attacking) {
			
			sword.setSize(2, 12);
			sword.setPosition(getWidth()/2,getHeight()/2+10);
			final long[] PLAYER_ANIMATE = new long[] { speedFrame, speedFrame, speedFrame, speedFrame };
			if (!running) {
				animate(PLAYER_ANIMATE, 28, 31, true);
			} else {
				animate(PLAYER_ANIMATE, 56, 59, true);
			}

			directionMove = Direction.UP;
			directionPointing = Direction.UP;
		}
		if (running) {
			body.setLinearVelocity(0, speed * 2);
		} else {
			body.setLinearVelocity(0, speed);
		}

	}

	public void setRunningDown() {
		if (directionMove != Direction.DOWN && !attacking) {
			
			sword.setSize(2, 12);
			sword.setPosition(getWidth()/2,getHeight()/2-14);
			
			
			final long[] PLAYER_ANIMATE = new long[] { speedFrame, speedFrame, speedFrame, speedFrame };
			if (!running) {
				animate(PLAYER_ANIMATE, 4, 7, true);
			} else {
				animate(PLAYER_ANIMATE, 32, 35, true);
			}
			directionMove = Direction.DOWN;
			directionPointing = Direction.DOWN;
		}
		if (running) {
			body.setLinearVelocity(0, -speed * 2);
		} else {
			body.setLinearVelocity(0, -speed);
		}
	}

	public void setRunningLeft() {
		if (directionMove != Direction.LEFT && !attacking) {
			sword.setSize(12, 2);
			sword.setPosition(getWidth()/2-(getWidth()/6)-6,getHeight()/ 2.5f);
			
			final long[] PLAYER_ANIMATE = new long[] { speedFrame, speedFrame, speedFrame, speedFrame };
			if (!running) {
				animate(PLAYER_ANIMATE, 12, 15, true);
			} else {
				animate(PLAYER_ANIMATE, 40, 43, true);
			}
			directionMove = Direction.LEFT;
			directionPointing = Direction.LEFT;
		}
		if (running) {
			body.setLinearVelocity(-speed * 2, 0);
		} else {
			body.setLinearVelocity(-speed, 0);
		}
	}

	public void setRunningRight() {

		if (directionMove != Direction.RIGHT && !attacking) {
			sword.setSize(12, 2);
			sword.setPosition(getWidth()/2+(getWidth()/6)+6,getHeight()/ 2.5f);
			final long[] PLAYER_ANIMATE = new long[] { speedFrame, speedFrame, speedFrame, speedFrame };
			if (!running) {
				animate(PLAYER_ANIMATE, 20, 23, true);
			} else {
				animate(PLAYER_ANIMATE, 48, 51, true);
			}
			directionMove = Direction.RIGHT;
			directionPointing = Direction.RIGHT;
		}
		if (running) {
			body.setLinearVelocity(speed * 2, 0);
		} else {
			body.setLinearVelocity(speed, 0);
		}
	}

	public void stopRunning() {
		body.setLinearVelocity(0, 0);
		if (!attacking) {
			stopAnimation();
		}
		shadow.stopAnimation();
		if (directionMove != null)
			switch (directionMove) {
			case UP:
				setCurrentTileIndex(31);
				shadow.setCurrentTileIndex(31);
				break;
			case DOWN:
				setCurrentTileIndex(7);
				shadow.setCurrentTileIndex(7);
				break;
			case RIGHT:
				setCurrentTileIndex(23);
				shadow.setCurrentTileIndex(23);
				break;
			case LEFT:
				setCurrentTileIndex(15);
				shadow.setCurrentTileIndex(15);
				break;
			case UPRIGHT:
				setCurrentTileIndex(23);
				shadow.setCurrentTileIndex(23);
				break;
			case UPLEFT:
				setCurrentTileIndex(35);
				shadow.setCurrentTileIndex(35);
				break;
			case DOWNRIGHT:
				setCurrentTileIndex(11);
				shadow.setCurrentTileIndex(11);
				break;
			case DOWNLEFT:
				setCurrentTileIndex(47);
				shadow.setCurrentTileIndex(47);
				break;
			default:
				break;
			}
		directionMove = Direction.NONE;
	}

	public void attack() {
		if (!attacking) {
			attacking = true;

			final long[] PLAYER_ANIMATE = new long[] { speedAttackFrame, speedAttackFrame, speedAttackFrame, speedAttackFrame, speedAttackFrame };
			int[] FRAMES;

			IAnimationListener animationListener = new IAnimationListener() {
				@Override
				public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
					attacking = false;
					directionMove = Direction.NONE;
					stopRunning();
				}

				@Override
				public void onAnimationStarted(AnimatedSprite pAnimatedSprite, int pInitialLoopCount) {
				}

				@Override
				public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite, int pOldFrameIndex, int pNewFrameIndex) {
				}

				@Override
				public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite, int pRemainingLoopCount, int pInitialLoopCount) {
				}
			};

			switch (directionPointing) {
			case UP:
				FRAMES = new int[] { 24, 25, 26, 27, 29 };
				animate(PLAYER_ANIMATE, FRAMES, false, animationListener);
				break;
			case DOWN:
				FRAMES = new int[] { 0, 1, 2, 3, 5 };
				animate(PLAYER_ANIMATE, FRAMES, false, animationListener);
				break;
			case LEFT:
				FRAMES = new int[] { 8, 9, 10, 11, 13 };
				animate(PLAYER_ANIMATE, FRAMES, false, animationListener);
				break;
			case RIGHT:
				FRAMES = new int[] { 16, 17, 18, 19, 21 };
				animate(PLAYER_ANIMATE, FRAMES, false, animationListener);
				break;
			default:
				break;

			}
		}
	}

	@Override
	public void setX(float pX) {
		super.setX(pX);
		shadow.setX(getX());
	}

	@Override
	public void setY(float pY) {
		super.setY(pY);
		shadow.setY(pY - getHeight() * 1.4f);
	}

	@Override
	public void setPosition(float pX, float pY) {
		super.setPosition(pX, pY);
		shadow.setPosition(getX(), pY - getHeight() * 1.4f);
	}

	public void jump() {
		if (footContacts < 1) {
			return;
		}
	}

	public void increaseFootContacts() {
		footContacts++;
	}

	public void decreaseFootContacts() {
		footContacts--;
	}

	public abstract void onDie();
}