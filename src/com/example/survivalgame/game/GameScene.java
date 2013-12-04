package com.example.survivalgame.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.shape.RectangularShape;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

import android.graphics.PointF;
import android.opengl.GLES20;
import android.util.Log;

import com.example.survivalgame.BaseScene;
import com.example.survivalgame.SceneManager;
import com.example.survivalgame.TextureGameManager;
import com.example.survivalgame.SceneManager.SceneType;
import com.example.survivalgame.util.Util;

public class GameScene extends BaseScene implements IOnSceneTouchListener {

	public HUD inventoryHud;
	private GameHUD gameHUD;
	private Player player;
	private MapGame map;

	Rectangle blockScreen;

	int bulletCounter = 20;

	boolean blocking = true;

	boolean moveAllowed = true;

	boolean checkCollision = true;

	float hourOfDay;

	Sprite light;

	public AnalogOnScreenControl movementOnScreenControl;
	private static final String TAG = "GAME";
	float x = 0, y = 0;
	float speed = 1.0f;
	HashMap<String, Integer> inventoryPlayer;
	ArrayList<ItemInventory> itemsAlreadyLoaded;
	int posInventoryItemY = 100;
	PointF beforeEntrancePosition;
	PointF teleportToPosition;
	Rectangle enterBuilding;
	CollisionManager collisionManager;

	BulletsPool bulletsPool;

	RectangularShape itemToPick;

	private void createBackground() {
		setBackground(new Background(Color.BLACK));
	}

	private void createHUD() {
		gameHUD = new GameHUD(camera, vbom, this);
		gameHUD.bulletCounter.setText("Bullets: " + bulletCounter);
	}

