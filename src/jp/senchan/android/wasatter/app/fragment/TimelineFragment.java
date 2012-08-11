package jp.senchan.android.wasatter.app.fragment;

import java.util.ArrayList;
import java.util.Collections;

import twitter4j.AsyncTwitter;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.androidquery.AQuery;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import jp.senchan.android.wasatter.BundleKey;
import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.Wasatter;
import jp.senchan.android.wasatter.WasatterActivity;
import jp.senchan.android.wasatter.WasatterListFragment;
import jp.senchan.android.wasatter.adapter.TimelineAdapter;
import jp.senchan.android.wasatter.app.ConfigActivity;
import jp.senchan.android.wasatter.app.PostActivity;
import jp.senchan.android.wasatter.client.TwitterAsyncClient;
import jp.senchan.android.wasatter.client.WassrClient;
import jp.senchan.android.wasatter.loader.TimelineLoader;
import jp.senchan.android.wasatter.model.api.APICallback;
import jp.senchan.android.wasatter.model.api.TimelinePager;
import jp.senchan.android.wasatter.model.api.WasatterStatus;
import jp.senchan.android.wasatter.model.api.impl.twitter.TwitterTimelinePager;
import jp.senchan.android.wasatter.model.api.impl.wassr.WassrTimelinePager;
import jp.senchan.android.wasatter.utils.WasatterStatusComparator;

public class TimelineFragment extends WasatterListFragment implements OnScrollListener,LoaderCallbacks<TimelinePager> {
	
	private static final String sDialogTag = "VersionInfoDialogFragment";
	private static final String sStateKeyTimeline = "timeline";
	private static final String sStateKeyWassrTimelinePager = "WassrTimelinePager";
	private static final String sStateKeyTwitterTimelinePager = "TwitterTimelinePager";
	
	public static final int MODE_TIMELINE       = 1;
	public static final int MODE_MENSION        = 2;
	public static final int MODE_MYPOST         = 3;
	public static final int MODE_DM             = 10;
	public static final int MODE_ODAI           = 100;
	public static final int MODE_CHANNEL_LIST   = 1000;
	public static final int MODE_CHANNEL_STATUS = 1001;
	
	public static final int SERVICE_TWITTER = 1;
	public static final int SERVICE_WASSR = 2;
	
	private int mMode;
	private WassrTimelinePager mWassrPager;
	private TwitterTimelinePager mTwitterPager;
	private boolean isFirstLoad = true;
	private TimelineAdapter mAdapter;
	private ArrayList<WasatterStatus> mTimeline = new ArrayList<WasatterStatus>();
	private int mLoadingCount = 0;

	
	@SuppressWarnings("unchecked")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		
		Bundle args = getArguments();
		if (args != null) {
			mMode = args.getInt(BundleKey.MODE, MODE_TIMELINE);
		} else {
			mMode = MODE_TIMELINE;
		}
		
		if (savedInstanceState != null) {
			mTwitterPager = (TwitterTimelinePager) savedInstanceState.getSerializable(sStateKeyTwitterTimelinePager);
			mWassrPager = (WassrTimelinePager) savedInstanceState.getSerializable(sStateKeyWassrTimelinePager);
			mTimeline = (ArrayList<WasatterStatus>) savedInstanceState.getSerializable(sStateKeyTimeline);
			mAdapter = new TimelineAdapter(getActivity(), mTimeline);
			setListAdapter(mAdapter);
		} else {
			loadTimeline();
		}

