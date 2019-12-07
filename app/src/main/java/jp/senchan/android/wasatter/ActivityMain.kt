package jp.senchan.android.wasatter

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemSelectedListener
import jp.senchan.android.wasatter.activity.Detail
import jp.senchan.android.wasatter.activity.Update
import jp.senchan.android.wasatter.adapter.Timeline
import jp.senchan.android.wasatter.repository.SettingsRepository
import jp.senchan.android.wasatter.task.TaskReloadTimeline
import java.util.*

class ActivityMain : Activity() {
    var list_timeline: ArrayList<WasatterItem>? = null
    var list_reply: ArrayList<WasatterItem>? = null
    var list_mypost: ArrayList<WasatterItem>? = null
    var list_odai: ArrayList<WasatterItem>? = null
    var list_channel_list: ArrayList<WasatterItem>? = null
    var list_channel: ArrayList<WasatterItem>? = null

    private val settingsRepository: SettingsRepository by lazy {
        SettingsRepository.getDefaultInstance(this)
    }

    lateinit var ls: ListView
    lateinit var button_reload_channel_list: Button
    lateinit var button_timeline: ToggleButton
    lateinit var button_reply: ToggleButton
    lateinit var button_mypost: ToggleButton
    lateinit var button_channel: ToggleButton
    lateinit var button_odai: ToggleButton
    lateinit var progress_image: ProgressBar
    lateinit var layout_progress_timeline: LinearLayout
    lateinit var layout_channel_list: LinearLayout
    lateinit var spinner_channel_list: Spinner
    lateinit var loading_timeline_text: TextView

    var first_load = true
    var reload_image = false
    var from_config = false
    var mode: Int = TaskReloadTimeline.MODE_TIMELINE
    private var selctedButtonId = 0
    var selected_channel: String? = null
    var selectedItem: WasatterItem? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.main)
        ls = findViewById<View>(R.id.timeline_list) as ListView
        progress_image = findViewById<View>(R.id.load_image_progress) as ProgressBar
        layout_progress_timeline = findViewById<View>(R.id.layout_load_timeline) as LinearLayout
        layout_channel_list = findViewById<View>(R.id.layout_channel_list) as LinearLayout
        loading_timeline_text = findViewById<View>(R.id.text_loading_timeline) as TextView
        spinner_channel_list = findViewById<View>(R.id.channel_list) as Spinner
        button_reload_channel_list = findViewById<Button>(R.id.button_reload_channel_list)
        button_reload_channel_list?.setOnClickListener(ChannelReloadButtonClickListener())
        // トグルボタンを代入
        button_timeline = findViewById<View>(R.id.toggle_button_timeline) as ToggleButton
        button_reply = findViewById<View>(R.id.toggle_button_reply) as ToggleButton
        button_mypost = findViewById<View>(R.id.toggle_button_mypost) as ToggleButton
        button_odai = findViewById<View>(R.id.toggle_button_odai) as ToggleButton
        button_channel = findViewById<View>(R.id.toggle_button_channel) as ToggleButton
        // トグルボタンにListViewの中身を切り替えるイベントを割り当て
        button_timeline
                .setOnClickListener(TimelineButtonClickListener())
        button_reply!!.setOnClickListener(ReplyButtonClickListener())
        button_mypost!!.setOnClickListener(MyPostButtonClickListener())
        button_channel
                .setOnClickListener(ChannelButtonClickListener())
        // トグルボタンの初期値をタイムラインに設定
        buttonSelect(R.id.toggle_button_timeline)
        // ボタンにイベントを割り当て
        val button_new = findViewById<View>(R.id.button_new_post) as Button
        button_new.setOnClickListener(ButtonNewPostListener())
        val button_setting = findViewById<View>(R.id.button_open_setting) as Button
        button_setting.setOnClickListener(ButtonOpenSettingListener())
        val button_reload = findViewById<View>(R.id.button_reload) as Button
        button_reload.setOnClickListener(ButtonReloadListener())
        spinner_channel_list
                .setOnItemSelectedListener(ChannelListClickListener())
        // 色んなところからいじれるように、Static変数に突っ込む
        Wasatter.main = this
    }

    override fun onResume() { // TODO 自動生成されたメソッド・スタブ
        super.onResume()
        // ボタンの表示設定
        val layout_buttons = findViewById<View>(R.id.layout_buttons) as LinearLayout
        if (settingsRepository.isDisplayButtons) {
            layout_buttons.visibility = View.VISIBLE
        } else {
            layout_buttons.visibility = View.GONE
        }
        // テーマの設定
        // TwitterもしくはWassrが有効になっているかチェックする
        val enable = (!settingsRepository.isTwitterEnabled)
        val twitter_oauth_empty = (settingsRepository.isTwitterEnabled
                && ("" == settingsRepository.twitterToken || "" == settingsRepository.twitterTokenSecret))
        val adb = AlertDialog.Builder(this)
        if (enable) {
            adb.setMessage(R.string.notice_message_no_enable)
            adb.setTitle(R.string.notice_title_no_enable)
            adb.setPositiveButton("OK", OpenSettingClickListener())
            adb.show()
        } else if (twitter_oauth_empty) {
            adb.setMessage(R.string.notice_message_no_twitter_oauth_token)
            adb.setPositiveButton("OK", OpenSettingClickListener())
            adb.show()
        } else if (first_load) {
            doReloadTask(mode)
        } else if (from_config) {
            val adapter = ls!!.adapter as WasatterAdapter
            adapter?.updateView()
            buttonSelect(checkWassrEnabled(selctedButtonId))
        }
    }

    val timeLine: Unit
        get() {
            doReloadTask(TaskReloadTimeline.MODE_TIMELINE)
        }

    val reply: Unit
        get() {
            doReloadTask(TaskReloadTimeline.MODE_REPLY)
        }

    val myPost: Unit
        get() {
            doReloadTask(TaskReloadTimeline.MODE_MYPOST)
        }

    val channelList: Unit
        get() {
            doReloadTask(TaskReloadTimeline.MODE_CHANNEL_LIST)
        }

    fun getChannel(channel: String?) {
        ls!!.adapter = null
        first_load = false
        val rt = TaskReloadTimeline(this, ls,
                TaskReloadTimeline.MODE_CHANNEL)
        rt.execute(channel)
    }

    fun doReloadTask(mode: Int) {
        ls!!.adapter = null
        first_load = false
        val rt = TaskReloadTimeline(this, ls, mode)
        rt.execute()
    }

    // メニューが生成される際に起動される。
