package jp.senchan.android.wasatter.next.ui.fragment;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.WasatterFragment;
import jp.senchan.android.wasatter.auth.params.OAuthTwitter;
import jp.senchan.android.wasatter.next.client.NewTwitterOAuthClient;
import jp.senchan.android.wasatter.next.listener.OnCallbackReceivedListener;
import jp.senchan.android.wasatter.next.listener.OnURLCreatedListener;
import jp.senchan.android.wasatter.next.task.GetTwitterOAuthRequestURLTask;
import jp.senchan.android.wasatter.next.ui.fragment.dialog.CreateAuthenticationURLProgressDialogFragment;

public class AddTwitterAccountFragment extends WasatterFragment implements OnURLCreatedListener, OnCallbackReceivedListener {
	
	NewTwitterOAuthClient mClient;
	private WebView mWebView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mWebView = new WebView(getActivity());
		mWebView.getSettings().setJavaScriptEnabled(true);
		
		mClient = new NewTwitterOAuthClient();
		
		WebViewClient client = new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Uri uri = Uri.parse(url);
				//TODO ホスト名外出し
				if(TextUtils.equals(uri.getAuthority(), OAuthTwitter.CALLBACK_HOST)) {
					onCallbackReceived(uri);
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
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		new CreateAuthenticationURLProgressDialogFragment().show(ft, "dialog");
		new GetTwitterOAuthRequestURLTask(this, mClient).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	public void dismissDialog () {
		FragmentManager fm = getFragmentManager();
		CreateAuthenticationURLProgressDialogFragment f = (CreateAuthenticationURLProgressDialogFragment) fm.findFragmentByTag("dialog");
		f.dismiss();
	}
	
	@Override
	public void onURLCreated (String url) {
		dismissDialog();
		mWebView.loadUrl(url);
	}
	
	public void saveToken() {
		//TODO 認証データーの保存
	}

	@Override
	public void onURLCreateFailure() {
		dismissDialog();
		toast(R.string.message_something_wrong).show();
	}

	@Override
	public void onCallbackReceived(Uri uri) {
	}
}
