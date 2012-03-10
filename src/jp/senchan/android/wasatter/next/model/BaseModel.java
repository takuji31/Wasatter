package jp.senchan.android.wasatter.next.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import jp.senchan.android.wasatter.Wasatter;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BaseModel {

    private static final String DB_NAME = "wasatter.db";
    private static final int DB_VERSION = 1;

    public class DBHelper extends SQLiteOpenHelper {

        private Context mContext;

        public DBHelper(Context c) {
            // TODO 自動生成されたコンストラクター・スタブ
            super(c, DB_NAME, null, DB_VERSION);
            mContext = c;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            AssetManager as = mContext.getAssets();
            try {
                InputStream in = as.open("create.sql");
                byte [] bytes = new byte[in.available()];
                in.read(bytes);
                String sqlString = new String(bytes);
                String[] sqls = sqlString.split(";");
                List<String> list = Arrays.asList(sqls);
                Iterator<String> it = list.iterator();
                while(it.hasNext()) {
                    String sql = it.next();
                    db.execSQL(sql);
                }
            } catch (IOException e) {
                // TODO 自動生成された catch ブロック
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO バージョンアップする時にテーブルなど追加する処理
        }

    }

    private DBHelper mDB;
    protected Wasatter mApplication;

    public BaseModel(Wasatter app) {
        mDB = new DBHelper(app);
        mApplication = app;
    }

    public SQLiteDatabase getReadableDatabase() {
        return mDB.getReadableDatabase();
    }

    public SQLiteDatabase getWritableDatabase() {
        return mDB.getWritableDatabase();
    }

}
