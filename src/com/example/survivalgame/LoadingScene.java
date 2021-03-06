package com.example.survivalgame;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;
import org.andengine.util.adt.color.Color;

import com.example.survivalgame.SceneManager.SceneType;

public class LoadingScene extends BaseScene {
	@Override
	public void createScene() {
		setBackground(new Background(Color.BLACK));
		Text textLoading = new Text(400, 240, resourcesManager.font, "Loading...", vbom);
		textLoading.setPosition(400, 240);
		attachChild(textLoading);
	}

	@Override
	public void onBackKeyPressed() {
		return;
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_LOADING;
	}

	@Override
	public void disposeScene() {

	}
}