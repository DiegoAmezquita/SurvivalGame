package com.example.survivalgame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXLoader;
import org.andengine.extension.tmx.TMXLoader.ITMXTilePropertiesListener;
import org.andengine.extension.tmx.TMXProperties;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.extension.tmx.TMXTileProperty;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.extension.tmx.util.exception.TMXLoadException;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;

import android.graphics.PointF;
import android.opengl.GLES20;
import android.util.Log;

import com.example.survivalgame.Player.Direction;
import com.example.survivalgame.SceneManager.SceneType;

public class GameScene extends BaseScene implements IOnSceneTouchListener {

	private HUD inventoryHud;

	private GameHUD gameHUD;

	private Text lifeText;

	private TMXTiledMap mTMXTiledMap;

	private Player player;

	private AnalogOnScreenControl movementOnScreenControl;

	private static final String TAG = "GAME";

	int mTouchCount = 0;
	float x = 0, y = 0;

	float speed = 1.0f;

	int mapWidth;
	int mapHeight;


	Rectangle[] limits;
	ArrayList<Rectangle> obstacles;
	ArrayList<Rectangle> items;

	ArrayList<Sprite> itemsInventory;

	HashMap<String, Integer> inventoryPlayer;

	ArrayList<ItemInventory> itemsAlreadyLoaded;

	HashMap<String, ITextureRegion> texturesObjects;

	int posInventoryItemY = 100;

	Rectangle bullet;
	Direction bulletDirection;

	Sprite teleport = null;
	PointF teleportPosition;

	Rectangle enterBuilding;

	private void createBackground() {
		setBackground(new Background(Color.BLACK));
	}

	private void createHUD() {
		gameHUD = new GameHUD(camera, resourcesManager, vbom);
	}

