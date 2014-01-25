package com.example.survivalgame.game;

import java.util.ArrayList;

import org.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl.IOnScreenControlListener;
import org.andengine.engine.camera.hud.controls.DigitalOnScreenControl;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.shape.Shape;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.AnimatedSprite.IAnimationListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.debugdraw.DebugRenderer;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.adt.color.Color;
import org.andengine.util.math.MathUtils;

import android.graphics.PointF;
import android.opengl.GLES20;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.example.survivalgame.BaseScene;
import com.example.survivalgame.SceneManager;
import com.example.survivalgame.TextureGameManager;
import com.example.survivalgame.SceneManager.SceneType;
import com.example.survivalgame.util.MoveTask;
import com.example.survivalgame.util.Task;
import com.example.survivalgame.util.Util;
import com.example.survivalgame.util.Util.Direction;

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

	public DigitalOnScreenControl movementOnScreenControl;
	// private static final String TAG = "GAME";
	float speed = 1.85f;

	PointF beforeEntrancePosition;
	PointF teleportToPosition;
	Rectangle enterBuilding;
	CollisionManager collisionManager;

	private PhysicsWorld mPhysicsWorld;

	BulletsPool bulletsPool;
	EnemiesPool enemiesPool;

	InventoryPlayer inventoryPlayer;

	Shape itemToPick;

	int life = 100;

	Sprite car;
	float carRotation = 0;

	boolean driveCar = false;

	int timeElapsedBetweenDamage = 2;

	ArrayList<Building> arrayBuildings;

	Building buildingActive;

	boolean checkCollidePlayer = false;

	AnimatedSprite blood;

	int framesElapsed = 0;

	private void createBackground() {
		setBackground(new Background(Color.BLACK));
	}

	private void createHUD() {
		gameHUD = new GameHUD(camera, vbom, this);
		bulletCounter = 20;
		Log.v("GAME", "Bullets " + bulletCounter);
		gameHUD.bulletCounter.setText("Balas: " + bulletCounter);
	}

	private void createInventoryHUD() {
		inventoryHUD = new InventoryHUD(camera, this, vbom);
	}

	public void createMap() {

		map = new MapGame("tmx/beach.tmx", activity, engine, vbom, mPhysicsWorld);
		for (int i = 0; i < map.getNumberLayers(); i++) {
			map.getLayer(i).detachSelf();
			if (!map.getLayer(i).getName().equals("Items")) {
				if (map.getLayer(i).getName().equals("trees")) {
					// map.getLayer(i).setZIndex(10000-(int)map.getLayer(i).getY());
					// Log.v("ZINDEX","Trees layer "+map.getLayer(i).getZIndex());
				}
				attachChild(map.getLayer(i));
			}
		}

		Log.v("Map", collisionManager.arrayTrees.size() + "");

		// ACA ESTA EL PROBLEMA DE RENDIMIENTO CARGAR LA TEXTURA QUE ES PARA
		// PODER USAR UN BATCH

		// final SpriteBatch staticSpriteBatch = new
		// SpriteBatch(resourcesManager.mBuildingTexture, 4000, vbom);
		//
		// for (int i = 0; i < collisionManager.arrayTrees.size(); i++) {
		// collisionManager.arrayTrees.get(i).setZIndex(10000 - (int)
		// collisionManager.arrayTrees.get(i).getY());
		// staticSpriteBatch.draw(collisionManager.arrayTrees.get(i));
		// }
		//
		// staticSpriteBatch.submit();
		// staticSpriteBatch.setPosition(0, 0);
		// attachChild(staticSpriteBatch);
		// sortChildren();

		// MapGame map2 = new MapGame("tmx/newDesert.tmx", activity, engine,
		// vbom);
		// map2.getLayer(0).detachSelf();
		// map2.getLayer(0).setPosition(-500, 0);
		// attachChild(map2.getLayer(0));

	}

	public void createPlayer() {
		player = new Player(Util.playerSpawn.x, Util.playerSpawn.y, this, vbom, camera, mPhysicsWorld) {
			@Override
			public void onDie() {
			}
		};
		player.setCurrentTileIndex(7);

		// player.shadow.setPosition(player);
		player.shadow.setPosition(player.getX(), player.getY() + player.getHeight() * 1.3f);
		attachChild(player.shadow);

		attachChild(player);

		// final Body redBody = PhysicsFactory.createBoxBody(mPhysicsWorld,
		// sword, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(5, 0.5f,
		// 0.5f));
		// mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(sword,
		// redBody, true, true));
		//
		// WeldJointDef j = new WeldJointDef();
		// j.initialize(player.body, redBody, player.body.getWorldCenter());
		// j.collideConnected = false;
		// mPhysicsWorld.createJoint(j);

		// Attach box2d debug renderer if you want to see debug.

	}

	public void createControl() {
		final float x1 = 80;
		final float y1 = 80;

		movementOnScreenControl = new DigitalOnScreenControl(x1, y1, camera, resourcesManager.mOnScreenControlBaseTextureRegion, resourcesManager.mOnScreenControlKnobTextureRegion, 0.1f, vbom,
				new IOnScreenControlListener() {

					@Override
					public void onControlChange(BaseOnScreenControl pBaseOnScreenControl, float pValueX, float pValueY) {
						if (driveCar) {
							float rotationInRad = (float) Math.atan2(pValueX, pValueY);
							carRotation = MathUtils.radToDeg(rotationInRad);
							if (carRotation != 0) {
								carRotation += 90;
								car.setRotation(carRotation);
							}
						}
						if (Util.moveAllowed) {
							if (pValueX > 0) {
								player.setRunningRight();
							} else if (pValueX < 0) {
								player.setRunningLeft();
							} else if (pValueY > 0) {
								player.setRunningUp();
							} else if (pValueY < 0) {
								player.setRunningDown();
							} else {
								player.stopRunning();
							}

							// if (pValueX > 0 && pValueY < 0) {
							// if (pValueX > (pValueY * -1)) {
							// player.setRunningRight();
							// } else {
							// player.setRunningDown();
							// }
							// } else if (pValueX > 0 && pValueY > 0) {
							// if (pValueX > pValueY) {
							// player.setRunningRight();
							// } else {
							// player.setRunningUp();
							// }
							// } else if (pValueX < 0 && pValueY > 0) {
							// if ((pValueX * -1) > pValueY) {
							// player.setRunningLeft();
							// } else {
							// player.setRunningUp();
							// }
							// } else if (pValueX < 0 && pValueY < 0) {
							// if (pValueX < pValueY) {
							// player.setRunningLeft();
							// player.setRunningDown();
							// }
							// } else {
							// player.stopRunning();
							// }
						}

					}
				});
		movementOnScreenControl.getControlBase().setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		movementOnScreenControl.getControlBase().setAlpha(0.5f);
		movementOnScreenControl.refreshControlKnobPosition();
		gameHUD.setChildScene(movementOnScreenControl);
	}

	public void createLimits() {

		FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);

		Rectangle topLimit = new Rectangle(map.getWidth() / 2, map.getHeight() - 30, map.getWidth(), 2, vbom);
		Rectangle bottomLimit = new Rectangle(map.getWidth() / 2, 0, map.getWidth(), 2, vbom);
		Rectangle leftLimit = new Rectangle(0, map.getHeight() / 2, 2, map.getHeight(), vbom);
		Rectangle rightLimit = new Rectangle(map.getWidth(), map.getHeight() / 2, 2, map.getHeight(), vbom);

		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(topLimit, PhysicsFactory.createBoxBody(mPhysicsWorld, topLimit, BodyType.KinematicBody, wallFixtureDef), true, true));
		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(bottomLimit, PhysicsFactory.createBoxBody(mPhysicsWorld, bottomLimit, BodyType.KinematicBody, wallFixtureDef), true, true));
		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(leftLimit, PhysicsFactory.createBoxBody(mPhysicsWorld, leftLimit, BodyType.KinematicBody, wallFixtureDef), true, true));
		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(rightLimit, PhysicsFactory.createBoxBody(mPhysicsWorld, rightLimit, BodyType.KinematicBody, wallFixtureDef), true, true));

		// collisionManager.addObstacle(topLimit);
		// collisionManager.addObstacle(bottomLimit);
		// collisionManager.addObstacle(leftLimit);
		// collisionManager.addObstacle(rightLimit);

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
		Building building = new Building("tmx/houseOne.tmx", -500, 0, vbom, activity.getAssets(), engine, mPhysicsWorld);
		// layer.setPosition(posX, posY);
		building.setUserData("houseOne");

		for (int i = 0; i < building.getNumberLayers(); i++) {
			TMXLayer layer = building.getLayer(i);
			layer.detachSelf();
			if (!building.getLayer(i).getName().equals("Items")) {
				attachChild(building.getLayer(i));
			}
		}

		arrayBuildings.add(building);

		Building buildingTwo = new Building("tmx/houseTwo.tmx", -1000, 0, vbom, activity.getAssets(), engine, mPhysicsWorld);
		// layer.setPosition(posX, posY);
		buildingTwo.setUserData("houseTwo");

		for (int i = 0; i < buildingTwo.getNumberLayers(); i++) {
			TMXLayer layer = buildingTwo.getLayer(i);
			layer.detachSelf();
			if (!buildingTwo.getLayer(i).getName().equals("Items")) {
				attachChild(buildingTwo.getLayer(i));
			}
		}

		arrayBuildings.add(buildingTwo);

		// attachChild(building.buildingFront);
	}

	public void createCar() {
		car = new Sprite(400, 240, resourcesManager.mCar, vbom);
		car.setScale(0.7f);
		collisionManager.addObstacle(car);
		attachChild(car);
	}

	public void init() {

		mPhysicsWorld = new PhysicsWorld(new Vector2(0, 0), false);

		beforeEntrancePosition = new PointF(0, 0);
		teleportToPosition = new PointF();

		collisionManager = CollisionManager.getInstance();

		bulletsPool = new BulletsPool(bulletCounter, vbom, mPhysicsWorld);

		enemiesPool = new EnemiesPool(20, this, vbom, mPhysicsWorld);

		inventoryPlayer = InventoryPlayer.getInstance();

		Util.moveTaskList = new ArrayList<MoveTask>();

		Util.taskList = new ArrayList<Task>();

		arrayBuildings = new ArrayList<Building>();

		buildingActive = null;

	}

	public void checkPickItem() {
		if (buildingActive == null) {
			itemToPick = collisionManager.checkPickItem(player.feet);
		} else {
			itemToPick = collisionManager.checkPickItem(buildingActive.items, player.feet);
		}
		if (itemToPick != null) {
			gameHUD.infoText.setVisible(true);
			gameHUD.infoText.setText(itemToPick.getUserData() + "");
			gameHUD.showButtonC();
		} else {
			gameHUD.infoText.setVisible(false);
			gameHUD.hideButtonC();
		}
	}

	public void fireBullet() {
		if (bulletCounter > 0) {
			// Bullet bullet = bulletsPool.getBullet();
			// bullet.setBusy();
			// bullet.setPosAndDir(player.getX(), player.getY()+5,
			// player.directionPointing);
			// attachChild(bullet);
			// bulletCounter--;
			// gameHUD.bulletCounter.setText("Bullets: " + bulletCounter);

			Bullet bullet1 = bulletsPool.getBullet();
			bullet1.setBusy();
			bullet1.setPosAndDir(player.getX(), player.getY(), player.directionPointing);
			bullet1.shotBullet();
			camera.setChaseEntity(bullet1);
			attachChild(bullet1);
			bulletCounter--;
			gameHUD.bulletCounter.setText("Bullets: " + bulletCounter);

			// Bullet bullet2 = bulletsPool.getBullet();
			// bullet2.setBusy();
			// bullet2.setPosAndDir(player.getX(), player.getY()-5,
			// player.directionPointing);
			// attachChild(bullet2);
			// bulletCounter--;
			// gameHUD.bulletCounter.setText("Bullets: " + bulletCounter);
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
						camera.setCenterDirect(player.getX(), player.getY());
						camera.setChaseEntity(player);
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
		for (int i = 0; i < Util.enemiesSpawn.size(); i++) {
			Enemy enemy = enemiesPool.getEnemy();

			enemy.setPosition(Util.enemiesSpawn.get(i).x, Util.enemiesSpawn.get(i).y);
			enemy.createEnemyBody();
			enemy.setBusy();
			attachChild(enemy);

			// camera.setChaseEntity(enemy);
		}

	}

	public void timeOut(float seconds) {
		Util.moveAllowed = false;
		player.stopRunning();
		registerUpdateHandler(new TimerHandler(seconds, true, new ITimerCallback() {
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				Util.moveAllowed = true;
				unregisterUpdateHandler(pTimerHandler);
			}
		}));
	}

	public void loadItems() {
		for (int i = 0; i < collisionManager.items.size(); i++) {
			attachChild(collisionManager.items.get(i));
		}
	}

	@Override
	public void createScene() {

		engine.registerUpdateHandler(new FPSLogger());
		camera.setZoomFactor(2.0f);

		init();
		createBackground();
		createHUD();
		createInventoryHUD();

		createMap();
		// createInitialBuildings();

		createInitialBuildings();

		loadItems();

		createPlayer();

		createControl();
		createLimits();
		startTimerDay();
		createInitialEnemies();

		blood = new AnimatedSprite(0, 0, resourcesManager.blood_region, vbom);
		blood.animate(30);
		attachChild(blood);

		// attachChild(new DebugRenderer(mPhysicsWorld, vbom));

		gameHUD.createPopupInformation("Busca un vehiculo", 7);

		registerUpdateHandler(mPhysicsWorld);

		mPhysicsWorld.setContactListener(new ContactListener() {
			@Override
			public void beginContact(final Contact pContact) {

			}

			@Override
			public void endContact(final Contact pContact) {
				/* Nothing. */
			}

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
				// TODO Auto-generated method stub

			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
				if (contact.getFixtureA().getBody().getUserData() != null && contact.getFixtureB().getBody().getUserData() != null) {
					String userDataA = (String) contact.getFixtureA().getBody().getUserData();
					String userDataB = (String) contact.getFixtureB().getBody().getUserData();
					if (userDataA != null && userDataB != null) {
						// Log.v("BUILDING", userDataA + " ||||| " + userDataB);
					}

					if ((userDataA.contains("door") && userDataB.equals("Player")) || (userDataA.equals("Player") && userDataB.contains("door"))) {
						Building building = getBuilding(userDataA.split("-")[1]);
						buildingActive = building;
						if (player.directionMove == Direction.UP) {
							MoveTask task = new MoveTask(player.body, new PointF(building.playerSpawnEntrance.x + building.getLayer(0).getX() - building.getLayer(0).getWidth() / 2,
									building.playerSpawnEntrance.y + building.getLayer(0).getY() - building.getLayer(0).getHeight() / 2));
							Util.moveTaskList.add(task);
							beforeEntrancePosition.x = player.getX();
							beforeEntrancePosition.y = player.getY();
						}
					} else if ((userDataA.equals("exit") && userDataB.equals("Player")) || (userDataA.equals("Player") && userDataB.equals("exit"))) {
						buildingActive = null;
						MoveTask task = new MoveTask(player.body, new PointF(beforeEntrancePosition.x, beforeEntrancePosition.y - 5));
						Util.moveTaskList.add(task);
					} else if ((userDataA.equals("Enemy") && userDataB.equals("Player")) || (userDataA.equals("Player") && userDataB.equals("Enemy"))) {
						damagePlayer();
					}

					// if (userDataA.equals("Player") ||
					// userDataB.equals("Player")) {
					checkCollidePlayer = true;
					// }
				}

			}
		});

		registerUpdateHandler(new IUpdateHandler() {

			@Override
			public void onUpdate(float pSecondsElapsed) {
				checkPickItem();
				player.setZIndex(10000 - (int) player.getY());
				sortChildren();
				framesElapsed++;
				// Log.v("ZINDEX","player "+player.getZIndex());
				if (framesElapsed > 30) {
					enemiesPool.updateEnemies(player);
					framesElapsed = 0;
				}

				if (!Util.moveTaskList.isEmpty()) {
					for (int i = 0; i < Util.moveTaskList.size(); i++) {
						timeOut(1);
						camera.setChaseEntity(null);
						if (beforeEntrancePosition.x == 0 && beforeEntrancePosition.y == 0) {
							beforeEntrancePosition.x = player.getX();
							beforeEntrancePosition.y = player.getY();
						}
						Util.moveTaskList.get(i).move();
						if (!Util.moveTaskList.get(i).hitByEnemy) {
							blockScreenToTeleport();
						} else {
							camera.setChaseEntity(player);
						}

					}
					Util.moveTaskList.clear();

				}
				if (!Util.taskList.isEmpty()) {
					for (int i = 0; i < Util.taskList.size(); i++) {
						Util.taskList.get(i).doTask();
						blood.setPosition(Util.taskList.get(i).enemy.getX(), Util.taskList.get(i).enemy.getY());
						blood.setZIndex(player.getZIndex() + 10);
						blood.setVisible(true);
						blood.animate(80, false, new IAnimationListener() {

							@Override
							public void onAnimationStarted(AnimatedSprite pAnimatedSprite, int pInitialLoopCount) {
								// TODO Auto-generated method stub
							}

							@Override
							public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite, int pRemainingLoopCount, int pInitialLoopCount) {
								// TODO Auto-generated method stub
							}

							@Override
							public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite, int pOldFrameIndex, int pNewFrameIndex) {
								// TODO Auto-generated method stub
							}

							@Override
							public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
								blood.setVisible(false);

							}
						});
					}
					Util.taskList.clear();
				}

				if (checkCollidePlayer) {
					checkBox();
				}
			}

			@Override
			public void reset() {
				// TODO Auto-generated method stub

			}

		});

		setOnSceneTouchListener(this);
	}

	public void checkBox() {
		if (buildingActive != null) {
			boolean stopChecking = true;
			for (int i = 0; i < buildingActive.boxes.size(); i++) {
				if (player.sword.collidesWith(buildingActive.boxes.get(i))) {
					stopChecking = false;
					break;
				}
			}

			if (stopChecking) {
				// checkCollidePlayer = false;
				gameHUD.infoText.setVisible(false);
				gameHUD.hideButtonC();
				gameHUD.hideBoxItems();
			} else {
				gameHUD.infoText.setVisible(true);
				gameHUD.infoText.setText("Caja");
				gameHUD.showButtonC();
			}
		}
	}

	public void checkHitEnemy() {
		for (int i = 0; i < enemiesPool.enemies.size(); i++) {
			Enemy enemy = enemiesPool.enemies.get(i);
			if (!enemy.isFree()) {
				if (player.sword.collidesWith(enemy)) {
					Util.taskList.add(new Task(enemy));
					return;
				}
			}
		}
	}

	public void actionButtonA() {

		// float[] popupPosition =
		// camera.getCameraSceneCoordinatesFromSceneCoordinates(player.getX() +
		// player.getWidth() / 2, player.getY());
		// gameHUD.createPopupConversation(popupPosition[0], popupPosition[1]);

		// gameHUD.createPopupConversation();

		player.running = !player.running;
		player.stopRunning();
	}

	public void releaseButtonA() {
		player.stopRunning();
	}

	public void actionButtonB() {
		player.attack();
		// fireBullet();
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
		// registerUpdateHandler(new TimerHandler(0.01f, true, new
		// ITimerCallback() {
		// @Override
		// public void onTimePassed(TimerHandler pTimerHandler) {
		// if (hourOfDay > 24) {
		// hourOfDay = 0;
		// }
		//
		// if (hourOfDay > 18 || hourOfDay < 6) {
		// Util.oppacityScreen = Util.oppacityScreen + 0.005f;
		// if (Util.oppacityScreen > 0.95f) {
		// Util.oppacityScreen = 0.95f;
		// }
		//
		// } else {
		// Util.oppacityScreen = Util.oppacityScreen - 0.005f;
		// if (Util.oppacityScreen < 0.0f) {
		// Util.oppacityScreen = 0.0f;
		// }
		// }
		//
		// gameHUD.timeDay.setText("Hour: " + ((int) hourOfDay));
		// hourOfDay += 0.005f;
		// }
		// }));
	}

	public void pickItem() {
		if (itemToPick != null) {
			String key = (String) itemToPick.getUserData();
			if (inventoryPlayer.inventory.containsKey(key)) {
				int value = inventoryPlayer.inventory.get(key) + 1;
				inventoryPlayer.inventory.put(key, value);
			} else if (inventoryPlayer.inventory.size() < 16) {
				inventoryPlayer.inventory.put(key, 1);
			}
			itemToPick.detachSelf();
			itemToPick = null;
			gameHUD.hideButtonC();
			bulletCounter++;
			gameHUD.bulletCounter.setText("Balas: " + bulletCounter);
		} else {
//			gameHUD.createPopupInformation("Esta Vacio", 3);

			ArrayList<Sprite> testBoxes = new ArrayList<Sprite>();

			for (int i = 0; i < 8; i++) {

				Sprite testPrueba = new Sprite(0, 0, TextureGameManager.getInstance().getTexture("shell"), vbom) {
					@Override
					public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
						if (pSceneTouchEvent.isActionUp()) {
							Log.v("GAME", "DEBE QUITARLO DE LA BOLSA");
							gameHUD.removeItemBox(this);
							return true;
						}

						return false;
					}
				};
				testPrueba.setUserData("shell");

				testBoxes.add(testPrueba);
			}

			gameHUD.populateItemBox(testBoxes);

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
			life -= 10;
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
		gameHUD.createPopupConversation();
		gameOver = true;
	}

	public Building getBuilding(String nameBuilding) {
		for (Building building : arrayBuildings) {
			if (building.getUserData().equals(nameBuilding)) {
				return building;
			}
		}
		return null;
	}

	public void useItem(ItemInventory item) {
		item.useItem();
	}

	public void addItemQuickMenu(ItemInventory item) {
		gameHUD.addItemQuickMenu(item);
	}

	public void addLife(int amount) {
		life += amount;
		gameHUD.lifeText.setText("Life: " + life + "%");
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {

		Util.centerX = pSceneTouchEvent.getX();
		Util.centerY = 480 - pSceneTouchEvent.getY();
		return false;
	}
}