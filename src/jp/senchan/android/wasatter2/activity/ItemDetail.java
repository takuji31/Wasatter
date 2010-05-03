package jp.senchan.android.wasatter2.activity;

import jp.senchan.android.wasatter2.R;
import jp.senchan.android.wasatter2.Setting;
import jp.senchan.android.wasatter2.Wasatter;
import jp.senchan.android.wasatter2.task.TaskToggleFavorite;
import jp.senchan.android.wasatter2.util.ResultCode;
import jp.senchan.android.wasatter2.util.WasatterItem;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 各つぶやき/ヒトコトの詳細
 *
 * @author Senka/Takuji
 *
 */
public class ItemDetail extends Activity {
	protected WasatterItem ws;
	public static String ADD_WASSR = "イイネ！する";
	public static String DEL_WASSR = "イイネ！を消す";
	public static String ADD_TWITTER = "お気に入りに追加する";
	public static String DEL_TWITTER = "お気に入りから削除する";
	public Button favoriteButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.wasatter_detail);
		favoriteButton = (Button) findViewById(R.id.button_favorite);
		this.ws = Wasatter.main.selectedItem;
		if (this.ws != null) {
			WasatterItem wss = this.ws;
			// サービス名/チャンネル名をセット
			TextView service_name = (TextView) this
					.findViewById(R.id.service_name);
			service_name.setText(wss.service);
			// ニックネームをセット
			TextView screen_name = (TextView) this
					.findViewById(R.id.screen_name);
			screen_name.setText(wss.name);
			// 本文をセット
			TextView status = (TextView) this.findViewById(R.id.status);
			status.setText(wss.html);
			// 画像をセット
			ImageView icon = (ImageView) this.findViewById(R.id.icon);
			Bitmap bmp = Wasatter.images.get(wss.profileImageUrl);
			if (bmp != null && Setting.isLoadImage()) {
				icon.setImageBitmap(bmp);
				icon.setVisibility(View.VISIBLE);
			} else {
				icon.setVisibility(View.GONE);
			}
			// 返信であるかどうか判定
			if (wss.replyUserNick != null && !wss.replyUserNick.equals("null")) {
				TextView reply_message = (TextView) this
						.findViewById(R.id.reply_text);
				SpannableStringBuilder sb = new SpannableStringBuilder("by ");
				sb.append(wss.replyUserNick);
				TextView reply_user_name = (TextView) this
						.findViewById(R.id.reply_user_name);
				reply_user_name.setText(sb.toString());
				if (wss.replyMessage != null) {
					SpannableStringBuilder sb2 = new SpannableStringBuilder(
							"> ");
					sb2.append(wss.replyMessage);
					reply_message.setText(sb2.toString());
				}

			} else {
				LinearLayout layout_reply = (LinearLayout) this
						.findViewById(R.id.layout_reply);
				layout_reply.setVisibility(View.GONE);
			}
			// OpenPermalinkボタンにイベント割り当て
			Button button_open_link = (Button) this
					.findViewById(R.id.button_open_link);
			button_open_link.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO 自動生成されたメソッド・スタブ
					String permalink = ItemDetail.this.ws.link;
					Intent intent_parmalink = new Intent(Intent.ACTION_VIEW,
							Uri.parse(permalink));
					startActivity(intent_parmalink);
				}
			});
			// Open URLボタンにイベント割り当て
			Button button_open_url = (Button) this
					.findViewById(R.id.button_open_url);
			button_open_url.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO 自動生成されたメソッド・スタブ
					String text = ItemDetail.this.ws.text;
					String url = Wasatter.getUrl(text);
					if (!"".equals(url)) {
						Intent intent_url = new Intent(Intent.ACTION_VIEW, Uri
								.parse(url));
						startActivity(intent_url);
					} else {
						AlertDialog.Builder ad = new AlertDialog.Builder(
								ItemDetail.this);
						// ad.setTitle(R.string.notice_title_no_url);
						ad.setMessage(R.string.notice_message_no_url);
						ad.setPositiveButton("OK", null);
						ad.show();
					}
				}
			});
			// Favoriteボタンにイベント割り当て
			Button button_favorite = (Button) this
					.findViewById(R.id.button_favorite);
			if (Wasatter.TWITTER.equals(wss.service)) {
				button_favorite.setText(ADD_TWITTER);
			} else if (wss.favorite != null
					&& wss.favorite.indexOf(Setting.getWassrId()) != -1) {
				button_favorite.setText(DEL_WASSR);
			} else {
				button_favorite.setText(ADD_WASSR);
			}
			button_favorite.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					new TaskToggleFavorite(ItemDetail.this)
							.execute(ItemDetail.this.ws);
				}
			});
			// Replyボタンにイベント割り当て
			Button button_reply = (Button) this.findViewById(R.id.reply_button);
			button_reply.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO 自動生成されたメソッド・スタブ
					Intent intent_reply = new Intent(ItemDetail.this,
							Update.class);
					intent_reply.putExtra(Wasatter.REPLY,
							ItemDetail.this.ws);
					startActivity(intent_reply);
				}
			});
		}
	}
	@Override
	protected void onPause() {
		super.onPause();
		if(isFinishing()){
			setResult(ResultCode.OK);
		}
	}
}
