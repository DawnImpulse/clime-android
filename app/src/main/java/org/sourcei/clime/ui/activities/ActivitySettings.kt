package org.sourcei.clime.ui.activities

import android.app.WallpaperManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.sourcei.clime.BuildConfig
import org.sourcei.clime.R
import org.sourcei.clime.utils.functions.putAny
import org.sourcei.clime.utils.functions.remove
import org.sourcei.clime.utils.functions.toast
import org.sourcei.clime.utils.handler.ImageHandler
import org.sourcei.clime.utils.reusables.*
import org.sourcei.clime.workers.AutoWallpaper
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @author Saksham
 *
 * @note Last Branch Update - develop
 * @note Created on 2019-09-21 by Saksham
 *
 * @note Updates :
 *  Saksham - 2019 09 22 - master - changing temperature units
 *  Saksham - 2019 09 23 - master - change wallpaper
 */
class ActivitySettings : AppCompatActivity() {

    // on create
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        setContentView(R.layout.activity_settings)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.frameLayout, MySettingsFragment())
                .commit()
    }

    // attaching preferences
    class MySettingsFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

        private lateinit var wallStatus: SwitchPreference
        private lateinit var wallWifi: SwitchPreference
        private lateinit var crashlytics: SwitchPreference
        private lateinit var analytics: SwitchPreference
        private lateinit var change: SwitchPreference
        private lateinit var units: Preference
        private lateinit var wallpaper: Preference

        // set preference layout
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)

            // get preferences
            wallStatus = findPreference("wallStatus")!!
            wallWifi = findPreference("wallWifi")!!
            crashlytics = findPreference("crashlytics")!!
            analytics = findPreference("analytics")!!
            units = findPreference("units")!!
            change = findPreference("change")!!
            wallpaper = findPreference("wallpaper")!!

            // setting application version
            findPreference<Preference>("version")!!.summary = "v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"

            // set units
            if (Prefs.getString(UNITS, METRIC) == IMPERIAL) {
                units.summary = "Imperial (°F) / Wind (Mph)"
            } else {
                units.summary = "Metric (°C) / Wind (Kmph)"
            }

            // listeners
            wallStatus.onPreferenceChangeListener = this
            wallWifi.onPreferenceChangeListener = this
            change.onPreferenceChangeListener = this

            units.onPreferenceClickListener = this
            wallpaper.onPreferenceClickListener = this

        }

        // preference change
        override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
            when (preference) {
                // wallpaper
                wallStatus -> {
                    newValue as Boolean
                    // if wallpaper enables
                    if (newValue) {
                        setWallpaper()
                    } else {
                        // wallpaper disabled
                        WorkManager.getInstance(context!!).cancelWorkById(UUID.fromString(Prefs.getString(AUTO_WALLPAPER, "")))
                        Prefs.remove(AUTO_WALLPAPER)
                    }
                }

                // wifi
                wallWifi -> {
                    Prefs.putAny("wallWifi", newValue as Boolean)
                    setWallpaper()
                }

                // change
                change -> {
                    WorkManager.getInstance(context!!).cancelWorkById(UUID.fromString(Prefs.getString(AUTO_WALLPAPER, "")))
                    Prefs.remove(AUTO_WALLPAPER)

                    newValue as Boolean
                    Prefs.putAny(CHANGE, newValue)
                    setWallpaper()
                }

            }

            return true
        }

        // on preference click
        override fun onPreferenceClick(preference: Preference?): Boolean {
            when (preference) {

                units -> {
                    val unit = Prefs.getString(UNITS, METRIC)
                    if (unit == METRIC) {
                        Prefs.putAny(UNITS, IMPERIAL)
                        units.summary = "Imperial (°F) / Wind (Mph)"
                    } else {
                        Prefs.putAny(UNITS, METRIC)
                        units.summary = "Metric (°C) / Wind (Kmph)"
                    }
                }

                wallpaper -> {
                    context!!.toast("changing wallpaper")
                    GlobalScope.launch {
                        ImageHandler.getBitmapWallpaper(context!!) {
                            if (it != null) {
                                WallpaperManager.getInstance(context).setBitmap(it)
                                (context as AppCompatActivity).runOnUiThread {
                                    context!!.toast("wallpaper changed successfully")
                                }
                            } else
                                (context as AppCompatActivity).runOnUiThread {
                                    context!!.toast("error changing wallpaper")
                                }
                        }
                    }
                }
            }


            return true
        }

        // remove & set wallpaper again
        private fun setWallpaper() {

            // remove wallpaper if exists
            if (Prefs.contains(AUTO_WALLPAPER)) {
                WorkManager.getInstance(context!!).cancelWorkById(UUID.fromString(Prefs.getString(AUTO_WALLPAPER, "")))
                Prefs.remove(AUTO_WALLPAPER)
            }

            // get wifi & time
            val isWifi = Prefs.getBoolean("wallWifi", true)
            val time = if (Prefs.getBoolean(CHANGE, true)) 60 else 30


            val builder = Constraints.Builder()
                    .setRequiredNetworkType(if (isWifi) NetworkType.UNMETERED else NetworkType.CONNECTED)
                    .build()

            // start wallpaper worker
            val uploadWorkRequest =
                    PeriodicWorkRequestBuilder<AutoWallpaper>(time.toLong(), TimeUnit.MINUTES)
                            .setConstraints(builder)
                            .build()

            WorkManager.getInstance(context!!).enqueue(uploadWorkRequest)
            Prefs.putAny(AUTO_WALLPAPER, uploadWorkRequest.id.toString())
        }
    }
}