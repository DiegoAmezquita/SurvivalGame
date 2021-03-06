package com.example.survivalgame.game;

import java.util.ArrayList;

import org.andengine.entity.shape.Shape;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class EnemiesPool {

	public ArrayList<Enemy> enemies;

	private VertexBufferObjectManager vbom;

	GameScene mGameScene;

	PhysicsWorld mWorld;

	public EnemiesPool(int enemiesToInit, GameScene gameScene, VertexBufferObjectManager vbom, PhysicsWorld mWorld) {
		enemies = new ArrayList<Enemy>();
		this.vbom = vbom;
		this.mGameScene = gameScene;
		this.mWorld = mWorld;

		for (int i = 0; i < enemies.size(); i++) {
			enemies.add(new Enemy(100, 100, vbom, mGameScene, mWorld));
		}
	}

	public Enemy getEnemy() {
		for (int i = 0; i < enemies.size(); i++) {
			Enemy enemy = enemies.get(i);
			if (enemy.isFree()) {
				CollisionManager.getInstance().addEnemy(enemy);
				return enemy;
			}
		}
		Enemy enemy = new Enemy(0, 0, vbom, mGameScene, mWorld);
		enemies.add(enemy);
		CollisionManager.getInstance().addEnemy(enemy);
		return enemy;
	}

	public void updateEnemies(Shape shape) {
		for (int i = 0; i < enemies.size(); i++) {
			Enemy enemy = enemies.get(i);
			if (!enemy.isFree()) {
				enemies.get(i).chaseEntity(shape);
			}
		}
	}

	public void stopEnemies() {
		for (int i = 0; i < enemies.size(); i++) {
			Enemy enemy = enemies.get(i);
			if (!enemy.isFree()) {
				enemies.get(i).stopRunning();
			}
		}
	}

	public void releaseEnemy(Enemy enemy) {
		enemy.release();
	}

}
