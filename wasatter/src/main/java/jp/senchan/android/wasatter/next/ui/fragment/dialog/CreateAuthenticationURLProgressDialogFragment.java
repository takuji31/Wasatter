package jp.senchan.android.wasatter.next.ui.fragment.dialog;

import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.WasatterDialogFragment;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

public class CreateAuthenticationURLProgressDialogFragment extends
		WasatterDialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		ProgressDialog dialog = new ProgressDialog(getActivity());
		dialog.setTitle(R.string.dialog_title_has_been_prepared);
		dialog.setMessage(getString(R.string.message_please_wait));
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setIndeterminate(true);
		return dialog;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		setCancelable(false);
	}
	
	@Override
	public void onCancel(DialogInterface dialog) {
		// TODO Auto-generated method stub
		super.onCancel(dialog);
	}
}