// この中でメニューのアイテムを追加したりする。
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        // メニューインフレーターを取得
        this.menuInflater.inflate(R.menu.main, menu)
        // できたらtrueを返す
        return true
    }

    // メニューのアイテムが選択された際に起動される。
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_open_setting -> openSetting()
            R.id.menu_open_version -> openVersion()
            R.id.menu_status_update -> openNewPost()
            R.id.menu_status_reload -> reload()
            else -> {
            }
        }
        return true
    }

    fun buttonSelect(id: Int) { // Wassr固有の機能はWassrを有効にしていないと利用出来ないようにする
        var id = id
        id = checkWassrEnabled(id)
        button_timeline!!.isChecked = false
        button_timeline!!.isClickable = true
        button_reply!!.isChecked = false
        button_reply!!.isClickable = true
        button_mypost!!.isChecked = false
        button_mypost!!.isClickable = true
        button_channel!!.isChecked = false
        button_channel!!.isClickable = true
        button_odai!!.isChecked = false
        button_odai!!.isClickable = true
        val btn = findViewById<View>(id) as ToggleButton
        btn.isChecked = true
        btn.isClickable = false
        ls!!.onItemClickListener = TimelineItemClickListener()
        var channnel_list_visible = View.GONE
        if (id == R.id.toggle_button_channel) {
            channnel_list_visible = View.VISIBLE
        }
        layout_channel_list!!.visibility = channnel_list_visible
        selctedButtonId = id
    }

    fun checkWassrEnabled(id: Int): Int {
        var id = id
        button_odai!!.isEnabled = false
        button_channel!!.isEnabled = false
        val wassr_function_list = ArrayList<Int>()
        wassr_function_list.add(R.id.toggle_button_channel)
        wassr_function_list.add(R.id.toggle_button_odai)
        if (wassr_function_list.indexOf(Integer.valueOf(id)) != -1) {
            id = R.id.toggle_button_timeline
        }
        return id
    }

    /**
     * 設定ダイアログを開くメソッド
     */
    fun openSetting() {
        val intent_setting = Intent(this, settingsRepository::class.java)
        from_config = true
        this.startActivity(intent_setting)
    }

    /**
     * 投稿ウィンドウを開くメソッド
     */
    fun openNewPost() {
        val intent_status = Intent(this, Update::class.java)
        this.startActivity(intent_status)
    }

    /**
     * リロードを実行するメソッド
     */
    fun reload() {
        if (button_timeline!!.isChecked) {
            timeLine
        } else if (button_reply!!.isChecked) {
            reply
        } else if (button_mypost!!.isChecked) {
            myPost
        } else if (button_channel!!.isChecked) {
            if (selected_channel != null) {
                getChannel(selected_channel)
            }
        }
    }

    fun openVersion() {
        val ad = AlertDialog.Builder(this)
        ad.setTitle(R.string.menu_title_version)
        val sb = SpannableStringBuilder(getString(R.string.app_name))
        sb.append(" ")
        try {
            sb.append(packageManager.getPackageInfo(packageName, PackageManager.GET_META_DATA).versionName)
        } catch (e: PackageManager.NameNotFoundException) { //ありえない
            e.printStackTrace()
        }
        ad.setMessage(sb.toString())
        ad.setPositiveButton("OK", null)
        ad.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == IntentCode.MAIN_ITEMDETAIL) {
            try {
                val adapter = ls
                        .getAdapter() as Timeline
                adapter.updateView()
            } catch (e: ClassCastException) {
                e.printStackTrace()
            }
        }
    }

    /*
     * Innner Class
     */
