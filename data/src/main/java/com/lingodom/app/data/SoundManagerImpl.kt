package com.lingodom.app.data

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Plays sound effects for game events.
 *
 * - Key clicks use a lightweight ToneGenerator (DTMF tone).
 * - Win / loss effects use a ToneGenerator sequence on a background thread
 *   to produce a musical jingle without shipping audio files.
 *
 * All sound work is dispatched to a single-thread executor to prevent
 * unbounded thread creation from rapid taps.
 */
@Singleton
class SoundManagerImpl @Inject constructor(
    @ApplicationContext @Suppress("UNUSED_PARAMETER") context: Context
) : SoundManager {

    companion object {
        private const val TAG = "SoundManagerImpl"
    }

    private val executor: ExecutorService = Executors.newSingleThreadExecutor { r ->
        Thread(r, "SoundManager").apply { isDaemon = true }
    }

    override fun playSound(effectType: Int) {
        when (effectType) {
            AudioManager.FX_KEYPRESS_RETURN -> playVictoryJingle()
            AudioManager.FX_KEYPRESS_DELETE -> playFailureTone()
            else -> playTapTone()
        }
    }

    // ── Key tap ─────────────────────────────────────────────────────

    private fun playTapTone() {
        executor.execute {
            var generator: ToneGenerator? = null
            try {
                generator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
                generator.startTone(ToneGenerator.TONE_DTMF_S, 50)
                Thread.sleep(150)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to play tap tone", e)
            } finally {
                try { generator?.release() } catch (_: Exception) { }
            }
        }
    }

    // ── Victory jingle (rising C-E-G triad, ~600 ms total) ──────────

    private fun playVictoryJingle() {
        executor.execute {
            var gen: ToneGenerator? = null
            try {
                gen = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
                gen.startTone(ToneGenerator.TONE_DTMF_1, 140)   // Note 1 — C
                Thread.sleep(180)
                gen.startTone(ToneGenerator.TONE_DTMF_4, 140)   // Note 2 — E
                Thread.sleep(180)
                gen.startTone(ToneGenerator.TONE_DTMF_7, 250)   // Note 3 — G (longer)
                Thread.sleep(350)
            } catch (e: Exception) {
                Log.e(TAG, "Victory jingle failed", e)
            } finally {
                try { gen?.release() } catch (_: Exception) { }
            }
        }
    }

    // ── Failure tone (descending 2-note) ────────────────────────────

    private fun playFailureTone() {
        executor.execute {
            var gen: ToneGenerator? = null
            try {
                gen = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
                gen.startTone(ToneGenerator.TONE_DTMF_9, 180)   // High note
                Thread.sleep(220)
                gen.startTone(ToneGenerator.TONE_DTMF_1, 300)   // Low note
                Thread.sleep(400)
            } catch (e: Exception) {
                Log.e(TAG, "Failure tone failed", e)
            } finally {
                try { gen?.release() } catch (_: Exception) { }
            }
        }
    }
}
