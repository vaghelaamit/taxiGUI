package epposkotlin.easypay.com.epposkotlin.activities

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.InputFilter
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.DatePicker
import com.easypay.easypaypos.common.AppConstants
import com.easypay.easypaypos.common.Preference
import com.easypay.easypaypos.common.URLGenerator
import com.easypay.easypaypos.common.Utils
import com.easypay.easypaypos.entities.BillerEntity
import com.easypay.easypaypos.network.VolleyJsonRequest
import com.example.admin.easypaypos.R
import kotlinx.android.synthetic.main.janmitra_enrolment.*
import org.json.JSONObject
import java.util.*


/**
 * Created by AMIT on 22-01-2018.
 */
class JanmitraNewEnrolment : AppCompatActivity(), View.OnClickListener {

    private var shake: Animation? = null
    private var documentList: ArrayList<String> = ArrayList()
    private var namePrefixList: ArrayList<String> = ArrayList()
    private var salutation: String = ""
    private var gender: String = ""
    private var maritalStatus: String = ""
    private var objBiller: BillerEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.janmitra_enrolment)

        setSupportActionBar(main_toolbar)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        objBiller = intent.getSerializableExtra(AppConstants.OBJ_BILLER) as BillerEntity
        shake = AnimationUtils.loadAnimation(this@JanmitraNewEnrolment, R.anim.shake)

        setNamePrefixAdapter()
        setDocumentAdapterData()

        submit.setOnClickListener(this)
        clear.setOnClickListener(this)

        selectNameSpnr.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2 == 1) {
                    salutation = "Mr"
                } else if (p2 == 2) {
                    salutation = "Mrs"
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        })

        kycDocumentSpnr.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                var maxLengthofEditText = 0
                if (i == 0) {
                    maxLengthofEditText = 0
                    docNumber.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(maxLengthofEditText)))
                } else if (i == 1) {
                    maxLengthofEditText = 12
                    docNumber.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(maxLengthofEditText)))
                    docNumber.setHint("Enter Adharcard Number")
                } else if (i == 2) {
                    maxLengthofEditText = 10
                    docNumber.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(maxLengthofEditText)))
                    docNumber.setHint("Enter Pancard Number")
                } else if (i == 3) {
                    maxLengthofEditText = 20
                    docNumber.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(maxLengthofEditText)))
                    docNumber.setHint("Enter Driving Licence Number")
                } else if (i == 4) {
                    maxLengthofEditText = 10
                    docNumber.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(maxLengthofEditText)))
                    docNumber.setHint("Enter Voter-Id")
                } else if (i == 5) {
                    maxLengthofEditText = 9
                    docNumber.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(maxLengthofEditText)))
                    docNumber.setHint("Enter Passport Number")
                } else if (i == 6) {
                    maxLengthofEditText = 20
                    docNumber.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(maxLengthofEditText)))
                    docNumber.setHint("Enter Jobcard Number")
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {
                return
            }
        })

        btnMale.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                gender = "male"
            }
        })

        btnFemale.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                gender = "female"
            }
        })

        btnMarried.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                maritalStatus = "married"
            }
        })

        btnSingle.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                maritalStatus = "single"
            }
        })

        dob.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN ->
                        showDatePickerDialog()
                }
                return false
            }
        })

        city.setText("Ahmedabad")
        state.setText("Gujarat")
    }

    fun hideKeyBoard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(dob.getWindowToken(), 0)
    }

    fun setNamePrefixAdapter() {
        namePrefixList.clear()
        namePrefixList.add("Select")
        namePrefixList.add("Mr")
        namePrefixList.add("Mrs")

        selectNameSpnr.adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, namePrefixList)
    }

    fun setDocumentAdapterData() {
        documentList.clear()
        documentList.add("Select")
        documentList.add(getString(R.string.adharcard))
        documentList.add(getString(R.string.pancard))
        documentList.add(getString(R.string.driving_licence))
        documentList.add(getString(R.string.voterid))
        documentList.add(getString(R.string.passport))
        documentList.add(getString(R.string.nRega_job_card))

        kycDocumentSpnr.adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, documentList)
    }

    override fun onClick(p0: View?) {
        when {
            p0!!.id.equals(R.id.submit) -> submitData()
            p0.id.equals(R.id.clear) -> clearData()
        }
    }

    fun showDatePickerDialog() {
        hideKeyBoard()
        val c: Calendar = Calendar.getInstance()
        val mYear = c.get(Calendar.YEAR)
        val mMonth = c.get(Calendar.MONTH)
        val mDay = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, object : DatePickerDialog.OnDateSetListener {

            override fun onDateSet(view: DatePicker, year: Int,
                                   monthOfYear: Int, dayOfMonth: Int) {

                dob.setText(Utils.appendZero(dayOfMonth.toString()) + "/" + Utils.appendZero((monthOfYear + 1).toString()) + "/" + year)

            }
        }, mYear, mMonth, mDay)
        datePickerDialog.show()
    }

    fun setDate(milies: Long, startData: Boolean) {
        Log.e("date", "selected" + milies)
    }

    fun validateData(): Boolean {
        var isValid: Boolean = true

        if (selectNameSpnr.selectedItemPosition == 0) {
            isValid = false
            selectNameSpnr.startAnimation(shake)
        } else if (TextUtils.isEmpty(firstName.text)) {
            isValid = false
            firstName.setError("Enter firstname")
            firstName.startAnimation(shake)
        } else if (TextUtils.isEmpty(lastName.text)) {
            isValid = false
            lastName.setError("Enter lastname")
            lastName.startAnimation(shake)
        } else if (TextUtils.isEmpty(topup_cardNumber.text)) {
            isValid = false
            topup_cardNumber.setError("Enter cardnumber")
            topup_cardNumber.startAnimation(shake)
        } else if (topup_cardNumber.text.length < 12) {
            isValid = false
            topup_cardNumber.setError("Enter valid cardnumber")
            topup_cardNumber.startAnimation(shake)
        } else if (TextUtils.isEmpty(dob.text)) {
            isValid = false
            dob.setError("Enter DOB")
            dob.startAnimation(shake)
        } else if (TextUtils.isEmpty(gender)) {
            isValid = false
            genderGroup.startAnimation(shake)
        } else if (TextUtils.isEmpty(maritalStatus)) {
            isValid = false
            maritalStatusGroup.startAnimation(shake)
        } else if (kycDocumentSpnr.selectedItemPosition == 0) {
            isValid = false
            kycDocumentSpnr.startAnimation(shake)
        } else if (TextUtils.isEmpty(docNumber.text)) {
            isValid = false
            docNumber.setError("Enter KYC document number")
            docNumber.startAnimation(shake)
        } else if (TextUtils.isEmpty(addLine1.text)) {
            isValid = false
            addLine1.setError("Enter address 1")
            addLine1.startAnimation(shake)
        } else if (TextUtils.isEmpty(zipCode.text)) {
            isValid = false
            zipCode.setError("Enter zipcode")
            zipCode.startAnimation(shake)
        } else if (zipCode.text.length < 6) {
            isValid = false
            zipCode.setError("Enter valid zipcode")
            zipCode.startAnimation(shake)
        } else if (TextUtils.isEmpty(city.text)) {
            isValid = false
            city.setError("Enter city")
            city.startAnimation(shake)
        } else if (TextUtils.isEmpty(state.text)) {
            isValid = false
            state.setError("Enter state")
            state.startAnimation(shake)
        } else if (TextUtils.isEmpty(mobile.text)) {
            isValid = false
            mobile.setError("Enter mobile")
            mobile.startAnimation(shake)
        } else if (mobile.text.length < 10) {
            isValid = false
            mobile.setError("Enter valid mobile")
            mobile.startAnimation(shake)
        }
        return isValid
    }

    fun submitData() {
        if (validateData()) {
            val reqJson = JSONObject()
            reqJson.put("orderId", Utils.uniqueOrderId)
            reqJson.put("retailerCode", Preference.getStringPreference(this, AppConstants.PREF_RETAILER_CODE))
            reqJson.put("billerCode", objBiller!!.billerCode!!)
            reqJson.put("deviceCode", Preference.getStringPreference(this, AppConstants.PREF_DEVICE_CODE))
            reqJson.put("staffCode", Preference.getStringPreference(this, AppConstants.PREF_STAFFCODE))
            reqJson.put("serviceType", "CIVICAMCJANMITRACARD")
            reqJson.put("cardRefNumber", topup_cardNumber.text)
            reqJson.put("salutation", salutation)
            reqJson.put("firstName", firstName.text)
            reqJson.put("lastName", lastName.text)
            reqJson.put("dob", dob.text)
            reqJson.put("address1", addLine1.text)
            reqJson.put("address2", addLine2.text)
            reqJson.put("currentCountry", "India")
            reqJson.put("state", state.text)
            reqJson.put("city", city.text)
            reqJson.put("zipCode", zipCode.text)
            reqJson.put("mobileNo", mobile.text)
            reqJson.put("email", email.text)
            //reqJson.put("sessionId", "")

            val doc = docNumber.text
            if (kycDocumentSpnr.selectedItemPosition == 1) {
                reqJson.put("aadharNumber", doc)
                reqJson.put("panNumber", "")
                reqJson.put("drivingLicenseNo", "")
                reqJson.put("voterId", "")
                reqJson.put("passportNumber", "")
                reqJson.put("nRegaJobCard", "")
            } else if (kycDocumentSpnr.selectedItemPosition == 2) {
                reqJson.put("panNumber", doc)
                reqJson.put("aadharNumber", "")
                reqJson.put("drivingLicenseNo", "")
                reqJson.put("voterId", "")
                reqJson.put("passportNumber", "")
                reqJson.put("nRegaJobCard", "")
            } else if (kycDocumentSpnr.selectedItemPosition == 3) {
                reqJson.put("drivingLicenseNo", doc)
                reqJson.put("aadharNumber", "")
                reqJson.put("panNumber", "")
                reqJson.put("voterId", "")
                reqJson.put("passportNumber", "")
                reqJson.put("nRegaJobCard", "")
            } else if (kycDocumentSpnr.selectedItemPosition == 4) {
                reqJson.put("voterId", doc)
                reqJson.put("aadharNumber", "")
                reqJson.put("panNumber", "")
                reqJson.put("drivingLicenseNo", "")
                reqJson.put("passportNumber", "")
                reqJson.put("nRegaJobCard", "")
            } else if (kycDocumentSpnr.selectedItemPosition == 5) {
                reqJson.put("passportNumber", doc)
                reqJson.put("aadharNumber", "")
                reqJson.put("panNumber", "")
                reqJson.put("drivingLicenseNo", "")
                reqJson.put("voterId", "")
                reqJson.put("nRegaJobCard", "")
            } else {
                reqJson.put("nRegaJobCard", doc)
                reqJson.put("aadharNumber", "")
                reqJson.put("panNumber", "")
                reqJson.put("drivingLicenseNo", "")
                reqJson.put("voterId", "")
                reqJson.put("passportNumber", "")
            }

            reqJson.put("proxyNo", "")
            reqJson.put("middleName", "")
            reqJson.put("phone1", "")
            reqJson.put("phone2", "")
            VolleyJsonRequest.request(this@JanmitraNewEnrolment, URLGenerator.generateURL(this, URLGenerator.CUSTOMER_CARD_LINKING), reqJson, volleyResponce, true)
        }
    }

    private val volleyResponce = object : VolleyJsonRequest.OnJsonResponse {
        override fun responseReceived(jsonObj: JSONObject) {
            if (jsonObj.optInt("status").equals(AppConstants.SUCCESS_VALIDATION)) {
                Utils.showToast(this@JanmitraNewEnrolment, jsonObj.optString("statusMessage"))
                finish()
            }
        }

        override fun errorReceived(code: Int, message: String) {
            Utils.showToast(this@JanmitraNewEnrolment, message)
        }
    }

    fun clearData() {

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId.equals(android.R.id.home)) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}