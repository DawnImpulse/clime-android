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
import android.content.Context
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import kotlinx.android.synthetic.main.activity_place.*
import org.sourcei.android.permissions.Permissions
import org.sourcei.clime.R
import org.sourcei.clime.utils.functions.logd
import org.sourcei.clime.utils.functions.loge
import org.sourcei.clime.utils.functions.toast

/**
 * @info -
 *
 * @author - Saksham
 * @note Last Branch Update - master
 *
 * @note Created on 2019-09-21 by Saksham
 * @note Updates :
 */
class ActivityPlace : AppCompatActivity(), PlaceSelectionListener, View.OnClickListener {
    private lateinit var placesClient: PlacesClient
    private lateinit var autocompleteFragment: AutocompleteSupportFragment
    private lateinit var locationManager: LocationManager
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    // on create
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place)

        placesClient = Places.createClient(this)
        autocompleteFragment = searchPlace as AutocompleteSupportFragment
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
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
                getLocation()
            }
        }
    }

    // on place selected
    override fun onPlaceSelected(place: Place) {
        logd("Place: " + place.name + ", " + place.latLng)
    }

    // place selection error
    override fun onError(status: Status) {
        loge("An error occurred: $status")
        toast("error occurred while selecting place. Please try again")
    }



    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(location: LocationResult?) {
            super.onLocationResult(location)

            if (location != null) {
                logd("here 1: " + location.lastLocation.latitude)
            } else
                toast("location not available, enable your device gps/internet then try again. Alternatively you can search for your city", Toast.LENGTH_LONG)
        }
    }


    // get user location
    @SuppressLint("MissingPermission")
    private fun getLocation() {


        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            if (it != null) {
                logd("here : " + it.latitude)
            } else {
                fusedLocationProviderClient.requestLocationUpdates(LocationRequest().setNumUpdates(1), locationCallback, Looper.getMainLooper())
            }
        }


        /*for fine location
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10f, LocationListenerCallback)

        } else if (F.isConnected(this)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10f, LocationListenerCallback)

        } else
            toast("kindly enable either GPS / Internet to get location")

        for opening gps settings activity
        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))*/


    }
}