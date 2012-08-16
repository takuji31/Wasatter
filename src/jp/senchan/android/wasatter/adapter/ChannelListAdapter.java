package jp.senchan.android.wasatter.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.androidquery.AQuery;

import android.content.Context;
import android.view.View;
import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.model.api.WasatterStatus;
import jp.senchan.android.wasatter.model.api.impl.wassr.WassrChannel;

public class ChannelListAdapter extends BaseTimelineAdapter {

	public ChannelListAdapter(Context context, ArrayList<WasatterStatus> list) {
		super(context, list);
	}

	@Override
	public int getViewLayoutId(int position) {
		return R.layout.channel_row;
	}

	@Override
	public View createView(int position, WasatterStatus item, View v) {
		
		AQuery aq = new AQuery(v);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		
		aq.id(R.id.textViewName).text(item.getBody());
		aq.id(R.id.textViewNameEn).text(item.getStatusId());
		aq.id(R.id.textViewLastMessagedAt).text(sdf.format(new Date(item.getTime())));
		//TODO デフォルトアイコン設定する
		aq.id(R.id.imageViewIcon).image(R.drawable.ic_default_user_icon).image(((WassrChannel)item).getImageUrl());
		
		return v;
	}

}
