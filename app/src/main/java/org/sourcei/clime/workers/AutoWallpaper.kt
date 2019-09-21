/**
 * ISC License
 *
 * Copyright 2018-2019, Saksham (DawnImpulse)
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
package org.sourcei.clime.workers

import android.app.WallpaperManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.concurrent.futures.CallbackToFutureAdapter
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.google.common.util.concurrent.ListenableFuture
import org.sourcei.clime.utils.handler.ImageHandler

/**
 * @info -
 *
 * @author - Saksham
 * @note Last Branch Update - master
 *
 * @note Created on 2019-09-21 by Saksham
 * @note Updates :
 */
class AutoWallpaper(private val appContext: Context, workerParams: WorkerParameters) : ListenableWorker(appContext, workerParams) {
    private lateinit var wallpaperManager: WallpaperManager
    private lateinit var handler: Handler

    // ----------------
    //   start work
    // ----------------
    override fun startWork(): ListenableFuture<Result> {
        return CallbackToFutureAdapter.getFuture { completer ->

            wallpaperManager = WallpaperManager.getInstance(appContext)
            handler = Handler(Looper.getMainLooper())
            wallpaperChange {
                if (it)
                    completer.set(Result.success())
                else
                    completer.set(Result.retry())
            }
        }
    }

    // -----------------------------
    //   wallpaper change handling
    // -----------------------------
    private fun wallpaperChange(callback: (Boolean) -> Unit) {
        ImageHandler.getBitmapWallpaper(appContext) {
            if (it != null) {
                wallpaperManager.setBitmap(it)
                callback(true)
            } else
                callback(false)
        }
    }

}
