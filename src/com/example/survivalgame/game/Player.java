package com.example.survivalgame.game;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.modifier.PathModifier.IPathModifierListener;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.color.Color;
import org.andengine.util.modifier.ease.EaseSineInOut;

import android.graphics.PointF;

import com.badlogic.gdx.math.Vector2;
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

	Body playerBody;

	float speed = 1;

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

		playerBody = PhysicsFactory.createBoxBody(mWorld, this, BodyType.DynamicBody, playerFixtureDef);
		playerBody.setUserData("Player");
		mWorld.registerPhysicsConnector(new PhysicsConnector(this, playerBody, true, true));

		// createSword(vbom);
	}

	public void createSword(VertexBufferObjectManager vbom) {
		Rectangle sword = new Rectangle(getWidth() + 3, getHeight() / 2, 10, 3, vbom);
		attachChild(sword);

		final Path path = new Path(3).to(getWidth(), getHeight() / 2).to(getWidth() + 20, getHeight() / 2).to(getWidth(), getHeight() / 2);

		sword.setRotationCenter(0, 0);

		// sword.registerEntityModifier(new LoopEntityModifier(new
		// RotationModifier(1, -70, 100)));

		sword.registerEntityModifier(new LoopEntityModifier(new PathModifier(0.3f, path)));

	}

	// ---------------------------------------------
	// CLASS LOGIC
	// ---------------------------------------------

	public void setRunningUp() {
		if (directionMove != Direction.UP) {
			final long[] PLAYER_ANIMATE = new long[] { 200, 200, 200, 200 };
			animate(PLAYER_ANIMATE, 12, 15, true);
			shadow.animate(PLAYER_ANIMATE, 12, 15, true);
			directionMove = Direction.UP;
			directionPointing = Direction.UP;
		}
		playerBody.setLinearVelocity(0, speed);
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

	public void setRunningDown() {
		if (directionMove != Direction.DOWN) {
			final long[] PLAYER_ANIMATE = new long[] { 200, 200, 200, 200 };
			animate(PLAYER_ANIMATE, 0, 3, true);
			shadow.animate(PLAYER_ANIMATE, 0, 3, true);
			directionMove = Direction.DOWN;
			directionPointing = Direction.DOWN;
		}
		playerBody.setLinearVelocity(0, -speed);
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

	public void setRunningLeft() {
		if (directionMove != Direction.LEFT) {
			final long[] PLAYER_ANIMATE = new long[] { 200, 200, 200, 200 };
			animate(PLAYER_ANIMATE, 4, 7, true);
			shadow.animate(PLAYER_ANIMATE, 4, 7, true);
			directionMove = Direction.LEFT;
			directionPointing = Direction.LEFT;
		}
		playerBody.setLinearVelocity(-speed, 0);
	}

	public void setRunningRight() {
		if (directionMove != Direction.RIGHT) {
			final long[] PLAYER_ANIMATE = new long[] { 200, 200, 200, 200 };
			animate(PLAYER_ANIMATE, 8, 11, true);
			shadow.animate(PLAYER_ANIMATE, 8, 11, true);
			directionMove = Direction.RIGHT;
			directionPointing = Direction.RIGHT;
		}
		playerBody.setLinearVelocity(speed, 0);
	}

	public void stopRunning() {
		playerBody.setLinearVelocity(0, 0);
		stopAnimation();
		shadow.stopAnimation();
		if (directionMove != null)
			switch (directionMove) {
			case UP:
				setCurrentTileIndex(13);
				shadow.setCurrentTileIndex(13);
				break;
			case DOWN:
				setCurrentTileIndex(3);
				shadow.setCurrentTileIndex(3);
				break;
			case RIGHT:
				setCurrentTileIndex(8);
				shadow.setCurrentTileIndex(8);
				break;
			case LEFT:
				setCurrentTileIndex(7);
				shadow.setCurrentTileIndex(7);
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