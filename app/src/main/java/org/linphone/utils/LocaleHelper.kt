package org.linphone.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import androidx.core.os.ConfigurationCompat
import java.util.Locale
import org.linphone.LinphoneApplication
import org.linphone.core.tools.Log

object LocaleHelper {
    private const val TAG = "[LocaleHelper]"

    const val LOCALE_ENGLISH = "en"
    const val LOCALE_FRENCH = "fr"
    const val LOCALE_ARABIC = "ar"
    const val LOCALE_SYSTEM_DEFAULT = ""

    /** Get list of available locales */
    fun getAvailableLocales(): List<Pair<String, String>> {
        return listOf(
            Pair(LOCALE_SYSTEM_DEFAULT, "System Default"),
            Pair(LOCALE_ENGLISH, "English"),
            Pair(LOCALE_FRENCH, "Français"),
            Pair(LOCALE_ARABIC, "العربية")
        )
    }

    fun applyLocale(context: Context): Context {
        val localeCode = getCurrentLocale(context)
        val locale =
            if (localeCode == LOCALE_SYSTEM_DEFAULT) {
                val configuration = Resources.getSystem().configuration
                ConfigurationCompat.getLocales(configuration).get(0) ?: Locale.getDefault()
            } else {
                Locale.forLanguageTag(localeCode)
            }
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        // Handle RTL for Arabic
        if (localeCode == LOCALE_ARABIC) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                config.setLayoutDirection(locale)
            }
        }

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val localeList = LocaleList(locale)
            LocaleList.setDefault(localeList)
            config.setLocales(localeList)
            context.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
            context
        }
    }

    /** Get current locale from preferences */
    fun getCurrentLocale(context: Context): String {
        val prefs = LinphoneApplication.corePreferences
        return prefs.appLocale
    }

    /** Set and persist locale preference */
    fun setLocale(context: Context, localeCode: String) {
        Log.i("$TAG Setting locale to: $localeCode")
        val prefs = LinphoneApplication.corePreferences
        prefs.appLocale = localeCode
    }

    /** Get locale display name */
    fun getLocaleDisplayName(localeCode: String, context: Context): String {
        return when (localeCode) {
            LOCALE_SYSTEM_DEFAULT -> context.getString(org.linphone.R.string.locale_system_default)
            LOCALE_ENGLISH -> context.getString(org.linphone.R.string.locale_english)
            LOCALE_FRENCH -> context.getString(org.linphone.R.string.locale_french)
            LOCALE_ARABIC -> context.getString(org.linphone.R.string.locale_arabic)
            else -> localeCode
        }
    }

    /** Check if locale is RTL (Right-to-Left) */
    fun isRTL(localeCode: String): Boolean {
        return localeCode == LOCALE_ARABIC
    }
}
