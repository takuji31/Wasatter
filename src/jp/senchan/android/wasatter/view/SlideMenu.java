package jp.senchan.android.wasatter.view;

import jp.senchan.android.wasatter2.R;
import jp.senchan.android.wasatter.next.Functions;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

public class SlideMenu {
	private static boolean sMenuShown = false;
	private static View sMenu;
	private static LinearLayout sContent;
	private static FrameLayout sParent;
	private static int sMenuSize;
	private static int sStatusHeight = 0;
	private Activity mActivity;
	private SlideMenuAdapter mAdapter;
	private OnItemClickListener mItemClickListener;

	public SlideMenu(Activity activity) {
		mActivity = activity;
	}

	// call this in your onCreate() for screen rotation
	public void checkEnabled() {
		if (sMenuShown) {
			show(false);
		}
	}

	public void show() {
		// get the height of the status bar
		if (sStatusHeight == 0) {
			Rect rectgle = new Rect();
			Window window = mActivity.getWindow();
			window.getDecorView().getWindowVisibleDisplayFrame(rectgle);
			sStatusHeight = rectgle.top;
		}
		show(true);
	}

	public void show(boolean animate) {
		sMenuSize = Functions.dpToPx(250, mActivity);
		sContent = ((LinearLayout) mActivity.findViewById(android.R.id.content)
				.getParent());
		FrameLayout.LayoutParams parm = (FrameLayout.LayoutParams) sContent
				.getLayoutParams();
		parm.setMargins(sMenuSize, 0, -sMenuSize, 0);
		sContent.setLayoutParams(parm);
		// animation for smooth slide-out
		TranslateAnimation ta = new TranslateAnimation(-sMenuSize, 0, 0, 0);
		ta.setDuration(500);
		if (animate) {
			sContent.startAnimation(ta);
		}
		sParent = (FrameLayout) sContent.getParent();
		LayoutInflater inflater = (LayoutInflater) mActivity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		sMenu = inflater.inflate(R.layout.menu, null);
		FrameLayout.LayoutParams lays = new FrameLayout.LayoutParams(-1, -1, 3);
		lays.setMargins(0, sStatusHeight, 0, 0);
		sMenu.setLayoutParams(lays);
		sParent.addView(sMenu);
		ListView list = (ListView) mActivity.findViewById(R.id.menu_listview);
		list.setOnItemClickListener(mItemClickListener);
		if (animate) {
			sMenu.startAnimation(ta);
		}
		sMenu.findViewById(R.id.overlay).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						hide();
					}
				});
		Functions.enableDisableViewGroup(
				(LinearLayout) sParent.findViewById(android.R.id.content)
						.getParent(), false);
		sMenuShown = true;
		list.setAdapter(mAdapter);
	}

	public void setAdapter(SlideMenuAdapter adapter) {
		mAdapter = adapter;
	}
	
	public void setOnItemClickListener(OnItemClickListener listener) {
		mItemClickListener = listener;
	}

	public void hide() {
		TranslateAnimation ta = new TranslateAnimation(0, -sMenuSize, 0, 0);
		ta.setDuration(500);
		sMenu.startAnimation(ta);
		sParent.removeView(sMenu);

		TranslateAnimation tra = new TranslateAnimation(sMenuSize, 0, 0, 0);
		tra.setDuration(500);
		sContent.startAnimation(tra);
		FrameLayout.LayoutParams parm = (FrameLayout.LayoutParams) sContent
				.getLayoutParams();
		parm.setMargins(0, 0, 0, 0);
		sContent.setLayoutParams(parm);
		Functions.enableDisableViewGroup(
				(LinearLayout) sParent.findViewById(android.R.id.content)
						.getParent(), true);
		sMenuShown = false;
	}

	public boolean isMenuShown() {
		return sMenuShown;
	}
}