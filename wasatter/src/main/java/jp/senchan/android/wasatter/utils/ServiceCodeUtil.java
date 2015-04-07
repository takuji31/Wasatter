package jp.senchan.android.wasatter.utils;

import android.content.Context;

public class ServiceCodeUtil {
	public static int resIdToId(Context c, int resID) {
		return c.getResources().getInteger(resID);
	}
	
	public static boolean equals(Context c, int id, int resId) {
		return resIdToId(c, resId) == id;
	}
}
