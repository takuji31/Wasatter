package jp.senchan.android.wasatter.app.fragment;

import com.actionbarsherlock.app.SherlockDialogFragment;

import twitter4j.auth.AccessToken;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import jp.senchan.android.wasatter2.R;
import jp.senchan.android.wasatter.ResultCode;
import jp.senchan.android.wasatter.WasatterFragment;
import jp.senchan.android.wasatter.auth.params.OAuthTwitter;
import jp.senchan.android.wasatter.client.TwitterClient;
import jp.senchan.android.wasatter.next.listener.OnAccessTokenReceivedListener;
import jp.senchan.android.wasatter.next.listener.OnURLCreatedListener;
import jp.senchan.android.wasatter.next.task.GetTwitterOAuthAccessTokenTask;
import jp.senchan.android.wasatter.next.task.GetTwitterOAuthRequestURLTask;
import jp.senchan.android.wasatter.next.ui.fragment.dialog.CreateAuthenticationURLProgressDialogFragment;

public class SetTwitterAccountFragment extends WasatterFragment implements OnURLCreatedListener, OnAccessTokenReceivedListener {

	private static final String TAG_DIALOG = "dialog";
	TwitterClient mClient;
	private WebView mWebView;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mWebView = new WebView(getActivity());
		mWebView.getSettings().setJavaScriptEnabled(true);

		mClient = new TwitterClient(app());

		WebViewClient client = new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Uri uri = Uri.parse(url);
				//TODO ホスト名外出し
				if(TextUtils.equals(uri.getAuthority(), OAuthTwitter.CALLBACK_HOST)) {
					getAccessToken(uri);
					return true;
				}
				return false;
			}
		};
		mWebView.setWebViewClient(client);

		return mWebView;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		runGetAuthorizationURLTask();
	}

	public void runGetAuthorizationURLTask() {
		showDialogFragment(new CreateAuthenticationURLProgressDialogFragment());
		new GetTwitterOAuthRequestURLTask(this, mClient).supportExecute();
	}

	private void showDialogFragment(SherlockDialogFragment fragment) {
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		fragment.show(ft, TAG_DIALOG);
	}

	public void dismissDialog () {
		FragmentManager fm = getFragmentManager();
		SherlockDialogFragment f = (SherlockDialogFragment) fm.findFragmentByTag(TAG_DIALOG);
		if(f != null) {
			f.dismiss();
			fm.beginTransaction().remove(f).commit();
		}
	}

	@Override
	public void onURLCreated (String url) {
		dismissDialog();
		mWebView.loadUrl(url);
	}

	public void getAccessToken(Uri uri) {
		//TODO ダイアログ表示
		new GetTwitterOAuthAccessTokenTask(this, mClient).supportExecute(uri);
	}

	@Override
	public void onURLCreateFailure() {
		dismissDialog();
		app().toast(R.string.message_something_wrong).show();
	}

	@Override
	public void onAccessTokenReceiveFailure() {
		dismissDialog();
		app().toast(R.string.message_something_wrong).show();
	}

	@Override
	public void onAccessTokenReceived(AccessToken token) {
		app().setTwitterAccessToken(token);
		Activity activity = getActivity();
		activity.setResult(ResultCode.OK);
		activity.finish();
	}
}
