/**
 *
 */
package jp.senchan.android.wasatter2.util;

import java.util.Comparator;

import jp.senchan.android.wasatter2.item.Item;


/**
 * @author Senka/Takuji
 *
 */
public class ItemComparator implements Comparator<Item> {
	@Override
	public int compare(Item object1, Item object2) {
		// TODO 自動生成されたメソッド・スタブ
		return object1.epoch > object2.epoch ? -1 : 1;
	}

}
