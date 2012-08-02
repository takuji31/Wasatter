package jp.senchan.android.wasatter.next.ui.fragment.imagepicker;

import java.io.InputStream;

import jp.senchan.android.wasatter.WasatterFragment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.util.Log;
import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.WasatterDialogFragment;
import jp.senchan.android.wasatter.next.IntentCode;


public class ImagePickerFragment extends WasatterFragment {
	public static final String TAG_DIALOG = "tag_dialog";
	private OnImagePickedLisntener mListener;
	public Uri mImageUri;
	public ImagePickerFragment(OnImagePickedLisntener listener) {
		mListener = listener;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ImagePickerDialogFragment f = new ImagePickerDialogFragment(this);
		f.show(getFragmentManager().beginTransaction(), TAG_DIALOG);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Bitmap image = null;
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		try {
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
			image = BitmapFactory.decodeStream(in);
			in.close();
		} catch (OutOfMemoryError e) {
			Log.d("ImagePicker", "Memory tarinai yo!");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		mListener.onImagePicked(image);
	}
	
	
	public interface OnImagePickedLisntener {
		public void onImagePicked(Bitmap image);
	}
	/*
	 * 表示するダイアログのFragment
	 */
	private class ImagePickerDialogFragment extends WasatterDialogFragment {
		private ImagePickerFragment mFragment;
		
		public ImagePickerDialogFragment(ImagePickerFragment fragment) {
			mFragment = fragment;
		}
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.dialog_title_select_image_source);
			builder.setItems(R.array.image_sources, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case 0:
						pickImageFromGallery();
						break;

					case 1:
						pickImageFromCamera();
						break;
					}
					setShowsDialog(false);
				}
			});
			return builder.create();
		}
		
		public void pickImageFromGallery() {
			Intent intentGallery = new Intent();
			intentGallery.setType("image/*");
			intentGallery.setAction(Intent.ACTION_GET_CONTENT);
			mFragment.startActivityForResult(intentGallery, IntentCode.REQUEST_GALLERY);
		}
		public void pickImageFromCamera() {
		    String filename = System.currentTimeMillis() + ".jpg";
		    
		    ContentValues values = new ContentValues();
		    values.put(MediaStore.Images.Media.TITLE, filename);
		    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
		    mImageUri = activity().getContentResolver().insert(
		            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
		    
			Intent intentGallery = new Intent();
			intentGallery.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
		    intentGallery.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
			mFragment.startActivityForResult(intentGallery, IntentCode.REQUEST_CAMERA);
		}
	}
}
