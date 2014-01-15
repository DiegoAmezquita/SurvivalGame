package com.example.survivalgame.game;

import java.util.HashMap;

public class InventoryPlayer {

	public HashMap<String, Integer> inventory;

	static InventoryPlayer instance;
	
	private InventoryPlayer(){
		inventory = new HashMap<String, Integer>();
	}

	public static InventoryPlayer getInstance() {
		if (instance == null) {
			instance = new InventoryPlayer();
		}
		return instance;
	}

}
