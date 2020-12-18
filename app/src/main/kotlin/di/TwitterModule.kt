package jp.senchan.android.wasatter.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.senchan.android.wasatter.Wasatter
import twitter4j.AsyncTwitter
import twitter4j.AsyncTwitterFactory
import twitter4j.conf.ConfigurationBuilder

@Module
@InstallIn(SingletonComponent::class)
object TwitterModule {
    @Provides
    fun twitter(): AsyncTwitter = AsyncTwitterFactory(
        ConfigurationBuilder()
            .setOAuthConsumerKey(Wasatter.OAUTH_KEY)
            .setOAuthConsumerSecret(Wasatter.OAUTH_SECRET)
            .build()
    ).instance
}