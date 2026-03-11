package com.lingodom.app.di

import com.lingodom.app.data.PreferencesManager
import com.lingodom.app.data.PreferencesManagerInterface
import com.lingodom.app.data.SoundManager
import com.lingodom.app.data.SoundManagerInterface
import com.lingodom.app.data.WordRepository
import com.lingodom.app.data.WordRepositoryInterface
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    abstract fun bindWordRepository(impl: WordRepository): WordRepositoryInterface

    @Binds
    abstract fun bindPreferencesManager(impl: PreferencesManager): PreferencesManagerInterface

    @Binds
    abstract fun bindSoundManager(impl: SoundManager): SoundManagerInterface
}
