package jp.senchan.android.wasatter;

import jp.senchan.android.wasatter3.R;
import jp.senchan.lib.ui.BaseFragment;

public class WasatterFragment extends BaseFragment {
	
	public WasatterActivity activity() {
		return (WasatterActivity) getActivity();
	}
	
	public Wasatter app() {
		return activity().app();
	}

}