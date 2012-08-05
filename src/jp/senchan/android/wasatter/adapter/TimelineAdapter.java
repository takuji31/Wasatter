package jp.senchan.android.wasatter.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.androidquery.AQuery;

import android.content.Context;
import android.view.View;
import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.Wasatter;
import jp.senchan.android.wasatter.model.api.WasatterStatus;
import jp.senchan.android.wasatter.model.api.WasatterUser;
import jp.senchan.android.wasatter.model.api.impl.wassr.WassrStatus;
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
		WasatterUser user = st.getUser();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/yy HH:mm::ss");
		
		aq.id(R.id.textViewName).text(user.getName());
		aq.id(R.id.textViewServiceName).text(st.getServiceName());
		aq.id(R.id.textViewBody).text(st.getBody());
		aq.id(R.id.textViewCreatedAt).text(sdf.format(new Date(st.getTime())));
		aq.id(R.id.imageViewIcon).image(R.drawable.ic_default_user_icon).image(user.getProfileImageUrl());
		
		return v;
	}

}
