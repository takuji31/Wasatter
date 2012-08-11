package jp.senchan.android.wasatter.model.api;

import java.util.ArrayList;

public abstract class TimelinePager extends ArrayList<WasatterStatus> {

	private static final long serialVersionUID = 1L;
	
	public abstract void loadNext();
}
