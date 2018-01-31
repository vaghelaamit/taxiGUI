package com.easypay.easypaypos.dialogs

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.DialogFragment
import android.text.InputFilter
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.Window
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

import com.easypay.easypaypos.acitvities.LoginActivity
import com.easypay.easypaypos.common.AppConstants
import com.easypay.easypaypos.common.Preference
import com.easypay.easypaypos.common.URLGenerator
import com.easypay.easypaypos.common.Utils
import com.easypay.easypaypos.exceptions.InternetNotAvailableException
import com.easypay.easypaypos.network.VolleyJsonRequest
import com.example.admin.easypaypos.R

import org.json.JSONException
import org.json.JSONObject

import java.text.SimpleDateFormat
import java.util.TimeZone

/**
 * Created by ADMIN on 31-Aug-16.
 */
class OTPDialog : DialogFragment() {

    internal var dateFormatter = SimpleDateFormat("mm:ss")
    private var minuteCountDown = AppConstants.FIVE_MINUTES
    private var timeHandler: Handler? = null
    private var txtOtpTime: TextView? = null
    private var txtOtpDetail: TextView? = null
    private var btnVarify: Button? = null
    private var btnResend: Button? = null
    private var edtOTP: EditText? = null
    private val smsReceiver: BroadcastReceiver? = null
    private var shake: Animation? = null
    private var inputManager: InputMethodManager? = null
    private var lActivity: LoginActivity? = null
    private val timeRunnable = object : Runnable {
        override fun run() {
            minuteCountDown -= 1000
            if (minuteCountDown > 0) {
                txtOtpTime!!.text = dateFormatter.format(minuteCountDown)
                timeHandler!!.postDelayed(this, 1050)
            } else {
                btnResend!!.visibility = View.VISIBLE
                txtOtpTime!!.visibility = View.GONE
                txtOtpDetail!!.setTextColor(Color.RED)
                txtOtpDetail!!.text = getString(R.string.varificaiton_time_expired)
            }
        }
    }
    private val varifyotpResponse = object : VolleyJsonRequest.OnJsonResponse {
        override fun responseReceived(jsonObj: JSONObject) {
            if (jsonObj != null) {
                try {
                    val status = jsonObj.optInt("status")
                    if (status == AppConstants.SUCCESS_DATA) {
                        val dataJson = jsonObj.getJSONObject("data")
                        lActivity!!.resetPasswordToken = dataJson.getString("resetPasswordToken")
                        lActivity!!.showNewPasswordDialog()
                        dismiss()
                    } else {
                        inputManager!!.hideSoftInputFromWindow(edtOTP!!.windowToken, 0)
                        Utils.showToast(activity, jsonObj.optString("statusMessage"))
                    }
                } catch (e: JSONException) {
                    Log.e(TAG, "responseReceived: generateOTP", e)
                }

            }
        }

        override fun errorReceived(code: Int, message: String) {
            Log.e(TAG, "errorReceived:generateOTP $code $message")
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            lActivity = context as LoginActivity
        } catch (e: ClassCastException) {
            Log.e(TAG, "onAttach: ", e)
        }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(R.layout.dialog_otp)

        shake = AnimationUtils.loadAnimation(activity, R.anim.shake)
        inputManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        dateFormatter.timeZone = TimeZone.getTimeZone("UTC")

        txtOtpTime = dialog.findViewById(R.id.varifyotp_txttime) as TextView
        txtOtpDetail = dialog.findViewById(R.id.varifyotp_txttimedetail) as TextView
        btnVarify = dialog.findViewById(R.id.varifyotp_btnsubmit) as Button
        btnResend = dialog.findViewById(R.id.varifyotp_btnresend) as Button
        edtOTP = dialog.findViewById(R.id.varifyotp_edtotp) as EditText
        edtOTP!!.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(6))
        startTimer()


        btnResend!!.setOnClickListener {
            startTimer()
            txtOtpDetail!!.text = getString(R.string.time_left)
            btnResend!!.visibility = View.GONE
        }

        btnVarify!!.setOnClickListener(View.OnClickListener {
            val otp = edtOTP!!.text.toString()
            if (TextUtils.isEmpty(otp)) {
                edtOTP!!.error = "Enter OTP"
                edtOTP!!.startAnimation(shake)
                return@OnClickListener
            }
            verifyOTP(otp)
        })

        return dialog
    }

    fun verifyOTP(otp: String) {
        try {
            val requestJson = JSONObject()
            requestJson.put("deviceMacAddress", Preference.getStringPreference(activity, AppConstants.PREF_DEVICE_SERIAL))
            requestJson.put("retailerCode", Preference.getStringPreference(activity, AppConstants.PREF_RETAILER_CODE))
            requestJson.put("otp", otp)
            VolleyJsonRequest.request(activity, URLGenerator.generateURL(activity, URLGenerator.FORGOTPASSWORD_VARIFY_OTP),
                    requestJson, varifyotpResponse, true)
        } catch (e: JSONException) {
            Log.e(TAG, "generateOTP: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showToast(activity, e.message!!)
        }

    }

    override fun onDismiss(dialog: DialogInterface) {
        timeHandler!!.removeCallbacks(timeRunnable)
        super.onDismiss(dialog)
    }

    private fun startTimer() {
        if (timeHandler == null) {
            timeHandler = Handler()
        } else {
            timeHandler!!.removeCallbacks(timeRunnable)
        }
        txtOtpTime!!.visibility = View.VISIBLE
        minuteCountDown = AppConstants.FIVE_MINUTES
        timeHandler!!.post(timeRunnable)
        txtOtpDetail!!.setTextColor(Color.BLACK)

    }

    companion object {

        private val TAG = "OTPDialog"
    }


}