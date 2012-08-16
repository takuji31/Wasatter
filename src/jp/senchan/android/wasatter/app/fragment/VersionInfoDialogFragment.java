package jp.senchan.android.wasatter.app.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import jp.senchan.android.wasatter2.R;
import jp.senchan.android.wasatter.WasatterDialogFragment;

public class VersionInfoDialogFragment extends WasatterDialogFragment {
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Activity activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.menu_title_version);
        SpannableStringBuilder sb = new SpannableStringBuilder(
                getString(R.string.app_name));
        sb.append(" ");
        try {
            sb.append(activity.getPackageManager().getPackageInfo(activity.getPackageName(),
                    PackageManager.GET_META_DATA).versionName);
        } catch (NameNotFoundException e) {
            // ありえない
            e.printStackTrace();
        }
        builder.setMessage(sb.toString());
        builder.setPositiveButton(android.R.string.ok, null);
        return builder.create();
	}
}
