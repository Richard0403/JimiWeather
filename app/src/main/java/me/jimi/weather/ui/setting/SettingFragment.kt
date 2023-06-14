package me.jimi.weather.ui.setting

import android.content.Intent
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import me.jimi.weather.R
import me.jimi.weather.ui.about.AboutActivity


/** 设置页面 **/
class SettingFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.app_setting, rootKey)
        val aboutPreferences = findPreference<Preference>("about")
        aboutPreferences?.intent = Intent(requireContext(), AboutActivity::class.java)
    }
}