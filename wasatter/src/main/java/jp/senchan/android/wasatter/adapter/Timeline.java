package jp.senchan.android.wasatter.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.Wasatter;
import jp.senchan.android.wasatter.WasatterActivity;
import jp.senchan.android.wasatter.WasatterItem;
import jp.senchan.android.wasatter.client.OldWassrClient;
import jp.senchan.android.wasatter.next.Functions;
import android.content.Context;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquery.AQuery;

public class Timeline extends ArrayAdapter<WasatterItem> implements
        WasatterAdapter {

    private ArrayList<WasatterItem> items;
    private LayoutInflater inflater;
    private WasatterActivity mActivity;

    public Timeline(WasatterActivity context, int textViewResourceId,
            ArrayList<WasatterItem> items, boolean channel) {
        super(context, textViewResourceId, items);
        mActivity = context;
        this.items = items;
        this.inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View view, ViewGroup parent) {

    	Wasatter app = mActivity.app();
    	
    	if (view == null) {
            // 受け取ったビューがnullなら新しくビューを生成
            view = this.inflater.inflate(R.layout.old_timeline_row, null);
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
            //TODO 設定可能にすべき？
            //text.setSingleLine(!Setting.isDisplayBodyMultiLine());
            text.setSingleLine(false);
            /*
            if (!Setting.isDisplayBodyMultiLine()) {
                text.setEllipsize(TruncateAt.MARQUEE);
            } else {
            */
                text.setEllipsize(null);
            //}
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
            AQuery aq = new AQuery(view);
            aq.id(R.id.icon);
            if (app.isImageLoadEnabled()) {
            	aq.image(item.profileImageUrl, true, true, 0, R.drawable.ic_default_user_icon);
            } else {
                aq.image(R.drawable.ic_default_user_icon);
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
                int favoriteIconSize = Functions.dpToPx(16, mActivity);
                int mergin = 2;
                if (app.isLoadFavoriteImage()) {
                    for (int i = 0; i < count; i++) {
                        ImageView add_icon = new ImageView(view.getContext());
                        AQuery icon_aq = new AQuery(add_icon);
                        add_icon.setLayoutParams(new LayoutParams(favoriteIconSize, favoriteIconSize));
                        add_icon.setPadding(mergin, mergin, mergin, mergin);
                        icon_aq.image(OldWassrClient.FAVORITE_ICON_URL.replace("[user]", favorites.get(i)), true, false);
                        layout_favorite_icons.addView(add_icon);
                    }
                }
            }

            // 投稿日時表示するぜヒャッハー
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