/**
 *
 */
package jp.senchan.android.wasatter

import java.util.*

/**
 * @author Senka/Takuji
 */
class StatusItemComparator : Comparator<WasatterItem> {
    override fun compare(object1: WasatterItem, object2: WasatterItem): Int { // TODO 自動生成されたメソッド・スタブ
        return if (object1.epoch > object2.epoch) -1 else 1
    }
}