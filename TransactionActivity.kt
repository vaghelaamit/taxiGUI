package com.easypay.easypaypos.acitvities

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.communicator.CommunicatorConstant
import com.easypay.easypaypos.common.*
import com.easypay.easypaypos.entities.BillerEntity
import com.easypay.easypaypos.entities.ReceiptEntity
import com.easypay.easypaypos.entities.TransactionEntity
import com.easypay.easypaypos.exceptions.InternetNotAvailableException
import com.easypay.easypaypos.network.VolleyJsonRequest
import com.example.admin.easypaypos.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class TransactionActivity : AppCompatActivity() {

    private var handler: Handler? = null
    private var btnCancel: Button? = null
    private var btnOk: Button? = null

    private var objBiller: BillerEntity? = null
    private var objTransaction: TransactionEntity? = null
    private var objTransactionToSend: TransactionEntity? = null
    private var inputManager: InputMethodManager? = null

    private var layoutTransactions: LinearLayout? = null
    private var txtPayableAmount: TextView? = null
    private var txtPaymentMode: TextView? = null
    private var rightAlignedParameter: RelativeLayout.LayoutParams? = null
    private var epApp: EPPosAPP? = null

    private var redColor: Boolean = false
    private val colorChanger = object : Runnable {
        override fun run() {
            var color = Color.RED
            if (redColor)
                color = Color.BLACK
            txtPayableAmount!!.setTextColor(color)
            redColor = !redColor
            handler!!.postDelayed(this, 300)
        }
    }
    private val transactionResponse = object : VolleyJsonRequest.OnJsonResponse {
        override fun responseReceived(jsonObj: JSONObject) {
            //try{
            val status = jsonObj.optInt("status")
            inputManager!!.hideSoftInputFromWindow(txtPayableAmount!!.windowToken, 0)
            if (status == AppConstants.SUCCESS_TRANSACTION) {
                val dataJson = jsonObj.optJSONObject("data")
                if (dataJson != null) {
                    val receipt = Gson().fromJson<ReceiptEntity>(dataJson.toString(), ReceiptEntity::class.java)
                    redirectToPayReceipt(receipt, jsonObj.optString("statusMessage"), jsonObj.optString("statusLine"))
                }
            } else {
                val dataJson = jsonObj.optJSONObject("data")
                if (dataJson != null) {
                    val receipt = Gson().fromJson<ReceiptEntity>(dataJson.toString(), ReceiptEntity::class.java)
                    redirectToPayReceipt(receipt, jsonObj.optString("statusMessage"), jsonObj.optString("statusLine"))
                }
                Utils.showToast(this@TransactionActivity, jsonObj.optString("statusMessage"))
            }
        }

        override fun errorReceived(code: Int, message: String) {
            Utils.showToast(this@TransactionActivity, message)
            Log.e(TAG, "errorReceived : $code $message")
            finish()
        }
    }
    private val chargesResponse = object : VolleyJsonRequest.OnJsonResponse {
        override fun responseReceived(jsonObj: JSONObject) {
            try {
                val status = jsonObj.getInt("status")
                if (status == AppConstants.SUCCESS_DATA) {
                    val billDetails = jsonObj.getJSONArray("billDetails")
                    setCharges(billDetails)

                    for (i in 0 until billDetails.length()) {
                        val charge = billDetails.getJSONObject(i)
                        epApp!!.sendCommunicatorMessage(CommunicatorConstant.ADDTOCHARGES_ACTION, charge.toString())
                    }
                } else {
                    Utils.showToast(this@TransactionActivity, jsonObj.optString("statusMessage"))
                }
            } catch (e: JSONException) {
                Log.e(TAG, "responseReceived: ", e)

            }
        }

        override fun errorReceived(code: Int, message: String) {
            Log.e(TAG, "errorReceived:generateOTP $code $message")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction)

        epApp = application as EPPosAPP

        objBiller = intent.getSerializableExtra(AppConstants.OBJ_BILLER) as BillerEntity
        objTransaction = intent.getSerializableExtra(AppConstants.OBJ_TRANSACTION) as TransactionEntity

        rightAlignedParameter = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        rightAlignedParameter!!.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)

        init()

        btnCancel!!.setOnClickListener {
            clearCartOnCustomer()
            finish()
        }

        btnOk!!.setOnClickListener { makeRequest() }

        objTransaction!!.totalBillAmount = objTransaction!!.totalAmount

        setPaymentMode()
        addTransactions()
        loadCharges()
    }

    private fun clearCartOnCustomer() {
        try {
            val obj = JSONObject()
            obj.put("act", "clear")
            epApp!!.sendCommunicatorMessage(CommunicatorConstant.CLEARCART_ACTION, obj.toString())
        } catch (e: JSONException) {
            Log.e(TAG, "clearCartOnCustomer: ", e)
        }

    }

    private fun addTransactions() {
        var layout = RelativeLayout(this@TransactionActivity)
        layout.addView(getTextView("Bill Amount"))
        layout.addView(getTextView(Utils.formatAmount(objTransaction!!.totalAmount)), rightAlignedParameter)
        layoutTransactions!!.addView(layout)

        layout = RelativeLayout(this@TransactionActivity)
        layout.addView(getTextView(getString(R.string.promo_discount)))
        layout.addView(getTextView(Utils.formatAmount(0.0)), rightAlignedParameter)
        layoutTransactions!!.addView(layout)
    }

    private fun getTextView(text: String?): TextView {
        val textView = TextView(this@TransactionActivity)
        textView.setTextColor(Color.BLACK)
        textView.text = text
        textView.gravity = Gravity.RIGHT or Gravity.END
        textView.textSize = resources.getDimensionPixelSize(R.dimen.txt_size_medium).toFloat()
        return textView

    }

    private fun setPaymentMode() {
        var paymentMode: String? = ""
        val billsToPays = objTransaction!!.billsToPay
        if (!billsToPays.isEmpty())
            paymentMode = billsToPays[0].payment!!.paymentMode
        else {
            val recharges = objTransaction!!.recharges
            if (!recharges.isEmpty())
                paymentMode = recharges[0].payment!!.paymentMode
        }
        txtPaymentMode!!.setText(String.format(getString(R.string.payment_mode_format), paymentMode))
    }

    ///////////////////////////////////////////////////////////////////////////
    // Load changes
    ///////////////////////////////////////////////////////////////////////////
    private fun loadCharges() {
        try {
            val requestJson = JSONObject()
            requestJson.put("deviceCode", Preference.getStringPreference(this@TransactionActivity, AppConstants.PREF_DEVICE_CODE))
            requestJson.put("retailerCode", Preference.getStringPreference(this@TransactionActivity, AppConstants.PREF_RETAILER_CODE))
            requestJson.put("billDetails", prepareBillDetailArray())
            VolleyJsonRequest.request(this@TransactionActivity, URLGenerator.generateURL(this, URLGenerator.GET_CHARGES), requestJson, chargesResponse, true)
        } catch (e: JSONException) {
            Log.e(TAG, "generateOTP: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showAlert(this@TransactionActivity, e.message!!)
        }

    }

    private fun setCharges(billDetails: JSONArray?) {
        try {
            if (billDetails != null) {
                var totalChargeAmount = 0.0
                val bills = objTransaction!!.billsToPay
                if (bills != null) {
                    for (i in bills.indices) {
                        val detail = billDetails.getJSONObject(i)
                        val chargeToken = object : TypeToken<List<TransactionEntity.ChargeArray>>() {
                        }.type
                        val chargesArray = detail.getJSONArray("chargeArray")
                        val chargeArray = Gson().fromJson<List<TransactionEntity.ChargeArray>>(chargesArray.toString(), chargeToken)
                        val bill = bills[i]
                        bill.chargeArray = chargeArray
                        var chargeAmount = 0
                        for (charge in chargeArray) {
                            totalChargeAmount += charge.value
                            chargeAmount += charge.value.toInt()
                            addCharges(charge.name, charge.value)
                        }
                        bill.payment!!.paidAmount = bill.payment!!.paidAmount + chargeAmount
                    }
                    txtPayableAmount!!.text = Utils.formatAmount(objTransaction!!.totalAmount + totalChargeAmount)
                    objTransaction!!.totalAmount = objTransaction!!.totalAmount + totalChargeAmount
                    objTransaction!!.totalChrgAmount = totalChargeAmount
                }
                val recharges = objTransaction!!.recharges
                if (recharges != null) {
                    for (i in recharges.indices) {
                        val detail = billDetails.getJSONObject(i)
                        val chargeToken = object : TypeToken<List<TransactionEntity.ChargeArray>>() {
                        }.type
                        val chargesArray = detail.getJSONArray("chargeArray")
                        val chargeArray = Gson().fromJson<List<TransactionEntity.ChargeArray>>(chargesArray.toString(), chargeToken)
                        val recharge = recharges[i]
                        recharge.chargeArray = chargeArray
                        var chargeAmount = 0
                        for (charge in chargeArray) {
                            totalChargeAmount += charge.value
                            chargeAmount += charge.value.toInt()
                            addCharges(charge.name, charge.value)
                        }
                        recharge.payment!!.paidAmount = recharge.payment!!.paidAmount + chargeAmount
                    }
                }
                val epMart = objTransaction!!.epMart
                if (epMart != null) {
                    val productsToBuys = epMart.productsToBuy
                    for (i in productsToBuys.indices) {
                        val detail = billDetails.getJSONObject(i)
                        val chargeToken = object : TypeToken<List<TransactionEntity.ChargeArray>>() {
                        }.type
                        val chargesArray = detail.getJSONArray("chargeArray")
                        val chargeArray = Gson().fromJson<List<TransactionEntity.ChargeArray>>(chargesArray.toString(), chargeToken)
                        val product = productsToBuys[i]
                        var chargeAmount = 0
                        for (charge in chargeArray) {
                            totalChargeAmount += charge.value * product.quantity
                            chargeAmount += (charge.value * product.quantity).toInt()
                            addCharges(charge.name, charge.value * product.quantity)
                            charge.value = charge.value * product.quantity
                        }
                        product.chargeArray = chargeArray
                        product.payment!!.paidAmount = product.payment!!.paidAmount + chargeAmount
                    }
                }
                txtPayableAmount!!.text = Utils.formatAmount(objTransaction!!.totalAmount + totalChargeAmount)
                objTransaction!!.totalAmount = objTransaction!!.totalAmount + totalChargeAmount
                objTransaction!!.totalChrgAmount = totalChargeAmount

            } else
                txtPayableAmount!!.text = Utils.formatAmount(objTransaction!!.totalAmount)
        } catch (e: JSONException) {
            Log.e(TAG, "setCharges: ", e)
        }

    }

    private fun addCharges(name: String?, value: Double) {
        val layout = RelativeLayout(this@TransactionActivity)
        layout.addView(getTextView(name))
        layout.addView(getTextView(Utils.formatAmount(value)), rightAlignedParameter)
        layoutTransactions!!.addView(layout)
    }

    private fun prepareBillDetailArray(): JSONArray {
        val billDetails = JSONArray()
        try {
            var detail: JSONObject
            val bills = objTransaction!!.billsToPay
            if (bills != null && !bills.isEmpty()) {
                for (bill in bills) {
                    detail = JSONObject()
                    detail.put("billerCode", bill.billerCode)
                    detail.put("amount", bill.amount)
                    detail.put("serviceType", bill.serviceType)
                    detail.put("paymentMode", "PaymentMode." + bill.payment!!.paymentMode!!)
                    billDetails.put(detail)
                }
            }
            val recharges = objTransaction!!.recharges
            if (recharges != null && !recharges.isEmpty()) {
                for (recharge in recharges) {
                    detail = JSONObject()
                    detail.put("billerCode", recharge.billerCode)
                    detail.put("amount", recharge.amount)
                    detail.put("serviceType", recharge.serviceType)
                    detail.put("paymentMode", "PaymentMode." + recharge.payment!!.paymentMode!!)
                    billDetails.put(detail)
                }
            }
            val epMart = objTransaction!!.epMart
            if (epMart != null) {
                val productsToBuys = epMart.productsToBuy
                for (pro in productsToBuys) {
                    detail = JSONObject()
                    detail.put("billerCode", epMart.billerCode)
                    detail.put("amount", pro.productPrice)
                    detail.put("serviceType", epMart.serviceType)
                    detail.put("paymentMode", AppConstants.PAYMENTMODE_CASH)
                    billDetails.put(detail)
                }
            }

        } catch (e: JSONException) {
            Log.e(TAG, "prepareBillDetailArray: ", e)
        }

        return billDetails
    }

    private fun makeRequest() {
        try {

            if (objTransaction!!.billsToPay != null && objTransaction!!.billsToPay.size > 0 &&
                    objTransaction!!.billsToPay[0].payment!!.paymentMode!!.equals(AppConstants.MODE_Mobikwik, ignoreCase = true)) {
                val i = Intent(this, OtpActivity::class.java)
                i.putExtra(AppConstants.OBJ_BILLER, objBiller)
                i.putExtra(AppConstants.OBJ_TRANSACTION, objTransaction)
                startActivity(i)
            } else {
                if (objBiller!!.billerCode == "UUGV073E" || objBiller!!.billerCode == "UPGV074E"
                        || objBiller!!.billerCode == "UMGV075E" || objBiller!!.billerCode == "UDGV076E" ||
                        objBiller!!.billerCode == "AHMUREL048E" || objBiller!!.billerCode == "AHMUBSE047E"
                        || objBiller!!.billerCode == "AHMUBSE046E") {

                    objTransactionToSend = TransactionEntity()
                    objTransactionToSend = objTransaction

                    val request = Gson().toJson(objTransactionToSend)
                    val requestJson = JSONObject(request)
                    removeExtraParaPgvcl(requestJson)
                    VolleyJsonRequest.request(this@TransactionActivity, URLGenerator.generateURL(this, URLGenerator.TRANSACTION_NEW), requestJson, transactionResponse, true)
                } else if (objBiller!!.billerCode == AppConstants.BILLERCODE_JANMITRA) {
                    val request = Gson().toJson(objTransaction)
                    val requestJson = JSONObject(request)
                    removeParaJanmitra(requestJson)
                    VolleyJsonRequest.request(this@TransactionActivity, URLGenerator.generateURL(this, URLGenerator.TRANSACTION_NEW), requestJson, transactionResponse, true)
                } else {
                    val request = Gson().toJson(objTransaction)
                    val requestJson = JSONObject(request)
                    VolleyJsonRequest.request(this@TransactionActivity, URLGenerator.generateURL(this, URLGenerator.TRANSACTION_NEW), requestJson, transactionResponse, true)
                }
            }
        } catch (e: JSONException) {
            Log.e(TAG, "generateOTP: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showAlert(this@TransactionActivity, e.message!!)
        }

    }

    private fun removeParaJanmitra(requestJson: JSONObject) {
        val billsToPayArr = requestJson.optJSONArray("billsToPay")
        var paymentObj: JSONObject
        for (i in 0 until billsToPayArr.length()) {
            var obj: JSONObject? = null
            try {
                obj = billsToPayArr.getJSONObject(i)
                obj!!.remove("amount")
                obj.remove("amountDue")
                obj.remove("billerName")
                obj.remove("dueDate")
                paymentObj = obj.getJSONObject("payment")
                paymentObj.remove("productPrice")
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    private fun removeExtraParaPgvcl(requestJson: JSONObject) {
        val billsToPayArr = requestJson.optJSONArray("billsToPay")
        var paymentObj: JSONObject
        for (i in 0 until billsToPayArr.length()) {
            var obj: JSONObject? = null
            try {
                obj = billsToPayArr.getJSONObject(i)
                obj!!.remove("amount")
                obj.remove("amountAfterDueDate")
                obj.remove("billId")
                obj.remove("custAccNo")
                obj.remove("customerId")
                obj.remove("dateOfBirth")
                obj.remove("email")
                obj.remove("mobile")
                obj.remove("otpVerificationForValidation")
                obj.remove("valVersion")
                obj.remove("warningMsg")
                paymentObj = obj.getJSONObject("payment")
                paymentObj.remove("productPrice")
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    private fun redirectToPayReceipt(receipt: ReceiptEntity?, statusMessage: String, statusLine: String) {

        val starter = Intent(this@TransactionActivity, PaymentReceiptActivity::class.java)
        starter.putExtra(AppConstants.OBJ_RECEIPT, receipt)
        starter.putExtra(AppConstants.STATUS_MESSAGE, statusMessage)
        starter.putExtra(AppConstants.TRANSACTION_STATUS, statusLine)
        starter.putExtra(AppConstants.OBJ_TRANSACTION, objTransaction)
        starter.putExtra(AppConstants.OBJ_BILLER, objBiller)
        startActivity(starter)

        finish()

    }

    private fun init() {
        txtPaymentMode = findViewById(R.id.transaction_txtpaymentmode) as TextView
        txtPayableAmount = findViewById(R.id.transaction_txtpayable) as TextView
        layoutTransactions = findViewById(R.id.transaction_layouttran) as LinearLayout
        inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        btnCancel = findViewById(R.id.transaction_btncancel) as Button
        btnOk = findViewById(R.id.transaction_btnok) as Button

        val amountToPay = objTransaction!!.totalAmount
        txtPayableAmount!!.text = Utils.formatAmount(amountToPay) + "\n( " + EnglishNumberToWords.convert(amountToPay.toLong()) + " rupees only )"
    }

    public override fun onPause() {
        super.onPause()
        if (handler != null)
            handler!!.removeCallbacks(colorChanger)
    }

    override fun onResume() {
        super.onResume()
        if (handler == null) {
            handler = Handler()
            handler!!.post(colorChanger)
        }
    }

    companion object {
        private val TAG = "TransactionActivity"
    }
}
