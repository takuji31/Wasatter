/**
 *
 */
package jp.senchan.android.wasatter.util;

import java.util.Comparator;

import jp.senchan.android.wasatter.item.Item;


/**
 * @author Senka/Takuji
 *
 */
public class ItemComparator implements Comparator<Item> {
	
	public int compare(Item object1, Item object2) {
		// TODO 自動生成されたメソッド・スタブ
		return object1.epoch > object2.epoch ? -1 : 1;
	}

}
