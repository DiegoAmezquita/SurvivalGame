package com.example.survivalgame.game;

import java.util.ArrayList;

import org.andengine.entity.shape.RectangularShape;

public class CollisionManager {

	ArrayList<RectangularShape> obstacles;
	ArrayList<RectangularShape> items;

	ArrayList<RectangularShape> doors;


	static CollisionManager instance;

	private CollisionManager() {
		obstacles = new ArrayList<RectangularShape>();
		items = new ArrayList<RectangularShape>();
		doors = new ArrayList<RectangularShape>();
	}

	public static CollisionManager getInstance() {
		if (instance == null) {
			instance = new CollisionManager();
		}
		return instance;
	}

	public void addObstacle(RectangularShape shape) {
		obstacles.add(shape);
	}

	public void addItem(RectangularShape shape) {
		items.add(shape);
	}

	public void addDoor(RectangularShape shape) {
		doors.add(shape);
	}



	public boolean checkCollisionObstacles(RectangularShape shape) {
		for (int i = 0; i < obstacles.size(); i++) {
			if (shape.collidesWith(obstacles.get(i))) {
				return true;
			}
		}
		return false;
	}

	public RectangularShape checkPickItem(RectangularShape shape) {
		for (int i = 0; i < items.size(); i++) {
			if (shape.collidesWith(items.get(i)) && items.get(i).hasParent()) {
				return items.get(i);
			}
		}
		return null;
	}

	public RectangularShape checkDoor(RectangularShape shape) {
		for (int i = 0; i < doors.size(); i++) {
			if (shape.collidesWith(doors.get(i))) {
				return doors.get(i);
			}
		}
		return null;
	}

	public void emptyAll() {
		obstacles.clear();
		items.clear();
		doors.clear();
	}

}
