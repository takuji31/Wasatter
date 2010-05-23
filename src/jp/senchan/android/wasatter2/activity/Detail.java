package jp.senchan.android.wasatter2.activity;

import jp.senchan.android.wasatter2.R;
import jp.senchan.android.wasatter2.Setting;
import jp.senchan.android.wasatter2.Wasatter;
import jp.senchan.android.wasatter2.item.Item;
import jp.senchan.android.wasatter2.task.Favorite;
import jp.senchan.android.wasatter2.task.TaskToggleFavorite;
import jp.senchan.android.wasatter2.util.ResultCode;
import jp.senchan.android.wasatter2.util.WasatterItem;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources.Theme;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Html.ImageGetter;
import android.view.View;
import android.view.Window;
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
public class Detail extends Activity {
	protected Item item;
	public static String ADD_WASSR = "イイネ！する";
	public static String DEL_WASSR = "イイネ！を消す";
	public static String ADD_TWITTER = "お気に入りに追加する";
	public static String DEL_TWITTER = "お気に入りから削除する";
	public Button favoriteButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.wasatter_detail);
		favoriteButton = (Button) findViewById(R.id.button_favorite);
		try {
			Item item = Wasatter.main.selectedItem;
			this.item = item;
			// サービス名/チャンネル名をセット
			TextView service_name = (TextView) this
					.findViewById(R.id.service_name);
			service_name.setText(item.service);
			// ニックネームをセット
			TextView screen_name = (TextView) this
					.findViewById(R.id.screen_name);
			screen_name.setText(item.name);
			// 本文をセット
			TextView status = (TextView) this.findViewById(R.id.status);
			CharSequence html = Html.fromHtml(item.html,
					new ImageGetter() {

						@Override
						public Drawable getDrawable(String source) {
							Bitmap bmp = Wasatter.images
									.get(source);
							BitmapDrawable bd = new BitmapDrawable(
									bmp);
							// TODO:解像度ごとにサイズ変えられたらいいなああああ
							Rect bounds = new Rect(0, 0, 20, 20);
							bd.setBounds(bounds);
							return bd;
						}
					}, null);
			status.setText(html);
			// 画像をセット
			ImageView icon = (ImageView) this.findViewById(R.id.icon);
			Bitmap bmp = Wasatter.images.get(item.profileImageUrl);
			icon.setImageBitmap(bmp);
			// 返信であるかどうか判定
			if (item.replyUserNick != null && !item.replyUserNick.equals("null")) {
				TextView reply_message = (TextView) this
						.findViewById(R.id.reply_text);
				SpannableStringBuilder sb = new SpannableStringBuilder("by ");
				sb.append(item.replyUserNick);
				TextView reply_user_name = (TextView) this
						.findViewById(R.id.reply_user_name);
				reply_user_name.setText(sb.toString());
				if (item.replyMessage != null) {
					SpannableStringBuilder sb2 = new SpannableStringBuilder(
							"> ");
					sb2.append(item.replyMessage);
					reply_message.setText(sb2.toString());
				}

			} else {
				LinearLayout layout_reply = (LinearLayout) this
						.findViewById(R.id.layout_reply);
				layout_reply.setVisibility(View.GONE);
			}

			//パーマリンクを表示する
			TextView permaLink = (TextView) findViewById(R.id.permaLink);
			permaLink.setText(item.link);

			// Favoriteボタンにイベント割り当て
			Button button_favorite = (Button) this
					.findViewById(R.id.button_favorite);
			if (Wasatter.TWITTER.equals(item.service)) {
				button_favorite.setText(ADD_TWITTER);
			} else if (item.favorite != null
					&& item.favorite.indexOf(Setting.getWassrId()) != -1) {
				button_favorite.setText(DEL_WASSR);
			} else {
				button_favorite.setText(ADD_WASSR);
			}
			button_favorite.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					new Favorite(Detail.this).execute(Detail.this.item);
				}
			});
			// Replyボタンにイベント割り当て
			Button button_reply = (Button) this.findViewById(R.id.reply_button);
			button_reply.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO 自動生成されたメソッド・スタブ
					Intent intent_reply = new Intent(Detail.this, Update.class);
					intent_reply.putExtra(Wasatter.REPLY, Detail.this.item);
					startActivity(intent_reply);
				}
			});
		} catch (NullPointerException e) {
			// 何かおかしかったら終了させる
			finish();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (isFinishing()) {
			setResult(ResultCode.OK);
		}
	}
}
