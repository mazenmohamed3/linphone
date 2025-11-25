package org.linphone.utils

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import org.linphone.LinphoneApplication.Companion.corePreferences
import org.linphone.R
import org.linphone.core.tools.Log

object LocaleSpinnerHelper {

        data class LocaleOption(val code: String, val displayName: String)

        fun setupLocaleSpinner(context: Context, spinner: Spinner, recreateCallback: () -> Unit) {
                val locales = listOf(
                        LocaleOption("", context.getString(R.string.locale_system_default)),
                        LocaleOption(LocaleHelper.LOCALE_ENGLISH, context.getString(R.string.locale_english)),
                        LocaleOption(LocaleHelper.LOCALE_FRENCH, context.getString(R.string.locale_french)),
                        LocaleOption(LocaleHelper.LOCALE_ARABIC, context.getString(R.string.locale_arabic))
                )

                val adapter = ArrayAdapter(
                        context,
                        android.R.layout.simple_spinner_item,
                        locales.map { it.displayName }
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                // 1. Clear listener during setup to prevent accidental triggering
                spinner.onItemSelectedListener = null
                spinner.adapter = adapter

                // 2. Set the current selection visually
                val currentLocaleCode = corePreferences.appLocale
                val currentIndex = locales.indexOfFirst { it.code == currentLocaleCode }

                if (currentIndex >= 0) {
                        spinner.setSelection(currentIndex, false)
                }

                // 3. Attach the listener safely
                spinner.post {
                        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                        val selectedLocale = locales[position].code

                                        // Only recreate if the value is ACTUALLY different
                                        if (selectedLocale != corePreferences.appLocale) {
                                                Log.i("[LocaleSpinnerHelper] Changing locale to [$selectedLocale]")
                                                corePreferences.appLocale = selectedLocale
                                                recreateCallback()
                                        }
                                }

                                override fun onNothingSelected(parent: AdapterView<*>?) {}
                        }
                }
        }
}
