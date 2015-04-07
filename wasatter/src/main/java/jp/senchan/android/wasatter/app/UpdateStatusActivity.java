package jp.senchan.android.wasatter.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import jp.senchan.android.wasatter.WasatterActivity;
import jp.senchan.android.wasatter.app.fragment.ImagePickerFragment;
import jp.senchan.android.wasatter.app.fragment.ServiceCheckDialogFragment.OnServiceCheckedListener;
import jp.senchan.android.wasatter.app.fragment.UpdateStatusFragment;
import jp.senchan.android.wasatter.app.fragment.ImagePickerDialogFragment.OnImagePickerListener;
import jp.senchan.android.wasatter.app.fragment.ImagePickerFragment.OnImagePickedLisntener;

public class UpdateStatusActivity extends WasatterActivity implements OnImagePickedLisntener, OnImagePickerListener, OnServiceCheckedListener {
	
	private static final String sPostFragmentTag = "PostFragment";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager fm = getSupportFragmentManager();
        
        Fragment f = fm.findFragmentById(android.R.id.content);
        if (f == null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(android.R.id.content, Fragment.instantiate(this, UpdateStatusFragment.class.getName()), sPostFragmentTag);
            ft.commit();
		}
    }
    
    private UpdateStatusFragment getPostFragment() {
    	return (UpdateStatusFragment) getSupportFragmentManager().findFragmentByTag(sPostFragmentTag);
    }

    private ImagePickerFragment getImagePickerFragment() {
    	return (ImagePickerFragment) getSupportFragmentManager().findFragmentByTag(UpdateStatusFragment.TAG_PICKER);
    }

	@Override
	public void onImagePicked(String path) {
		UpdateStatusFragment postFragment = getPostFragment();
		if (postFragment != null) {
			postFragment.setPostImage(path);
		}
		ImagePickerFragment imagePickerFragment = getImagePickerFragment();
		if (imagePickerFragment != null) {
			//XXX onActivityResultで呼ばれる(onResumeより先に呼ばれる)からcheckStateLossはとばす
			getSupportFragmentManager().beginTransaction().remove(imagePickerFragment).commitAllowingStateLoss();
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

	@Override
	public void onServiceChecked(boolean[] selected) {
		UpdateStatusFragment f = getPostFragment();
		if (f != null) {
			//TODO 投稿サービスの選択
		}
	}
    
}
