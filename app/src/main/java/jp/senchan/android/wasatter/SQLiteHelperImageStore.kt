/**
 *
 */
package jp.senchan.android.wasatter

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * @author takuji
 */
class SQLiteHelperImageStore(context: Context?) : SQLiteOpenHelper(context, "imagestore.db", null, 1) {
    /*
	 * (非 Javadoc)
	 *
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite
	 * .SQLiteDatabase)
	 */
    override fun onCreate(db: SQLiteDatabase) { // TODO 自動生成されたメソッド・スタブ
        db
                .execSQL("create table imagestore(url text primary key,filename text,created integer)")
    }

    override fun onUpgrade(arg0: SQLiteDatabase, arg1: Int, arg2: Int) { // TODO 自動生成されたメソッド・スタブ
    }
}