package com.example.survivalgame.util;

import android.graphics.Point;

public class Util {

	public enum Direction {
		UP, DOWN, LEFT, RIGHT, UPRIGHT, UPLEFT, DOWNRIGHT, DOWNLEFT, NONE;
	}

	public static float centerX = 0;
	public static float centerY = 0;

	public static float widthScreen;
	public static float heightScreen;

	public static float oppacityScreen = 0.95f;
	
	public static float ratioWidth = 1f;
	public static float ratioHeight = 1f;
	
	
	public static Point getRandomPosition(){
		Point randomPosition = new Point();
		randomPosition.x = (int)(Math.random()*500);
		randomPosition.y = (int)(Math.random()*500);
		return randomPosition;
	}

}
