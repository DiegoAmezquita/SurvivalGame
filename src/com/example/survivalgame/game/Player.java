package com.example.survivalgame.game;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.example.survivalgame.ResourcesManager;
import com.example.survivalgame.Util.Direction;

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

	// ---------------------------------------------
	// CONSTRUCTOR
	// ---------------------------------------------

	public Player(float pX, float pY, VertexBufferObjectManager vbo, Camera camera) {
		super(pX, pY, ResourcesManager.getInstance().player_region, vbo);
		setScale(2);

		feet = new Rectangle(0, this.getHeight() - this.getHeight() / 3, this.getWidth(), this.getHeight() / 3, vbo);
		attachChild(feet);
		feet.setVisible(false);
		camera.setChaseEntity(this);
		directionPointing = Direction.RIGHT;
	}

	// ---------------------------------------------
	// CLASS LOGIC
	// ---------------------------------------------

	public void setRunningUp() {
		if (directionMove != Direction.UP) {
			final long[] PLAYER_ANIMATE = new long[] { 200, 200, 200, 200 };
			animate(PLAYER_ANIMATE, 12, 15, true);
			directionMove = Direction.UP;
			directionPointing = Direction.UP;
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

	public void setRunningDown() {
		if (directionMove != Direction.DOWN) {
			final long[] PLAYER_ANIMATE = new long[] { 200, 200, 200, 200 };
			animate(PLAYER_ANIMATE, 0, 3, true);
			directionMove = Direction.DOWN;
			directionPointing = Direction.DOWN;
		}
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
			directionMove = Direction.LEFT;
			directionPointing = Direction.LEFT;
		}
	}

	public void setRunningRight() {
		if (directionMove != Direction.RIGHT) {
			final long[] PLAYER_ANIMATE = new long[] { 200, 200, 200, 200 };
			animate(PLAYER_ANIMATE, 8, 11, true);
			directionMove = Direction.RIGHT;
			directionPointing = Direction.RIGHT;
		}
	}

	public void stopRunning() {
		stopAnimation();
		if (directionMove != null)
			switch (directionMove) {
			case UP:
				setCurrentTileIndex(13);
				break;
			case DOWN:
				setCurrentTileIndex(3);
				break;
			case RIGHT:
				setCurrentTileIndex(8);
				break;
			case LEFT:
				setCurrentTileIndex(7);
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