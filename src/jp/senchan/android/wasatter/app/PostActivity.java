package jp.senchan.android.wasatter.app;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import jp.senchan.android.wasatter.WasatterActivity;
import jp.senchan.android.wasatter.app.fragment.ImagePickerFragment;
import jp.senchan.android.wasatter.app.fragment.PostFragment;
import jp.senchan.android.wasatter.app.fragment.ImagePickerDialogFragment.OnImagePickerListener;
import jp.senchan.android.wasatter.app.fragment.ImagePickerFragment.OnImagePickedLisntener;

public class PostActivity extends WasatterActivity implements OnImagePickedLisntener, OnImagePickerListener {
	
	private static final String sPostFragmentTag = "PostFragment";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager fm = getSupportFragmentManager();
        
        Fragment f = fm.findFragmentById(android.R.id.content);
        if (f == null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(android.R.id.content, Fragment.instantiate(this, PostFragment.class.getName()), sPostFragmentTag);
            ft.commit();
		}
    }
    
    private PostFragment getPostFragment() {
    	return (PostFragment) getSupportFragmentManager().findFragmentByTag(sPostFragmentTag);
    }

    private ImagePickerFragment getImagePickerFragment() {
    	return (ImagePickerFragment) getSupportFragmentManager().findFragmentByTag(PostFragment.TAG_PICKER);
    }

	@Override
	public void onImagePicked(Bitmap image) {
		PostFragment f = getPostFragment();
		if (f != null) {
			f.setPostImage(image);
		}
	}

	@Override
	public void clearPickedImage() {
		onImagePicked(null);
	}

	@Override
	public void onImageSourceSelected(int which) {
		ImagePickerFragment f = getImagePickerFragment();
		if (f != null) {
			f.startPickImage(which);
		}
	}
    
}
