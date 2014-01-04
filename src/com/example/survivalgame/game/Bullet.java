package com.example.survivalgame.game;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.graphics.PointF;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.example.survivalgame.util.Util.Direction;

public class Bullet extends Rectangle {

	private boolean free;

	private float speed = 15f;

	Direction directionMove;

	PointF initialPosition;

	PhysicsWorld mWorld;

	Body bulletBody;

	PhysicsConnector physicConnector;

	FixtureDef bulletFixtureDef;

	public enum kindOfBullet {
		PISTOL, SHOTGUN, SNIPER
	}

	public Bullet(float pX, float pY, VertexBufferObjectManager pVertexBufferObjectManager, PhysicsWorld mWorld) {
		super(pX + 200, pY, 5, 5, pVertexBufferObjectManager);

		this.mWorld = mWorld;
		bulletFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);

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
		if (hasParent()) {
			getParent().detachChild(this);
		}
		mWorld.unregisterPhysicsConnector(physicConnector);
		if (bulletBody != null) {
			mWorld.destroyBody(bulletBody);
		}

		bulletBody.setLinearVelocity(0, 0);

	}

	public void setPosAndDir(float pX, float pY, Direction direction) {
		this.directionMove = direction;

		setPosition(pX + 100, pY);

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

	public void shotBullet() {

		bulletBody = PhysicsFactory.createBoxBody(mWorld, this, BodyType.DynamicBody, bulletFixtureDef);
		bulletBody.setUserData(this);
		if (physicConnector == null) {
			physicConnector = new PhysicsConnector(this, bulletBody, true, true);
		} else {
			mWorld.unregisterPhysicsConnector(physicConnector);
		}

		mWorld.registerPhysicsConnector(physicConnector);

		Log.v("GAME", "Shooting");

		if (directionMove != null) {
			switch (directionMove) {
			case UP:
				bulletBody.applyForce(new Vector2(0, speed), new Vector2());
				break;
			case DOWN:
				bulletBody.applyForce(new Vector2(0, -speed), new Vector2());
				break;
			case RIGHT:
				bulletBody.applyLinearImpulse(speed, 0, 0, 0);
				break;
			case LEFT:
				bulletBody.applyForce(new Vector2(-speed, 0), new Vector2());
				break;
			default:
				break;
			}
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

		if (getY() < initialPosition.y - 500 || getY() > initialPosition.y + 500 || getX() < initialPosition.x - 500 || getX() > initialPosition.x + 500) {
			release();
			getParent().detachChild(this);
		} else if (CollisionManager.getInstance().checkCollisionObstacles(this) != null || CollisionManager.getInstance().checkCollisionEnemy(this)) {
			release();
			getParent().detachChild(this);
		}

	}

}
