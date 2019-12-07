package jp.senchan.android.wasatter.utils

import android.widget.Toast
import jp.senchan.android.wasatter.Wasatter

object ToastUtil {
    @JvmOverloads
    fun show(text: String?, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(Wasatter.CONTEXT, text, duration).show()
    }

    fun showLong(text: String?) {
        show(text, Toast.LENGTH_LONG)
    }
}