		getListView().setOnScrollListener(this);
	}
	
	private void combineTimeline() {
		mTimeline.clear();
		mTimeline.addAll(mWassrPager);
		mTimeline.addAll(mTwitterPager);
		Collections.sort(mTimeline, new WasatterStatusComparator());
		if (mAdapter == null) {
			mAdapter = new TimelineAdapter(getActivity(), mTimeline);
			setListAdapter(mAdapter);
		} else {
			setListShown(true);
			mAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(sStateKeyTwitterTimelinePager, mTwitterPager);
		outState.putSerializable(sStateKeyWassrTimelinePager, mWassrPager);
		outState.putSerializable(sStateKeyTimeline, mTimeline);
	}
	
	@Override
	public void onDestroy() {
		destroyLoader();
		super.onDestroy();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.timeline, menu);
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		MenuItem item = menu.findItem(R.id.menu_reload);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.menu_post) {
			Intent intent = new Intent(getActivity(), PostActivity.class);
			startActivity(intent);
			return true;
		}
		if (id == R.id.menu_reload) {
			reloadTimeline();
			return true;
		}
		if (id == R.id.menu_config) {
			Intent intent = new Intent(getActivity(), ConfigActivity.class);
			startActivity(intent);
			return true;
		}
		if (id == R.id.menu_version_info) {
			VersionInfoDialogFragment fragment = (VersionInfoDialogFragment) Fragment.instantiate(getActivity(), VersionInfoDialogFragment.class.getName());
			fragment.show(getFragmentManager(), sDialogTag);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (mLoadingCount == 0 &&firstVisibleItem + visibleItemCount == totalItemCount) {
			//TODO 2度以上走らないようにする
			loadTimeline();
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {}
	
	private int getWassrLoaderId() {
		return mMode * SERVICE_WASSR;
	}
	
	private int getTwitterLoaderId() {
		return mMode * SERVICE_TWITTER;
	}
	
	private void destroyLoader() {
		getLoaderManager().destroyLoader(getWassrLoaderId());
		getLoaderManager().destroyLoader(getTwitterLoaderId());
	}
	
	private void reloadTimeline() {
		destroyLoader();
		isFirstLoad = false;
		setListAdapter(null);
		setListShown(false);
		mAdapter = null;
		mWassrPager = null;
		mTwitterPager = null;
		mLoadingCount = 0;
		loadTimeline();
	}
	
	private void loadTimeline() {
		final Wasatter app = app();
		if (app.canLoadWassrTimeline()) {
			int id = getWassrLoaderId();
			Bundle args = new Bundle();
			args.putInt(BundleKey.SERVICE, SERVICE_WASSR);
			if (isFirstLoad) {
				getLoaderManager().initLoader(id, args, this);
			} else {
				getLoaderManager().restartLoader(id, args, this);
			}
		}
		if (app.canLoadTwitterTimeline()) {
			int id = getTwitterLoaderId();
			Bundle args = new Bundle();
			args.putInt(BundleKey.SERVICE, SERVICE_TWITTER);
			if (isFirstLoad) {
				getLoaderManager().initLoader(id, args, this);
			} else {
				getLoaderManager().restartLoader(id, args, this);
			}
		}
		isFirstLoad = false;
	}

	@Override
	public Loader<TimelinePager> onCreateLoader(int id, Bundle args) {
		int service = args.getInt(BundleKey.SERVICE);
		Wasatter app = app();
		TimelinePager pager = null;
		if (service == SERVICE_WASSR) {
			if (mWassrPager == null) {
				mWassrPager = new WassrTimelinePager(app, mMode);
			}
			pager = mWassrPager;
		} else if (service == SERVICE_TWITTER) {
			if (mTwitterPager == null) {
				mTwitterPager = new TwitterTimelinePager(app, mMode);
			}
			pager = mTwitterPager;
		} else {
			app.showErrorToast();
			return null;
		}
		mLoadingCount++;
		return new TimelineLoader(app, pager);
	}

	@Override
	public void onLoadFinished(Loader<TimelinePager> loader, TimelinePager data) {
		mLoadingCount--;
		if (data.getLastResultCode() != TimelinePager.SUCCESS) {
			app().showErrorToast();
		} else {
			combineTimeline();
		}
	}

	@Override
	public void onLoaderReset(Loader<TimelinePager> loader) {
		// TODO Auto-generated method stub
		
	}

}
