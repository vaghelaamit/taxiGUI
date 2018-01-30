package com.easypay.easypaypos.network

import android.content.Context

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley

/**
 * Created by ADMIN on 7/18/2016.
 */
class VolleyRequestQueue(context: Context) {
    private var mRequestQueue: RequestQueue? = null
    var imageLoader: ImageLoader? = null

    val requestQueue: RequestQueue
        get() {
            if (mRequestQueue == null) {
                mRequestQueue = Volley.newRequestQueue(mCtx!!.applicationContext)
            }
            return mRequestQueue!!
        }

    init {
        mCtx = context
        mRequestQueue = requestQueue

        imageLoader = ImageLoader(mRequestQueue, LruBitmapCache(
                LruBitmapCache.getCacheSize(context)))
    }

    fun <T> addToRequestQueue(req: Request<T>) {
        requestQueue.add(req)
    }

    companion object {

        private var mInstance: VolleyRequestQueue? = null
        private var mCtx: Context? = null

        @Synchronized
        fun getInstance(context: Context): VolleyRequestQueue {
            if (mInstance == null) {
                mInstance = VolleyRequestQueue(context)
            }
            return mInstance!!
        }
    }

}
