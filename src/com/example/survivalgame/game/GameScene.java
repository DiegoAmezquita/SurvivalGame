package com.example.survivalgame.game;

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
import org.andengine.entity.shape.Shape;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.adt.color.Color;
import org.andengine.util.math.MathUtils;

import android.graphics.PointF;
import android.opengl.GLES20;
import android.util.Log;

import com.example.survivalgame.BaseScene;
import com.example.survivalgame.SceneManager;
import com.example.survivalgame.SceneManager.SceneType;
import com.example.survivalgame.util.Util;

public class GameScene extends BaseScene implements IOnSceneTouchListener {

	public GameHUD gameHUD;
	public InventoryHUD inventoryHUD;
	private Player player;
	private MapGame map;

	int bulletCounter = 20;
	boolean blocking = true;
	boolean moveAllowed = true;
	boolean checkCollision = true;
	float hourOfDay = 6;
	boolean gameOver = false;
	Sprite light;

	public AnalogOnScreenControl movementOnScreenControl;
	// private static final String TAG = "GAME";
	float x = 0, y = 0;
	float speed = 0.85f;

	PointF beforeEntrancePosition;
	PointF teleportToPosition;
	Rectangle enterBuilding;
	CollisionManager collisionManager;

	BulletsPool bulletsPool;

	EnemiesPool enemiesPool;

	InventoryPlayer inventoryPlayer;

	Shape itemToPick;

	int life = 100;

	Sprite car;
	float carRotation = 0;

	boolean driveCar = false;

	int timeElapsedBetweenDamage = 2;

	private void createBackground() {
		setBackground(new Background(Color.BLACK));
	}

	private void createHUD() {
		gameHUD = new GameHUD(camera, vbom, this);
		bulletCounter = 20;
		Log.v("GAME", "Bullets " + bulletCounter);
		gameHUD.bulletCounter.setText("Bullets: " + bulletCounter);
	}

	private void createInventoryHUD() {
		inventoryHUD = new InventoryHUD(camera, this, vbom);
	}

	public void createMap() {

		map = new MapGame("tmx/beach.tmx", activity, engine, vbom);
		for (int i = 0; i < map.getNumberLayers(); i++) {
			map.getLayer(i).detachSelf();
			if(!map.getLayer(i).getName().equals("Items")){
				attachChild(map.getLayer(i));	
			}
				
		}
		
		

		// MapGame map2 = new MapGame("tmx/newDesert.tmx", activity, engine,
		// vbom);
		// map2.getLayer(0).detachSelf();
		// map2.getLayer(0).setPosition(-500, 0);
		// attachChild(map2.getLayer(0));

		for (int i = 0; i < collisionManager.items.size(); i++) {
			attachChild(collisionManager.items.get(i));
		}

	}

