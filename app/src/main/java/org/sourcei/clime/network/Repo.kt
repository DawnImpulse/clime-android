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
package org.sourcei.clime.network

import org.sourcei.clime.utils.handler.ErrorHandler
import org.sourcei.clime.utils.objects.ObjectWeather
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * @info -
 *
 * @author - Saksham
 * @note Last Branch Update - master
 *
 * @note Created on 2019-09-21 by Saksham
 * @note Updates :
 */
object Repo {
    val client = RetroApiClient.getClient().create(Source::class.java)

    // get weather from coordinates
    fun getWeatherCoordinates(lat: Double, long: Double, callback: (Any?, ObjectWeather?) -> Unit) {

        val call = client.coordinates(lat.toString(), long.toString())

        call.enqueue(object : Callback<ObjectWeather> {

            // response
            override fun onResponse(call: Call<ObjectWeather>, response: Response<ObjectWeather>) {
                if (response.isSuccessful) {
                    callback(null, response.body()!!)
                } else
                    callback(ErrorHandler.parseError(response), null)
            }

            // on failure
            override fun onFailure(call: Call<ObjectWeather>, t: Throwable) {
                callback(t.toString(), null)
            }
        })
    }

    // get weather from city
    fun getWeatherCity(city: String, callback: (Any?, ObjectWeather?) -> Unit) {

        val call = client.city(city)

        call.enqueue(object : Callback<ObjectWeather> {

            // response
            override fun onResponse(call: Call<ObjectWeather>, response: Response<ObjectWeather>) {
                if (response.isSuccessful) {
                    callback(null, response.body()!!)
                } else
                    callback(ErrorHandler.parseError(response), null)
            }

            // on failure
            override fun onFailure(call: Call<ObjectWeather>, t: Throwable) {
                callback(t.toString(), null)
            }
        })
    }
}