package com.easypay.easypaypos.network

import android.content.Context

import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest

/**
 * Created by ADMIN on 7/18/2016.
 */
object VolleyStringRequest {

    fun request(context: Context, method: Int, url: String, onResponse: OnStringResponse) {
        val stringRequest = StringRequest(method, url,
                Response.Listener { response -> onResponse.responseReceived(response) }, Response.ErrorListener { error -> onResponse.errorReceived(error.networkResponse.statusCode, error.message!!) })

        VolleyRequestQueue.getInstance(context).addToRequestQueue(stringRequest)
    }

    interface OnStringResponse {
        fun responseReceived(resonse: String)

        fun errorReceived(code: Int, message: String)
    }
}
