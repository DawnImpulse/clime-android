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
package org.sourcei.clime.utils.handler

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import org.sourcei.clime.R

/**
 * @info -
 *
 * @author - Saksham
 * @note Last Branch Update - master
 *
 * @note Created on 2019-09-21 by Saksham
 * @note Updates :
 *  Saksham - 2019 09 21 - master - non cancellable progress
 */
object DialogHandler {
    private lateinit var alertDialog: AlertDialog

    // simple ok dialog
    fun simpleOk(context: Context, title: String, message: String, positive: DialogInterface.OnClickListener) {
        val builder = AlertDialog.Builder(context)
        builder
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", positive)
                .setNegativeButton("CANCEL") { dialog, _ ->
                    dialog.dismiss()
                }
                .setCancelable(false)

        alertDialog = builder.create()
        alertDialog.show()
    }

    // indeterminate progress bar
    fun progress(context: Context, callback: () -> Unit) {
        val builder = AlertDialog.Builder(context)
        builder.setNegativeButton("CANCEL") { dialog, _ ->
            dialog.dismiss()
            callback()
        }

        alertDialog = builder.create()
        alertDialog.setView(LayoutInflater.from(context).inflate(R.layout.inflator_progress_dialog, null))
        alertDialog.show()
    }

    // indeterminate progress bar
    fun progressNonCancellable(context: Context) {
        val builder = AlertDialog.Builder(context)
       builder.setCancelable(false)

        alertDialog = builder.create()
        alertDialog.setView(LayoutInflater.from(context).inflate(R.layout.inflator_progress_dialog, null))
        alertDialog.show()
    }

    // dismiss
    fun dismiss() {
        try {
            alertDialog.dismiss()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}