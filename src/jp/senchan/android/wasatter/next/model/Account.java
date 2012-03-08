package jp.senchan.android.wasatter.next.model;

import java.util.ArrayList;

import jp.senchan.android.wasatter.next.model.dataobject.AccountData;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Account extends BaseModel {
    
    public static final String TABLE_NAME = "accounts";

    public Account(Context c) {
        super(c);
    }

    public ArrayList<AccountData> getAccountList() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<AccountData> list = new ArrayList<AccountData>();

        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, "id ASC");

        c.moveToFirst();

        while(c.moveToNext()) {
            AccountData data = new AccountData(c);
            list.add(data);
        }

        c.close();
        db.close();

        return list;
    }

    public AccountData insert(int type, String name, String token, String tokenSecret) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("type", type);
        v.put("name", name);
        v.put("token", token);
        v.put("token_secret", tokenSecret);
        long insertId = db.insert(TABLE_NAME, "", v);
        AccountData resultData = null;
        if(insertId != -1) {
            resultData = new AccountData(insertId, type, name, token, tokenSecret);
        }
        db.close();
        return resultData;
    }

}
