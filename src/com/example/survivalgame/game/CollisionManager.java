package com.example.survivalgame.game;

import java.util.ArrayList;

import org.andengine.entity.shape.Shape;


public class CollisionManager {

	ArrayList<Shape> obstacles;
	ArrayList<Shape> items;

	ArrayList<Shape> doors;
	
	ArrayList<Enemy> enemies;

	static CollisionManager instance;

	private CollisionManager() {
		obstacles = new ArrayList<Shape>();
		items = new ArrayList<Shape>();
		doors = new ArrayList<Shape>();
		enemies = new ArrayList<Enemy>();
	}

	public static CollisionManager getInstance() {
		if (instance == null) {
			instance = new CollisionManager();
		}
		return instance;
	}

	public void addObstacle(Shape shape) {
		obstacles.add(shape);
	}

	public void addItem(Shape shape) {
		items.add(shape);
	}

	public void addDoor(Shape shape) {
		doors.add(shape);
	}

	public void removeObstacle(Shape shape) {
		obstacles.remove(shape);
	}
	
	
	public void addEnemy(Enemy enemy) {
		enemies.add(enemy);
	}
	
	public void removeEnemy(Enemy enemy) {
		enemies.remove(enemy);
	}

	public boolean checkCollisionObstacles(Shape shape) {
		for (int i = 0; i < obstacles.size(); i++) {
			if (shape.collidesWith(obstacles.get(i))) {
				return true;
			}
		}
		return false;
	}

	public Shape checkPickItem(Shape shape) {
		for (int i = 0; i < items.size(); i++) {
			if (shape.collidesWith(items.get(i)) && items.get(i).hasParent()) {
				return items.get(i);
			}
		}
		return null;
	}

	public Shape checkDoor(Shape shape) {
		for (int i = 0; i < doors.size(); i++) {
			if (shape.collidesWith(doors.get(i))) {
				return doors.get(i);
			}
		}
		return null;
	}
	
	
	public boolean checkCollisionEnemy(Shape shape) {
		for (int i = 0; i < enemies.size(); i++) {
			Enemy enemy = enemies.get(i);
			if (shape.collidesWith(enemy)) {
				removeEnemy(enemy);
				enemy.getParent().detachChild(enemy);
				enemy.release();
				return true;
			}
		}
		return false;
	}

	public void emptyAll() {
		obstacles.clear();
		items.clear();
		doors.clear();
	}

}
