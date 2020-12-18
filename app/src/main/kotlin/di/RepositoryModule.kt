package jp.senchan.android.wasatter.di

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.senchan.android.wasatter.repository.SettingsRepository

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    fun settingsRepository(application: Application) =
        SettingsRepository.getDefaultInstance(application)
}