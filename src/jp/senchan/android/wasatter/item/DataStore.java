package jp.senchan.android.wasatter.item;

import java.io.IOException;
import java.util.Properties;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.htree.HTree;
import jp.senchan.android.wasatter.Wasatter;

public class DataStore {
	/**
	 * Wassrのヒトコト、rid=>Item
	 */
	public static HTree statusWassr;
	public static final String KEY_STATUS_WASSR = "status_wassr";
	/**
	 * Twitterのつぶやき、rid=>Item
	 */
	public static HTree statusTwitter;
	public static final String KEY_STATUS_TWITTER = "status_twitter";
	/**
	 * アイコンキャッシュ、url=>ファイル名
	 */
	public static HTree icon;
	public static final String KEY_ICON = "icon";
	/**
	 * アイコンキャッシュ、url=>ファイル名
	 */
	public static HTree icon_loaded;
	public static final String KEY_ICON_LOADED = "icon_loaded";
	/**
	 * ユーザー情報(Wassr)、screenName=>ユーザー情報
	 */
	public static HTree userWassr;
	public static final String KEY_USER_WASSR = "user_wassr";
	/**
	 * ユーザー情報(Twitter)、screenName=>ユーザー情報
	 */
	public static HTree userTwitter;
	public static final String KEY_USER_TWITTER = "user_twitter";

	/**
	 * レコードマネージャー
	 */
	public static RecordManager recman;

	static {
		Properties pt = new Properties();
		try {
			recman = RecordManagerFactory.createRecordManager(Wasatter
					.getDataPath("data/datastore"), pt);
			//各レコードをロードする
			statusWassr = load(KEY_STATUS_WASSR);
			statusTwitter = load(KEY_STATUS_TWITTER);
			userWassr = load(KEY_USER_WASSR);
			userTwitter = load(KEY_USER_TWITTER);
			icon = load(KEY_ICON);
			icon_loaded = load(KEY_ICON_LOADED);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static HTree load(String key) throws IOException{
		HTree obj;
		long recid = recman.getNamedObject(key);
		if (recid != 0) {
			obj = HTree.load(recman, recid);
		} else {
			obj = HTree.createInstance(recman);
			recman.setNamedObject(key , obj.getRecid());
		}
		return obj;
	}

}
