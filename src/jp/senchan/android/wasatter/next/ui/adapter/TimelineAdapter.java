package jp.senchan.android.wasatter.next.ui.adapter;

import java.util.ArrayList;

import com.androidquery.AQuery;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.Wasatter;
import jp.senchan.android.wasatter.next.model.api.WasatterStatus;
import jp.senchan.android.wasatter.next.model.api.WassrStatus;
import jp.senchan.lib.view.ArrayListAdapter;

public class TimelineAdapter extends ArrayListAdapter<WasatterStatus> {

	public TimelineAdapter(Context context, ArrayList<WasatterStatus> list) {
		super(context, list);
	}

	@Override
	public int getViewLayoutId(int position) {
		// TODO Auto-generated method stub
		return R.layout.timeline_row;
	}

	@Override
	public View createView(int position, WasatterStatus item, View v) {
		
		WassrStatus st = (WassrStatus) item;
		AQuery aq = new AQuery(v);
		aq.id(R.id.textViewName).text(st.user.name);
		aq.id(R.id.textViewId).text(st.screenName());
		aq.id(R.id.textViewServiceName).text(Wasatter.SERVICE_WASSR);
		aq.id(R.id.textViewBody).text(st.body);
		aq.id(R.id.imageViewIcon).image(R.drawable.ic_default_user_icon).image(st.user.profileImageUrl);
		
		return v;
	}

}
