package com.example.survivalgame.game;

import java.util.ArrayList;

import org.andengine.entity.shape.RectangularShape;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class EnemiesPool {

	private ArrayList<Enemy> enemies;

	private VertexBufferObjectManager vbom;
	
	GameScene mGameScene;

	public EnemiesPool(int enemiesToInit,GameScene gameScene, VertexBufferObjectManager vbom) {
		enemies = new ArrayList<Enemy>();
		this.vbom = vbom;
		this.mGameScene = gameScene;

		for (int i = 0; i < enemies.size(); i++) {
			enemies.add(new Enemy(0, 0, vbom,mGameScene));
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
		Enemy enemy = new Enemy(0, 0, vbom,mGameScene);
		enemies.add(enemy);
		CollisionManager.getInstance().addEnemy(enemy);
		return enemy;
	}

	public void updateEnemies(RectangularShape shape) {
		for (int i = 0; i < enemies.size(); i++) {
			Enemy enemy = enemies.get(i);
			if (!enemy.isFree()) {
				enemies.get(i).chaseEntity(shape);
			}
		}
	}

	// public void checkCollision(RectangularShape shape) {
	// for (int i = 0; i < enemies.size(); i++) {
	// Enemy enemy = enemies.get(i);
	// if (!enemy.isFree() && shape.collidesWith(enemy)) {
	// enemy.release();
	// enemy.getParent().detachChild(enemy);
	// }
	// }
	// }

	public void releaseEnemy(Enemy enemy) {
		enemy.release();
	}

}
