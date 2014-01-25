package com.example.survivalgame.util;

import com.example.survivalgame.game.Enemy;

public class Task {

	public Enemy enemy;

	public Task(Enemy enemy) {
		this.enemy = enemy;
	}

	public void doTask() {
		if (enemy != null) {
			enemy.release();
		}
	}

}
