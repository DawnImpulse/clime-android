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
package org.sourcei.clime.workers

import android.app.WallpaperManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.concurrent.futures.CallbackToFutureAdapter
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.google.android.gms.maps.model.LatLng
import com.google.common.util.concurrent.ListenableFuture
import com.google.gson.Gson
import org.sourcei.clime.network.Repo
import org.sourcei.clime.utils.functions.*
import org.sourcei.clime.utils.handler.ImageHandler
import org.sourcei.clime.utils.reusables.*

/**
 * @info -
 *
 * @author - Saksham
 * @note Last Branch Update - master
 *
 * @note Created on 2019-09-21 by Saksham
 * @note Updates :
 *  Saksham - 2019 09 21 - master - get current weather
 */
class AutoWallpaper(private val appContext: Context, workerParams: WorkerParameters) : ListenableWorker(appContext, workerParams) {
    private lateinit var wallpaperManager: WallpaperManager
    private lateinit var handler: Handler

    // start work
    override fun startWork(): ListenableFuture<Result> {
        return CallbackToFutureAdapter.getFuture { completer ->

            wallpaperManager = WallpaperManager.getInstance(appContext)
            handler = Handler(Looper.getMainLooper())
            getWeather {
                if (it)
                    completer.set(Result.success())
                else
                    completer.set(Result.retry())
            }
        }
    }

    // get weather for the place
    private fun getWeather(callback: (Boolean) -> Unit) {
        if (Prefs.contains(WEATHER)) {
            val latlon = Gson().fromJson(Prefs.getString(LATLON, ""), LatLng::class.java)
            Repo.getWeatherCoordinates(latlon.latitude, latlon.longitude) { e, r ->
                e?.let {
                    loge(it)
                    callback(false)
                }
                r?.let {

                    val t = "${F.toCelsius(it.main.temp.toFloat())}°C"
                    val w = it.weather[0].description.toCamelCase()

                    Prefs.putAny(WEATHER, w)
                    Prefs.putAny(TEMPERATURE, t)

                    // only change wallpaper if there is change in condition (icon change)
                    if (Prefs.getString(ICON, "") == it.weather[0].icon && Prefs.contains(WALL_CHANGED))
                        callback(true)
                    else {
                        Prefs.putAny(ICON, it.weather[0].icon)
                        Prefs.putAny(WALL_CHANGED, true)
                        wallpaperChange(callback)
                    }
                }
            }
        } else
            callback(false)
    }

    // get image & change homescreen wallpaper
    private fun wallpaperChange(callback: (Boolean) -> Unit) {

        ImageHandler.getBitmapWallpaper(appContext) {
            if (it != null) {
                wallpaperManager.setBitmap(it)
                handler.post {
                    appContext.toast("wallpaper changed")
                }
                callback(true)
            } else
                callback(false)
        }
    }


}
