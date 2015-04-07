package jp.senchan.android.wasatter.app.fragment;

import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.WasatterDialogFragment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

/*
 * 表示するダイアログのFragment
 */
public class ImagePickerDialogFragment extends WasatterDialogFragment {
	private OnImagePickerListener mListener;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnImagePickerListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must be implements " + OnImagePickerListener.class.getName());
		}
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.dialog_title_select_image_source);
		builder.setItems(R.array.image_sources, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mListener.onImageSourceSelected(which);
			}
		});
		return builder.create();
	}
	
	public interface OnImagePickerListener {
		public void onImageSourceSelected(int which);
	}
}