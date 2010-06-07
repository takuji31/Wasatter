package jp.senchan.android.wasatter2.item;

import java.util.HashMap;

public class ItemStore {
	/**
	 * Wassrのヒトコト、rid=>Item
	 */
	public static HashMap<String, Item> wassr = new HashMap<String, Item>();
	/**
	 * Twitterのつぶやき、rid=>Item
	 */
	public static HashMap<String, Item> twitter = new HashMap<String, Item>();

}
