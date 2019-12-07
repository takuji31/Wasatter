package jp.senchan.android.wasatter.activity

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.*
import jp.senchan.android.wasatter.R
import jp.senchan.android.wasatter.Wasatter
import jp.senchan.android.wasatter.WasatterItem
import jp.senchan.android.wasatter.task.TaskUpdate

/**
 * @author Senka/Takuji
 */
class Update : Activity() {
    protected var ws: WasatterItem? = null
    protected var reply = false
    protected var channel = false
    protected var channelId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) { // TODO 自動生成されたメソッド・スタブ
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.status_update)
        val extras = this.intent.extras
        channel = Wasatter.main!!.button_channel!!.isChecked
        if (extras != null) {
            ws = extras.getSerializable(Wasatter.REPLY) as WasatterItem?
        }
        // チェックボックスの設定
        val wassr_enable = findViewById<View>(R.id.check_post_wassr) as CheckBox
        val twitter_enable = findViewById<View>(R.id.check_post_twitter) as CheckBox
        wassr_enable.isChecked = Setting.Companion.isWassrEnabled()
        wassr_enable.isClickable = Setting.Companion.isWassrEnabled()
        twitter_enable.isChecked = Setting.Companion.isTwitterEnabled()
        twitter_enable.isClickable = Setting.Companion.isTwitterEnabled()
        if (channel) {
            twitter_enable.isChecked = false
            twitter_enable.isClickable = false
            channelId = Wasatter.main!!.selected_channel
        }
        // 返信の場合は元メッセージの表示処理等を追加
        if (ws != null) {
            reply = true
            if (ws!!.text != null) {
                val sb = SpannableStringBuilder()
                sb.append("> ")
                sb.append(ws!!.text)
                val reply_message = findViewById<View>(R.id.reply_message) as TextView
                reply_message.text = sb.toString()
            }
            if (ws!!.name != null) {
                val sb2 = SpannableStringBuilder()
                sb2.append("by ")
                sb2.append(ws!!.name)
                val reply_user_nick = findViewById<View>(R.id.reply_user_name) as TextView
                reply_user_nick.text = sb2.toString()
            }
            if (ws!!.service == Wasatter.SERVICE_TWITTER) { // 更にTwitterなら@ユーザー名をテキストの初期値にする。
                val et = findViewById<View>(R.id.post_status_text) as EditText
                et.setText(SpannableStringBuilder("@").append(ws!!.id)
                        .append(" ").toString())
            }
        }
        // 更に返信なら指定されたサービスのみチェックを入れて非表示にする。
        if (reply) {
            wassr_enable.isChecked = (ws!!.service
                    == Wasatter.SERVICE_WASSR)
            twitter_enable.isChecked = (ws!!.service
                    == Wasatter.SERVICE_TWITTER)
            val layout_service = findViewById<View>(R.id.layout_service) as LinearLayout
            layout_service.visibility = View.GONE
        } else {
            val layout_reply = findViewById<View>(R.id.layout_reply) as LinearLayout
            layout_reply.visibility = View.GONE
        }
        val post_btn = findViewById<View>(R.id.post_button) as Button
        post_btn?.setOnClickListener(View.OnClickListener { v ->
            val status = findViewById<View>(R.id.post_status_text) as EditText
            val wassr = findViewById<View>(R.id.check_post_wassr) as CheckBox
            val twitter = findViewById<View>(R.id.check_post_twitter) as CheckBox
            val sb = status
                    .text as SpannableStringBuilder
            val update_button = v as Button
            // 未入力チェック
            if ("" == sb.toString()) {
                val adb = AlertDialog.Builder(
                        this@Update)
                adb.setTitle("")
                adb.setMessage(R.string.notice_message_required)
                adb.setPositiveButton("OK", null)
                adb.show()
                return@OnClickListener
            }
            // 二重投稿防止
            update_button.isClickable = false
            val ut = TaskUpdate(
                    reply, wassr.isChecked,
                    twitter.isChecked, channel)
            ut
                    .execute(
                            sb.toString(),
                            if (reply) ws!!.rid else null, channelId)
            finish()
        })
    }
}