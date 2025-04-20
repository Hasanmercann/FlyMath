package com.achelmas.numart

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LanguageManager {

    fun setLocaleLanguage(context: Context, language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)

        // Save the selected language
        saveSelectedLanguage(context, language)
    }

    fun setLocal(context: Context) {
        val selectedLanguage = loadSelectedLanguage(context)
        if (!selectedLanguage.isNullOrEmpty()) {
            if (Constants.SUPPORTED_LANGUAGES.contains(selectedLanguage)) {
                setLocaleLanguage(context, selectedLanguage)
            } else {
                setLocaleLanguage(context, "en")
            }
        } else {
            // No saved language, set device language or fallback to English
            val deviceLanguage = getDeviceLanguage()
            val languageToSet =
                if (Constants.SUPPORTED_LANGUAGES.contains(deviceLanguage)) deviceLanguage else "en"
            setLocaleLanguage(context, languageToSet)
        }
    }

    fun loadLocale(context: Context) {
        setLocal(context)
    }

    fun saveSelectedLanguage(context: Context, language: String) {
        val preferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString("Selected_Language", language)
        editor.apply()
    }

    fun loadSelectedLanguage(context: Context): String? {
        val preferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        return preferences.getString("Selected_Language", null)
    }

    // Get the device's default language
    private fun getDeviceLanguage(): String {
        val deviceLocale = Locale.getDefault().language
        return deviceLocale.lowercase()
    }

    fun getCurrentLanguage(context: Context): String {
        return loadSelectedLanguage(context) ?: getDeviceLanguage()
    }
}