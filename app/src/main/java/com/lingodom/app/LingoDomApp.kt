package com.lingodom.app

import android.app.Application
import com.lingodom.app.data.PreferencesManager
import com.lingodom.app.data.SoundManager
import com.lingodom.app.data.WordRepository

class LingoDomApp : Application() {

    lateinit var wordRepository: WordRepository
        private set

    lateinit var preferencesManager: PreferencesManager
        private set

    lateinit var soundManager: SoundManager
        private set

    override fun onCreate() {
        super.onCreate()
        wordRepository = WordRepository(this)
        preferencesManager = PreferencesManager(this)
        soundManager = SoundManager(this)
    }
}
