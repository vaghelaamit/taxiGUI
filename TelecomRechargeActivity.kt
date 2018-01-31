package com.easypay.easypaypos.acitvities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.InputFilter
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.android.volley.toolbox.NetworkImageView
import com.easypay.easypaypos.common.AppConstants
import com.easypay.easypaypos.common.Preference
import com.easypay.easypaypos.common.URLGenerator
import com.easypay.easypaypos.common.Utils
import com.easypay.easypaypos.database.BillerMappingHelper
import com.easypay.easypaypos.database.SubCategoryHelper
import com.easypay.easypaypos.entities.BillerEntity
import com.easypay.easypaypos.entities.PlanEntity
import com.easypay.easypaypos.entities.SubCategoryEntity
import com.easypay.easypaypos.entities.TransactionEntity
import com.easypay.easypaypos.exceptions.InternetNotAvailableException
import com.easypay.easypaypos.network.VolleyJsonRequest
import com.easypay.easypaypos.network.VolleyRequestQueue
import com.example.admin.easypaypos.R
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class TelecomRechargeActivity : AppCompatActivity() {
    //CustomViewPager pager;

    private var txtTabLeft: TextView? = null
    private var txtTabRight: TextView? = null
    private var btnProceed: Button? = null
    private var btnCancel: Button? = null
    private var phoneNumber: String? = null
    private val operator: String? = null
    private var amount: Double = 0.toDouble()
    private val promoCode: String? = null
    private val rechartType: Int = 0
    private var edtNumber: EditText? = null
    private var edtAmount: EditText? = null
    private var rdValidity: RadioButton? = null
    private var rdSpecial: RadioButton? = null
    private var rdTypeGroup: RadioGroup? = null
    private var shake: Animation? = null
    private var objBiller: BillerEntity? = null
    private var layoutPromo: LinearLayout? = null
    private var layoutTab: LinearLayout? = null
    private var edtOperator: EditText? = null
    private var btnBrowsePlan: Button? = null
    private var serial: String? = ""
    private var inputManager: InputMethodManager? = null

    private var txtCountryCode: TextView? = null
    private var txtTitle: TextView? = null
    private var imgLogo: NetworkImageView? = null
    private var plansArray: JSONArray? = null
    private var retailerCode: String? = null
    private var staffCode: String? = null
    private var deviceCode: String? = null
    private var serviceType: String? = null
    private var billerCategoryCode: String? = null
    private var subCategoryCode = "Telecom.Prepaid"
    private var dataJson: JSONObject? = null
    private var parentBillercode: String? = null
    private val plans: List<PlanEntity>? = null
    private val validationResponse = object : VolleyJsonRequest.OnJsonResponse {
        override fun responseReceived(jsonObj: JSONObject) {
            try {
                val status = jsonObj.optInt("status")
                if (status == AppConstants.SUCCESS_VALIDATION) {
                    parentBillercode = jsonObj.getString("parentBillerCode")
                    dataJson = jsonObj.getJSONObject("data")
                    plansArray = dataJson!!.optJSONArray("plans")
                    if (plansArray == null || plansArray!!.length() == 0) {
                        val amountValue = edtAmount!!.text.toString()
                        if (TextUtils.isEmpty(amountValue)) {
                            edtAmount!!.error = "Enter amount"
                            edtAmount!!.startAnimation(shake)
                            return
                        } else {
                            amount = java.lang.Double.parseDouble(amountValue)
                            makeTransactionObject()
                        }
                    } else {
                        val starter = Intent(this@TelecomRechargeActivity, BrowsePlanActivity::class.java)
                        starter.putExtra("planArray", plansArray!!.toString())
                        startActivityForResult(starter, AppConstants.BROWSEPLAN_CODE)
                    }
                } else {
                    inputManager!!.hideSoftInputFromWindow(edtNumber!!.windowToken, 0)
                    Utils.showToast(this@TelecomRechargeActivity, jsonObj.optString("statusMessage"))
                }
            } catch (e: JSONException) {
                Log.e(TAG, "responseReceived: generateOTP", e)
            }
        }

        override fun errorReceived(code: Int, message: String) {
            Log.e(TAG, "errorReceived:generateOTP $code $message")
        }
    }

    private val isValid: Boolean
        get() {
            phoneNumber = edtNumber!!.text.toString()
            if (TextUtils.isEmpty(phoneNumber)) {
                edtNumber!!.error = "Enter number"
                edtAmount!!.startAnimation(shake)
                return false
            }
            val amountValue = edtAmount!!.text.toString()
            if (TextUtils.isEmpty(amountValue)) {
                edtAmount!!.error = "Enter amount"
                edtAmount!!.startAnimation(shake)
                return false
            } else {
                if (!TextUtils.isEmpty(amountValue))
                    amount = java.lang.Double.parseDouble(amountValue)
            }

            return true
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_telecom_recharge)

        objBiller = intent.getSerializableExtra(AppConstants.OBJ_BILLER) as BillerEntity

        serial = Preference.getStringPreference(this@TelecomRechargeActivity, AppConstants.PREF_DEVICE_SERIAL)

        init()

        if (objBiller != null) {
            if (objBiller!!.isEpPromoFlag) {
                //layoutPromo.setVisibility(View.VISIBLE);
                //changed to gone
                layoutPromo!!.visibility = View.GONE
            }
            edtOperator!!.setText(objBiller!!.billerName)
        }


        txtTabLeft!!.setOnClickListener {
            if (!URLGenerator.isNigeria)
                rdTypeGroup!!.visibility = View.VISIBLE
            else
                rdTypeGroup!!.visibility = View.INVISIBLE

            txtTabLeft!!.setBackgroundColor(Color.WHITE)
            txtTabRight!!.setBackgroundResource(R.drawable.black_border)
            subCategoryCode = "Telecom.Prepaid"
            if (!URLGenerator.isNigeria)
                btnBrowsePlan!!.visibility = View.VISIBLE
        }

        txtTabRight!!.setOnClickListener {
            rdTypeGroup!!.visibility = View.INVISIBLE
            txtTabRight!!.setBackgroundColor(Color.WHITE)
            txtTabLeft!!.setBackgroundResource(R.drawable.black_border)
            subCategoryCode = "Telecom.Postpaid"
            if (URLGenerator.isNigeria)
                btnBrowsePlan!!.visibility = View.GONE
        }


        btnProceed!!.setOnClickListener(View.OnClickListener {
            if (dataJson == null) {
                validateRequest()
            } else {
                val amountText = edtAmount!!.text.toString()
                if (TextUtils.isEmpty(amountText)) {
                    edtAmount!!.error = "Enter amount"
                    edtAmount!!.startAnimation(shake)
                    return@OnClickListener
                } else
                    amount = java.lang.Double.parseDouble(amountText)

                if (amount > 0)
                    makeTransactionObject()
                else {
                    edtAmount!!.error = "Enter amount greater than zero"
                    edtAmount!!.startAnimation(shake)
                    return@OnClickListener
                }
            }
        })

        btnCancel!!.setOnClickListener { finish() }

        retailerCode = Preference.getStringPreference(this@TelecomRechargeActivity, AppConstants.PREF_RETAILER_CODE)
        staffCode = Preference.getStringPreference(this@TelecomRechargeActivity, AppConstants.PREF_STAFFCODE)
        deviceCode = Preference.getStringPreference(this@TelecomRechargeActivity, AppConstants.PREF_DEVICE_CODE)

        txtTitle!!.text = getString(R.string.mob_recharge)
        setLogo()

        edtNumber!!.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(10))

        /*   edtNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                showDialog();
            }
        });
    */
        btnBrowsePlan!!.setOnClickListener(View.OnClickListener {
            phoneNumber = edtNumber!!.text.toString()
            if (TextUtils.isEmpty(phoneNumber)) {
                edtNumber!!.error = "Enter number"
                edtAmount!!.startAnimation(shake)
                return@OnClickListener
            }

            //validateRequest();

            val starter = Intent(this@TelecomRechargeActivity, BrowsePlanActivity::class.java)
            starter.putExtra("billerCode", objBiller!!.billerCode)
            starter.putExtra("serialNo", phoneNumber)
            //if (plansArray != null)
            //starter.putExtra("planArray", plansArray.toString());
            startActivityForResult(starter, AppConstants.BROWSEPLAN_CODE)
        })

        if (!URLGenerator.isNigeria) {
            rdTypeGroup!!.visibility = View.VISIBLE
            btnBrowsePlan!!.visibility = View.VISIBLE
        } else {
            rdTypeGroup!!.visibility = View.INVISIBLE
            btnBrowsePlan!!.visibility = View.INVISIBLE
        }

        /*if (URLGenerator.isNigeria)
            edtAmount.setEnabled(false);*/
        /*

        edtOperator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(edtOperator);
            }
        });*/

        billerCategoryCode = objBiller!!.billerCategoryDetail!!.categoryCode
        if (billerCategoryCode == AppConstants.CATEGORY_DTH) {
            layoutTab!!.visibility = View.GONE
        }

        if (objBiller!!.billerCode == AppConstants.BILLERCODE_NIGERIA_MUTLICHOICE_CABLETV && URLGenerator.isNigeria) {
            txtTitle!!.text = getString(R.string.dth_title)
            edtNumber!!.hint = getString(R.string.enter_number)
            txtCountryCode!!.visibility = View.GONE
        }
    }

    private fun init() {
        txtCountryCode = findViewById(R.id.telrecharge_txtcountrycode) as TextView
        layoutTab = findViewById(R.id.telrechage_layouttab) as LinearLayout
        txtTitle = findViewById(R.id.telrecharge_txttitle) as TextView
        imgLogo = findViewById(R.id.telrecharge_imglogo) as NetworkImageView
        inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        btnBrowsePlan = findViewById(R.id.telrecharge_btnbrowseplan) as Button
        edtOperator = findViewById(R.id.telrecharge_edtoperator) as EditText
        layoutPromo = findViewById(R.id.telrecharge_layoutpromo) as LinearLayout
        rdTypeGroup = findViewById(R.id.telrecharge_radiogroup) as RadioGroup
        shake = AnimationUtils.loadAnimation(this@TelecomRechargeActivity, R.anim.shake)
        rdValidity = findViewById(R.id.telrecharge_radiovalidity) as RadioButton
        rdSpecial = findViewById(R.id.telrecharge_radiospecial) as RadioButton
        btnProceed = findViewById(R.id.telrecharge_btnproceed) as Button
        txtTabLeft = findViewById(R.id.telrecharge_txttableft) as TextView
        txtTabRight = findViewById(R.id.telrecharge_txttabright) as TextView
        btnCancel = findViewById(R.id.telrecharge_btncancel) as Button
        edtAmount = findViewById(R.id.telrecharge_edtamount) as EditText
        edtNumber = findViewById(R.id.telrecharge_edtmobile) as EditText

        val subCategoryEntityList = BillerMappingHelper.getSubCategoryListByBiller(this@TelecomRechargeActivity, objBiller!!.billerCode!!)
        for (subCat in subCategoryEntityList!!) {
            if (subCat.subcategoryCode == "Telecom.PrepaidValidity") {
                rdValidity!!.isEnabled = true
            }
            if (subCat.subcategoryCode == "Telecom.Postpaid") {
                txtTabRight!!.isEnabled = true
            }
            if (subCat.subcategoryCode == "Telecom.PrepaidSpecialRecharge") {
                rdSpecial!!.isEnabled = true
            }
            if (subCat.subcategoryCode == "Telecom.Prepaid") {
                txtTabLeft!!.isEnabled = true
            }
        }

        rdValidity!!.setOnCheckedChangeListener { compoundButton, b ->
            if (b)
                subCategoryCode = "Telecom.PrepaidValidity"
        }
        rdSpecial!!.setOnCheckedChangeListener { compoundButton, b ->
            if (b)
                subCategoryCode = "Telecom.PrepaidSpecialRecharge"
        }


    }

    private fun setLogo() {
        val requestQueue = VolleyRequestQueue.getInstance(this@TelecomRechargeActivity)
        val imageLoader = requestQueue.imageLoader
        imgLogo!!.setImageUrl(objBiller!!.billerCategoryDetail!!.catImagePath, imageLoader)
    }


    private fun validateRequest() {

        if (isValid) {
            var subCat: SubCategoryEntity? = null
            if (billerCategoryCode == AppConstants.CATEGORY_DTH) {
                subCat = BillerMappingHelper.getSubCategoryByBiller(this@TelecomRechargeActivity, objBiller!!.billerCode!!)
            } else {
                subCat = SubCategoryHelper.getSubCategory(this@TelecomRechargeActivity, SubCategoryHelper.KEY_SUBCATEGORYCODE + "=?",
                        arrayOf(subCategoryCode), null, null, null)
            }
            if (subCat != null) {

                serviceType = subCat.serviceType
                try {
                    val requestJson = JSONObject()
                    requestJson.put("retailerCode", retailerCode)
                    requestJson.put("staffCode", staffCode)
                    requestJson.put("billerCode", objBiller!!.billerCode)
                    requestJson.put("deviceCode", deviceCode)
                    requestJson.put("amount", amount)
                    requestJson.put("serviceNo", phoneNumber)
                    requestJson.put("orderId", System.currentTimeMillis())
                    requestJson.put("serviceType", serviceType)
                    requestJson.put("cityCode", "AHD")
                    requestJson.put("deviceMacAddress", serial)

                    VolleyJsonRequest.request(this@TelecomRechargeActivity, URLGenerator.generateURL(this, URLGenerator.VALIDATION), requestJson, validationResponse, true)

                } catch (e: JSONException) {
                    Log.e(TAG, "generateOTP: JSONException", e)
                } catch (e: InternetNotAvailableException) {
                    Utils.showAlert(this@TelecomRechargeActivity, e.message!!)
                }

            } else {
                Utils.showToast(this@TelecomRechargeActivity, getString(R.string.servicetype_not_available))
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == AppConstants.BROWSEPLAN_CODE) {
                val planAmount = data.getIntExtra("amount", 0)
                edtAmount!!.setText(planAmount.toString())
                amount = planAmount.toDouble()
                if (dataJson == null) {
                    //validateRequest();
                } else {
                    if (URLGenerator.isNigeria) {
                        try {
                            dataJson!!.put("paymentId", data.getStringExtra("paymentCode"))
                            if (amount <= 0)
                                return
                        } catch (e: JSONException) {
                            Log.e(TAG, "onActivityResult: ", e)
                        }

                    } else {
                        val amountValue = edtAmount!!.text.toString()
                        if (TextUtils.isEmpty(amountValue)) {
                            edtAmount!!.error = "Enter amount"
                            edtAmount!!.startAnimation(shake)
                            return
                        }
                    }
                    makeTransactionObject()
                }
            }
        }
    }

    private fun makeTransactionObject() {
        try {
            val orderId = dataJson!!.getString("orderId")
            val paymentId = dataJson!!.getString("paymentId")
            val customerId = dataJson!!.optString("customerId")
            val billId = dataJson!!.optString("billId")

            val amountDue = dataJson!!.optString("amountDue")
            val amountAfterDue = dataJson!!.optString("amountAfterDueDate")
            val invoiceNo = dataJson!!.optString("invoiceNo")
            val dueDate = dataJson!!.optString("dueDate")

            val transaction = TransactionEntity()
            transaction.deviceCode = deviceCode
            transaction.localTxnId = System.currentTimeMillis()
            transaction.retailerCode = retailerCode
            transaction.staffCode = staffCode
            transaction.totalAmount = amount
            transaction.totalCash = amount
            transaction.epCustMobileNo = phoneNumber
            if (serviceType == AppConstants.TALKTIME_TYPE) {
                val recharge = TransactionEntity.Recharges()
                recharge.billerCode = objBiller!!.billerCode
                recharge.parentBillerCode = parentBillercode
                recharge.billerName = objBiller!!.billerName
                recharge.localTxnBillId = System.currentTimeMillis()
                recharge.orderId = orderId
                recharge.amount = amount
                recharge.paymentId = paymentId
                recharge.serviceType = serviceType
                recharge.customerNumber = phoneNumber
                val recharges = ArrayList<TransactionEntity.Recharges>()
                recharges.add(recharge)
                transaction.recharges = recharges
            } else {
                val billPay = TransactionEntity.BillsToPay()
                if (!TextUtils.isEmpty(amountDue)) {
                    billPay.amountDue = java.lang.Double.parseDouble(amountDue) + amount
                    billPay.amount = amount
                } else {
                    billPay.amount = amount
                    billPay.amountDue = amount
                }
                billPay.billerCode = objBiller!!.billerCode
                billPay.dueDate = dueDate
                billPay.parentBillerCode = parentBillercode
                billPay.localTxnBillId = System.currentTimeMillis()
                billPay.orderId = orderId
                billPay.billerName = objBiller!!.billerName
                billPay.paymentId = paymentId
                billPay.serviceType = serviceType
                billPay.serviceNo = phoneNumber

                val billsToPays = ArrayList<TransactionEntity.BillsToPay>()
                billsToPays.add(billPay)
                transaction.billsToPay = billsToPays
            }

            redirectToAvailLoyalty(transaction)


        } catch (e: JSONException) {
            Log.e(TAG, "makeTransactionObject: ", e)
        }

    }

    private fun redirectToAvailLoyalty(objTransaction: TransactionEntity) {
        val starter = Intent(this@TelecomRechargeActivity, AvailLoyaltyBenifit::class.java)
        starter.putExtra(AppConstants.OBJ_TRANSACTION, objTransaction)
        starter.putExtra(AppConstants.OBJ_BILLER, objBiller)
        startActivity(starter)
        finish()
    }

    companion object {
        private val TAG = "TelecomRechargeActivity"
    }


}
