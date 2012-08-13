package jp.senchan.android.wasatter.app.fragment;

import java.io.InputStream;

import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.WasatterFragment;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import jp.senchan.android.wasatter.next.IntentCode;
import jp.senchan.android.wasatter.utils.UriResolver;


public class ImagePickerFragment extends WasatterFragment {
	private static final String sTagDialog = "ImagePickerDialogFragment";
	
	private OnImagePickedLisntener mListener;
	private Uri mImageUri;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		FragmentManager fm = getFragmentManager();
		ImagePickerDialogFragment fragment = (ImagePickerDialogFragment) fm.findFragmentByTag(sTagDialog);
		if (fragment == null) {
			fragment = (ImagePickerDialogFragment) Fragment.instantiate(getActivity(), ImagePickerDialogFragment.class.getName(), null);
			fragment.show(getFragmentManager(), sTagDialog);
		}
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnImagePickedLisntener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must be implements " + OnImagePickedLisntener.class.getName());
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		String path = null;
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		try {
			//メモリー対策で以前の画像を解放
			mListener.clearPickedImage();
			Uri uri = null;
			if(data != null) {
				//EXTRA_OUTPUTを使うと戻り値のIntentはnullになるくさい
				uri = data.getData();
			}
			if (requestCode == IntentCode.REQUEST_CAMERA) {
				if (uri == null) {
					//getDataではURIが取れない機種がある
					uri = mImageUri;
				}
			}
			InputStream in = activity().getContentResolver().openInputStream(uri);
			if (in.available() == 0 && requestCode == IntentCode.REQUEST_CAMERA) {
				//EXTRA_OUTPUTでまったく違うURIに書き出す機種もある、その場合は最新のものを読み込む
			    Cursor c = MediaStore.Images.Media.query(activity().getContentResolver(),
			    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
			      null,null,
			      MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");

			    c.moveToFirst();
			    String id =c.getString(c.getColumnIndexOrThrow(BaseColumns._ID));
			    uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
			    in = activity().getContentResolver().openInputStream(uri);
			}
			in.close();
			path = UriResolver.getPath(getActivity(), uri);
		} catch (OutOfMemoryError e) {
			Log.d("ImagePicker", "Memory tarinai yo!");
			e.printStackTrace();
			app().toast(R.string.message_out_of_memory_error).show();
		} catch (Exception e) {
			e.printStackTrace();
		}
		mListener.onImagePicked(path);
	}
	
	private void pickImageFromGallery() {
		Intent intentGallery = new Intent();
		intentGallery.setType("image/*");
		intentGallery.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intentGallery, IntentCode.REQUEST_GALLERY);
	}
	private void pickImageFromCamera() {
	    String filename = System.currentTimeMillis() + ".jpg";
	    
	    ContentValues values = new ContentValues();
	    values.put(MediaStore.Images.Media.TITLE, filename);
	    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
	    mImageUri = activity().getContentResolver().insert(
	            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
	    
		Intent intentCamera = new Intent();
		intentCamera.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
	    intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
		startActivityForResult(intentCamera, IntentCode.REQUEST_CAMERA);
	}
	
	public void startPickImage(int whitch) {
		switch (whitch) {
			case 0:
				pickImageFromGallery();
			break;
	
			case 1:
				pickImageFromCamera();
			break;
	
			default:
				throw new IllegalArgumentException("Wrong source!");
		}
	}
	
	
	public interface OnImagePickedLisntener {
		public void onImagePicked(String imagePath);
		public void clearPickedImage();
	}
}