// OnClickListenerの定義
    private inner class TimelineButtonClickListener : View.OnClickListener {
        override fun onClick(v: View) {
            mode = TaskReloadTimeline.MODE_TIMELINE
            buttonSelect(v.id)
            if (list_timeline == null) {
                timeLine
            } else {
                val adapter = Timeline(baseContext,
                        R.id.timeline_list, list_timeline,
                        false)
                ls!!.adapter = adapter
                ls!!.requestFocus()
            }
        }
    }

    private inner class ReplyButtonClickListener : View.OnClickListener {
        override fun onClick(v: View) {
            mode = TaskReloadTimeline.MODE_REPLY
            buttonSelect(v.id)
            if (list_reply == null) {
                reply
            } else {
                val adapter = Timeline(baseContext,
                        R.id.timeline_list, list_reply, false)
                ls!!.adapter = adapter
                ls!!.requestFocus()
            }
        }
    }

    private inner class MyPostButtonClickListener : View.OnClickListener {
        override fun onClick(v: View) {
            mode = TaskReloadTimeline.MODE_MYPOST
            buttonSelect(v.id)
            if (list_mypost == null) {
                myPost
            } else {
                val adapter = Timeline(baseContext,
                        R.id.timeline_list, list_mypost,
                        false)
                ls!!.adapter = adapter
                ls!!.requestFocus()
            }
        }
    }

    private inner class ChannelButtonClickListener : View.OnClickListener {
        override fun onClick(v: View) {
            mode = TaskReloadTimeline.MODE_CHANNEL_LIST
            buttonSelect(v.id)
            if (list_channel_list == null) {
                channelList
            }
            // チャンネルの内容取ってたら表示する。
            if (list_channel != null) {
                val adapter = Timeline(
                        ls!!.context,
                        R.layout.timeline_row, list_channel,
                        true)
                ls!!.adapter = adapter
            }
        }
    }

    private inner class ChannelReloadButtonClickListener : View.OnClickListener {
        override fun onClick(v: View) {
            channelList
        }
    }

    /**
     * ダイアログから設定画面を開くOnClickListener
     *
     * @author takuji
     */
    private inner class OpenSettingClickListener : DialogInterface.OnClickListener {
        override fun onClick(dialog: DialogInterface, which: Int) {
            openSetting()
        }
    }

    /**
     * タイムラインをクリックした時のOnClickListener
     *
     * @author takuji
     */
    private inner class TimelineItemClickListener : OnItemClickListener {
        override fun onItemClick(parent: AdapterView<*>, view: View, position: Int,
                                 id: Long) {
            val listView = parent as ListView
            // 選択されたアイテムを取得します
            selectedItem = listView
                    .adapter.getItem(position) as WasatterItem
            val intent_detail = Intent(this@ActivityMain,
                    Detail::class.java)
            this@ActivityMain.startActivityForResult(intent_detail,
                    IntentCode.MAIN_ITEMDETAIL)
        }
    }

    private inner class ChannelListClickListener : OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View,
                                    position: Int, id: Long) {
            val spinner = parent as Spinner
            val item = spinner.adapter.getItem(
                    position) as WasatterItem
            getChannel(item.id)
            selected_channel = item.id
        }

        override fun onNothingSelected(arg0: AdapterView<*>?) { // TODO 自動生成されたメソッド・スタブ
        }
    }
    /*
     * ボタンクリック時のリスナー
     */
    /**
     * 投稿ウィンドウを開くOnClickListener
     */
    private inner class ButtonNewPostListener : View.OnClickListener {
        override fun onClick(v: View) { // TODO 自動生成されたメソッド・スタブ
            openNewPost()
        }
    }

    /**
     * 設定ダイアログを開くOnClickListener
     */
    private inner class ButtonOpenSettingListener : View.OnClickListener {
        override fun onClick(v: View) { // TODO 自動生成されたメソッド・スタブ
            openSetting()
        }
    }

    /**
     * タイムラインをリロードするOnClickListener
     */
    private inner class ButtonReloadListener : View.OnClickListener {
        override fun onClick(v: View) { // TODO 自動生成されたメソッド・スタブ
            reload()
        }
    }
}