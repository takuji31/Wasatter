/**
 *
 */
package jp.senchan.android.wasatter.utils;

import java.util.Comparator;

import jp.senchan.android.wasatter.model.api.WasatterStatus;

/**
 * @author Senka/Takuji
 * 
 */
public class WasatterStatusComparator implements Comparator<WasatterStatus> {
	@Override
	public int compare(WasatterStatus lhs, WasatterStatus rhs) {
        return lhs.getTime() > rhs.getTime() ? -1 : 1;
	}

}