	public void createInventory() {
		inventoryHud = new HUD();

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
				ItemInventory itemTest = new ItemInventory(180, posInventoryItemY, entry.getKey(), texturesObjects.get(entry.getKey()), resourcesManager, vbom);
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

	public void createTMXMap() {
		try {

			final TMXLoader tmxLoader = new TMXLoader(activity.getAssets(), engine.getTextureManager(), TextureOptions.BILINEAR_PREMULTIPLYALPHA, vbom, new ITMXTilePropertiesListener() {

				@Override
				public void onTMXTileWithPropertiesCreated(TMXTiledMap pTMXTiledMap, TMXLayer pTMXLayer, TMXTile pTMXTile, TMXProperties<TMXTileProperty> pTMXTileProperties) {

					if (!pTMXTileProperties.isEmpty()) {
						String nameProperty = pTMXTileProperties.get(0).getName();
						if (nameProperty.equals("teleport")) {
							teleport = new Sprite(pTMXTile.getTileX(), pTMXTile.getTileY(), pTMXTile.getTextureRegion(), vbom);
							String value = pTMXTileProperties.get(0).getValue();
							Log.v(TAG, value);
							teleportPosition = new PointF();
							teleportPosition.x = Float.parseFloat(value.split("-")[0]);
							teleportPosition.y = Float.parseFloat(value.split("-")[1]);
							texturesObjects.put(nameProperty, pTMXTile.getTextureRegion());
							teleport.setUserData(nameProperty);
							itemsInventory.add(teleport);
						} else {
							texturesObjects.put(nameProperty, pTMXTile.getTextureRegion());
							Sprite item = new Sprite(pTMXTile.getTileX(), pTMXTile.getTileY(), pTMXTile.getTextureRegion(), vbom);
							item.setUserData(nameProperty);
							itemsInventory.add(item);
						}
					}
				}
			});
			this.mTMXTiledMap = tmxLoader.loadFromAsset("tmx/newDesert.tmx");

		} catch (final TMXLoadException e) {
			Debug.e(e);
		}

		final TMXLayer tmxLayer = this.mTMXTiledMap.getTMXLayers().get(0);
		mapWidth = tmxLayer.getWidth();
		mapHeight = tmxLayer.getHeight();
		attachChild(tmxLayer);

		for (int i = 0; i < itemsInventory.size(); i++) {
			attachChild(itemsInventory.get(i));
		}

		// attachChild(teleport);

	}

	public void createPlayer() {
		player = new Player(200, 200, vbom, camera) {
			@Override
			public void onDie() {
			}
		};
		player.setCurrentTileIndex(8);

		attachChild(player);
	}

	public void createControl() {
		final float x1 = 20;
		final float y1 = 470 - ResourcesManager.getInstance().mOnScreenControlBaseTextureRegion.getHeight();
		movementOnScreenControl = new AnalogOnScreenControl(x1, y1, camera, ResourcesManager.getInstance().mOnScreenControlBaseTextureRegion,
				ResourcesManager.getInstance().mOnScreenControlKnobTextureRegion, 0.1f, vbom, new IAnalogOnScreenControlListener() {
					@Override
					public void onControlChange(final BaseOnScreenControl pBaseOnScreenControl, final float pValueX, final float pValueY) {

						if (pValueX > 0 && pValueY < 0) {
							// if ((pValueX + (pValueY * -1)) > 1.3f) {
							// player.setRunningUpRight();
							// } else {
							if (pValueX > (pValueY * -1)) {
								player.setRunningRight();
							} else {
								player.setRunningUp();
							}
							// }
						} else if (pValueX > 0 && pValueY > 0) {
							// if ((pValueX + pValueY) > 1.3f) {
							// player.setRunningDownRight();
							// } else {
							if (pValueX > pValueY) {
								player.setRunningRight();
							} else {
								player.setRunningDown();
							}
							// }
						} else if (pValueX < 0 && pValueY > 0) {
							// if (((pValueX * -1) + pValueY) > 1.3f) {
							// player.setRunningDownLeft();
							// } else {
							if ((pValueX * -1) > pValueY) {
								player.setRunningLeft();
							} else {
								player.setRunningDown();
							}
							// }
						} else if (pValueX < 0 && pValueY < 0) {
							// if ((pValueX + pValueY) < -1.3f) {
							// player.setRunningUpLeft();
							// } else {
							if (pValueX < pValueY) {
								player.setRunningLeft();
							} else {
								player.setRunningUp();
							}
							// }
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

		setChildScene(movementOnScreenControl);
	}

	public void createLimits() {

		Rectangle topLimit = new Rectangle(0, 30, mapWidth, 2, vbom);
		Rectangle bottomLimit = new Rectangle(0, mapHeight, mapWidth, 2, vbom);
		Rectangle leftLimit = new Rectangle(0, 0, 2, mapHeight, vbom);
		Rectangle rightLimit = new Rectangle(mapWidth, 0, 2, mapHeight, vbom);

		limits[0] = topLimit;
		limits[1] = bottomLimit;
		limits[2] = leftLimit;
		limits[3] = rightLimit;

		topLimit.setVisible(false);
		bottomLimit.setVisible(false);
		leftLimit.setVisible(false);
		rightLimit.setVisible(false);

		attachChild(topLimit);
		attachChild(bottomLimit);
		attachChild(leftLimit);
		attachChild(rightLimit);
	}

	public void init() {
		limits = new Rectangle[4];
		obstacles = new ArrayList<Rectangle>();
		items = new ArrayList<Rectangle>();
		itemsInventory = new ArrayList<Sprite>();

		itemsAlreadyLoaded = new ArrayList<ItemInventory>();

		inventoryPlayer = new HashMap<String, Integer>();

		texturesObjects = new HashMap<String, ITextureRegion>();
	}

	public void addObstacles() {
		for (int i = 0; i < obstacles.size(); i++) {
			// attachChild(obstacles.get(i));
		}
	}

	public boolean checkCollisionObstacles() {
		for (int i = 0; i < obstacles.size(); i++) {
			if (player.feet.collidesWith(obstacles.get(i))) {
				return false;
			}
		}
		return true;
	}

	public void checkPickItem() {
		for (int i = 0; i < itemsInventory.size(); i++) {
			if (player.collidesWith(itemsInventory.get(i))) {
				if (itemsInventory.get(i).getUserData().equals("teleport")) {
					player.setX(teleportPosition.x * 32);
					player.setX(teleportPosition.y * 32);
				} else if (itemsInventory.get(i).hasParent()) {
					String key = (String) itemsInventory.get(i).getUserData();
					if (inventoryPlayer.containsKey(key)) {
						int value = inventoryPlayer.get(key) + 1;
						inventoryPlayer.put(key, value);
					} else {
						inventoryPlayer.put(key, 1);
					}
					detachChild(itemsInventory.get(i));
				}
			}

		}
	}

	public void fireBullet() {
		bullet = new Rectangle(50, 50, 20, 5, vbom);
		attachChild(bullet);
	}

	@Override
	public void createScene() {
		init();
		createBackground();
		createHUD();
		createTMXMap();
		createPlayer();
		createControl();
		createInventory();
		createLimits();
		addObstacles();
		fireBullet();

//		enterBuilding = new Rectangle(0, 0, 800, 480, vbom);
//		enterBuilding.setColor(Color.BLACK);
//		enterBuilding.setAlpha(0.1f);
//		gameHUD.attachChild(enterBuilding);

		// AnimatedSprite explosion = new AnimatedSprite(100, 100,
		// ResourcesManager.getInstance().explosion_region, vbom);
		// attachChild(explosion);
		//
		// explosion.animate(100,true);

		registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void reset() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onUpdate(float pSecondsElapsed) {

//				float newAlpha = enterBuilding.getAlpha() + pSecondsElapsed;
//				Log.v(TAG,newAlpha+"");
//				if (enterBuilding.getAlpha() > 0.0f) {
//					Log.v(TAG,"SI ENTRA");
//					if (newAlpha > 1.0f) {
//						Log.v(TAG,"aca");
//						enterBuilding.setAlpha(0.0f);
//					} else {
//						Log.v(TAG,"acaaaa");
//						enterBuilding.setAlpha(newAlpha);
//					}
//				}

				PointF lastPosition = new PointF(player.getX(), player.getY());
				boolean canMove = true;

				x = x * speed;
				y = y * speed;

				player.setX(player.getX() + x);
				player.setY(player.getY() + y);

				for (int i = 0; i < limits.length; i++) {
					if (player.feet.collidesWith(limits[i])) {
						canMove = false;
						break;
					}
				}
				if (canMove) {
					canMove = checkCollisionObstacles();
				}

				if (!canMove && camera.getHUD() == gameHUD) {
					player.setPosition(lastPosition.x, lastPosition.y);
				}

				checkPickItem();

				if (bulletDirection != null) {
					// Log.v(TAG,bulletDirection+"");
					switch (bulletDirection) {
					case UP:
						bullet.setY(bullet.getY() - 100 * pSecondsElapsed);
						break;
					case DOWN:
						bullet.setY(bullet.getY() + 100 * pSecondsElapsed);
						break;
					case RIGHT:
						bullet.setX(bullet.getX() + 100 * pSecondsElapsed);
						break;
					case LEFT:
						bullet.setX(bullet.getX() - 100 * pSecondsElapsed);
						break;

					default:
						break;
					}
				}

				// if ((player.getX() + x) > 0) {

				// }
			}
		});

		setOnSceneTouchListener(this);

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

		// TODO code responsible for disposing scene
		// removing all game scene objects.
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		return false;
	}
}