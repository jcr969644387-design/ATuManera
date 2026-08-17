package com.educalab.atumanera.util

import android.content.Context
import android.content.SharedPreferences

/** Preferencias locales simples: no se envían a ningún servidor. */
class AppPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("atumanera_prefs", Context.MODE_PRIVATE)

    var onboardingCompleted: Boolean
        get() = prefs.getBoolean(KEY_ONBOARDING, false)
        set(value) = prefs.edit().putBoolean(KEY_ONBOARDING, value).apply()

    var soundEnabled: Boolean
        get() = prefs.getBoolean(KEY_SOUND, true)
        set(value) = prefs.edit().putBoolean(KEY_SOUND, value).apply()

    var hapticsEnabled: Boolean
        get() = prefs.getBoolean(KEY_HAPTICS, true)
        set(value) = prefs.edit().putBoolean(KEY_HAPTICS, value).apply()

    companion object {
        private const val KEY_ONBOARDING = "onboarding_completed"
        private const val KEY_SOUND = "sound_enabled"
        private const val KEY_HAPTICS = "haptics_enabled"
    }
}
