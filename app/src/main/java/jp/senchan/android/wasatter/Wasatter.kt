package jp.senchan.android.wasatter

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import java.util.*
import java.util.regex.Pattern

class Wasatter : Application() {
    companion object {
        const val SERVICE_WASSR = "Wassr"
        const val SERVICE_TWITTER = "Twitter"
        private const val REGEX_URL = "https?://[^\\s]+"
        const val REPLY = "reply"
        const val OAUTH_KEY = "5WURvsXWy6pwsFyJvR7Yw"
        const val OAUTH_SECRET = "Ya9SKf2G0iPDRzrGeFrwTVe4eIgsjO8t4chCQl62vs"
        lateinit var CONTEXT: Context
        var images = HashMap<String, Bitmap>()
        lateinit var main: ActivityMain
        private const val ERROR_AUTH = "401"
        private const val ERROR_TMP = "503"
        var MODE_POSTING = "mode_posting"
        var MODE_DISPLAY = "mode_display"
        var MODE_ERROR = "mode_error"


        fun getUrl(text: String?): String {
            val pt = Pattern.compile(REGEX_URL,
                    Pattern.CASE_INSENSITIVE)
            val mc = pt.matcher(text)
            while (mc.find()) {
                return mc.group()
            }
            return ""
        }

        //TODO リソース外出し、というかそもそもこんなにエラー詳しくないほうがよいのではないか
        fun displayHttpError(error: String, service: String) {
            val message: String
            message = if (ERROR_TMP == error && SERVICE_WASSR == service) {
                "エラーが発生しました（Wassr,503）Wassrが一時的に不安定になっている可能性があります。"
            } else if (ERROR_TMP == error && SERVICE_TWITTER == service) {
                "エラーが発生しました（Twitter,503）API制限を超えた可能性があります。"
            } else if (ERROR_AUTH == error && SERVICE_WASSR == service) {
                "エラーが発生しました（Wassr,401）IDかパスワードが間違っている、もしくはAPI制限を超えた可能性があります。"
            } else if (ERROR_AUTH == error && SERVICE_TWITTER == service) {
                "エラーが発生しました（Twitter,401）IDかパスワードが間違っている可能性があります。"
            } else if ("JSON" == error) {
                "取得データが破損しています、リロードしてください。"
            } else {
                "ネットワークエラーが発生しました。リトライしてください。"
            }
            val ad = AlertDialog.Builder(main)
            ad.setMessage(message)
            ad.setPositiveButton("閉じる", null)
            ad.show()
        }
    }
}