package jp.senchan.android.wasatter;

import java.io.Serializable;
import java.util.ArrayList;

import android.text.SpannableStringBuilder;

import twitter4j.Status;

/**
 * Wasatterつぶやき＆ヒトコトクラス
 *
 * @author Senka/Takuji
 *
 */
public class WasatterItem implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * ユーザーID（発言者）
	 */
	public final String id;
	/**
	 * ユーザー名（発言者）
	 */
	public final String name;
	/**
	 * ヒトコト
	 */
	public final String text;
	/**
	 * プロフィール画像のURL
	 */
	public final String profileImageUrl;
	/**
	 * パーマリンク
	 */
	public final String link;
	/**
	 * RID
	 */
	public final String rid;
	/**
	 * サービスの種類
	 */
	public final String service;
	public final String replyUserNick;
	public final String replyMessage;
	public final long epoch;
	public final ArrayList<String> favorite;

	public final boolean favorited;

	public WasatterItem(String id, String name, String text, String profileImageUrl, String link, String rid, String service, String replyUserNick, String replyMessage, long epoch, ArrayList<String> favorite, boolean favorited) {
		this.id = id;
		this.name = name;
		this.text = text;
		this.profileImageUrl = profileImageUrl;
		this.link = link;
		this.rid = rid;
		this.service = service;
		this.replyUserNick = replyUserNick;
		this.replyMessage = replyMessage;
		this.epoch = epoch;
		this.favorite = favorite;
		this.favorited = favorited;
	}

	public WasatterItem(Status status) {
		this(
				status.getUser().getScreenName(),
				status.getUser().getScreenName(),
				status.getText(),
				status.getUser().getProfileImageURLHttps(),
				"https://twitter.com/" + status.getUser().getScreenName() + "/status/" + status.getId(),
				String.valueOf(status.getId()),
				"Twitter",
				status.getInReplyToScreenName(),
				"",
				status.getCreatedAt().getTime() / 1000L,
				new ArrayList<String>(),
				status.isFavorited()
		);
	}

	@Override
	public String toString() {
		// TODO 自動生成されたメソッド・スタブ
		return new SpannableStringBuilder(name).append("(").append(id).append(
				")").toString();
	}
}
