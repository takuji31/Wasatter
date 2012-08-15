package jp.senchan.android.wasatter.view;

import java.util.ArrayList;

import jp.senchan.android.wasatter.R;
import jp.senchan.lib.app.ArrayListAdapter;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

// just a simple adapter
public class SlideMenuAdapter extends ArrayListAdapter<SlideMenuItem> {

	public SlideMenuAdapter(Context context, ArrayList<SlideMenuItem> list) {
		super(context, list);
	}

	static class ViewHolder {
		public TextView label;
		public ImageView icon;
	}

	@Override
	public int getViewLayoutId(int position) {
		// TODO Auto-generated method stub
		return R.layout.menu_listitem;
	}

	@Override
	public View createView(int position, SlideMenuItem item, View v) {
		ViewHolder holder = (ViewHolder) v.getTag();
		if (holder == null) {
			holder = new ViewHolder();
			holder.label = (TextView) v
					.findViewById(R.id.menu_label);
			holder.icon = (ImageView) v
					.findViewById(R.id.menu_icon);
			v.setTag(holder);
		}
		String s = item.label;
		holder.label.setText(s);
		holder.icon.setImageResource(item.icon);
		return v;
	}
}