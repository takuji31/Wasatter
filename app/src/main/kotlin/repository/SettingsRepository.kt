package jp.senchan.android.wasatter.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import jp.senchan.android.wasatter.R

interface SettingsRepository {
    val isTwitterEnabled: Boolean

    val isDisplayBodyMultiLine: Boolean

    val isLoadTwitterTimeline: Boolean

    var twitterToken: String?

    var twitterTokenSecret: String?

    val isLoadImage: Boolean

    val isLoadFavoriteImage: Boolean

    val isDisplayButtons: Boolean

    companion object {
        fun getDefaultInstance(context: Context): SettingsRepository = SettingRepositoryImpl(context, PreferenceManager.getDefaultSharedPreferences(context))
    }
}

class SettingRepositoryImpl(private val context: Context, private val sharedPreferences: SharedPreferences) : SettingsRepository {
    override val isTwitterEnabled: Boolean
        get() = sharedPreferences.getBoolean("enable_twitter", false)

    override val isDisplayBodyMultiLine: Boolean
        get() = sharedPreferences.getBoolean("display_body_multi_line", false)

    override val isLoadTwitterTimeline: Boolean
        get() = sharedPreferences.getBoolean(
                context.getString(R.string.key_setting_twitter_load_timeline),
                true
        )

    override var twitterToken: String?
        get() = sharedPreferences.getString("twitter_token", "")
        set(value) {
            sharedPreferences.edit {
                putString("twitter_token", value)
            }
        }

    override var twitterTokenSecret: String?
        get() = sharedPreferences.getString("twitter_token_secret", "")
        set(value) {
            sharedPreferences.edit {
                putString("twitter_token_secret", value)
            }
        }

    override val isLoadImage: Boolean
        get() = sharedPreferences.getBoolean("display_load_image", true)

    override val isLoadFavoriteImage: Boolean
        get() = sharedPreferences.getBoolean("display_load_favorite_image", false)

    override val isDisplayButtons: Boolean
        get() =
            sharedPreferences.getBoolean(context.getString(R.string.key_setting_display_buttons), true)
}