package jp.senchan.android.wasatter.next.menu;

import android.content.Context;
import android.graphics.drawable.Drawable;
import jp.senchan.android.wasatter.app.HomeActivity;

public interface SlideMenuItem {
	public void handleEvent(HomeActivity activity);
	public void getViewId();
	public Drawable getIconDrawable(Context c);
	public String getTitle(Context c);
}
