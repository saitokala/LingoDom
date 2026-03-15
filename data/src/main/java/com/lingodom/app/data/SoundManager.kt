package com.lingodom.app.data

/**
 * Contract for playing sound effects.
 *
 * | effectType               | Sound produced                          |
 * |--------------------------|-----------------------------------------|
 * | FX_KEY_CLICK             | Short tap tone                          |
 * | FX_KEYPRESS_RETURN       | Rising 3-note victory jingle            |
 * | FX_KEYPRESS_DELETE       | Descending 2-note failure tone          |
 */
interface SoundManager {
    fun playSound(effectType: Int)
}
