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
package org.sourcei.clime.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import org.sourcei.android.permissions.Permissions
import org.sourcei.clime.R
import org.sourcei.clime.network.Model
import org.sourcei.clime.utils.functions.*
import org.sourcei.clime.utils.handler.ImageHandler
import org.sourcei.clime.utils.handler.StorageHandler
import org.sourcei.clime.utils.reusables.*
import java.io.File

/**
 * @info -
 *
 * @author - Saksham
 * @note Last Branch Update - master
 *
 * @note Created on 2019-09-21 by Saksham
 * @note Updates :
 *  Saksham - 2019 09 22 - master - user provided location handling
 */
@SuppressLint("SetTextI18n")
class ActivityMain : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener,View.OnClickListener {
    private lateinit var location: LatLng
    private lateinit var model: Model
    private var bitmap: Bitmap? = null

    // on create
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        model = Model(this)
        swipe.isRefreshing = true

        dataHandling()
        if (Prefs.contains(WEATHER))
            setAvailableData()

        swipe.setOnRefreshListener(this)
        settings.setOnClickListener(this)
        gps.setOnClickListener(this)
    }

    // on click listener
    override fun onClick(v: View) {
        when(v.id){
            settings.id -> openActivity(ActivitySettings::class.java)
            gps.id -> openActivity(ActivityPlace::class.java)
        }
    }

    // on resume
    override fun onResume() {
        super.onResume()

        if (Prefs.contains(NEWLATLON)) {
            val new = Gson().fromJson(Prefs.getString(NEWLATLON, ""), LatLng::class.java)
            if (::location.isInitialized) {
                if (location.latitude != new.latitude || location.longitude != new.longitude) {
                    swipe.isRefreshing = true
                    location = new
                    setData()
                    Prefs.remove(WALL_CHANGED)
                }
            } else {
                swipe.isRefreshing = true
                location = new
                setData()
                Prefs.remove(WALL_CHANGED)
            }
        }
    }

    // remove newlatlon
    override fun onDestroy() {
        super.onDestroy()

        Prefs.remove(NEWLATLON)
    }

    // swipe refresh listener
    override fun onRefresh() {
        swipe.isRefreshing = true
        dataHandling(true)
    }

    // handle stored data / ask for location & data
    private fun dataHandling(current: Boolean = false) {

        // if user location is present then use it
        if (Prefs.contains(USER_LOCATION)) {
            location = Gson().fromJson(Prefs.getString(USER_LOCATION, ""), LatLng::class.java)
            setData()
        } else
        // ask for location permission
            Permissions.askAccessCoarseLocationPermission(this) { e, r ->
                e?.let {
                    // start place activity
                    swipe.isRefreshing = false
                    startActivityForResult(Intent(this, ActivityPlace::class.java), 1)
                }
                r?.let {
                    // if location permission available then get user location
                    Prefs.remove(USER_LOCATION)

                    // handle location
                    fun handling(latLng: LatLng?) {
                        if (latLng != null) {
                            location = latLng
                            // get place name
                            F.getPlaceFromLatLon(this, latLng) {
                                runOnUiThread {
                                    if (it == null)
                                        toast("location changed,unable to find name of place due to no internet")
                                    Prefs.putAny(PLACE, it ?: "Somewhere on Earth")
                                    setData()
                                }
                            }
                        } else {
                            swipe.isRefreshing = false
                            startActivityForResult(Intent(this, ActivityPlace::class.java), 1)
                        }
                    }

                    if (current)
                        F.getCurrentLocation(this) { handling(it) }
                    else
                        F.getLocation(this) { handling(it) }
                }
            }
    }

    // set new data
    private fun setData() {
        model.getWeatherCoordinates(location.latitude, location.longitude) { e, r ->
            e?.let {
                toast(it.toString())
                loge(it)
                swipe.isRefreshing = false
            }
            r?.let {
                details.show()
                val p = "${it.name}, ${it.sys.country}"
                val t = "${F.toCelsius(it.main.temp.toFloat())}°C"
                val w = it.weather[0].description.toCamelCase()
                val h = it.main.humidity
                val c = it.clouds.all
                val wi = it.wind.speed.toDouble().round(1).toString()


                place.text = Prefs.getString(PLACE, p)
                temperature.text = t
                weather.text = w
                humid.text = "$h%  humidity"
                clouds.text = "$c%  clouds"
                wind.text = "${wi}    winds (k/h)"

                Prefs.putAny(WEATHER, w)
                Prefs.putAny(TEMPERATURE, t)
                Prefs.putAny(HUMIDITY, h)
                Prefs.putAny(CLOUDS, c)
                Prefs.putAny(WIND, wi)
                Prefs.putAny(LATLON, Gson().toJson(location))
                Prefs.putAny(ICON, it.weather[0].icon)

                gradient.setGradient(
                        Gradients.getWeatherGradients(it.weather[0].icon).toIntArray(),
                        0,
                        Angles.random().toFloat()
                )
                swipe.isRefreshing = false

                // set image
                ImageHandler.getBitmap(bitmap, this) {
                    if (it != null) {
                        bitmap = it
                        image.setImageBitmap(it)
                        image.show()
                        mask.show()

                        // save bitmap in temp directory
                        StorageHandler.storeBitmapInFile(it, File(cacheDir, "homescreen.jpg"))
                    }
                }
            }
        }
    }

    // set stored data
    private fun setAvailableData() {

        details.show()

        val p = Prefs.getString(PLACE, "Somewhere on Earth")!!
        val t = Prefs.getString(TEMPERATURE, "")!!
        val w = Prefs.getString(WEATHER, "")!!
        val i = Prefs.getString(ICON, "")!!
        val h = Prefs.getString(HUMIDITY, "")!!
        val c = Prefs.getString(CLOUDS, "")!!
        val wi = Prefs.getString(WIND, "")!!

        location = Gson().fromJson(Prefs.getString(LATLON, ""), LatLng::class.java)

        place.text = p
        temperature.text = t
        weather.text = w
        humid.text = "$h%  humidity"
        clouds.text = "$c%  cloud Cover"
        wind.text = "${wi}    winds (k/h)"

        gradient.setGradient(Gradients.getWeatherGradients(i).toIntArray(), 0, Angles.random().toFloat())

        if (File(cacheDir, "homescreen.jpg").exists()) {
            bitmap = StorageHandler.getBitmapFromFile(File(cacheDir, "homescreen.jpg"))
            image.setImageBitmap(bitmap)
            image.show()
            mask.show()
        }
    }
}