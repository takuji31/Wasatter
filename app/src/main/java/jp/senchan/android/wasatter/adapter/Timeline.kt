package jp.senchan.android.wasatter.adapter

import android.content.Context
import android.net.Uri
import android.text.SpannableStringBuilder
import android.text.TextUtils.TruncateAt
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.squareup.picasso.Picasso
import jp.senchan.android.wasatter.R
import jp.senchan.android.wasatter.WasatterAdapter
import jp.senchan.android.wasatter.WasatterItem
import jp.senchan.android.wasatter.repository.SettingsRepository
import java.text.SimpleDateFormat
import java.util.*

class Timeline(context: Context, textViewResourceId: Int,
               private val items: ArrayList<WasatterItem>?, channel: Boolean) : ArrayAdapter<WasatterItem>(context, textViewResourceId, items!!), WasatterAdapter {
    private val inflater: LayoutInflater =  context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private val settingsRepository by lazy {
        SettingsRepository.getDefaultInstance(context)
    }
    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val view = view ?: inflater.inflate(R.layout.timeline_row, null)
        // データの取得
        val item = items!![position]
        if (item != null) {
            val screenName = view.findViewById<View>(R.id.screen_name) as TextView
            // 名前をビューにセットする
            val text = view!!.findViewById<View>(R.id.status) as TextView
            if (screenName != null) {
                screenName.text = item.name
            }
            // テキストをビューにセットする
            if (text != null) {
                text.text = item.text
            }
            // テキストの行数を決定する。
            text.isSingleLine = !settingsRepository.isDisplayBodyMultiLine
            if (!settingsRepository.isDisplayBodyMultiLine) {
                text.ellipsize = TruncateAt.MARQUEE
            } else {
                text.ellipsize = null
            }
            // 返信元の名前をビューにセットする
            val reply_name = view.findViewById<View>(R.id.reply_name) as TextView
            if ("null" != item.replyUserNick && item.replyUserNick != null) {
                reply_name.text = SpannableStringBuilder(">").append(
                        item.replyUserNick).toString()
                reply_name.visibility = View.VISIBLE
            } else {
                reply_name.text = ""
                reply_name.visibility = View.GONE
            }
            // 画像をセット
            val icon = view.findViewById<View>(R.id.icon) as ImageView
            if (settingsRepository.isLoadImage) {
                Picasso.get().isLoggingEnabled = true
                Picasso.get()
                        .load(Uri.parse(item.profileImageUrl))
                        .into(icon)
                icon.visibility = View.VISIBLE
            } else {
                icon.visibility = View.GONE
            }
            // サービス名をビューにセットする
            val service = view.findViewById<View>(R.id.service_name) as TextView
            service.text = item.service
            // イイネ！アイコンリストを表示
            val layout_favorite_list = view
                    .findViewById<View>(R.id.layout_favorite) as LinearLayout
            val layout_favorite_icons = view
                    .findViewById<View>(R.id.layout_favorite_images) as LinearLayout
            if (item.favorite!!.size == 0) {
                layout_favorite_list.visibility = View.GONE
                layout_favorite_icons.removeAllViews()
            } else {
                val favorites = item.favorite
                val count = favorites.size
                val tv = TextView(view.context)
                tv.text = SpannableStringBuilder("x").append(count.toString()).toString()
                layout_favorite_icons.removeAllViews()
                layout_favorite_icons.addView(tv)
                layout_favorite_list.visibility = View.VISIBLE
                if (settingsRepository.isLoadFavoriteImage) {
                    for (i in 0 until count) {
                        val add_icon = ImageView(view.context)
                        add_icon.layoutParams = ViewGroup.LayoutParams(28, 28)
                        add_icon.setPadding(2, 2, 2, 2)
                        layout_favorite_icons.addView(add_icon)
                    }
                }
            }
            //投稿日時表示するぜヒャッハー
            val date = view.findViewById<View>(R.id.post_date) as TextView
            val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
            val now = Date()
            now.time = item.epoch * 1000
            date.text = sdf.format(now)
        }
        return view!!
    }

    override fun updateView() { // TODO 自動生成されたメソッド・スタブ
        super.notifyDataSetChanged()
    }
}