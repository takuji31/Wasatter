package jp.senchan.android.wasatter.app.fragment;

import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.WasatterDialogFragment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.os.Bundle;

public class ServiceCheckDialogFragment extends WasatterDialogFragment {

	private OnPostServiceSelectedListener mListener;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.dialog_title_select_service);
		String items[] = getActivity().getResources().getStringArray(R.array.service_names);
		int length = items.length;
		final boolean[] selected = new boolean[length];
		builder.setMultiChoiceItems(R.array.service_names, selected, new OnMultiChoiceClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				selected[which] = isChecked;
			}
		});
		builder.setPositiveButton(android.R.string.ok, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//TODO 選択処理
				mListener.onPostServiceSelected(selected);
			}
		});
		return builder.create();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnPostServiceSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must be implements " + OnPostServiceSelectedListener.class.getName());
		}
	}
	
	
	
	public interface OnPostServiceSelectedListener {
		public void onPostServiceSelected(boolean[] selected);
	}
}
