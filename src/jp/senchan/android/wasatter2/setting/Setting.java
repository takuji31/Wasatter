/**
 *
 */
package jp.senchan.android.wasatter2.setting;

import jp.senchan.android.wasatter2.Wasatter;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Window;

/**
 * アカウント設定
 *
 * @author Senka/Takuji
 *
 */
public class Setting extends Activity {
	public static SharedPreferences pref;
	public static final String MESSAGE = "設定を保存しました";
	public Handler handler = new Handler();

	static {
		pref = PreferenceManager.getDefaultSharedPreferences(Wasatter.CONTEXT);
	}
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_PROGRESS);
	}

	/**
	 * 設定を取得するメソッド
	 * @param key キー
	 * @param def デフォルト値
	 * @return 設定値
	 */
	public static String get(String key,String def){
		return pref.getString(key, def);
	}
	public static int get(String key,int def){
		return pref.getInt(key, def);
	}
	public static boolean get(String key,boolean def){
		return pref.getBoolean(key, def);
	}
	public static long get(String key,long def){
		return pref.getLong(key, def);
	}
	/**
	 * 設定を代入するメソッド
	 * @param key キー
	 * @param value 値
	 */
	public static void set(String key,String value){
		pref.edit().putString(key, value).commit();
	}
	public static void set(String key,int value){
		pref.edit().putInt(key, value).commit();
	}
	public static void set(String key,boolean value){
		pref.edit().putBoolean(key, value).commit();
	}
	public static void set(String key,long value){
		pref.edit().putLong(key, value).commit();
	}

}
