package jp.senchan.android.wasatter.model.api.impl.wassr;

import java.io.Serializable;

public class WassrReply implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String rid;
	public String loginId;
	public String screenName;
	public String message;
	public String replyStatusUrl;
}