	public void createInventory() {
		inventoryHud = new HUD();

		Rectangle background = new Rectangle(0, 0, 800, 480, vbom);
		background.setColor(Color.BLACK);
		inventoryHud.attachChild(background);

		Rectangle menuBorder = new Rectangle(0, 0, 500, 300, vbom);
		menuBorder.setPosition(400 - menuBorder.getWidth() / 2, 240 - menuBorder.getHeight() / 2);
		inventoryHud.attachChild(menuBorder);

		Rectangle menuBackground = new Rectangle(0, 0, 490, 290, vbom);
		menuBackground.setColor(Color.BLACK);
		menuBackground.setPosition(400 - menuBackground.getWidth() / 2, 240 - menuBackground.getHeight() / 2);
		inventoryHud.attachChild(menuBackground);

		Text inventoryText = new Text(20, 5, resourcesManager.font, "Inventario", new TextOptions(HorizontalAlign.LEFT), vbom);
		inventoryText.setPosition(400 - inventoryText.getWidth() / 2, 240 - inventoryText.getHeight() / 2);
		inventoryHud.attachChild(inventoryText);

		Text closeText = new Text(390, 470, resourcesManager.font, "Close", new TextOptions(HorizontalAlign.LEFT), vbom) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionDown()) {
					camera.setHUD(gameHUD);
					movementOnScreenControl.setVisible(true);
					movementOnScreenControl.setIgnoreUpdate(false);
				}
				return true;
			}
		};

		inventoryHud.registerTouchArea(closeText);
		closeText.setScale(0.7f);
		closeText.setPosition(400 - closeText.getWidth() / 2, 390 - closeText.getHeight());

		inventoryHud.attachChild(closeText);

	}

	public void populateInventory() {
		Iterator<Map.Entry<String, Integer>> entries = inventoryPlayer.entrySet().iterator();

		while (entries.hasNext()) {
			Map.Entry<String, Integer> entry = entries.next();
			Log.v(TAG, "Key = " + entry.getKey() + ", Value = " + entry.getValue());

			int position = checkAlreadyLoaded(entry.getKey());
			if (position == -1) {
				ItemInventory itemTest = new ItemInventory(180, posInventoryItemY, entry.getKey(), TextureGameManager.getInstance().getTexture(entry.getKey()), resourcesManager,
						vbom);
				itemTest.setQuantity(entry.getValue());
				inventoryHud.attachChild(itemTest);
				itemsAlreadyLoaded.add(itemTest);
				posInventoryItemY += 40;
			} else {
				ItemInventory itemTempo = itemsAlreadyLoaded.get(position);
				itemTempo.setQuantity(entry.getValue());
				itemsAlreadyLoaded.set(position, itemTempo);
			}

		}
	}

	public int checkAlreadyLoaded(String key) {
		for (int i = 0; i < itemsAlreadyLoaded.size(); i++) {
			if (itemsAlreadyLoaded.get(i).name.equals(key)) {
				return i;
			}
		}
		return -1;
	}

	public void createMap() {

		map = new MapGame("tmx/newDesert.tmx", activity, engine, vbom);
		attachChild(map.getLayer(0));

		for (int i = 0; i < collisionManager.items.size(); i++) {
			attachChild(collisionManager.items.get(i));
		}

	}

	public void createPlayer() {
		player = new Player(200, 200, vbom, camera) {
			@Override
			public void onDie() {
			}
		};
		player.setCurrentTileIndex(8);

		// player.shadow.setPosition(player);
		player.shadow.setPosition(player.getX(), player.getY() + player.getHeight() * 1.3f);
		attachChild(player.shadow);

		attachChild(player);

	}

	public void createControl() {
		final float x1 = 20;
		final float y1 = 470 - resourcesManager.mOnScreenControlBaseTextureRegion.getHeight();
		movementOnScreenControl = new AnalogOnScreenControl(x1, y1, camera, resourcesManager.mOnScreenControlBaseTextureRegion, resourcesManager.mOnScreenControlKnobTextureRegion,
				0.1f, vbom, new IAnalogOnScreenControlListener() {
					@Override
					public void onControlChange(final BaseOnScreenControl pBaseOnScreenControl, final float pValueX, final float pValueY) {

						if (pValueX > 0 && pValueY < 0) {
							if (pValueX > (pValueY * -1)) {
								player.setRunningRight();
							} else {
								player.setRunningUp();
							}
						} else if (pValueX > 0 && pValueY > 0) {
							if (pValueX > pValueY) {
								player.setRunningRight();
							} else {
								player.setRunningDown();
							}
						} else if (pValueX < 0 && pValueY > 0) {
							if ((pValueX * -1) > pValueY) {
								player.setRunningLeft();
							} else {
								player.setRunningDown();
							}
						} else if (pValueX < 0 && pValueY < 0) {
							if (pValueX < pValueY) {
								player.setRunningLeft();
							} else {
								player.setRunningUp();
							}
						} else {
							player.stopRunning();
						}
						x = pValueX * 1.5f;
						y = pValueY * 1.5f;

					}

					@Override
					public void onControlClick(final AnalogOnScreenControl pAnalogOnScreenControl) {
						/* Nothing. */
					}
				});
		movementOnScreenControl.getControlBase().setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		movementOnScreenControl.getControlBase().setAlpha(0.5f);

		gameHUD.setChildScene(movementOnScreenControl);
	}

	public void createLimits() {

		Rectangle topLimit = new Rectangle(0, 30, map.getWidth(), 2, vbom);
		Rectangle bottomLimit = new Rectangle(0, map.getHeight(), map.getWidth(), 2, vbom);
		Rectangle leftLimit = new Rectangle(0, 0, 2, map.getHeight(), vbom);
		Rectangle rightLimit = new Rectangle(map.getWidth(), 0, 2, map.getHeight(), vbom);

		collisionManager.addObstacle(topLimit);
		collisionManager.addObstacle(bottomLimit);
		collisionManager.addObstacle(leftLimit);
		collisionManager.addObstacle(rightLimit);

		topLimit.setVisible(false);
		bottomLimit.setVisible(false);
		leftLimit.setVisible(false);
		rightLimit.setVisible(false);

		attachChild(topLimit);
		attachChild(bottomLimit);
		attachChild(leftLimit);
		attachChild(rightLimit);
	}

	public void createInitialBuildings() {
		Building building = new Building("tmx/building.tmx", vbom, activity.getAssets(), engine);
		TMXLayer layer = building.getLayer(0);
		layer.setPosition(-building.getWidth() - 350, 0);
		building.buildingFront.setPosition(300, 200);

		Log.v("GAME", "X: " + layer.getX() + " Y: " + layer.getY());

		attachChild(layer);
		attachChild(building.buildingFront);

	}

	public void init() {

		beforeEntrancePosition = new PointF();
		teleportToPosition = new PointF();

		collisionManager = CollisionManager.getInstance();
		itemsAlreadyLoaded = new ArrayList<ItemInventory>();
		inventoryPlayer = new HashMap<String, Integer>();
		bulletsPool = new BulletsPool(bulletCounter, vbom);
		

	}

	public void checkPickItem() {
		itemToPick = collisionManager.checkPickItem(player.feet);
		if (itemToPick != null) {
			gameHUD.showButtonC();
		} else {
			gameHUD.hideButtonC();
		}
	}

	public void fireBullet() {
		if (bulletCounter > 0) {
			Bullet bullet = bulletsPool.getBullet();
			bullet.setBusy();
			bullet.setPosAndDir(player.getX(), player.getY(), player.directionPointing);
			attachChild(bullet);
			bulletCounter--;
			gameHUD.bulletCounter.setText("Bullets: " + bulletCounter);
		}
	}

	public void blockScreenToTeleport() {
		blockScreen.setAlpha(0);
		blockScreen.setVisible(true);
		registerUpdateHandler(new TimerHandler(0.001f, true, new ITimerCallback() {
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				if (blocking) {
					float newAlpha = blockScreen.getAlpha() + 0.005f;
					if (newAlpha >= 1) {
						player.setPosition(teleportToPosition.x, teleportToPosition.y);
						camera.setCenterDirect(player.getX(), player.getY());
						blocking = false;
					} else {
						blockScreen.setAlpha(newAlpha);
					}
				} else {
					float newAlpha = blockScreen.getAlpha() - 0.005f;
					if (newAlpha <= 0) {
						blockScreen.setVisible(false);
						blocking = true;
						checkCollision = true;
						moveAllowed = true;
						unregisterUpdateHandler(pTimerHandler);
					} else {
						blockScreen.setAlpha(newAlpha);
					}
				}

			}
		}));
	}

	public void createLight() {
		light = new Sprite(-200, -400, resourcesManager.light_region, vbom);
		light.setBlendingEnabled(true);
		light.setBlendFunctionSource(GLES20.GL_DST_COLOR);
		light.setAlpha(0.0f);
		// light.setScale(1.5f);
		light.setZIndex(0);

		Sprite light2 = new Sprite(0, 0, resourcesManager.light_region, vbom);
		light2.setBlendingEnabled(true);
		light2.setBlendFunctionSource(GLES20.GL_DST_COLOR);
		light2.setAlpha(0.0f);
		// light2.setScale(1.5f);
		light2.setZIndex(0);

		Sprite light3 = new Sprite(0, 0, resourcesManager.light_region, vbom);
		light3.setBlendingEnabled(true);
		light3.setBlendFunctionSource(GLES20.GL_DST_COLOR);
		light3.setAlpha(0.0f);
		// light2.setScale(1.5f);
		light3.setZIndex(0);

		light.attachChild(light2);
		light.attachChild(light3);

		attachChild(light);

	}

	@Override
	public void createScene() {
		init();
		createBackground();
		createHUD();
		createMap();
		createInitialBuildings();

		blockScreen = new Rectangle(-1000, -1000, 2000, 2000, vbom);
		blockScreen.setColor(Color.BLACK);
		blockScreen.setVisible(false);
		attachChild(blockScreen);

		// Rectangle rec = new Rectangle(0, 0, map.getWidth(), map.getHeight(),
		// vbom);
		// rec.setColor(0.01f, 0.01f, 0.01f, 0.0f);
		// rec.setShaderProgram(SpotLight.getInstance());
		// attachChild(rec);

		// createLight();

		createPlayer();
		createControl();
		createInventory();
		createLimits();
		startTimerDay();

		// pScene.attachChild(light2);

		registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void reset() {
			}

			@Override
			public void onUpdate(float pSecondsElapsed) {

				PointF lastPosition = new PointF(player.getX(), player.getY());
				boolean canMove = true;

				x = x * speed;
				y = y * speed;

				if (moveAllowed) {
					player.setX(player.getX() + x);
					player.setY(player.getY() + y);

					// light.setPosition((player.getX()+player.getWidth()/2)-(light.getWidth()/2),
					// (player.getY()+player.getHeight()/2)-(light.getHeight()/2));
				}

				if (collisionManager.checkCollisionObstacles(player.feet)) {
					canMove = false;
				}

				if (!canMove && camera.getHUD() == gameHUD) {
					player.setPosition(lastPosition.x, lastPosition.y);

				}

				checkPickItem();

				if (checkCollision) {
					Rectangle door = (Rectangle) collisionManager.checkDoor(player.feet);
					if (door != null) {
						checkCollision = false;
						String[] relocation = ((String) door.getUserData()).split(",");
						if (relocation[0].equals("teleport")) {
							beforeEntrancePosition.x = player.getX();
							beforeEntrancePosition.y = player.getX();
							teleportToPosition.x = Float.parseFloat(relocation[1]);
							teleportToPosition.y = Float.parseFloat(relocation[2]);
							blockScreenToTeleport();
						} else {
							teleportToPosition.x = beforeEntrancePosition.x;
							teleportToPosition.y = beforeEntrancePosition.y;
							blockScreenToTeleport();
						}
						moveAllowed = false;
					}
				}

				bulletsPool.updateBullets();

				Util.centerX = Util.centerX + 1;
				Util.centerY = Util.centerY + 1;

			}
		});

		setOnSceneTouchListener(this);

	}

	public void actionButtonA() {
		speed = 1.1f;
	}

	public void releaseButtonA() {
		speed = 1.0f;
	}

	public void actionButtonB() {
		fireBullet();
	}

	public void releaseButtonB() {

	}

	public void actionButtonC() {
		// gameHUD.nightRect.setVisible(!gameHUD.nightRect.isVisible());
		pickItem();
	}

	public void releaseButtonC() {

	}

	public void startTimerDay() {
		registerUpdateHandler(new TimerHandler(0.01f, true, new ITimerCallback() {
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				if (hourOfDay > 24) {
					hourOfDay = 0;
				}

				if (hourOfDay > 18 || hourOfDay < 6) {
					Util.radio = Util.radio + 0.005f;
					if (Util.radio > 0.95f) {
						Util.radio = 0.95f;
					}

				} else {
					Util.radio = Util.radio - 0.005f;
					if (Util.radio < 0.0f) {
						Util.radio = 0.0f;
					}
				}

				gameHUD.timeDay.setText("Hour: " + ((int) hourOfDay));
				hourOfDay += 0.005f;
			}
		}));
	}

	public void pickItem() {
		if (itemToPick != null) {
			String key = (String) itemToPick.getUserData();
			if (inventoryPlayer.containsKey(key)) {
				int value = inventoryPlayer.get(key) + 1;
				inventoryPlayer.put(key, value);
			} else {
				inventoryPlayer.put(key, 1);
			}
			detachChild(itemToPick);
			itemToPick = null;
			gameHUD.hideButtonC();
			bulletCounter++;
			gameHUD.bulletCounter.setText("Bullets: " + bulletCounter);
		}
	}

	@Override
	public void onBackKeyPressed() {
		camera.setChaseEntity(null);
		camera.setCenterDirect(400, 240);
		SceneManager.getInstance().loadMenuScene(engine);
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_GAME;
	}

	@Override
	public void disposeScene() {
		camera.setHUD(null);
		camera.setCenter(400, 240);
		collisionManager.emptyAll();

		// TODO code responsible for disposing scene
		// removing all game scene objects.
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {

		Util.centerX = pSceneTouchEvent.getX();
		Util.centerY = 480 - pSceneTouchEvent.getY();
		return false;
	}
}