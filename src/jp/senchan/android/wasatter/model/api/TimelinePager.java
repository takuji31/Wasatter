package jp.senchan.android.wasatter.model.api;

import java.util.ArrayList;

public abstract class TimelinePager extends ArrayList<WasatterStatus> {

	private static final long serialVersionUID = 1L;
	
	public static final int SUCCESS = 200;
	public static final int FAILURE = -1;

	private int mLastResultCode = -1;

	protected int mMode;
	
	public abstract void loadNext();
	
	public void setLastResultCode(int result) {
		mLastResultCode = result;
	}

	public int getLastResultCode() {
		return mLastResultCode;
	}
	
}
