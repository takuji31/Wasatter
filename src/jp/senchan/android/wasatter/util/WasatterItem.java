package jp.senchan.android.wasatter.util;

import java.io.Serializable;
import java.util.ArrayList;

import android.text.SpannableStringBuilder;

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
	public String id;
	/**
	 * ユーザー名（発言者）
	 */
	public String name;
	/**
	 * ヒトコト
	 */
	public String text;
	/**
	 * プロフィール画像のURL
	 */
	public String profileImageUrl;
	/**
	 * パーマリンク
	 */
	public String link;
	/**
	 * RID
	 */
	public String rid;

	public CharSequence html;
	/**
	 * サービスの種類
	 */
	public String service;
	public String replyUserNick;
	public String replyMessage;
	public long epoch;
	public ArrayList<String> favorite = new ArrayList<String>();
	public boolean channel;

	
	public String toString() {
		// TODO 自動生成されたメソッド・スタブ
		return new SpannableStringBuilder(name).append("(").append(id).append(
				")").toString();
	}
	public boolean favorited;
}
