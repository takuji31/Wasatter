package jp.senchan.android.wasatter.next.model.dataobject;

import android.database.Cursor;

public class AccountData {

    public long id;
    public int type;
    public String name;
    public String token;
    public String tokenSecret;

    public AccountData(long id, int type, String name, String token,
            String tokenSecret) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.token = token;
        this.tokenSecret = tokenSecret;
    }

    public AccountData(long id, int type, String name) {
        this(id, type, name, "", "");
    }

    public AccountData(Cursor c) {
        this(c.getLong(c.getColumnIndexOrThrow("id")),
             c.getInt(c.getColumnIndexOrThrow("type")),
             c.getString(c.getColumnIndexOrThrow("name")),
             c.getString(c.getColumnIndexOrThrow("token")),
             c.getString(c.getColumnIndexOrThrow("token_secret")));
    }
}
