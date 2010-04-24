package jp.senchan.android.wasatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.SpannableStringBuilder;
import android.text.TextUtils.TruncateAt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AdapterTimeline extends ArrayAdapter<WasatterItem> implements
		WasatterAdapter {

	private ArrayList<WasatterItem> items;
	private LayoutInflater inflater;

	public AdapterTimeline(Context context, int textViewResourceId,
			ArrayList<WasatterItem> items, boolean channel) {
		super(context, textViewResourceId, items);
		this.items = items;
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public View getView(int position, View view, ViewGroup parent) {
		if (view == null) {
			// 受け取ったビューがnullなら新しくビューを生成
			view = this.inflater.inflate(R.layout.timeline_row, null);
		}
		// データの取得
		WasatterItem item = (WasatterItem) this.items.get(position);
		if (item != null) {
			TextView screenName = (TextView) view
					.findViewById(R.id.screen_name);

			// 名前をビューにセットする
			TextView text = (TextView) view.findViewById(R.id.status);
			if (screenName != null) {
				screenName.setText(item.name);
			}
			// テキストをビューにセットする
			if (text != null) {
				text.setText(item.text);
			}
			// テキストの行数を決定する。
			text.setSingleLine(!Setting.isDisplayBodyMultiLine());
			if (!Setting.isDisplayBodyMultiLine()) {
				text.setEllipsize(TruncateAt.MARQUEE);
			} else {
				text.setEllipsize(null);
			}
			// 返信元の名前をビューにセットする
			TextView reply_name = (TextView) view.findViewById(R.id.reply_name);
			if (reply_name != null && !"null".equals(item.replyUserNick)
					&& item.replyUserNick != null) {
				reply_name.setText(new SpannableStringBuilder(">").append(
						item.replyUserNick).toString());
				reply_name.setVisibility(View.VISIBLE);
			} else if (reply_name != null) {
				reply_name.setText("");
				reply_name.setVisibility(View.GONE);
			}
			// アイコンをロードする。
			Bitmap image = Wasatter.images.get(item.profileImageUrl);
			ImageView icon = (ImageView) view.findViewById(R.id.icon);
			if (!Setting.isLoadImage()) {
				icon.setVisibility(View.GONE);
			} else {
				icon.setImageBitmap(image);
				icon.setVisibility(View.VISIBLE);
			}
			// サービス名をビューにセットする
			TextView service = (TextView) view.findViewById(R.id.service_name);
			if (service != null) {
				service.setText(item.service);
			}
			// イイネ！アイコンリストを表示
			LinearLayout layout_favorite_list = (LinearLayout) view
					.findViewById(R.id.layout_favorite);
			LinearLayout layout_favorite_icons = (LinearLayout) view
					.findViewById(R.id.layout_favorite_images);
			if (item.favorite.size() == 0) {
				layout_favorite_list.setVisibility(View.GONE);
				layout_favorite_icons.removeAllViews();
			} else {
				ArrayList<String> favorites = item.favorite;
				int count = favorites.size();
				TextView tv = new TextView(view.getContext());
				tv.setText(new SpannableStringBuilder("x").append(
						String.valueOf(count)).toString());
				layout_favorite_icons.removeAllViews();
				layout_favorite_icons.addView(tv);
				layout_favorite_list.setVisibility(View.VISIBLE);
				if (Setting.isLoadFavoriteImage()) {
					for (int i = 0; i < count; i++) {
						ImageView add_icon = new ImageView(view.getContext());
						add_icon.setImageBitmap(Wasatter.images
								.get(WassrClient.FAVORITE_ICON_URL.replace(
										"[user]", favorites.get(i))));
						add_icon.setLayoutParams(new LayoutParams(28, 28));
						add_icon.setPadding(2, 2, 2, 2);
						layout_favorite_icons.addView(add_icon);
					}
				}
			}

			//投稿日時表示するぜヒャッハー
			TextView date = (TextView) view.findViewById(R.id.post_date);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date now = new Date();
			now.setTime(item.epoch * 1000);
			date.setText(sdf.format(now));
		}
		return view;
	}

	@Override
	public void updateView() {
		// TODO 自動生成されたメソッド・スタブ
		super.notifyDataSetChanged();
	}
}