package jp.senchan.android.wasatter.app.fragment;

import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.WasatterFragment;
import jp.senchan.android.wasatter.app.fragment.ImagePickerFragment.OnImagePickedLisntener;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.androidquery.AQuery;

public class PostFragment extends WasatterFragment {
	public static final String TAG_PICKER = "tag_picker";
	
	private Bitmap mImageBitmap;
    @Override
    public void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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

	public void setPostImage(Bitmap image) {
		//Bitmapのデーターを解放してメモリーを空ける
		if (mImageBitmap != null) {
			mImageBitmap.recycle();
			mImageBitmap = null;
		}
		
		mImageBitmap = image;
		AQuery aq = new AQuery(activity(), getView());
		aq.id(R.id.imageViewPreview).image(image);
	}

}
