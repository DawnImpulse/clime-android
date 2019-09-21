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

import androidx.appcompat.app.AppCompatActivity
import org.sourcei.clime.utils.objects.ObjectWeather
import org.sourcei.clime.utils.reusables.Lifecycle

/**
 * @info -
 *
 * @author - Saksham
 * @note Last Branch Update - master
 *
 * @note Created on 2019-09-21 by Saksham
 * @note Updates :
 */
class Model(private val activity: AppCompatActivity) {

    // get weather from coordinates
    fun getWeatherCoordinates(lat: String, long: String, callback: (Any?, ObjectWeather?) -> Unit) {
        Lifecycle.onStart(activity) {
            Repo.getWeatherCoordinates(lat, long, callback)
        }
    }

    // get weather city
    fun getWeatherCity(lat: String, long: String, callback: (Any?, ObjectWeather?) -> Unit) {
        Lifecycle.onStart(activity) {
            Repo.getWeatherCoordinates(lat, long, callback)
        }
    }
}