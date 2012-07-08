package jp.senchan.android.wasatter.next.ui.view;

import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.next.Functions;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class SlideMenu {
	// just a simple adapter
	public static class SlideMenuAdapter extends
			ArrayAdapter<SlideMenu.SlideMenuAdapter.MenuDesc> {
		Activity act;
		SlideMenu.SlideMenuAdapter.MenuDesc[] items;

		class MenuItem {
			public TextView label;
			public ImageView icon;
		}

		static class MenuDesc {
			public int icon;
			public String label;
		}

		public SlideMenuAdapter(Activity act,
				SlideMenu.SlideMenuAdapter.MenuDesc[] items) {
			super(act, R.id.menu_label, items);
			this.act = act;
			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rowView = convertView;
			if (rowView == null) {
				LayoutInflater inflater = act.getLayoutInflater();
				rowView = inflater.inflate(R.layout.menu_listitem, null);
				MenuItem viewHolder = new MenuItem();
				viewHolder.label = (TextView) rowView
						.findViewById(R.id.menu_label);
				viewHolder.icon = (ImageView) rowView
						.findViewById(R.id.menu_icon);
				rowView.setTag(viewHolder);
			}

			MenuItem holder = (MenuItem) rowView.getTag();
			String s = items[position].label;
			holder.label.setText(s);
			holder.icon.setImageResource(items[position].icon);

			return rowView;
		}
	}

	private static boolean menuShown = false;
	private static View menu;
	private static LinearLayout content;
	private static FrameLayout parent;
	private static int menuSize;
	private static int statusHeight = 0;
	private Activity act;

	public SlideMenu(Activity act) {
		this.act = act;
	}

	// call this in your onCreate() for screen rotation
	public void checkEnabled() {
		if (menuShown)
			this.show(false);
	}

	public void show() {
		// get the height of the status bar
		if (statusHeight == 0) {
			Rect rectgle = new Rect();
			Window window = act.getWindow();
			window.getDecorView().getWindowVisibleDisplayFrame(rectgle);
			statusHeight = rectgle.top;
		}
		this.show(true);
	}

	public void show(boolean animate) {
		menuSize = Functions.dpToPx(250, act);
		content = ((LinearLayout) act.findViewById(android.R.id.content)
				.getParent());
		FrameLayout.LayoutParams parm = (FrameLayout.LayoutParams) content
				.getLayoutParams();
		parm.setMargins(menuSize, 0, -menuSize, 0);
		content.setLayoutParams(parm);
		// animation for smooth slide-out
		TranslateAnimation ta = new TranslateAnimation(-menuSize, 0, 0, 0);
		ta.setDuration(500);
		if (animate)
			content.startAnimation(ta);
		parent = (FrameLayout) content.getParent();
		LayoutInflater inflater = (LayoutInflater) act
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		menu = inflater.inflate(R.layout.menu, null);
		FrameLayout.LayoutParams lays = new FrameLayout.LayoutParams(-1, -1, 3);
		lays.setMargins(0, statusHeight, 0, 0);
		menu.setLayoutParams(lays);
		parent.addView(menu);
		ListView list = (ListView) act.findViewById(R.id.menu_listview);
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// handle your menu-click
			}
		});
		if (animate)
			menu.startAnimation(ta);
		menu.findViewById(R.id.overlay).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						SlideMenu.this.hide();
					}
				});
		Functions.enableDisableViewGroup(
				(LinearLayout) parent.findViewById(android.R.id.content)
						.getParent(), false);
		((ExtendedViewPager) act.findViewById(R.id.pager))
				.setPagingEnabled(false);
		((ExtendedPagerTabStrip) act.findViewById(R.id.tabStrip))
				.setNavEnabled(false);
		menuShown = true;
		this.fill();
	}

	public void fill() {
		ListView list = (ListView) act.findViewById(R.id.menu_listview);
		SlideMenuAdapter.MenuDesc[] items = new SlideMenuAdapter.MenuDesc[5];
		// fill the menu-items here
		items[0] = new SlideMenuAdapter.MenuDesc();
		items[1] = new SlideMenuAdapter.MenuDesc();
		items[2] = new SlideMenuAdapter.MenuDesc();
		items[3] = new SlideMenuAdapter.MenuDesc();
		items[4] = new SlideMenuAdapter.MenuDesc();
		items[0].label = "Profile";
		items[0].icon = R.drawable.ic_action_profile;
		items[1].label = "Timeline";
		items[2].label = "Reply";
		items[3].label = "Odai";
		items[4].label = "Channel";
		SlideMenuAdapter adap = new SlideMenuAdapter(act, items);
		list.setAdapter(adap);
	}

	public void hide() {
		TranslateAnimation ta = new TranslateAnimation(0, -menuSize, 0, 0);
		ta.setDuration(500);
		menu.startAnimation(ta);
		parent.removeView(menu);

		TranslateAnimation tra = new TranslateAnimation(menuSize, 0, 0, 0);
		tra.setDuration(500);
		content.startAnimation(tra);
		FrameLayout.LayoutParams parm = (FrameLayout.LayoutParams) content
				.getLayoutParams();
		parm.setMargins(0, 0, 0, 0);
		content.setLayoutParams(parm);
		Functions.enableDisableViewGroup(
				(LinearLayout) parent.findViewById(android.R.id.content)
						.getParent(), true);
		((ExtendedViewPager) act.findViewById(R.id.pager))
				.setPagingEnabled(true);
		((ExtendedPagerTabStrip) act.findViewById(R.id.tabStrip))
				.setNavEnabled(true);
		menuShown = false;
	}
}