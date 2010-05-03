/**
 *
 */
package jp.senchan.android.wasatter2.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author takuji
 *
 */
public class DBHelper extends SQLiteOpenHelper {

	public DBHelper(Context context) {
		//名前がひどいけど、一度これで作っちゃったからやむを得ない…＞＜
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
		// ImageStoreテーブル→プロフィール、イイネアイコン
		db.execSQL("create table imagestore(url text primary key,filename text,created integer)");
		//db.execSQL("");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//1.0のDBから2.0のDBにアップグレード
		if(oldVersion == 1){

		}


	}
}
