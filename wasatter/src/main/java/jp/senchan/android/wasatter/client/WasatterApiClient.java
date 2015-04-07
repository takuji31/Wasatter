package jp.senchan.android.wasatter.client;

public interface WasatterApiClient {
	public boolean updateStatus(String body, String filePath, String replyId);
}
