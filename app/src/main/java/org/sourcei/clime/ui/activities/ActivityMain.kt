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
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import org.sourcei.clime.R
import org.sourcei.clime.network.Model
import org.sourcei.clime.utils.functions.F
import org.sourcei.clime.utils.functions.loge
import org.sourcei.clime.utils.functions.toCamelCase
import org.sourcei.clime.utils.functions.toast
import org.sourcei.clime.utils.reusables.LOCATION
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
class ActivityMain : AppCompatActivity() {
    private lateinit var location: LatLng
    private lateinit var model: Model

    // on create
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        model = Model(this)

        if (!Prefs.contains(LOCATION)) {
            F.getLocation(this) {
                if (it != null) {
                    location = it
                    setData()
                } else
                    startActivityForResult(Intent(this, ActivityPlace::class.java), 1)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                val location = Gson().fromJson(data!!.data.toString(), LatLng::class.java)
                this.location = location
                setData()
            } else
                toast("unable to get location, kindly provide your location in Settings", Toast.LENGTH_LONG)
        }
    }

    // set new data
    @SuppressLint("SetTextI18n")
    fun setData() {
        model.getWeatherCoordinates(location.latitude, location.longitude) { e, r ->
            e?.let {
                toast(it.toString())
                loge(it)
            }
            r?.let {
                place.text = "${it.name}, ${it.sys.country}"
                temperature.text = "${F.toCelsius(it.main.temp.toFloat())}°C"
                weather.text = it.weather[0].description.toCamelCase()
            }
        }
    }
}