package com.lingodom.app.fake

import com.lingodom.app.data.SoundManager

/** Records sound effect calls for test assertions. */
class FakeSoundManager : SoundManager {
    val playedEffects = mutableListOf<Int>()

    override fun playSound(effectType: Int) {
        playedEffects.add(effectType)
    }
}
