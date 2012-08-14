package jp.senchan.android.wasatter.app.fragment;

import java.io.File;
import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.Wasatter;
import jp.senchan.android.wasatter.WasatterFragment;
import jp.senchan.android.wasatter.client.TwitterClient;
import jp.senchan.android.wasatter.client.WasatterApiClient;
import jp.senchan.android.wasatter.client.WassrClient;
import jp.senchan.android.wasatter.loader.ItemPostLoader;
import jp.senchan.android.wasatter.utils.ServiceCodeUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.androidquery.AQuery;

public class PostFragment extends WasatterFragment implements LoaderCallbacks<Boolean> {
	
	private static final String sStateKeyImage = "post_image_path";
	private static final String sStateKeyPostingWassr = "posting_wassr";
	private static final String sStateKeyPostingTwitter = "posting_twitter";
	private static final String sStateKeySucceedWassr = "succeed_wassr";
	private static final String sStateKeySucceedTwitter = "succeed_twitter";
	private static final String sTagDialog = "UpdateStatusProgressDialogFragment";
	

	public static final String TAG_PICKER = "tag_picker";
	
	private boolean mPostingWassr = false;
	private boolean mPostingTwitter = false;
	private boolean mSucceedWassr = false;
	private boolean mSucceedTwitter = false;
	private String mImageFilePath;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
        	setPostImage(savedInstanceState.getString(sStateKeyImage));
        	mPostingWassr = savedInstanceState.getBoolean(sStateKeyPostingWassr);
        	mPostingTwitter = savedInstanceState.getBoolean(sStateKeyPostingTwitter);
        	mSucceedWassr = savedInstanceState.getBoolean(sStateKeySucceedWassr);
        	mSucceedTwitter = savedInstanceState.getBoolean(sStateKeySucceedTwitter);
        	checkPostState();
		}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.post, null);
        
        return v;
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	outState.putString(sStateKeyImage, mImageFilePath);
    	outState.putBoolean(sStateKeyPostingWassr, mPostingWassr);
    	outState.putBoolean(sStateKeyPostingTwitter, mPostingTwitter);
    	outState.putBoolean(sStateKeySucceedWassr, mSucceedWassr);
    	outState.putBoolean(sStateKeySucceedTwitter, mSucceedTwitter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.post, menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
		case R.id.menu_image:
			ImagePickerFragment df = (ImagePickerFragment) Fragment.instantiate(getActivity(), ImagePickerFragment.class.getName(), null);
			getFragmentManager().beginTransaction().add(df, TAG_PICKER).commit();
			break;
		case R.id.menu_post:
			startPost();
			break;
		default:
			break;
		}
    	return true;
    }

	private void startPost() {
		//TODO 適切なマルチポスト
		mPostingWassr = true;
		mPostingTwitter = true;
		mSucceedTwitter = true;
		mSucceedWassr = true;
		checkPostState();
	}

	private void checkPostState() {
		LoaderManager lm = getLoaderManager();
		if (mPostingWassr) {
			lm.initLoader(ServiceCodeUtil.resIdToId(getActivity(), R.integer.service_id_wassr), null, this);
		}
		if (mPostingTwitter) {
			lm.initLoader(ServiceCodeUtil.resIdToId(getActivity(), R.integer.service_id_twitter), null, this);
		}
		UpdateStatusProgressDialogFragment f = getProgressDialogFragment();
		if (f == null && (mPostingWassr || mPostingTwitter)) {
			f = (UpdateStatusProgressDialogFragment) Fragment.instantiate(getActivity(), UpdateStatusProgressDialogFragment.class.getName());
			f.show(getFragmentManager(), sTagDialog);
		}
		
	}

	public void setPostImage(String path) {
		mImageFilePath = path;
		AQuery aq = new AQuery(activity(), getView());
		if (!TextUtils.isEmpty(path)) {
			aq.id(R.id.imageViewPreview).image(new File(path), 480);
		} else {
			aq.id(R.id.imageViewPreview).image((Drawable)null);
		}
	}
	
	private UpdateStatusProgressDialogFragment getProgressDialogFragment() {
		FragmentManager fm = getFragmentManager();
		return (UpdateStatusProgressDialogFragment) fm.findFragmentByTag(sTagDialog);
	}

	@Override
	public Loader<Boolean> onCreateLoader(int id, Bundle args) {
		WasatterApiClient client = null;
		Wasatter app = app();

		AQuery aq = new AQuery(getActivity(), getView());
		String body = aq.id(R.id.editTextBody).getEditable().toString();
		if (ServiceCodeUtil.equals(getActivity(), id, R.integer.service_id_wassr)) {
			client = new WassrClient(app);
		} else if (ServiceCodeUtil.equals(getActivity(), id, R.integer.service_id_twitter)) {
			client = new TwitterClient(app);
		}
		return new ItemPostLoader(getActivity(), client, body, mImageFilePath, "");
	}

	@Override
	public void onLoadFinished(Loader<Boolean> loader, Boolean data) {
		int id = loader.getId();
		if (ServiceCodeUtil.equals(getActivity(), id, R.integer.service_id_wassr)) {
			mPostingWassr = false;
			mSucceedWassr = data;
		}
		if (ServiceCodeUtil.equals(getActivity(), id, R.integer.service_id_twitter)) {
			mPostingTwitter = false;
			mSucceedTwitter = data;
		}
		final UpdateStatusProgressDialogFragment f = getProgressDialogFragment();
		if (!mPostingWassr && !mPostingTwitter) {
			if (!mSucceedWassr || !mSucceedTwitter) {
				//TODO 失敗したほうを再投稿できるようにすること
				app().showErrorToast();
				//onLoadFinishedでFragmentの処理をするならAllowingStateLossしないといけない
				f.dismissAllowingStateLoss();
			} else {
				getActivity().finish();
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<Boolean> loader) {}
	
}
