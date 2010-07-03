package jp.senchan.android.wasatter.activity;

import jp.senchan.android.wasatter.Setting;
import jp.senchan.android.wasatter.Wasatter;
import jp.senchan.android.wasatter.task.TaskUpdate;
import jp.senchan.android.wasatter.util.UrlGetter;
import jp.senchan.android.wasatter.util.WasatterItem;
import jp.senchan.android.wasatter.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Senka/Takuji
 *
 */
public class Update extends Activity {
	protected WasatterItem ws;
	protected boolean reply;
	protected boolean channel;
	protected String channelId;
	public static final int REQUEST_CAMERA = 1;
	public static final int SELECT_IMAGE = 2;
	public static String attachFileName;

	
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
		if (channel) {
			twitter_enable.setChecked(false);
			twitter_enable.setClickable(false);
		}
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
			if (this.ws.service.equals(Wasatter.TWITTER)) {
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
					.equals(Wasatter.WASSR));
			twitter_enable.setChecked(this.ws.service
					.equals(Wasatter.TWITTER));
			LinearLayout layout_service = (LinearLayout) this
					.findViewById(R.id.layout_service);
			layout_service.setVisibility(View.GONE);
		} else {
			LinearLayout layout_reply = (LinearLayout) this
					.findViewById(R.id.layout_reply);
			layout_reply.setVisibility(View.GONE);
		}

		Button post_btn = (Button) this.findViewById(R.id.post_button);
		if (post_btn != null) {
			post_btn.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					EditText status = (EditText) Update.this
							.findViewById(R.id.post_status_text);
					CheckBox wassr = (CheckBox) Update.this
							.findViewById(R.id.check_post_wassr);
					CheckBox twitter = (CheckBox) Update.this
							.findViewById(R.id.check_post_twitter);
					SpannableStringBuilder sb = (SpannableStringBuilder) status
							.getText();
					Button update_button = (Button) v;
					// 未入力チェック
					if ("".equals(sb.toString())) {
						AlertDialog.Builder adb = new AlertDialog.Builder(
								Update.this);
						adb.setTitle("");
						adb.setMessage(R.string.notice_message_required);
						adb.setPositiveButton("OK", null);
						adb.show();
						return;
					}
					// 二重投稿防止
					update_button.setClickable(false);
					TaskUpdate ut = new TaskUpdate(
							Update.this.reply, wassr.isChecked(),
							twitter.isChecked(),
							Update.this.channel);
					ut
							.execute(
									sb.toString(),
									Update.this.reply ? Update.this.ws.rid
											: null,
									Update.this.channelId,attachFileName);
					Update.this.finish();
				}
			});
		}

		Button short_button = (Button) this.findViewById(R.id.short_button);
		short_button.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO 自動生成されたメソッド・スタブ
				EditText status = (EditText) Update.this
						.findViewById(R.id.post_status_text);
				String str = status.getText().toString();
				String replace;
				String url = Wasatter.getUrl(str);
				String short_url = UrlGetter.bitly(url, UrlGetter.JMP);
				if (!"".equals(short_url)) {
					replace = str.replace(url, short_url);
				} else {
					replace = str;
				}
				status.setText(replace);
			}
		});

		// カメラアプリの起動ボタン作ってみた
		Button openCamera = (Button) findViewById(R.id.openCamera);
		openCamera.setOnClickListener(new OnClickListener() {

			
			public void onClick(View v) {
				// TODO 自動生成されたメソッド・スタブ
				final Intent intent = new Intent(
						MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent, REQUEST_CAMERA);
			}
		});
		// 画像選択もできるようにした
		Button selectImage = (Button) findViewById(R.id.selectImage);
		selectImage.setOnClickListener(new OnClickListener() {

			
			public void onClick(View v) {
				// TODO 自動生成されたメソッド・スタブ
				final Intent intent = new Intent(Intent.ACTION_PICK);
				intent.setType("image/*");
				startActivityForResult(intent, SELECT_IMAGE);
			}
		});
	}

	
	protected void onActivityResult(final int requestCode,
			final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		try {
		Bitmap image = null;
		if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
			// get the picture
			image = (Bitmap) data.getExtras().get("data");
		}
		if (requestCode == SELECT_IMAGE && resultCode == RESULT_OK) {
			// Content
			// Providerとは、全てのアプリケーションからデータの読み書きが可能なオブジェクトで、パッケージ間でデータ共有を行う唯一の手段
			Uri photoUri = data.getData();
			ContentResolver cont_reslv = getContentResolver();
			if (photoUri != null) {
					image = MediaStore.Images.Media.getBitmap(cont_reslv, photoUri);
			}
		}

		//画像がセットされたら一時パスに保存する
		if(image != null){
			ImageView image_preview = (ImageView) findViewById(R.id.cameraResult);
			image_preview.setImageBitmap(image);
			Wasatter.saveTempImage(image);
			attachFileName = Wasatter.getImageTempPath()+"temp.jpg";
		}
		} catch (Throwable e) {
			e.printStackTrace();
			Toast.makeText(Update.this, "画像の添付に失敗しました。サイズオーバーです…。", Toast.LENGTH_SHORT).show();
		}
	}
}