	public void createPlayer() {
		player = new Player(500, 500, vbom, camera) {
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
		final float x1 = 80;
		final float y1 = 80;
		movementOnScreenControl = new AnalogOnScreenControl(x1, y1, camera, resourcesManager.mOnScreenControlBaseTextureRegion, resourcesManager.mOnScreenControlKnobTextureRegion, 0.1f, vbom,
				new IAnalogOnScreenControlListener() {
					@Override
					public void onControlChange(final BaseOnScreenControl pBaseOnScreenControl, final float pValueX, final float pValueY) {

						if (driveCar) {
							float rotationInRad = (float) Math.atan2(pValueX, pValueY);
							carRotation = MathUtils.radToDeg(rotationInRad);
							if (carRotation != 0) {
								carRotation += 90;
								car.setRotation(carRotation);
							}
						}

						if (pValueX > 0 && pValueY < 0) {
							if (pValueX > (pValueY * -1)) {
								player.setRunningRight();
							} else {
								player.setRunningDown();
							}
						} else if (pValueX > 0 && pValueY > 0) {
							if (pValueX > pValueY) {
								player.setRunningRight();
							} else {
								player.setRunningUp();
							}
						} else if (pValueX < 0 && pValueY > 0) {
							if ((pValueX * -1) > pValueY) {
								player.setRunningLeft();
							} else {
								player.setRunningUp();
							}
						} else if (pValueX < 0 && pValueY < 0) {
							if (pValueX < pValueY) {
								player.setRunningLeft();
							} else {
								player.setRunningDown();
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

		Rectangle topLimit = new Rectangle(map.getWidth() / 2, map.getHeight() - 30, map.getWidth(), 2, vbom);
		Rectangle bottomLimit = new Rectangle(map.getWidth() / 2, 0, map.getWidth(), 2, vbom);
		Rectangle leftLimit = new Rectangle(0, map.getHeight() / 2, 2, map.getHeight(), vbom);
		Rectangle rightLimit = new Rectangle(map.getWidth(), map.getHeight() / 2, 2, map.getHeight(), vbom);

		collisionManager.addObstacle(topLimit);
		collisionManager.addObstacle(bottomLimit);
		collisionManager.addObstacle(leftLimit);
		collisionManager.addObstacle(rightLimit);

		// topLimit.setVisible(false);
		// bottomLimit.setVisible(false);
		// leftLimit.setVisible(false);
		// rightLimit.setVisible(false);

		attachChild(topLimit);
		attachChild(bottomLimit);
		attachChild(leftLimit);
		attachChild(rightLimit);
	}

	public void createInitialBuildings(int posX, int posY, ITextureRegion textureFront) {
		Building building = new Building("tmx/building.tmx", vbom, activity.getAssets(), engine);
		TMXLayer layer = building.getLayer(0);
		layer.setPosition(-building.getWidth() - 350, 0);
		building.createBuildingFront(textureFront);
		building.buildingFront.setPosition(posX, posY);

		Log.v("GAME", "X: " + layer.getX() + " Y: " + layer.getY());

		building.buildingFront.setZIndex(10000 - (int) building.buildingFront.getY());
		layer.detachSelf();
		attachChild(layer);
		attachChild(building.buildingFront);

	}

	public void testBatch() {
		int nextX = 100;

		ITextureRegion[] arrayTexturesBuildings = new ITextureRegion[9];
		arrayTexturesBuildings[0] = resourcesManager.mBuildingFront1;
		arrayTexturesBuildings[1] = resourcesManager.mBuildingFront2;
		arrayTexturesBuildings[2] = resourcesManager.mBuildingFront3;
		arrayTexturesBuildings[3] = resourcesManager.mBuildingFront4;
		arrayTexturesBuildings[4] = resourcesManager.mBuildingFront5;
		arrayTexturesBuildings[5] = resourcesManager.mBuildingFront6;
		arrayTexturesBuildings[6] = resourcesManager.mBuildingFront7;
		arrayTexturesBuildings[7] = resourcesManager.mBuildingFront8;
		arrayTexturesBuildings[8] = resourcesManager.mBuildingFront9;

		for (int i = 0; i < 9; i++) {

			// createInitialBuildings(nextX, 600, arrayTexturesBuildings[i]);
			nextX += arrayTexturesBuildings[i].getWidth() + 10;
			// Sprite test = new Sprite(nextX, 600, arrayTexturesBuildings[i],
			// vbom);

			// attachChild(test);
			// collisionManager.addObstacle(test);
			// test.setZIndex(10000 - (int) test.getY());
		}

	}

	public void createCar() {
		car = new Sprite(400, 240, resourcesManager.mCar, vbom);
		car.setScale(0.7f);
		collisionManager.addObstacle(car);
		attachChild(car);
	}

	public void init() {

		beforeEntrancePosition = new PointF();
		teleportToPosition = new PointF();

		collisionManager = CollisionManager.getInstance();

		bulletsPool = new BulletsPool(bulletCounter, vbom);

		enemiesPool = new EnemiesPool(20, this, vbom);

		inventoryPlayer = InventoryPlayer.getInstance();

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
			bullet.setPosAndDir(player.getX(), player.getY()+5, player.directionPointing);
			attachChild(bullet);
			bulletCounter--;
			gameHUD.bulletCounter.setText("Bullets: " + bulletCounter);
			
			
			Bullet bullet1 = bulletsPool.getBullet();
			bullet1.setBusy();
			bullet1.setPosAndDir(player.getX(), player.getY(), player.directionPointing);
			attachChild(bullet1);
			bulletCounter--;
			gameHUD.bulletCounter.setText("Bullets: " + bulletCounter);
			
			
			Bullet bullet2 = bulletsPool.getBullet();
			bullet2.setBusy();
			bullet2.setPosAndDir(player.getX(), player.getY()-5, player.directionPointing);
			attachChild(bullet2);
			bulletCounter--;
			gameHUD.bulletCounter.setText("Bullets: " + bulletCounter);
		}
	}

	public void blockScreenToTeleport() {
		gameHUD.blockScreen.setAlpha(0);
		gameHUD.blockScreen.setVisible(true);
		registerUpdateHandler(new TimerHandler(0.001f, true, new ITimerCallback() {
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				if (blocking) {
					float newAlpha = gameHUD.blockScreen.getAlpha() + 0.005f;
					if (newAlpha >= 1) {
						player.setPosition(teleportToPosition.x, teleportToPosition.y);
						camera.setCenterDirect(player.getX(), player.getY());
						blocking = false;
					} else {
						gameHUD.blockScreen.setAlpha(newAlpha);
					}
				} else {
					float newAlpha = gameHUD.blockScreen.getAlpha() - 0.005f;
					if (newAlpha <= 0) {
						gameHUD.blockScreen.setVisible(false);
						blocking = true;
						checkCollision = true;
						moveAllowed = true;
						unregisterUpdateHandler(pTimerHandler);
					} else {
						gameHUD.blockScreen.setAlpha(newAlpha);
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

	public void createInitialEnemies() {
		for (int i = 0; i < 5; i++) {
			Enemy enemy = enemiesPool.getEnemy();
			int newX = (int) (Math.random() * 1000);
			int newY = (int) (Math.random() * 800);

			Log.v("GAME", "zombie x " + newX);
			Log.v("GAME", "zombie y " + newY);

			enemy.setPosition(newX, newY);
			enemy.setBusy();
			attachChild(enemy);
		}

	}

	@Override
	public void createScene() {
		 camera.setZoomFactor(2.0f);

		init();
		createBackground();
		createHUD();
		createInventoryHUD();
		createMap();
		// createInitialBuildings();

		testBatch();

		// Rectangle rec = new Rectangle(0, 0, map.getWidth(), map.getHeight(),
		// vbom);
		// rec.setColor(0.01f, 0.01f, 0.01f, 0.0f);
		// rec.setShaderProgram(SpotLight.getInstance());
		// attachChild(rec);

		// createLight();

		createPlayer();

//		createCar();

		createControl();
		createLimits();
		startTimerDay();
		createInitialEnemies();

		// pScene.attachChild(light2);

		registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void reset() {
			}

			@Override
			public void onUpdate(float pSecondsElapsed) {

				if (!gameOver) {

					PointF lastPosition = new PointF(player.getX(), player.getY());
					boolean canMove = true;

					if (driveCar) {
						car.setPosition(car.getX() + x * 1.5f, car.getY() + y * 1.5f);
					} else {

						x = x * speed;
						y = y * speed;

						if (moveAllowed) {
							player.setX(player.getX() + x);
							player.setY(player.getY() + y);
							player.setZIndex(10000 - (int) player.getY());
							player.shadow.setZIndex(10000 - (int) player.getY());
						}

						Shape shape = collisionManager.checkCollisionObstacles(player.feet);

						if (shape != null) {
							if (shape == car) {
								// driveCar = true;
								// camera.setChaseEntity(car);
								// player.setVisible(false);
								// player.shadow.setVisible(false);
							}
							canMove = false;
						}

						if (!canMove && camera.getHUD() == gameHUD) {
							player.setPosition(lastPosition.x, lastPosition.y);
							player.setZIndex(10000 - (int) player.getY());
							player.shadow.setZIndex(10000 - (int) player.getY());
						}
						sortChildren();

						checkPickItem();

						if (checkCollision) {
							Shape door = (Shape) collisionManager.checkDoor(player.feet);
							if (door != null) {
								Log.v("GAME","DOOR");
								checkCollision = false;
								String[] relocation = ((String) door.getUserData()).split(",");
								if (relocation[0].equals("teleport")) {
									beforeEntrancePosition.x = player.getX();
									beforeEntrancePosition.y = player.getY() - 10;
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

					if (driveCar) {
						enemiesPool.updateEnemies(car);
					} else {
						enemiesPool.updateEnemies(player);
					}

				}
			}
		});

		setOnSceneTouchListener(this);

	}

	public void actionButtonA() {

		// float[] popupPosition =
		// camera.getCameraSceneCoordinatesFromSceneCoordinates(player.getX() +
		// player.getWidth() / 2, player.getY());
		// gameHUD.createPopupConversation(popupPosition[0], popupPosition[1]);
		speed = 1.0f;
	}

	public void releaseButtonA() {
		speed = 0.85f;
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
					Util.oppacityScreen = Util.oppacityScreen + 0.005f;
					if (Util.oppacityScreen > 0.95f) {
						Util.oppacityScreen = 0.95f;
					}

				} else {
					Util.oppacityScreen = Util.oppacityScreen - 0.005f;
					if (Util.oppacityScreen < 0.0f) {
						Util.oppacityScreen = 0.0f;
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
			if (inventoryPlayer.inventory.containsKey(key)) {
				int value = inventoryPlayer.inventory.get(key) + 1;
				inventoryPlayer.inventory.put(key, value);
			} else {
				inventoryPlayer.inventory.put(key, 1);
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

	public void damagePlayer() {
		if (timeElapsedBetweenDamage == 2) {
			life = life - 10;
			gameHUD.lifeText.setText("Life: " + life + "%");
			if (life == 0) {
				endGame();
			} else {
				timeElapsedBetweenDamage = 0;
				registerUpdateHandler(new TimerHandler(1f, true, new ITimerCallback() {
					@Override
					public void onTimePassed(TimerHandler pTimerHandler) {
						timeElapsedBetweenDamage++;
						if (timeElapsedBetweenDamage == 2) {
							unregisterUpdateHandler(pTimerHandler);
						}
					}
				}));
			}
		}
	}

	public void endGame() {
		gameHUD.createPopupConversation(400, 240);
		gameOver = true;
	}

	public void useItem(ItemInventory item) {

	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {

		Util.centerX = pSceneTouchEvent.getX();
		Util.centerY = 480 - pSceneTouchEvent.getY();
		return false;
	}
}