package com.easypay.easypaypos.network

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.easypay.easypaypos.common.AppConstants
import com.easypay.easypaypos.common.AppLog
import com.easypay.easypaypos.common.DeviceInfo
import com.easypay.easypaypos.common.Utils
import com.easypay.easypaypos.exceptions.InternetNotAvailableException
import com.easypay.easypaypos.exceptions.UnExpectedException
import com.easypay.easypaypos.views.EPProgressDialog
import com.example.admin.easypaypos.R
import org.json.JSONObject

/**
 * Created by ADMIN on 7/18/2016.
 */
object VolleyJsonRequest {

    private var progressDialog: EPProgressDialog? = null
    private const val TAG = "VolleyJsonRequest"

    @Throws(InternetNotAvailableException::class, UnExpectedException::class)
    fun request(context: Context, url: String, requestObject: JSONObject?, onResponse: OnJsonResponse, isProgressShow: Boolean): JsonObjectRequest? {
        var jsObjRequest: JsonObjectRequest? = null
        if (DeviceInfo.isInternetConnected(context)) {
            Log.e("VollyURL", url)
            try {
                if (progressDialog == null && isProgressShow) {
                    progressDialog = EPProgressDialog(context)
                    progressDialog!!.isIndeterminate = true
                    progressDialog!!.setCancelable(false)
                    progressDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    //progressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(context,R.drawable.ep_progress));
                    if (!progressDialog!!.isShowing && !(context as Activity).isFinishing) {
                        progressDialog!!.show()
                    }
                }
                AppLog.e(TAG, "Vollyrequest " + requestObject.toString())

                jsObjRequest = JsonObjectRequest(url, requestObject, Response.Listener { response ->
                    AppLog.e(TAG, "Vollyresponse " + response.toString())
                    if (progressDialog != null && progressDialog!!.isShowing) {
                        progressDialog!!.dismiss()
                        progressDialog = null
                    }
                    val status = response.getInt("status")
                    if (status in intArrayOf(AppConstants.SUCCESS_DATA, AppConstants.SUCCESS_TRANSACTION, AppConstants.SUCCESS_VALIDATION,224))
                        onResponse.responseReceived(response)
                    else
                        onResponse.errorReceived(status, response.getString("statusMessage"))
                }, Response.ErrorListener { error ->
                    if (progressDialog != null && progressDialog!!.isShowing) {
                        progressDialog!!.dismiss()
                        progressDialog = null
                    }

                    if (error.networkResponse != null) {
                        onResponse.errorReceived(error.networkResponse.statusCode, error.localizedMessage)
                    } else
                        Utils.showToast(context, error.localizedMessage)
                })
                //}
            } catch (e: ArithmeticException) {
                Log.e("caught", "request: ", e)
            }

            VolleyRequestQueue.getInstance(context).addToRequestQueue(jsObjRequest!!)
        } else {
            throw InternetNotAvailableException(context.getString(R.string.internet_not_available))
        }
        return jsObjRequest
    }

    interface OnJsonResponse {
        fun responseReceived(jsonObj: JSONObject)
        fun errorReceived(code: Int, message: String)
    }

}
