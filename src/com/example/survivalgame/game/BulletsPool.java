package com.example.survivalgame.game;

import java.util.ArrayList;

import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class BulletsPool {

	private ArrayList<Bullet> bullets;

	private VertexBufferObjectManager vbom;

	public BulletsPool(int bulletsToInit, VertexBufferObjectManager vbom) {
		bullets = new ArrayList<Bullet>();
		this.vbom = vbom;

		for (int i = 0; i < bullets.size(); i++) {
			bullets.add(new Bullet(0, 0, vbom));
		}
	}

	public Bullet getBullet() {
		for (int i = 0; i < bullets.size(); i++) {
			Bullet bullet = bullets.get(i);
			if (bullet.isFree()) {
				return bullet;
			}
		}
		Bullet bullet = new Bullet(0, 0, vbom);
		bullets.add(bullet);
		return bullet;
	}

	public void updateBullets() {
		for (int i = 0; i < bullets.size(); i++) {
			Bullet bullet = bullets.get(i);
			if (!bullet.isFree()) {
				bullets.get(i).updatePosition();
			}
		}
	}

	public void releaseBullet(Bullet bullet) {
		bullet.release();
	}

}
