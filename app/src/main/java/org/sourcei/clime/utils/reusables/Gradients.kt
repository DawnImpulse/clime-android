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
package org.sourcei.clime.utils.reusables

import androidx.core.graphics.toColorInt

/**
 * @info -
 *
 * @author - Saksham
 * @note Last Branch Update - master
 *
 * @note Created on 2019-09-21 by Saksham
 * @note Updates :
 */
object Gradients {

    // get gradient for a weather condition
    fun getWeatherGradients(icon: String): List<Int> {

        val gradient = allGradients()[icon]
        return gradient?.map { it.toColorInt() } ?: allGradients()["01d"]!!.map { it.toColorInt() }
    }

    // all gradients
    fun allGradients(): Map<String, List<String>> {

        val map = mutableMapOf<String, List<String>>()


        map["01d"] = listOf("#2980B9", "#6DD5FA", "#FFFFFF")
        map["01n"] = listOf("#0F2027", "#203A43", "#2c5364")
        map["02d"] = listOf("#0575E6", "#021B79")
        map["02n"] = listOf("#000046", "#1CB5E0")
        map["03d"] = listOf("#0575E6", "#021B79")
        map["03n"] = listOf("#000046", "#1CB5E0")
        map["04d"] = listOf("#0575E6", "#021B79")
        map["04n"] = listOf("#000046", "#1CB5E0")
        map["09d"] = listOf("#00c6ff", "#0072ff")
        map["09n"] = listOf("#360033", "#0b8793")
        map["10d"] = listOf("#00c6ff", "#0072ff")
        map["10n"] = listOf("#360033", "#0b8793")
        map["11d"] = listOf("#5f2c82", "#49a09d")
        map["11n"] = listOf("#780206", "#061161")
        map["13d"] = listOf("#2980B9", "#6DD5FA", "#FFFFFF")
        map["13n"] = listOf("#005AA7", "#FFFDE4")
        map["50d"] = listOf("#BA5370", "#F4E2D8")
        map["50n"] = listOf("#ED4264", "#FFEDBC")

        return map
    }

    // get a random gradient
    fun getRandomGradient(): IntArray {
        val map = allGradients()
        val keys = map.keys.toList()

        return map[keys.random()]!!.map { it.toColorInt() }.toIntArray()
    }

}