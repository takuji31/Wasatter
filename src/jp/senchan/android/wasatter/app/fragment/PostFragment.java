package jp.senchan.android.wasatter.app.fragment;

import java.io.File;

import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.WasatterFragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.androidquery.AQuery;

public class PostFragment extends WasatterFragment {
	
	private static final String sStateKeyImage = "image";
	
	public static final String TAG_PICKER = "tag_picker";
	
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
		default:
			break;
		}
    	return true;
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
	
}
