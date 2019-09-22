/**
 * ISC License
 *
 * Copyright 2019, Saksham (DawnImpulse)
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
package org.sourcei.clime.utils.functions

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Point
import android.location.Geocoder
import android.net.ConnectivityManager
import android.os.Build
import android.os.Looper
import android.view.WindowManager
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.sourcei.clime.utils.handler.DialogHandler
import kotlin.random.Random


/**
 * @info -
 *
 * @author - Saksham
 * @note Last Branch Update - master
 *
 * @note Created on 2019-09-21 by Saksham
 * @note Updates :
 */
object F {


    // convert dp - px
    fun dpToPx(dp: Int, context: Context): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }

    // get display height
    fun displayDimensions(context: Context): Point {
        val point = Point()
        val mWindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = mWindowManager.defaultDisplay
        display.getSize(point) //The point now has display dimens
        return point
    }

    // generate shortid
    fun shortid(): String {
        val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..10)
                .map { Random.nextInt(0, charPool.size) }
                .map(charPool::get)
                .joinToString("")
    }

    // connection listener
    fun isConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cm.isDefaultNetworkActive
        } else {
            cm.activeNetworkInfo?.isConnected == true
        }
    }

    // verify two bitmaps
    fun compareBitmaps(b1: Bitmap?, b2: Bitmap?, callback: (Boolean) -> Unit) {

        if (b1 == null || b2 == null) {
            callback(false)
        } else
            GlobalScope.launch {
                try {
                    callback(b1.sameAs(b2)) // callback with compare
                } catch (e: Exception) {
                    //Crashlytics.logException(e)
                    e.printStackTrace()
                    callback(false)
                }
            }
    }

    // get location
    fun getLocation(context: Context, callback: (LatLng?) -> Unit) {
        var update = false

        // location callback
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(location: LocationResult?) {
                super.onLocationResult(location)

                DialogHandler.dismiss()
                if (update) {
                    if (location != null) {
                        callback(
                                LatLng(
                                        location.lastLocation.latitude,
                                        location.lastLocation.longitude
                                )
                        )
                    } else
                        callback(null)
                }

                update = false
            }
        }

        // start location listener
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            if (it != null) {
                callback(LatLng(it.latitude, it.longitude))
            } else {
                DialogHandler.progress(context) {
                    update = false
                    callback(null)
                }
                fusedLocationProviderClient.requestLocationUpdates(LocationRequest().setNumUpdates(1), locationCallback, Looper.getMainLooper())
            }
        }
    }

    // get current location
    fun getCurrentLocation(context: Context, callback: (LatLng?) -> Unit) {
        var update = true

        // location callback
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(location: LocationResult?) {
                super.onLocationResult(location)

                if (update) {
                    if (location != null) {
                        callback(LatLng(location.lastLocation.latitude, location.lastLocation.longitude))
                    } else
                        callback(null)
                }

                update = false
            }
        }

        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationProviderClient.requestLocationUpdates(LocationRequest().setNumUpdates(1), locationCallback, Looper.getMainLooper())
    }

    // convert K -> C
    fun toCelsius(temp: Float): Float {
        return (temp - 273.15).round(1).toFloat()
    }

    // convert K -> F
    fun toFarenheit(temp: Float): Float {
        return ((temp - 273) * 1.8 + 32).round(1).toFloat()
    }

    // convert speed to miles
    fun toMiles(speed: String): String {
        val s = speed.toDouble()
        return (s / 1.609).round(1).toString()
    }

    // search term for wallpaper
    fun getSearchTerm(icon: String): String {
        return when (icon) {
            "01d" -> "clear sky"
            "01n" -> "night sky"
            "02d" -> "clouds"
            "02n" -> "night clouds"
            "03d" -> "clouds"
            "03n" -> "night clouds"
            "04d" -> "clouds"
            "04n" -> "night clouds"
            "09d" -> "rain"
            "09n" -> "rain night"
            "10d" -> "rain"
            "10n" -> "rain night"
            "11d" -> "thunderstorm"
            "11n" -> "thunderstorm"
            "13d" -> "snow day"
            "13n" -> "snow night"
            "50d" -> "mist"
            else -> "mist night"
        }
    }

    // get place from coordinates
    @SuppressLint("DefaultLocale")
    fun getPlaceFromLatLon(context: Context, latLng: LatLng, callback: (String?) -> Unit) {

        DialogHandler.progressNonCancellable(context)
        GlobalScope.launch {
            try {
                val geocoder = Geocoder(context)
                val address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

                val locality = address[0].locality
                val country = address[0].countryName
                val code = address[0].countryCode

                if (locality != null && locality != "null")
                    callback("${locality}, $code")
                else
                    callback(country)

                DialogHandler.dismiss()

            } catch (e: Exception) {
                e.printStackTrace()
                callback(null)
            }
        }
    }

    @SuppressLint("DefaultLocale")
    fun getPlaceFromLatLonSync(context: Context, latLng: LatLng): String {

        return try {
            val geocoder = Geocoder(context)
            val address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

            val locality = address[0].locality
            val country = address[0].countryName
            val code = address[0].countryCode

            return if (locality != null && locality != "null")
                "${locality}, $code"
            else
                country

        } catch (e: Exception) {
            e.printStackTrace()
            "Somewhere on Earth"
        }
    }

}