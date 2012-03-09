package jp.senchan.android.wasatter.next.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.WasatterFragment;

public class AddWassrAccountFragment extends WasatterFragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.login_wassr, null);
		
		Button loginButton = (Button) v.findViewById(R.id.button_login);
		loginButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				createAccount();
			}
		});
		return v;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		activity.setTitle(R.string.service_name_wassr);
	}
	
	public void createAccount() {
		
		getActivity().finish();
	}
}
