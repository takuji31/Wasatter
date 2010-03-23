package jp.senchan.android.wasatter;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author Senka/Takuji
 *
 */
public class ActivityUpdateStatus extends Activity {
	protected WasatterItem ws;
	protected boolean reply;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.status_update);
		Bundle extras = this.getIntent().getExtras();
		if (extras != null) {
			this.ws = (WasatterItem) extras.getSerializable(Wasatter.REPLY);
		}

		// チェックボックスの設定
		CheckBox wassr_enable = (CheckBox) this
				.findViewById(R.id.check_post_wassr);
		CheckBox twitter_enable = (CheckBox) this
				.findViewById(R.id.check_post_twitter);
		wassr_enable.setChecked(Setting.isWassrEnabled());
		wassr_enable.setClickable(Setting.isWassrEnabled());
		twitter_enable.setChecked(Setting.isTwitterEnabled());
		twitter_enable.setClickable(Setting.isTwitterEnabled());
		// 返信の場合は元メッセージの表示処理等を追加
		if (this.ws != null) {
			this.reply = true;
			if (this.ws.text != null) {
				SpannableStringBuilder sb = new SpannableStringBuilder();
				sb.append("> ");
				sb.append(this.ws.text);
				TextView reply_message = (TextView) this
						.findViewById(R.id.reply_message);
				reply_message.setText(sb.toString());
			}
			if (this.ws.name != null) {
				SpannableStringBuilder sb2 = new SpannableStringBuilder();
				sb2.append("by ");
				sb2.append(this.ws.name);
				TextView reply_user_nick = (TextView) this
						.findViewById(R.id.reply_user_name);
				reply_user_nick.setText(sb2.toString());
			}
			if (this.ws.service.equals(Wasatter.SERVICE_TWITTER)) {
				// 更にTwitterなら@ユーザー名をテキストの初期値にする。
				EditText et = (EditText) this
						.findViewById(R.id.post_status_text);
				et.setText(new SpannableStringBuilder("@").append(this.ws.id)
						.append(" ").toString());
			}
		}
		// 更に返信なら指定されたサービスのみチェックを入れて非表示にする。
		if (reply) {
			wassr_enable.setChecked(this.ws.service
					.equals(Wasatter.SERVICE_WASSR));
			twitter_enable.setChecked(this.ws.service
					.equals(Wasatter.SERVICE_TWITTER));
			LinearLayout layout_service = (LinearLayout) this
					.findViewById(R.id.layout_service);
			layout_service.setVisibility(View.GONE);
		}else{
			LinearLayout layout_reply = (LinearLayout) this
			.findViewById(R.id.layout_reply);
			layout_reply.setVisibility(View.GONE);
		}

		Button post_btn = (Button) this.findViewById(R.id.post_button);
		if (post_btn != null) {
			post_btn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					EditText status = (EditText) ActivityUpdateStatus.this
							.findViewById(R.id.post_status_text);
					CheckBox wassr = (CheckBox) ActivityUpdateStatus.this
							.findViewById(R.id.check_post_wassr);
					CheckBox twitter = (CheckBox) ActivityUpdateStatus.this
							.findViewById(R.id.check_post_twitter);
					SpannableStringBuilder sb = (SpannableStringBuilder) status
							.getText();
					Button update_button = (Button) v;
					//未入力チェック
					if("".equals(sb.toString())){
						AlertDialog.Builder adb = new AlertDialog.Builder(ActivityUpdateStatus.this);
						adb.setTitle("");
						adb.setMessage(R.string.notice_message_required);
						adb.setPositiveButton("OK", null);
						adb.show();
						return;
					}
					// 二重投稿防止
					update_button.setClickable(false);
					TaskUpdate ut = new TaskUpdate(ActivityUpdateStatus.this.reply,wassr.isChecked(),twitter.isChecked());
					ut.execute(sb.toString(),ActivityUpdateStatus.this.reply ? ActivityUpdateStatus.this.ws.rid : null);
					ActivityUpdateStatus.this.finish();
				}
			});
		}

		Button short_button = (Button)this.findViewById(R.id.short_button);
		short_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO 自動生成されたメソッド・スタブ
				EditText status = (EditText)ActivityUpdateStatus.this.findViewById(R.id.post_status_text);
				String str = status.getText().toString();
				String replace;
				String url = Wasatter.getUrl(str);
				String short_url = UrlGetter.bitly(url, UrlGetter.JMP);
				if(!"".equals(short_url)){
					replace = str.replace(url, short_url);
				}else{
					replace = str;
				}
				status.setText(replace);
			}
		});
	}
}
