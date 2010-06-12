package jp.senchan.android.wasatter2.item;

import java.io.IOException;
import java.util.Properties;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.htree.HTree;
import jp.senchan.android.wasatter2.Wasatter;

public class ItemStore {
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
			long recid = recman.getNamedObject(KEY_STATUS_WASSR);
			if (recid != 0) {
				statusWassr = HTree.load(recman, recid);
			} else {
				statusWassr = HTree.createInstance(recman);
				recman.setNamedObject(KEY_STATUS_WASSR, statusWassr.getRecid());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
