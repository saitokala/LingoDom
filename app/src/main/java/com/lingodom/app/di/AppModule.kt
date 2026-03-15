package com.lingodom.app.di

import com.lingodom.app.data.PreferencesManager
import com.lingodom.app.data.PreferencesManagerImpl
import com.lingodom.app.data.SoundManager
import com.lingodom.app.data.SoundManagerImpl
import com.lingodom.app.data.WordRepository
import com.lingodom.app.data.WordRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Singleton
    @Binds
    abstract fun bindWordRepository(impl: WordRepositoryImpl): WordRepository

    @Singleton
    @Binds
    abstract fun bindPreferencesManager(impl: PreferencesManagerImpl): PreferencesManager

    @Singleton
    @Binds
    abstract fun bindSoundManager(impl: SoundManagerImpl): SoundManager
}
