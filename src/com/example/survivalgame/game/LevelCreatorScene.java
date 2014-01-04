package com.example.survivalgame.game;

import org.andengine.entity.primitive.Rectangle;

import com.example.survivalgame.BaseScene;
import com.example.survivalgame.SceneManager.SceneType;

public class LevelCreatorScene extends BaseScene {

	@Override
	public void createScene() {
		Rectangle rectangle = new Rectangle(0, 0, 50, 50, vbom);
		attachChild(rectangle);
		
	}

	@Override
	public void onBackKeyPressed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public SceneType getSceneType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void disposeScene() {
		// TODO Auto-generated method stub
		
	}

}
