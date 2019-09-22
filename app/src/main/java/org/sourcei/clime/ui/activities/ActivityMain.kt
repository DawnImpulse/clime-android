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
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
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
 */
@SuppressLint("SetTextI18n")
class ActivityMain : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {
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
        settings.setOnClickListener { openActivity(ActivitySettings::class.java) }
    }

    // on resume
    override fun onResume() {
        super.onResume()

        if (Prefs.contains(NEWLATLON)) {
            val new = Gson().fromJson(NEWLATLON, LatLng::class.java)
            if (::location.isInitialized) {
                if (location.latitude != new.latitude || location.longitude != new.longitude) {
                    location = new
                    setData()
                }
            } else {
                location = new
                setData()
            }
        }
    }

    // set activity result (for location)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                val location = Gson().fromJson(data!!.data.toString(), LatLng::class.java)
                this.location = location
                setData()
            } else {
                toast("unable to get location, kindly provide your location in Settings", Toast.LENGTH_LONG)
                swipe.isRefreshing = false
            }
        }
    }

    // swipe refresh listener
    override fun onRefresh() {
        swipe.isRefreshing = true
        dataHandling(true)
    }

    // handle stored data / ask for location & data
    private fun dataHandling(current: Boolean = false) {
        Permissions.askAccessCoarseLocationPermission(this) { e, r ->
            e?.let {
                toast("permission denied, kindly provide location permission to get current location for weather data. Will only be used once (only now)", Toast.LENGTH_LONG)
                swipe.isRefreshing = false
            }
            r?.let {

                fun handling(latLng: LatLng?) {
                    if (latLng != null) {
                        location = latLng
                        logd(location.toString())
                        setData()
                    } else
                        startActivityForResult(Intent(this, ActivityPlace::class.java), 1)
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
                val p = "${it.name}, ${it.sys.country}"
                val t = "${F.toCelsius(it.main.temp.toFloat())}°C"
                val w = it.weather[0].description.toCamelCase()


                place.text = p
                temperature.text = t
                weather.text = w

                Prefs.putAny(PLACE, p)
                Prefs.putAny(WEATHER, w)
                Prefs.putAny(TEMPERATURE, t)
                Prefs.putAny(LATLON, Gson().toJson(location))
                Prefs.putAny(ICON, it.weather[0].icon)

                gradient.setGradient(Gradients.getWeatherGradients(it.weather[0].icon).toIntArray(), 0, Angles.random().toFloat())
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
        val p = Prefs.getString(PLACE, "")!!
        val t = Prefs.getString(TEMPERATURE, "")!!
        val w = Prefs.getString(WEATHER, "")!!
        val i = Prefs.getString(ICON, "")!!
        location = Gson().fromJson(Prefs.getString(LATLON, ""), LatLng::class.java)

        place.text = p
        temperature.text = t
        weather.text = w

        gradient.setGradient(Gradients.getWeatherGradients(i).toIntArray(), 0, Angles.random().toFloat())
        if (File(cacheDir, "homescreen.jpg").exists()) {
            bitmap = StorageHandler.getBitmapFromFile(File(cacheDir, "homescreen.jpg"))
            image.setImageBitmap(bitmap)
            image.show()
            mask.show()
        }
    }
}