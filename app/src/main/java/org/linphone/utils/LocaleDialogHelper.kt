package org.linphone.utils

import android.app.AlertDialog
import android.content.Context
import android.view.View
import org.linphone.LinphoneApplication.Companion.corePreferences
import org.linphone.R
import org.linphone.core.tools.Log

/** Utility class to set up and handle locale selection via icon and dialog */
object LocaleDialogHelper {

    data class LocaleOption(val code: String, val displayName: String)

    /**
     * Sets up a locale icon that opens a dialog when clicked
     * @param context Android context
     * @param iconView The icon view to attach click listener to
     * @param recreateCallback Callback to recreate activity/fragment when locale changes
     */
    fun setupLocaleIcon(context: Context, iconView: View, recreateCallback: () -> Unit) {
        val locales =
                listOf(
                        LocaleOption("", context.getString(R.string.locale_system_default)),
                        LocaleOption(
                                LocaleHelper.LOCALE_ENGLISH,
                                context.getString(R.string.locale_english)
                        ),
                        LocaleOption(
                                LocaleHelper.LOCALE_FRENCH,
                                context.getString(R.string.locale_french)
                        ),
                        LocaleOption(
                                LocaleHelper.LOCALE_ARABIC,
                                context.getString(R.string.locale_arabic)
                        )
                )

        iconView.setOnClickListener { showLocaleDialog(context, locales, recreateCallback) }
    }

    private fun showLocaleDialog(
            context: Context,
            locales: List<LocaleOption>,
            recreateCallback: () -> Unit
    ) {
        val currentLocale = corePreferences.appLocale
        val currentIndex = locales.indexOfFirst { it.code == currentLocale }

        AlertDialog.Builder(context)
                .setTitle(R.string.settings_locale_title)
                .setSingleChoiceItems(
                        locales.map { it.displayName }.toTypedArray(),
                        currentIndex
                ) { dialog, which ->
                    val selectedLocale = locales[which].code
                    if (selectedLocale != corePreferences.appLocale) {
                        corePreferences.appLocale = selectedLocale
                        Log.i(
                                "[LocaleDialogHelper] Locale changed to: ${locales[which].displayName} ($selectedLocale)"
                        )
                        dialog.dismiss()
                        recreateCallback()
                    } else {
                        dialog.dismiss()
                    }
                }
                .setNegativeButton(android.R.string.cancel, null)
                .show()
    }
}
