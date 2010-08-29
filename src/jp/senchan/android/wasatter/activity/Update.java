package jp.senchan.android.wasatter.activity;

import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.Wasatter;
import jp.senchan.android.wasatter.client.Twitter;
import jp.senchan.android.wasatter.client.Wassr;
import jp.senchan.android.wasatter.item.Item;
import jp.senchan.android.wasatter.task.UpdateStatus;
import jp.senchan.android.wasatter.util.UrlGetter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Html.ImageGetter;
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
	public Item ws;
	protected boolean channel;
	protected String channelId;
	public static final int REQUEST_CAMERA = 1;
	public static final int SELECT_IMAGE = 2;
	public static String attachFileName;
	public boolean isReply;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.status_update);
		Bundle extras = this.getIntent().getExtras();
		if (extras != null) {
			this.ws = (Item) extras.getSerializable(Wasatter.REPLY);
		}
		isReply = (ws != null);
		

		// チェックボックスの設定
		CheckBox checkPostWassr = (CheckBox) this
				.findViewById(R.id.check_post_wassr);
		CheckBox checkPostTwitter = (CheckBox) this
				.findViewById(R.id.check_post_twitter);
		boolean enableWassr = Wassr.enabled() && (!isReply || Wasatter.WASSR.equals(this.ws.service));
		boolean enableTwitter = Twitter.enabled() && (!isReply || Wasatter.TWITTER.equals(this.ws.service));
		checkPostWassr.setChecked(enableWassr);
		checkPostWassr.setClickable(enableWassr);
		checkPostTwitter.setChecked(enableTwitter);
		checkPostTwitter.setClickable(enableTwitter);
		if(isReply){
			//Replyの場合はそもそもサービス指定いらない
			LinearLayout layoutService = (LinearLayout) findViewById(R.id.layout_service);
			layoutService.setVisibility(View.GONE);
			
			//Replyの場合は返信元の発言を表示する
			TextView replyMessage = (TextView) findViewById(R.id.reply_message);
			TextView replyName = (TextView) findViewById(R.id.reply_user_name);
			//FIXME コピペしてるのでまとめたい
			CharSequence html = Html.fromHtml(ws.html,
					new ImageGetter() {
						public Drawable getDrawable(String source) {
							// 必要な画像のURLをあらかじめ取得
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
			replyMessage.setText(html);
			replyName.setText(this.ws.name);
		}else{
			//Replyでない場合はそもそも返信元がない
			LinearLayout layoutReply = (LinearLayout) findViewById(R.id.layout_reply);
			layoutReply.setVisibility(View.GONE);
		}
		Button post_btn = (Button) this.findViewById(R.id.post_button);
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
					adb.setMessage(R.string.notice_message_required);
					adb.setPositiveButton("OK", null);
					adb.show();
					return;
				}
				// 二重投稿防止
				update_button.setClickable(false);
				UpdateStatus task = new UpdateStatus(Update.this,wassr.isChecked(),twitter.isChecked());
				task.execute(sb.toString());
			}
		});

		Button short_button = (Button) this.findViewById(R.id.short_button);
		short_button.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
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

		// TODO Twitterの画像投稿どうすんの
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
					image = MediaStore.Images.Media.getBitmap(cont_reslv,
							photoUri);
				}
			}

			// 画像がセットされたら一時パスに保存する
			if (image != null) {
				ImageView image_preview = (ImageView) findViewById(R.id.cameraResult);
				image_preview.setImageBitmap(image);
				Wasatter.saveTempImage(image);
				attachFileName = Wasatter.getImageTempPath() + "temp.jpg";
			}
		} catch (Throwable e) {
			// FIXME ここに来るのはまずOutOfMemoryErrorがほぼ100%。如何にして発生しないようにするか。
			e.printStackTrace();
			Toast.makeText(Update.this, "画像の添付に失敗しました。サイズオーバーです…。",
					Toast.LENGTH_SHORT).show();
		}
	}
}
