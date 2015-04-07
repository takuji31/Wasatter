package jp.senchan.android.wasatter.loader;

import jp.senchan.android.wasatter.client.WasatterApiClient;
import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

public class UpdateStatusLoader extends AsyncTaskLoader<Boolean> {

	private WasatterApiClient mClient;
	private String mBody;
	private String mImagePath;
	private String mReplyId;
	
	public UpdateStatusLoader(Context context,WasatterApiClient client, String body, String imagePath, String replyId) {
		super(context);
		mClient = client;
		mBody = body;
		mImagePath = imagePath;
		mReplyId = replyId;
	}

	@Override
	public Boolean loadInBackground() {
		return mClient.updateStatus(mBody, mImagePath, mReplyId);
	}
	
	@Override
	protected void onStartLoading() {
		forceLoad();
	}
	
	@Override
	protected void onStopLoading() {
		super.onStopLoading();
		cancelLoad();
	}

}
