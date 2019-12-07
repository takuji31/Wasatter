package jp.senchan.android.wasatter.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.squareup.picasso.Picasso
import jp.senchan.android.wasatter.R
import jp.senchan.android.wasatter.ResultCode
import jp.senchan.android.wasatter.Wasatter
import jp.senchan.android.wasatter.WasatterItem
import jp.senchan.android.wasatter.task.TaskToggleFavorite

/**
 * 各つぶやき/ヒトコトの詳細
 *
 * @author Senka/Takuji
 */
class Detail : Activity() {
    protected var ws: WasatterItem? = null
    var favoriteButton: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.wasatter_detail)
        favoriteButton = findViewById<View>(R.id.button_favorite) as Button
        ws = Wasatter.main!!.selectedItem
        if (ws != null) {
            val wss = ws
            // サービス名/チャンネル名をセット
            val service_name = findViewById<View>(R.id.service_name) as TextView
            service_name.text = wss!!.service
            // ニックネームをセット
            val screen_name = findViewById<View>(R.id.screen_name) as TextView
            screen_name.text = wss.name
            // 本文をセット
            val status = findViewById<View>(R.id.status) as TextView
            status.text = wss.text
            // 画像をセット
            val icon = findViewById<View>(R.id.icon) as ImageView
            if (Setting.Companion.isLoadImage()) {
                Picasso.get()
                        .load(Uri.parse(wss.profileImageUrl))
                        .into(icon)
                icon.visibility = View.VISIBLE
            } else {
                icon.visibility = View.GONE
            }
            // 返信であるかどうか判定
            if (wss.replyUserNick != null && wss.replyUserNick != "null") {
                val reply_message = findViewById<View>(R.id.reply_text) as TextView
                val sb = SpannableStringBuilder("by ")
                sb.append(wss.replyUserNick)
                val reply_user_name = findViewById<View>(R.id.reply_user_name) as TextView
                reply_user_name.text = sb.toString()
                if (wss.replyMessage != null) {
                    val sb2 = SpannableStringBuilder(
                            "> ")
                    sb2.append(wss.replyMessage)
                    reply_message.text = sb2.toString()
                }
            } else {
                val layout_reply = findViewById<View>(R.id.layout_reply) as LinearLayout
                layout_reply.visibility = View.GONE
            }
            // OpenPermalinkボタンにイベント割り当て
            val button_open_link = findViewById<View>(R.id.button_open_link) as Button
            button_open_link.setOnClickListener {
                // TODO 自動生成されたメソッド・スタブ
                val permalink = ws!!.link
                val intent_parmalink = Intent(Intent.ACTION_VIEW,
                        Uri.parse(permalink))
                startActivity(intent_parmalink)
            }
            // Open URLボタンにイベント割り当て
            val button_open_url = findViewById<View>(R.id.button_open_url) as Button
            button_open_url.setOnClickListener {
                // TODO 自動生成されたメソッド・スタブ
                val text = ws!!.text
                val url = Wasatter.getUrl(text)
                if ("" != url) {
                    val intent_url = Intent(Intent.ACTION_VIEW, Uri
                            .parse(url))
                    startActivity(intent_url)
                } else {
                    val ad = AlertDialog.Builder(
                            this@Detail)
                    // ad.setTitle(R.string.notice_title_no_url);
                    ad.setMessage(R.string.notice_message_no_url)
                    ad.setPositiveButton("OK", null)
                    ad.show()
                }
            }
            // Favoriteボタンにイベント割り当て
            val button_favorite = findViewById<View>(R.id.button_favorite) as Button
            if (Wasatter.SERVICE_TWITTER == wss.service) {
                button_favorite.text = ADD_TWITTER
            } else if (wss.favorite != null
                    && wss.favorite.indexOf(Setting.Companion.getWassrId()) != -1) {
                button_favorite.text = DEL_WASSR
            } else {
                button_favorite.text = ADD_WASSR
            }
            button_favorite.setOnClickListener {
                TaskToggleFavorite(this@Detail)
                        .execute(ws)
            }
            // Replyボタンにイベント割り当て
            val button_reply = findViewById<View>(R.id.reply_button) as Button
            button_reply.setOnClickListener {
                // TODO 自動生成されたメソッド・スタブ
                val intent_reply = Intent(this@Detail,
                        Update::class.java)
                intent_reply.putExtra(Wasatter.REPLY,
                        ws)
                startActivity(intent_reply)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (isFinishing) {
            setResult(ResultCode.OK)
        }
    }

    companion object {
        var ADD_WASSR = "イイネ！する"
        var DEL_WASSR = "イイネ！を消す"
        var ADD_TWITTER = "お気に入りに追加する"
        var DEL_TWITTER = "お気に入りから削除する"
    }
}