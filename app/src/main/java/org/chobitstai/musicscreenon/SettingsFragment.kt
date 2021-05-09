package org.chobitstai.musicscreenon

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import androidx.preference.SwitchPreferenceCompat
import kotlin.math.log

class SettingsFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {
    var serviceEnable = false
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        val pref:Preference? = findPreference("screen_on")
        pref?.onPreferenceChangeListener = this
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        serviceEnable = newValue as Boolean
        if (serviceEnable) {
            //Toast.makeText(activity, "enable", Toast.LENGTH_SHORT).show()
            if (Settings.System.canWrite(activity)) {
                val intent = Intent(activity, ScreenOnService::class.java)
                activity?.startForegroundService(intent)
            } else {
                val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                startActivity(intent)
                return false
            }
        } else {
            //Toast.makeText(activity, "disable", Toast.LENGTH_SHORT).show()
            val intent = Intent(activity, ScreenOnService::class.java)
            activity?.stopService(intent)
        }
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("service_enabled", serviceEnable)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //val enabled = savedInstanceState?.getBoolean("SERVICE_ENABLE", false)
        //val sp = view?.findViewById(R.id.my_service_enable) as SwitchPreference
        if (savedInstanceState == null) {
            activity?.intent?.getBooleanExtra("service_enabled", false)?.let { findPreference<SwitchPreferenceCompat>("screen_on")?.setChecked(it) }
        } else {
            savedInstanceState.getBoolean("service_enabled", false).let { findPreference<SwitchPreferenceCompat>("screen_on")?.setChecked(it) }
        }
        //savedInstanceState?.getBoolean("SERVICE_ENABLE", false)?.let { findPreference<SwitchPreferenceCompat>("screen_on")?.setChecked(it) }
        //val ck = activity?.intent?.getBooleanExtra("service_enabled",false)
        //Log.d("MyApp", "onCreate $ck")
    }
}