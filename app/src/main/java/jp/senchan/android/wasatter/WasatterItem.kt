package jp.senchan.android.wasatter

import twitter4j.Status
import java.io.Serializable
import java.util.*

/**
 * Wasatterつぶやき＆ヒトコトクラス
 *
 * @author Senka/Takuji
 */
data class WasatterItem(
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
            "https://twitter.com/${status.user.screenName}/status/${status.id}",
            status.id.toString(),
            "Twitter",
            status.inReplyToScreenName,
            "",
            status.createdAt.time / 1000L,
            ArrayList<String?>(),
            status.isFavorited
    )

    companion object {
        /**
         *
         */
        private const val serialVersionUID = 1L
    }

}