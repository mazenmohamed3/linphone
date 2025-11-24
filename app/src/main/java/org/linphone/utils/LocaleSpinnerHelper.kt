package org.linphone.utils

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import org.linphone.LinphoneApplication.Companion.corePreferences
import org.linphone.R

/** Utility class to set up and handle locale spinners across the app */
object LocaleSpinnerHelper {

    data class LocaleOption(val code: String, val displayName: String)

    /**
     * Sets up a locale spinner with current selection and change listener
     * @param context Android context
     * @param spinner The Spinner view to set up
     * @param recreateCallback Callback to recreate activity/fragment when locale changes
     */
    fun setupLocaleSpinner(context: Context, spinner: Spinner, recreateCallback: () -> Unit) {
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

        val adapter =
                ArrayAdapter(
                        context,
                        android.R.layout.simple_spinner_item,
                        locales.map { it.displayName }
                )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Set current selection
        val currentLocale = corePreferences.appLocale
        val currentIndex = locales.indexOfFirst { it.code == currentLocale }
        if (currentIndex >= 0) {
            spinner.setSelection(currentIndex, false)
        }

        // Handle selection changes
        var isFirstSelection = true
        spinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                    ) {
                        // Skip the initial selection that happens when setting up the spinner
                        if (isFirstSelection) {
                            isFirstSelection = false
                            return
                        }

                        val selectedLocale = locales[position].code
                        if (selectedLocale != corePreferences.appLocale) {
                            corePreferences.appLocale = selectedLocale
                            Log.i(
                                    "[LocaleSpinnerHelper] Locale changed to: ${locales[position].displayName} ($selectedLocale)"
                            )
                            // Recreate to apply new locale
                            recreateCallback()
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // Do nothing
                    }
                }
    }
}
