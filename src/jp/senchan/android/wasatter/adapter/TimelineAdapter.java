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
		
		AQuery aq = new AQuery(v);
		WasatterUser user = item.getUser();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/yy HH:mm:ss");
		
		aq.id(R.id.textViewName).text(user.getName());
		aq.id(R.id.textViewServiceName).text(item.getServiceName());
		aq.id(R.id.textViewBody).text(item.getBody());
		aq.id(R.id.textViewCreatedAt).text(sdf.format(new Date(item.getTime())));
		aq.id(R.id.imageViewIcon).image(R.drawable.ic_default_user_icon).image(user.getProfileImageUrl());
		
		if (item.isRetweet()) {
			WasatterUser retweetUser = item.getRetweetUser();
			aq.id(R.id.textViewRetweetedBy).visible().text(String.format(mContext.getString(R.string.message_retweeted_by), retweetUser.getScreenName()));
			aq.id(R.id.imageViewRetweetIcon).visible().image(R.drawable.ic_default_user_icon).image(retweetUser.getProfileImageUrl());
		} else {
			aq.id(R.id.textViewRetweetedBy).gone();
			aq.id(R.id.imageViewRetweetIcon).gone();
		}
		
		return v;
	}

}
