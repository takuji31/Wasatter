/**
 *
 */
package jp.senchan.android.wasatter;

import java.util.Comparator;

/**
 * @author Senka/Takuji
 * 
 */
public class StatusItemComparator implements Comparator<WasatterItem> {
    @Override
    public int compare(WasatterItem object1, WasatterItem object2) {
        // TODO 自動生成されたメソッド・スタブ
        return object1.epoch > object2.epoch ? -1 : 1;
    }

}
