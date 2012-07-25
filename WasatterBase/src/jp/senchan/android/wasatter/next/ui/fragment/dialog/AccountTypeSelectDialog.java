package jp.senchan.android.wasatter.next.ui.fragment.dialog;

import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.WasatterDialogFragment;
import jp.senchan.android.wasatter.next.listener.OnServiceSelectedListener;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

public class AccountTypeSelectDialog extends WasatterDialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.dialog_title_select_service);
		builder.setItems(getActivity().getResources().getStringArray(R.array.service_names), new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				OnServiceSelectedListener listener = (OnServiceSelectedListener) getActivity();
				listener.onServiceSelected(which);
				dialog.dismiss();
			}
		});
		return builder.create();
	}
}
