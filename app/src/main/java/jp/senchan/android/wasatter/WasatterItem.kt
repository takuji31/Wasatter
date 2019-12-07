package jp.senchan.android.wasatter

import android.text.SpannableStringBuilder
import twitter4j.Status
import java.io.Serializable
import java.util.*

/**
 * Wasatterつぶやき＆ヒトコトクラス
 *
 * @author Senka/Takuji
 */
class WasatterItem(
        /**
         * ユーザーID（発言者）
         */
        val id: String,
        /**
         * ユーザー名（発言者）
         */
        val name: String?,
        /**
         * ヒトコト
         */
        val text: String?,
        /**
         * プロフィール画像のURL
         */
        val profileImageUrl: String,
        /**
         * パーマリンク
         */
        val link: String,
        /**
         * RID
         */
        val rid: String,
        /**
         * サービスの種類
         */
        val service: String, val replyUserNick: String?, val replyMessage: String?, val epoch: Long, val favorite: ArrayList<String?>?, val favorited: Boolean) : Serializable {

    constructor(status: Status) : this(
            status.user.screenName,
            status.user.screenName,
            status.text,
            status.user.profileImageURLHttps,
            "https://twitter.com/" + status.user.screenName + "/status/" + status.id, status.id.toString(),
            "Twitter",
            status.inReplyToScreenName,
            "",
            status.createdAt.time / 1000L,
            ArrayList<String?>(),
            status.isFavorited
    ) {
    }

    override fun toString(): String { // TODO 自動生成されたメソッド・スタブ
        return SpannableStringBuilder(name).append("(").append(id).append(
                ")").toString()
    }

    companion object {
        /**
         *
         */
        private const val serialVersionUID = 1L
    }

}