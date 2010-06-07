package jp.senchan.android.wasatter2.item;

import java.io.Serializable;
import java.util.ArrayList;

import android.text.SpannableStringBuilder;

public class Item implements Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * ユーザーID（発言者）
	 */
	public String screenName;
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

	/**
	 * HTML
	 */
	public String html;
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
		return new SpannableStringBuilder(name).append("(").append(screenName).append(
				")").toString();
	}
	public boolean favorited;

	
	public boolean equals(Object o) {
		// TODO 自動生成されたメソッド・スタブ
		try{
			Item obj = (Item) o;
			//サービス名とridが一致してたら同じモノと考える
			return this.service.equals(obj.service) && this.rid.equals(obj.rid);
		}catch(Exception e){

		}
		return false;
	}

}
