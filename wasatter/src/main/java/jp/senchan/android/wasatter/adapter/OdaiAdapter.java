package jp.senchan.android.wasatter.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.androidquery.AQuery;

import android.content.Context;
import android.view.View;
import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.model.api.WasatterStatus;

public class OdaiAdapter extends BaseTimelineAdapter {

	public OdaiAdapter(Context context,
			ArrayList<WasatterStatus> list) {
		super(context, list);
	}

	@Override
	public int getViewLayoutId(int position) {
		return R.layout.odai_row;
	}

	@Override
	public View createView(int position, WasatterStatus item,
			View v) {
		AQuery aq = new AQuery(v);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		
		setHtmlText(item.getBody(), aq, R.id.textViewBody);
		aq.id(R.id.textViewCreatedAt).text(sdf.format(new Date(item.getTime())));
		
		return v;
	}

}
