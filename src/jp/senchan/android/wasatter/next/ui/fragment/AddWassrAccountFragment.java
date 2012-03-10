package jp.senchan.android.wasatter.next.ui.fragment;

import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.ResultCode;
import jp.senchan.android.wasatter.WasatterFragment;
import jp.senchan.android.wasatter.next.listener.OnAuthenticationResultListener;
import jp.senchan.android.wasatter.next.model.Account;
import jp.senchan.android.wasatter.next.task.AuthenticateWassrTask;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class AddWassrAccountFragment extends WasatterFragment implements OnAuthenticationResultListener {
	
	private AuthenticateWassrTask currentTask;
	
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
			toast(R.string.message_input_login_id_and_password).show();
			return;
		}
		buttonLogin.setClickable(false);
		editTextLoginId.setEnabled(false);
		editTextPassword.setEnabled(false);
		currentTask = new AuthenticateWassrTask(this);
		currentTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, id, password);
	}

	@Override
	public void onAuthenticationResult(boolean result) {
		if(result) {
			//TODO 非同期
			String id = editTextLoginId.getText().toString();
			String password = editTextPassword.getText().toString();
			new Account(app()).createWassrAccount(id, password);
			getActivity().setResult(ResultCode.OK);
			getActivity().finish();	
		} else {
			toast(R.string.message_something_wrong).show();
			buttonLogin.setClickable(true);
			editTextLoginId.setEnabled(true);
			editTextPassword.setEnabled(true);
		}
	}
}
