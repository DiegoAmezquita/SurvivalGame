package com.example.survivalgame.game;

import java.util.ArrayList;

import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.graphics.drawable.shapes.Shape;
import android.util.Log;

public class BulletsPool {

	private ArrayList<Bullet> bullets;

	private VertexBufferObjectManager vbom;
	
	PhysicsWorld mWorld;
	
	private ArrayList<Bullet> bulletsToRelease;

	public BulletsPool(int bulletsToInit, VertexBufferObjectManager vbom,PhysicsWorld mWorld) {
		bullets = new ArrayList<Bullet>();
		bulletsToRelease = new ArrayList<Bullet>();
		this.vbom = vbom;
		this.mWorld = mWorld;

		for (int i = 0; i < bullets.size(); i++) {
			bullets.add(new Bullet(0, 0, vbom,mWorld));
		}
	}

	public Bullet getBullet() {
		for (int i = 0; i < bullets.size(); i++) {
			Bullet bullet = bullets.get(i);
			bullets.remove(bullet);
			if (bullet.isFree()) {
				return bullet;
			}
		}
		Bullet bullet = new Bullet(0, 0, vbom,mWorld);
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
		bullets.add(bullet);
		bullet.release();
	}
	
	
	public void addBulletToRelease(Bullet bullet){
		bulletsToRelease.add(bullet);
	}
	
	public void releaseBullets(){
		
		for (int i = 0; i < bulletsToRelease.size(); i++) {
			Log.v("GAME","RELEASING");
			bulletsToRelease.get(i).release();
		}
	}
	

}
