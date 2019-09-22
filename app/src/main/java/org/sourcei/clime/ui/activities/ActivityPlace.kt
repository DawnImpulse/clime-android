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

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_place.*
import org.sourcei.android.permissions.Permissions
import org.sourcei.clime.R
import org.sourcei.clime.utils.functions.*
import org.sourcei.clime.utils.reusables.Angles
import org.sourcei.clime.utils.reusables.Gradients
import org.sourcei.clime.utils.reusables.NEWLATLON
import org.sourcei.clime.utils.reusables.Prefs

/**
 * @info -
 *
 * @author - Saksham
 * @note Last Branch Update - master
 *
 * @note Created on 2019-09-21 by Saksham
 * @note Updates :
 *  Saksham - 2019 09 22 - master - save location in prefs
 */
class ActivityPlace : AppCompatActivity(), PlaceSelectionListener, View.OnClickListener {
    private lateinit var placesClient: PlacesClient
    private lateinit var autocompleteFragment: AutocompleteSupportFragment

    // on create
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place)

        // set background gradient
        placeBg.setGradient(Gradients.getRandomGradient(), 0, Angles.random().toFloat())

        // init places autocomplete
        placesClient = Places.createClient(this)
        autocompleteFragment = searchPlace as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME))

        autocompleteFragment.setOnPlaceSelectedListener(this)
        gps.setOnClickListener(this)
    }

    // on click handling
    override fun onClick(v: View) {
        Permissions.askAccessFineLocationPermission(this) { e, r ->
            e?.let {
                toast("permission denied, kindly provide location permission to get current location for weather data. Will only be used once (only now)", Toast.LENGTH_LONG)
            }
            r?.let {
                F.getLocation(this) {
                    if (it == null)
                        toast("location not available, enable your device gps/internet then try again. Alternatively you can search for your city", Toast.LENGTH_LONG)
                    else {
                        Prefs.putAny(NEWLATLON, Gson().toJson(it))
                        finish()
                    }
                }
            }
        }
    }

    // on place selected
    override fun onPlaceSelected(place: Place) {
        Prefs.putAny(NEWLATLON, Gson().toJson(place.latLng))
        finish()
    }

    // place selection error
    override fun onError(status: Status) {
        loge("An error occurred: $status")
        toast("error occurred while selecting place. Please try again")
    }
}