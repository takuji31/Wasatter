package jp.senchan.android.wasatter.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class UriResolver {
	/**
	 * UriからPathへの変換処理
	 * @param uri
	 * @return String
	 */
	public static String getPath(Context context, Uri uri) {
		if (uri == null) {
			return null;
		}
	    ContentResolver contentResolver = context.getContentResolver();
	    String[] columns = { MediaStore.Images.Media.DATA };
	    Cursor cursor = contentResolver.query(uri, columns, null, null, null);
	    cursor.moveToFirst();
	    String path = cursor.getString(0);
	    cursor.close();
	    return path;
	}
}
