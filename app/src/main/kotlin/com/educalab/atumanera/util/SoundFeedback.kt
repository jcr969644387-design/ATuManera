package com.educalab.atumanera.util

import android.media.AudioManager
import android.media.ToneGenerator

/**
 * Efectos de sonido cortos generados con [ToneGenerator]: no requieren
 * archivos de audio embebidos. Cada llamada crea un generador propio y lo
 * libera al terminar el tono.
 */
object SoundFeedback {

    fun playBuild() = playTone(ToneGenerator.TONE_PROP_BEEP)

    fun playSuccess() = playTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD)

    fun playReject() = playTone(ToneGenerator.TONE_CDMA_PIP)

    private fun playTone(tone: Int) {
        try {
            val generator = ToneGenerator(AudioManager.STREAM_MUSIC, 80)
            generator.startTone(tone, 150)
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                generator.release()
            }, 200)
        } catch (_: RuntimeException) {
            // El dispositivo no tiene un canal de audio disponible; se ignora en silencio.
        }
    }
}
