package jp.senchan.android.wasatter.app.fragment;

import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.WasatterDialogFragment;
import jp.senchan.android.wasatter.next.listener.OnServiceSelectedListener;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.os.Bundle;

public class PostServiceCheckDialog extends WasatterDialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.dialog_title_select_service);
		String items[] = getActivity().getResources().getStringArray(R.array.service_names);
		int length = items.length;
		final boolean[] checked = new boolean[length];
		builder.setMultiChoiceItems(R.array.service_names, checked, new OnMultiChoiceClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				checked[which] = isChecked;
			}
		});
		builder.setPositiveButton(android.R.string.ok, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//TODO 選択処理
			}
		});
		return builder.create();
	}
}
