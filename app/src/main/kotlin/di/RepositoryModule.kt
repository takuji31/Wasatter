package jp.senchan.android.wasatter.di

import android.app.Application
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.senchan.android.wasatter.repository.SettingsRepository
import jp.senchan.android.wasatter.repository.TwitterRepository
import jp.senchan.android.wasatter.repository.TwitterRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    fun settingsRepository(application: Application) =
        SettingsRepository.getDefaultInstance(application)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryBinderModule {
    @Binds
    abstract fun twitterRepository(twitterRepository: TwitterRepositoryImpl): TwitterRepository
}