package com.example.survivalgame.game;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;


public class Building extends Rectangle{

	public Building(float pX, float pY,  VertexBufferObjectManager vbom) {
		super(pX, pY, 200, 200, vbom);
		setColor(Color.RED);
	}

}
