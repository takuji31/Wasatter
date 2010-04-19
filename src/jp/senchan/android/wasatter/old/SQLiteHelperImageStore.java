/**
 *
 */
package jp.senchan.android.wasatter.old;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author takuji
 * 
 */
public class SQLiteHelperImageStore extends SQLiteOpenHelper {

	public SQLiteHelperImageStore(Context context) {
		super(context, "imagestore.db", null, 1);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite
	 * .SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO 自動生成されたメソッド・スタブ
		db
				.execSQL("create table imagestore(url text primary key,filename text,created integer)");

	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO 自動生成されたメソッド・スタブ

	}
}
