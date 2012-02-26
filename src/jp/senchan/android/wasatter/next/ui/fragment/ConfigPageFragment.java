package jp.senchan.android.wasatter.next.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import jp.senchan.android.wasatter3.R;
import jp.senchan.lib.ui.BaseFragment;

public class ConfigPageFragment extends BaseFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.config_page_type, null);

		return v;
	}
}
