package jp.senchan.android.wasatter.adapter;

import java.util.ArrayList;

import com.androidquery.AQuery;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;
import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.model.api.WasatterStatus;
import jp.senchan.android.wasatter.utils.URLImageParser;
import jp.senchan.lib.app.ArrayListAdapter;

public abstract class BaseTimelineAdapter extends ArrayListAdapter<WasatterStatus> {

	public BaseTimelineAdapter(Context context, ArrayList<WasatterStatus> list) {
		super(context, list);
	}

	public void setHtmlText(String htmlSource, AQuery aq, int textViewId) {
		aq.id(textViewId);
		TextView tv = aq.getTextView();
		URLImageParser parser = new URLImageParser(tv, mContext);
		Spanned html = Html.fromHtml(htmlSource, parser, null);
		aq.text(html);
	}
}
