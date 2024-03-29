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

import org.sourcei.clime.BuildConfig
import org.sourcei.clime.utils.objects.ObjectWeather
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @info -
 *
 * @author - Saksham
 * @note Last Branch Update - master
 *
 * @note Created on 2019-09-21 by Saksham
 * @note Updates :
 */
interface Source {


    @GET("data/2.5/weather")
    fun coordinates(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("APPID") appId: String = BuildConfig.WEATHER_API_KEY
    ): Call<ObjectWeather>



    @GET("data/2.5/weather")
    fun city(
        @Query("q") city: String,
        @Query("APPID") appId: String = BuildConfig.WEATHER_API_KEY
    ): Call<ObjectWeather>
}