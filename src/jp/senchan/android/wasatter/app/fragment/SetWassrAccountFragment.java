package jp.senchan.android.wasatter.app.fragment;

import java.util.ArrayList;

import com.androidquery.AQuery;

import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.ResultCode;
import jp.senchan.android.wasatter.Wasatter;
import jp.senchan.android.wasatter.WasatterFragment;
import jp.senchan.android.wasatter.client.WassrClient;
import jp.senchan.android.wasatter.model.api.WasatterStatus;
import jp.senchan.android.wasatter.next.listener.APICallback;
import jp.senchan.android.wasatter.next.listener.OnAuthenticationResultListener;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class SetWassrAccountFragment extends WasatterFragment implements OnAuthenticationResultListener {


	private EditText editTextLoginId;
	private EditText editTextPassword;
	private Button buttonLogin;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.login_wassr, null);

		editTextLoginId = (EditText) v.findViewById(R.id.edit_text_login_id);
		editTextPassword = (EditText) v.findViewById(R.id.edit_text_password);
		buttonLogin = (Button) v.findViewById(R.id.button_login);
		buttonLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				saveAccount();
			}
		});
		return v;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		activity.setTitle(R.string.service_name_wassr);
	}

	public void saveAccount() {
		String id = editTextLoginId.getText().toString();
		String password = editTextPassword.getText().toString();
		if(TextUtils.isEmpty(id) || TextUtils.isEmpty(password)) {
			app().toast(R.string.message_input_login_id_and_password).show();
			return;
		}
		buttonLogin.setClickable(false);
		editTextLoginId.setEnabled(false);
		editTextPassword.setEnabled(false);
		new WassrClient(new AQuery(getActivity()), id, password).friendTimeline(1, new APICallback<ArrayList<WasatterStatus>>() {
			
			@Override
			public void callback(String url, ArrayList<WasatterStatus> result,
					int status) {
					onAuthenticationResult(status == 200);
			}
		});
	}

	@Override
	public void onAuthenticationResult(boolean result) {
		if(result) {
			String id = editTextLoginId.getText().toString();
			String password = editTextPassword.getText().toString();
			Wasatter app = app();
			app.setWassrId(id);
			app.setWassrPass(password);
			getActivity().setResult(ResultCode.OK);
			getActivity().finish();
		} else {
			app().toast(R.string.message_something_wrong).show();
			buttonLogin.setClickable(true);
			editTextLoginId.setEnabled(true);
			editTextPassword.setEnabled(true);
		}
	}
}
