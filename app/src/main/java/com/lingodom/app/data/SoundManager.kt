package com.lingodom.app.data

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log

/**
 * Plays sound effects for game events.
 *
 * - Key clicks use a lightweight ToneGenerator (DTMF tone).
 * - Win / loss effects use a ToneGenerator sequence on a background thread
 *   to produce a musical jingle without shipping audio files.
 */
class SoundManager(@Suppress("UNUSED_PARAMETER") context: Context) {

    companion object {
        private const val TAG = "SoundManager"
    }

    /**
     * Plays a sound based on the [effectType] constant from [AudioManager].
     *
     * | effectType               | Sound produced                          |
     * |--------------------------|-----------------------------------------|
     * | FX_KEY_CLICK             | Short tap tone                          |
     * | FX_KEYPRESS_RETURN       | Rising 3-note victory jingle 🎵         |
     * | FX_KEYPRESS_DELETE       | Descending 2-note failure tone          |
     */
    fun playSound(effectType: Int) {
        when (effectType) {
            AudioManager.FX_KEYPRESS_RETURN -> playVictoryJingle()
            AudioManager.FX_KEYPRESS_DELETE -> playFailureTone()
            else -> playTapTone()
        }
    }

    // ── Key tap ─────────────────────────────────────────────────────

    private fun playTapTone() {
        var generator: ToneGenerator? = null
        try {
            generator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
            generator.startTone(ToneGenerator.TONE_DTMF_S, 50)
            val gen = generator
            generator = null
            Thread {
                try { Thread.sleep(150) } catch (_: InterruptedException) { }
                try { gen.release() } catch (_: Exception) { }
            }.start()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to play tap tone", e)
            try { generator?.release() } catch (_: Exception) { }
        }
    }

    // ── Victory jingle (rising C-E-G triad, ~600 ms total) ──────────

    private fun playVictoryJingle() {
        Thread {
            var gen: ToneGenerator? = null
            try {
                gen = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
                // Note 1 — C  (DTMF tone '1' ≈ 697+1209 Hz, bright)
                gen.startTone(ToneGenerator.TONE_DTMF_1, 140)
                Thread.sleep(180)
                // Note 2 — E  (DTMF tone '4' ≈ 770+1209 Hz, mid)
                gen.startTone(ToneGenerator.TONE_DTMF_4, 140)
                Thread.sleep(180)
                // Note 3 — G  (DTMF tone '7' ≈ 852+1209 Hz, high, longer)
                gen.startTone(ToneGenerator.TONE_DTMF_7, 250)
                Thread.sleep(350)
            } catch (e: Exception) {
                Log.e(TAG, "Victory jingle failed", e)
            } finally {
                try { gen?.release() } catch (_: Exception) { }
            }
        }.start()
    }

    // ── Failure tone (descending 2-note) ────────────────────────────

    private fun playFailureTone() {
        Thread {
            var gen: ToneGenerator? = null
            try {
                gen = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
                // High note
                gen.startTone(ToneGenerator.TONE_DTMF_9, 180)
                Thread.sleep(220)
                // Low note
                gen.startTone(ToneGenerator.TONE_DTMF_1, 300)
                Thread.sleep(400)
            } catch (e: Exception) {
                Log.e(TAG, "Failure tone failed", e)
            } finally {
                try { gen?.release() } catch (_: Exception) { }
            }
        }.start()
    }

}
