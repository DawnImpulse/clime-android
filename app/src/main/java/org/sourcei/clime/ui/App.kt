/**
 * ISC License
 *
 * Copyright 2018-2019, Saksham (DawnImpulse)
 *
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted,
 * provided that the above copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT,
 * INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS,
 * WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE
 * OR PERFORMANCE OF THIS SOFTWARE.
 **/
package org.sourcei.clime.ui

import android.app.Application
import androidx.multidex.MultiDex
import androidx.preference.PreferenceManager
import com.crashlytics.android.Crashlytics
import com.google.android.libraries.places.api.Places
import com.google.firebase.analytics.FirebaseAnalytics
import io.fabric.sdk.android.Fabric
import org.sourcei.clime.BuildConfig
import org.sourcei.clime.utils.reusables.ANALYTICS
import org.sourcei.clime.utils.reusables.CRASHLYTICS
import org.sourcei.clime.utils.reusables.Prefs

/**
 * @info -
 *
 * @author - Saksham
 * @note Last Branch Update - master
 *
 * @note Created on 2019-09-21 by Saksham
 * @note Updates :
 */
class App : Application(){

    // on create
    override fun onCreate() {
        super.onCreate()

        MultiDex.install(this)
        Places.initialize(this, BuildConfig.PLACES_API_KEY)
        Prefs = PreferenceManager.getDefaultSharedPreferences(this)
        analytics()
    }

    // enabling crashlytics in release builds
    private fun analytics() {
        if (!BuildConfig.DEBUG) {
            if (Prefs.getBoolean(CRASHLYTICS, true))
                Fabric.with(this, Crashlytics())
            FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(Prefs.getBoolean(ANALYTICS, true))
        }
    }
}