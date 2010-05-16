package jp.senchan.android.wasatter2.util;

import jp.senchan.android.wasatter2.Wasatter;
import android.widget.Toast;

public class ToastUtil {
	public static void show(String text,int duration){
		Toast.makeText(Wasatter.CONTEXT, text, duration).show();
	}
	public static void show(String text){
		show(text, Toast.LENGTH_SHORT);
	}
	public static void showLong(String text){
		show(text, Toast.LENGTH_LONG);
	}
}
