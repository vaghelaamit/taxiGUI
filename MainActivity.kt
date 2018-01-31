package com.easypay.easypaypos.acitvities

import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.BatteryManager.BATTERY_STATUS_CHARGING
import android.os.BatteryManager.BATTERY_STATUS_FULL
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.FragmentActivity
import android.support.v4.view.PagerAdapter
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.NetworkImageView
import com.communicator.CommunicatorConstant
import com.easypay.easypaypos.adapter.BillerAdapter
import com.easypay.easypaypos.adapter.CoverFlowAdapter
import com.easypay.easypaypos.adapter.MenuAdapter
import com.easypay.easypaypos.common.*
import com.easypay.easypaypos.database.BillerHelper
import com.easypay.easypaypos.database.BillerMappingHelper
import com.easypay.easypaypos.database.CategoryHelper
import com.easypay.easypaypos.database.DatabaseHelper
import com.easypay.easypaypos.entities.BillerEntity
import com.easypay.easypaypos.entities.CategoryEntity
import com.easypay.easypaypos.entities.TransactionEntity
import com.easypay.easypaypos.exceptions.InternetNotAvailableException
import com.easypay.easypaypos.network.VolleyJsonRequest
import com.easypay.easypaypos.network.VolleyRequestQueue
import com.example.admin.easypaypos.R
import com.facebook.drawee.view.SimpleDraweeView
import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import epposkotlin.easypay.com.epposkotlin.activities.JanmitraMainActivity
import it.moondroid.coverflow.components.ui.containers.FeatureCoverFlow
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : FragmentActivity(), MenuAdapter.OnMenuItemClick {

    internal var imgBattery: ImageView? = null
    internal var txtTime: TextView? = null
    internal var recyclerGridCategory: RecyclerView? = null
    internal var recyclerMenu: RecyclerView? = null
    internal var layoutCarousel: RelativeLayout? = null
    internal var txtUserName: TextView? = null
    internal var txtLogout: TextView? = null
    internal var txtRetailerCode: TextView? = null
    internal var imgProfile: NetworkImageView? = null
    internal var inputManager: InputMethodManager? = null
    internal var coverflowSingleImage: NetworkImageView? = null
    internal var txtWorkingBalance: TextView? = null
    internal var edtSearch: EditText? = null
    internal var txtComponent: TextView? = null
    internal var coverFlow: FeatureCoverFlow? = null
    internal var txtNoBillers: TextView? = null
    internal var layoutProfile: RelativeLayout? = null
    internal var layoutBalance: LinearLayout? = null
    internal var layoutCart: RelativeLayout? = null
    internal var txtCartCount: TextView? = null
    internal var cyclerPager: HorizontalInfiniteCycleViewPager? = null

    fun init() {

        imgBattery = main_imgbattery
        txtTime = main_txtTime
        recyclerMenu = main_menurecycler
        layoutCarousel = main_layoutcarousel
        txtUserName = main_txtname
        txtLogout = main_txtlogout
        txtRetailerCode = main_txtretailercode
        imgProfile = main_imgprofile
        coverflowSingleImage = coverflow_imgsingle
        txtWorkingBalance = main_txtcurrentbalance
        edtSearch = main_edtsearch
        txtComponent = main_txtComponent
        coverFlow = coverflow
        txtNoBillers = main_txtnobillers
        layoutProfile = main_layoutprofile
        layoutBalance = main_layoutbalance
        layoutCart = main_layoutcart
        txtCartCount = main_txtcartcount
        cyclerPager = main_cyclerviewpager
        recyclerGridCategory = main_recyclerbiller
    }

    internal var syncReceiver: BroadcastReceiver? = null
    private var cyclerPagerAdapter: CyclerPagerAdapter? = null
    private var epApp: EPPosAPP? = null
    private var balanceHandler: Handler? = null

    private var mContext: Context? = null
    private var batteryHandler: Handler? = null
    private var timeHandler: Handler? = null
    private val inflater: LayoutInflater? = null
    private val dateFormat = SimpleDateFormat("dd, MMM yyyy hh:mm a")
    private var imageLoader: ImageLoader? = null
    private var volleyRequestQueue: VolleyRequestQueue? = null
    private var billerCategories: MutableList<CategoryEntity>? = null
    private val adapter: CoverFlowAdapter? = null
    private var deviceSerial: String? = null
    private var retailerCode: String? = null
    private var billers: List<BillerEntity>? = null
    private var batteryReceiver: BroadcastReceiver? = null
    private var cartCountReceiver: BroadcastReceiver? = null
    private var staffCode: String? = null
    private var gson: Gson? = null

    private var billerLayoutParam: RelativeLayout.LayoutParams? = null
    /**
     * Battery handler
     */
    private val batteryChecker = object : Runnable {
        override fun run() {
            val chargingStatus = DeviceInfo.getBatteryStatus(this@MainActivity)
            setBatteryImage(chargingStatus)
            batteryHandler!!.postDelayed(this, (5 * 60 * 1000).toLong())
        }
    }
    private val timeSetter = object : Runnable {
        override fun run() {
            val currentTime = System.currentTimeMillis()
            txtTime!!.setText(dateFormat.format(currentTime).toUpperCase())
            timeHandler!!.postDelayed(this, (60 * 1000).toLong())
        }
    }
    private val profileResponse = object : VolleyJsonRequest.OnJsonResponse {
        override fun responseReceived(jsonObj: JSONObject) {
            val status = jsonObj.optInt("status")
            if (status == AppConstants.SUCCESS_DATA) {
                setRetailerName(jsonObj)
                Preference.savePreference(this@MainActivity, AppConstants.PREF_RETAILER_JSON, jsonObj.toString())
            } else {
                inputManager!!.hideSoftInputFromWindow(edtSearch!!.windowToken, 0)
                Utils.showToast(this@MainActivity, jsonObj.optString("statusMessage"))

            }
        }

        override fun errorReceived(code: Int, message: String) {
            Log.e(TAG, "errorReceived:loadBalance $code $message")
        }
    }
    private val balanceResponse = object : VolleyJsonRequest.OnJsonResponse {
        override fun responseReceived(jsonObj: JSONObject) {
            try {
                val status = jsonObj.optInt("status")
                if (status == AppConstants.SUCCESS_DATA) {
                    val capAccountId = jsonObj.getInt("accountId")
                    val workingBalance = jsonObj.getDouble("workingBalance")
                    val commissionBalance = jsonObj.getDouble("commissionBalance")

                    txtWorkingBalance!!.text = Utils.formatAmount(workingBalance)

                } else {
                    inputManager!!.hideSoftInputFromWindow(edtSearch!!.windowToken, 0)
                    Utils.showToast(this@MainActivity, jsonObj.optString("statusMessage"))
                }
            } catch (e: JSONException) {
                Log.e(TAG, "responseReceived: loadBalance", e)
            }
        }

        override fun errorReceived(code: Int, message: String) {
            Log.e(TAG, "errorReceived:loadBalance $code $message")
        }
    }
    private val balanceChecker = object : Runnable {
        override fun run() {
            loadBalance(false)
            balanceHandler!!.postDelayed(this, BALANCE_CHECK_INTERVAL)
        }
    }
    private val billerResponse = object : VolleyJsonRequest.OnJsonResponse {
        override//(69 values)
        fun responseReceived(jsonObj: JSONObject) {
            try {
                val status = jsonObj.optInt("status")
                if (status == AppConstants.SUCCESS_DATA) {
                    val billerArray = jsonObj.getJSONArray("allBillerList")
                    val listType = object : TypeToken<List<BillerEntity>>() {

                    }.type
                    billers = gson!!.fromJson<List<BillerEntity>>(billerArray.toString(), listType)
                    BillerHelper.insertBillers(this@MainActivity, billers!!)
                    for (biller in billers!!) {
                        val categoryCode = biller.billerCategoryDetail!!.categoryCode
                        if (categoryCode == AppConstants.CATEGORY_ECOMMERCE) {
                            sendEcommerceCatToCustomer(biller.billerCode)
                        }
                    }
                } else {
                    inputManager!!.hideSoftInputFromWindow(edtSearch!!.windowToken, 0)
                    Utils.showToast(this@MainActivity, jsonObj.optString("statusMessage"))
                }
            } catch (e: JSONException) {
                Log.e(TAG, "responseReceived: loadBalance", e)
            }
        }

        override fun errorReceived(code: Int, message: String) {
            Log.e(TAG, "errorReceived:loadBalance $code $message")
        }
    }
    private val billerCategoryResponse = object : VolleyJsonRequest.OnJsonResponse {
        override fun responseReceived(jsonObj: JSONObject) {
            try {
                val status = jsonObj.optInt("status")
                if (status == AppConstants.SUCCESS_DATA) {
                    val billerArray = jsonObj.getJSONArray("data")
                    val listType = object : TypeToken<List<CategoryEntity>>() {

                    }.type
                    billerCategories = gson!!.fromJson<List<CategoryEntity>>(billerArray.toString(), listType) as MutableList<CategoryEntity>?
                    CategoryHelper.insertCategories(this@MainActivity, billerCategories!!)
                    setCategoryAdapter()
                    loadBillers()
                } else {
                    inputManager!!.hideSoftInputFromWindow(edtSearch!!.windowToken, 0)
                    Utils.showToast(this@MainActivity, jsonObj.optString("statusMessage"))
                }
            } catch (e: JSONException) {
                Log.e(TAG, "responseReceived: loadBalance", e)
            }
        }

        override fun errorReceived(code: Int, message: String) {
            Log.e(TAG, "errorReceived:loadBalance $code $message")
        }
    }
    private val settingsResponse = object : VolleyJsonRequest.OnJsonResponse {
        override fun responseReceived(jsonObj: JSONObject) {
            val status = jsonObj.optInt("status")
            try {
                if (status == AppConstants.SUCCESS_DATA) {
                    val dataArray = jsonObj.getJSONArray("data")
                    val objImage = dataArray.getJSONObject(1)
                    if (objImage != null)
                        Preference.savePreference(this@MainActivity, AppConstants.PREF_AVAIL_BGURL, objImage.getString("CUST_BACKGROUNDIMG"))
                } else {
                    inputManager!!.hideSoftInputFromWindow(edtSearch!!.windowToken, 0)
                    Utils.showToast(this@MainActivity, jsonObj.optString("statusMessage"))
                }
            } catch (e: JSONException) {
                Log.e(TAG, "responseReceived:  ", e)
            }
        }

        override fun errorReceived(code: Int, message: String) {
            Log.e(TAG, "errorReceived:loadBalance $code $message")
        }
    }

    private fun sendEcommerceCatToCustomer(billerCode: String?) {
        var billerCode = billerCode
        if (TextUtils.isEmpty(billerCode)) {
            var billerEntities: List<BillerEntity>? = null
            if (billers == null || billers!!.isEmpty())
                billerEntities = BillerHelper.getBillers(this@MainActivity, BillerHelper.KEY_categoryCode + "=?", arrayOf(AppConstants.CATEGORY_ECOMMERCE), null, null, null)
            else
                billerEntities = billers

            for (biller in billerEntities!!) {
                if (biller.billerCategoryDetail!!.categoryCode == AppConstants.CATEGORY_ECOMMERCE) {
                    billerCode = biller.billerCode
                }
            }
        }
        if (!TextUtils.isEmpty(billerCode)) {
            try {
                val obj = JSONObject()
                obj.put("categoryCode", AppConstants.CATEGORY_ECOMMERCE)
                obj.put("billerCode", billerCode)
                val subCat = BillerMappingHelper.getSubCategoryByBiller(this@MainActivity, billerCode!!)
                obj.put("serviceType", subCat.serviceType)
                epApp!!.sendCommunicatorMessage(CommunicatorConstant.ECOMMERCE_DATA_ACTION, obj.toString())
            } catch (e: JSONException) {
                Log.e(TAG, "responseReceived: ", e)
            }

        }
    }

    private fun setRetailerName(retailerJson: JSONObject?) {
        try {
            val firstName = retailerJson!!.getString("firstName")
            val nameBuilder = StringBuilder(firstName)
            val lastName = retailerJson.getString("lastName")
            if (!TextUtils.isEmpty(lastName)) nameBuilder.append(" " + lastName)

            val name = nameBuilder.toString()
            txtUserName!!.text = name
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    ///////////////////////////////////////////////////////////////////////////
    // Load working balance
    ///////////////////////////////////////////////////////////////////////////

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        epApp = application as EPPosAPP
        gson = Gson()

        // RoboGuice.injectMembers(this@MainActivity, this)

        recyclerMenu!!.layoutManager = LinearLayoutManager(this@MainActivity)

        val menuItems = MenuHelper.prepareMenu(this@MainActivity)
        recyclerMenu!!.adapter = MenuAdapter(this@MainActivity, menuItems, this)

        volleyRequestQueue = VolleyRequestQueue.getInstance(this@MainActivity)
        imageLoader = volleyRequestQueue!!.imageLoader

        deviceSerial = Preference.getStringPreference(this@MainActivity, AppConstants.PREF_DEVICE_SERIAL)
        staffCode = Preference.getStringPreference(this@MainActivity, AppConstants.PREF_STAFFCODE)

        if (recyclerGridCategory != null)
            recyclerGridCategory!!.layoutManager = GridLayoutManager(this@MainActivity, 5)

        mContext = this@MainActivity

        setViews()

        retriveEPSettings()

        val retailerJson = Preference.getStringPreference(this@MainActivity, AppConstants.PREF_RETAILER_JSON)
        if (TextUtils.isEmpty(retailerJson))
            loadRetailerProfile()
        else {
            try {
                setRetailerName(JSONObject(retailerJson))
            } catch (e: JSONException) {
                Log.e(TAG, "onCreate: ", e)
            }

        }

        layoutProfile!!.setOnClickListener {
            val starter = Intent(this@MainActivity, ProfileActivity::class.java)
            startActivity(starter)
        }

        var fcmToken = FirebaseInstanceId.getInstance().token
        if (!TextUtils.isEmpty(fcmToken))
            fcmToken = Preference.getStringPreference(this, AppConstants.FCM_TOKEN)

        //Log.e(TAG, "Token: " + fcmToken!!)

        txtLogout!!.setOnClickListener { logout() }

        layoutBalance!!.setOnClickListener {
            Utils.showAlert(this@MainActivity, getString(R.string.refresh_balance_Dtl), View.OnClickListener { loadBalance(true) })

            /* final SweetAlertDialog sd = new SweetAlertDialog(MainActivity.this);
                sd.setCancelable(false);
                sd.setCanceledOnTouchOutside(false);
                sd.setTitleText(getString(R.string.refresh_balance));
                sd.changeAlertType(SweetAlertDialog.WARNING_TYPE);
                sd.setContentText(getString(R.string.refresh_balance_Dtl));
                sd.setConfirmText(getString(R.string.dialog_ok));
                sd.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        loadBalance(true);
                        sd.dismiss();
                    }
                });
                sd.setCancelText(getString(R.string.dialog_cancel));
                sd.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sd.dismiss();
                    }
                });
                sd.show();*/
        }

        val display = windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)

        val density = resources.displayMetrics.density
        val dpHeight = outMetrics.heightPixels / density
        val dpWidth = outMetrics.widthPixels / density

        txtTime!!.setOnClickListener { shareDetailsToDevice() }

        /* NumberInputDialog dialog = NumberInputDialog.getInstance(null);
        dialog.show(getFragmentManager(), "ND");*/
        shareDetailsToDevice()

        layoutCart!!.setOnClickListener {
            val cartJson = Preference.getStringPreference(this@MainActivity, AppConstants.CART_JSON)
            if (!TextUtils.isEmpty(cartJson)) {
                val starter = Intent(this@MainActivity, CartDetailActivity::class.java)
                startActivity(starter)
            } else
                Utils.showToast(this@MainActivity, getString(R.string.no_items_in_cart))
        }

        billerLayoutParam = RelativeLayout.LayoutParams(Utils.dpToPixel(this@MainActivity, 600f), ViewGroup.LayoutParams.MATCH_PARENT)
        billerLayoutParam!!.setMargins(Utils.dpToPixel(this@MainActivity, 150f), 0, 0, 0)

    }

    private fun logout() {

        Utils.showAlert(this@MainActivity, getString(R.string.refresh_balance_Dtl), View.OnClickListener { doLogout() })


        /*
        final SweetAlertDialog sd = new SweetAlertDialog(MainActivity.this);
        sd.setCancelable(false);
        sd.setCanceledOnTouchOutside(false);
        sd.setTitleText(getString(R.string.confirm));
        sd.changeAlertType(SweetAlertDialog.WARNING_TYPE);
        sd.setContentText(getString(R.string.logout_warning));
        sd.setConfirmText("yes");
        sd.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {

            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {

            }
        });
        sd.setCancelText("no");
        sd.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sd.dismiss();
            }
        });
        sd.show();*/
    }

    private fun doLogout() {
        try {
            val requestJson = JSONObject()
            requestJson.put("deviceMacAddress", deviceSerial)
            requestJson.put("loginToken", Preference.getStringPreference(this@MainActivity, AppConstants.PREF_LOGINTOKEN))
            requestJson.put("action", "logout")
            requestJson.put("remark", "NORMAL LOGOUT")
            requestJson.put("staffCode", staffCode)
            VolleyJsonRequest.request(this@MainActivity, URLGenerator.generateURL(this@MainActivity, URLGenerator.LOGOUT_URL), requestJson, object : VolleyJsonRequest.OnJsonResponse {
                override fun responseReceived(jsonObj: JSONObject) {
                    val status = jsonObj.optInt("status")
                    if (status == AppConstants.SUCCESS_DATA) {
                        clearPreferences()
                    } else {
                        Utils.showToast(this@MainActivity, jsonObj.optString("statusMessage"))
                    }
                }

                override fun errorReceived(code: Int, message: String) {}
            }, true)
        } catch (e: JSONException) {
            Log.e(TAG, "loadReceipts: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showAlert(this@MainActivity, e.message)
        }

    }

    private fun clearPreferences() {
        val retailerCode = Preference.getStringPreference(this@MainActivity, AppConstants.PREF_RETAILER_CODE)
        val fcmToken = Preference.getStringPreference(this@MainActivity, AppConstants.FCM_TOKEN)
        val deviceSerial = Preference.getStringPreference(this@MainActivity, AppConstants.PREF_DEVICE_SERIAL)
        Preference.clearAll(this@MainActivity)
        Preference.savePreference(this@MainActivity, AppConstants.PREF_RETAILER_CODE, retailerCode!!)
        Preference.savePreference(this@MainActivity, AppConstants.FCM_TOKEN, fcmToken!!)
        Preference.savePreference(this@MainActivity, AppConstants.PREF_DEVICE_SERIAL, deviceSerial!!)
        DatabaseHelper.truncateTables(this@MainActivity)
        val starter = Intent(this@MainActivity, LoginActivity::class.java)
        startActivity(starter)
        finish()
    }

    private fun registerScreenBroadcastReceiver() {
        val theFilter = IntentFilter()
        /** System Defined Broadcast  */
        theFilter.addAction(Intent.ACTION_SCREEN_ON)
        theFilter.addAction(Intent.ACTION_SCREEN_OFF)

        val screenOnOffReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val strAction = intent.action

                val myKM = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

                if (strAction == Intent.ACTION_SCREEN_OFF || strAction == Intent.ACTION_SCREEN_ON) {
                    clearPreferences()
                }
            }
        }

        applicationContext.registerReceiver(screenOnOffReceiver, theFilter)
    }

    private fun loadRetailerProfile() {

        try {
            val requestJson = JSONObject()
            requestJson.put("deviceMacAddress", deviceSerial)
            requestJson.put("version", DeviceInfo.getAppVersion(this@MainActivity))
            VolleyJsonRequest.request(this@MainActivity, URLGenerator.generateURL(this, URLGenerator.RETRIVE_PROFILE), requestJson, profileResponse, true)
        } catch (e: JSONException) {
            Log.e(TAG, "loadBalance: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showAlert(this@MainActivity, e.message)
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, "loadBalance: NameNotFoundException", e)
        }

    }

    private fun retriveEPSettings() {

        try {
            val requestJson = JSONObject()
            requestJson.put("deviceMacAddress", deviceSerial)
            requestJson.put("retailerCode", retailerCode)
            VolleyJsonRequest.request(this@MainActivity, URLGenerator.generateURL(this, URLGenerator.SETTINGS_URL), requestJson, settingsResponse, true)
        } catch (e: JSONException) {
            Log.e(TAG, "loadBalance: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showAlert(this@MainActivity, e.message)
        }

    }

    private fun setViews() {
        retailerCode = Preference.getStringPreference(this@MainActivity, AppConstants.PREF_RETAILER_CODE)
        if (!TextUtils.isEmpty(retailerCode)) txtRetailerCode!!.text = retailerCode
        val imgFile = Preference.getStringPreference(this@MainActivity, AppConstants.PREF_USERIMAGE)
        if (!TextUtils.isEmpty(imgFile)) {
            imgProfile!!.setImageUrl(imgFile, imageLoader)
        }


        billerCategories = CategoryHelper.getCategories(this@MainActivity, null, null, null, null, null) as MutableList<CategoryEntity>?
        if (billerCategories == null || billerCategories!!.isEmpty())
            loadBillersCategories()
        else
            setCategoryAdapter()
    }

    private fun loadBalance(isProgressShown: Boolean) {
        try {
            val requestJson = JSONObject()
            requestJson.put("deviceMacAddress", deviceSerial)
            requestJson.put("version", DeviceInfo.getAppVersion(this@MainActivity))
            VolleyJsonRequest.request(this@MainActivity, URLGenerator.generateURL(this, URLGenerator.RETRIVE_BALANCE), requestJson, balanceResponse, isProgressShown)
        } catch (e: JSONException) {
            Log.e(TAG, "loadBalance: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showAlert(this@MainActivity, e.message)
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, "loadBalance: NameNotFoundException", e)
        }

    }

    private fun shareDetailsToDevice() {
        try {
            val requestJson = JSONObject()
            requestJson.put("deviceMacAddress", deviceSerial)
            requestJson.put("retailerCode", retailerCode)
            requestJson.put("staffCode", staffCode)
            requestJson.put("deviceCode", Preference.getStringPreference(this@MainActivity, AppConstants.PREF_DEVICE_CODE))
            epApp!!.sendCommunicatorMessage(CommunicatorConstant.ADD_COUPONS_ACTION, requestJson.toString())
        } catch (e: JSONException) {
            Log.e(TAG, "loadBalance: JSONException", e)
        }

    }

    ///////////////////////////////////////////////////////////////////////////
    //  Load Billers Categories
    ///////////////////////////////////////////////////////////////////////////
    private fun loadBillersCategories() {
        try {
            val requestJson = JSONObject()
            requestJson.put("deviceMacAddress", deviceSerial)
            requestJson.put("version", DeviceInfo.getAppVersion(this@MainActivity))
            VolleyJsonRequest.request(this@MainActivity, URLGenerator.generateURL(this, URLGenerator.RETRIVE_BILLER_CATEGORY), requestJson, billerCategoryResponse, true)
        } catch (e: JSONException) {
            Log.e(TAG, "loadBalance: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showAlert(this@MainActivity, e.message)
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, "loadBalance: NameNotFoundException", e)
        }

    }

    ///////////////////////////////////////////////////////////////////////////
    //  Load Billers
    ///////////////////////////////////////////////////////////////////////////
    private fun loadBillers() {
        try {
            val requestJson = JSONObject()
            requestJson.put("retailerCode", retailerCode)
            requestJson.put("deviceMacAddress", deviceSerial)
            requestJson.put("version", DeviceInfo.getAppVersion(this@MainActivity))
            VolleyJsonRequest.request(this@MainActivity, URLGenerator.generateURL(this, URLGenerator.RETRIVE_BILLERS), requestJson, billerResponse, true)
        } catch (e: JSONException) {
            Log.e(TAG, "loadBillers: JSONException", e)
        } catch (e: InternetNotAvailableException) {
            Utils.showAlert(this@MainActivity, e.message)
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, "loadBillers: NameNotFoundException", e)
        }

    }

    private fun setCategoryAdapter() {
        if (billerCategories != null && billerCategories!!.size > 0) {

            // remove inactive category
            val it = billerCategories!!.iterator()
            while (it.hasNext()) {
                val ct = it.next()
                if (ct.status > 0) it.remove()
            }

            Collections.sort(billerCategories!!, CategoryComparator())

            recyclerGridCategory!!.adapter = BillerAdapter(this@MainActivity, billerCategories!!, object : BillerAdapter.OnBillerClick {

                override fun billerClicked(position: Int) {
                    val categoryEntity = billerCategories!![position]
                    txtComponent!!.text = categoryEntity.categoryName
                    val billerEntities = BillerHelper.getBillers(this@MainActivity, BillerHelper.KEY_categoryCode + "=?", arrayOf(categoryEntity.categoryCode), null, null, null)
                    if (!billerEntities.isEmpty()) {
                        txtNoBillers!!.visibility = View.GONE
                        /*if(cyclerPagerAdapter!=null) {
                                cyclerPager.removeAllViews();
                            }*/
                        setBillers(billerEntities)
                    } else {
                        cyclerPager!!.visibility = View.GONE
                        coverflowSingleImage!!.visibility = View.GONE
                        txtNoBillers!!.visibility = View.VISIBLE
                    }
                }
            })
        }
    }

    private fun setBillers(billerEntities: List<BillerEntity>?) {
        layoutCarousel!!.removeView(layoutCarousel!!.findViewById(R.id.main_cyclerviewpager))
        if (billerEntities!!.size >= 1) {
            // coverflowSingleImage.setVisibility(View.GONE);
            /*   adapter = new CoverFlowAdapter(MainActivity.this, billerEntities, new OnRecyclerviewItemAction() {
                @Override
                public void onItemClicked(int position) {
                    BillerEntity biller = billerEntities.get(position);
                    openBillerScreen(biller);
                }

                @Override
                public void onItemRemoved(int position) {

                }
            });
            coverFlow.setAdapter(adapter);
            coverFlow.setVisibility(View.VISIBLE);*/
            // if (cyclerPagerAdapter == null) {
            cyclerPager = HorizontalInfiniteCycleViewPager(this@MainActivity)
            cyclerPager!!.id = R.id.main_cyclerviewpager
            cyclerPagerAdapter = CyclerPagerAdapter(billerEntities)
            cyclerPager!!.adapter = cyclerPagerAdapter
            cyclerPager!!.scrollDuration = 500
            cyclerPager!!.isMediumScaled = true
            cyclerPager!!.maxPageScale = 0.8f
            cyclerPager!!.minPageScale = 0.6f
            cyclerPager!!.centerPageScaleOffset = 35.0f
            cyclerPager!!.minPageScaleOffset = 5.0f
            layoutCarousel!!.addView(cyclerPager, billerLayoutParam)
            /*   } else {
                cyclerPagerAdapter.setBillers(billerEntities);
                cyclerPagerAdapter.notifyDataSetChanged();
            }*/
            // cyclerPager.setVisibility(View.VISIBLE);

        } /*else {
            cyclerPager.setVisibility(View.GONE);
            coverflowSingleImage.setVisibility(View.VISIBLE);
            final BillerEntity biller = billerEntities.get(0);
            coverflowSingleImage.setImageUrl(biller.getLogoPath(), imageLoader);
            coverflowSingleImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openBillerScreen(biller);
                }
            });
        }*/
    }

    private fun openBillerScreen(biller: BillerEntity) {
        var clazz: Class<*>? = null
        val category = biller.billerCategoryDetail
        val starter = Intent()
        if (category != null) {
            if (category.categoryCode == AppConstants.CATEGORY_TELECOM) {
                clazz = TelecomRechargeActivity::class.java
            } else if (category.categoryCode == AppConstants.CATEGORY_ELECTRICITY) {
                if (biller.billerCode == AppConstants.BILLERCODE_TORRENT)
                    clazz = TorrentPayActivity::class.java
                else if (biller.billerCode == AppConstants.BILLERCODE_NIGERIA_IKEJA && URLGenerator.isNigeria)
                    clazz = WalletPayActivity::class.java
                else if (biller.billerCode == "UUGV073E" || biller.billerCode == "UPGV074E" || biller.billerCode == "UMGV075E" || biller.billerCode == "UDGV076E" || biller.billerCode == "AHMUREL048E" || biller.billerCode == "AHMUBSE047E" || biller.billerCode == "AHMUBSE046E")
                    clazz = PgvclPayActivity::class.java
                else
                    Utils.showToast(this@MainActivity, getString(R.string.coming_soon))
            } else if (category.categoryCode == AppConstants.CATEGORY_LANDLINE) {
                clazz = LandlinePayActivity::class.java
            } else if (category.categoryCode == AppConstants.CATEGORY_GAS) {
                clazz = TorrentPayActivity::class.java
                starter.putExtra(AppConstants.IS_GASUTIL, true)
            } else if (category.categoryCode == AppConstants.CATEGORY_INSURANCE) {
                clazz = InsurancePayActivity::class.java
            } else if (category.categoryCode == AppConstants.CATEGORY_WALLET) {
                clazz = WalletPayActivity::class.java
                /* if (biller.getBillerCode().equals(AppConstants.BILLERCODE_BOOKMYSHOW))
                    clazz = WalletPayActivity.class;
                else
                    Utils.showToast(MainActivity.this, getString(R.string.coming_soon));*/
            } else if (category.categoryCode == AppConstants.CATEGORY_DATACARD) {
                clazz = DatacardPayActivity::class.java
            } else if (category.categoryCode == AppConstants.CATEGORY_DTH) {
                if (biller.billerCode == AppConstants.BILLERCODE_NIGERIA_MUTLICHOICE_CABLETV && URLGenerator.isNigeria)
                    clazz = TelecomRechargeActivity::class.java
                else
                    clazz = DTHPayActivity::class.java
            } else if (category.categoryCode == AppConstants.CATEGORY_CIVICBILLS) {
                if (biller.billerCode == AppConstants.BILLERCODE_AMC_PROPERTYTAX)
                    clazz = AMCPropertyTaxPayActivity::class.java
                else if (biller.billerCode == AppConstants.BILLERCODE_AMC_PROFESSIONALTAX)
                    clazz = AMCProffesionalTaxActivity::class.java
                else if (biller.billerCode == AppConstants.BILLERCODE_JANMITRA)
                    clazz = JanmitraMainActivity::class.java
            } else if (category.categoryCode == AppConstants.CATEGORY_BROADBAND) {
                clazz = BroadbandPayActivity::class.java
            } else if (category.categoryCode == AppConstants.CATEGORY_ECOMMERCE) {
                sendEcommerceCatToCustomer(biller.billerCode)
                Toast.makeText(mContext, "Sent to Customer Screen", Toast.LENGTH_SHORT).show()
            } else if (category.categoryCode == AppConstants.CATEGORY_REMITTANCE) {
                clazz = RemittanceSearchActivity::class.java
            } else if (category.categoryCode == AppConstants.CATEGORY_TRAVEL) {
                if (biller.billerCode == AppConstants.BILLERCODE_HOTEL)
                    clazz = HotelSearchingActivity::class.java
                else if (biller.billerCode == AppConstants.BILLERCODE_AIRTRAVEL)
                    clazz = FlightSearchActivity::class.java
                else if (biller.billerCode == AppConstants.BILLERCODE_REDBUS)
                    clazz = BusSearchActivity::class.java
                else
                    Utils.showToast(this@MainActivity, getString(R.string.coming_soon))
            }
        }

        if (clazz != null) {
            starter.setClass(this@MainActivity, clazz)
            starter.putExtra(AppConstants.OBJ_BILLER, biller)
            startActivity(starter)
        }
    }

    /**
     * Start all handlers
     */
    private fun startHandlers() {
        if (batteryHandler == null) batteryHandler = Handler()
        if (timeHandler == null) timeHandler = Handler()
        if (balanceHandler == null) balanceHandler = Handler()
        batteryHandler!!.post(batteryChecker)
        timeHandler!!.post(timeSetter)
        balanceHandler!!.post(balanceChecker)
    }

    override fun onResume() {
        super.onResume()
        loadBalance(false)
        startHandlers()
        registerBatteryBroadcastReceiver()
        registerSyncBiller()
        registerCartCountReceiver()
        sendEcommerceCatToCustomer(null)
        setFavouriteBillers()
        registerScreenBroadcastReceiver()
        setCartCount(null)
    }

    private fun registerCartCountReceiver() {
        val filter = IntentFilter(AppConstants.CART_COUNT_RECEIVER)
        cartCountReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val cartJson = intent.getStringExtra(AppConstants.CART_JSON)
                if (!TextUtils.isEmpty(cartJson))
                    setCartCount(cartJson)
                else
                    txtCartCount!!.text = "0"
            }
        }

        registerReceiver(cartCountReceiver, filter)
    }

    private fun setCartCount(cartJson: String?) {
        var cartJson = cartJson
        if (TextUtils.isEmpty(cartJson))
            cartJson = Preference.getStringPreference(this@MainActivity, AppConstants.CART_JSON)
        if (!TextUtils.isEmpty(cartJson)) {
            val objTran = gson!!.fromJson<TransactionEntity>(cartJson, TransactionEntity::class.java!!)
            var count = 0
            if (objTran.billsToPay != null) count += objTran.billsToPay.size
            if (objTran.recharges != null) count += objTran.recharges.size
            if (objTran.epMart != null) count += objTran.epMart.productsToBuy.size
            txtCartCount!!.text = count.toString()
        }
    }

    private fun setFavouriteBillers() {
        val favBillers = BillerHelper.getBillers(this@MainActivity, BillerHelper.KEY_favoriteBiller + "=?", arrayOf(AppConstants.TRUE_STATUS.toString()), null, null, BillerHelper.KEY_billerName)
        if (favBillers != null && !favBillers.isEmpty()) {
            setBillers(favBillers)
        }
    }

    override fun onPause() {
        super.onPause()
        removeHandlers()
    }

    override fun onBackPressed() {
        epApp!!.sendCommunicatorMessage("finish")
        epApp!!.stopCommunication()
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(batteryReceiver)
        unregisterReceiver(syncReceiver)
        unregisterReceiver(cartCountReceiver)
        Preference.clearPreference(applicationContext, AppConstants.CART_JSON)
    }

    private fun removeHandlers() {
        batteryHandler!!.removeCallbacks(batteryChecker)
        timeHandler!!.removeCallbacks(timeSetter)
        balanceHandler!!.removeCallbacks(balanceChecker)

    }

    /**
     * Set battery image
     *
     * @param status
     */
    private fun setBatteryImage(status: Int) {
        when (status) {
            BATTERY_STATUS_CHARGING -> imgBattery!!.setImageResource(R.drawable.ic_bcharging)
            BATTERY_STATUS_FULL -> imgBattery!!.setImageResource(R.drawable.ic_bfull)
            AppConstants.BATTERY_STATUS_CHARGED -> imgBattery!!.setImageResource(R.drawable.ic_bcharged)
            AppConstants.BATTERY_STATUS_HALF -> imgBattery!!.setImageResource(R.drawable.ic_bhalf)
            AppConstants.BATTERY_STATUS_LOW -> imgBattery!!.setImageResource(R.drawable.ic_blow)
            AppConstants.BATTERY_STATUS_EMPTY -> imgBattery!!.setImageResource(R.drawable.ic_bempty)
        }
    }

    /**
     * Receiver to catch battery change events
     */
    private fun registerBatteryBroadcastReceiver() {
        val batteryFilter = IntentFilter()
        batteryFilter.addAction(Intent.ACTION_POWER_CONNECTED)
        batteryFilter.addAction(Intent.ACTION_POWER_DISCONNECTED)
        batteryFilter.addAction(Intent.ACTION_BATTERY_LOW)
        batteryFilter.addAction(Intent.ACTION_BATTERY_OKAY)
        if (batteryReceiver == null) {
            batteryReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    val chargingStatus = DeviceInfo.getBatteryStatus(this@MainActivity)
                    setBatteryImage(chargingStatus)
                }
            }
        }
        registerReceiver(batteryReceiver, batteryFilter)
    }

    override fun menuSelected(position: Int) {
        var clazz: Class<*>? = null
        when (position) {
            AppConstants.MENUPOS_HOME -> {
            }
            AppConstants.MENUPOS_NOTIFICATION -> clazz = NotificationActivity::class.java
            AppConstants.MENUPOS_COMPLAINT -> clazz = ComplaintActivity::class.java
            AppConstants.MENUPOS_RECHARGE -> clazz = RechargeActivity::class.java
            AppConstants.MENUPOS_SUMMARY -> clazz = SummaryActivity::class.java
            AppConstants.MENUPOS_CASHRECIPT -> clazz = CashPaymentActivity::class.java
            AppConstants.MENUPOS_DASHBOARD -> clazz = DashBoardActivity::class.java
            AppConstants.MENUPOS_SETTING -> clazz = SettingActivity::class.java
            AppConstants.MENUPOS_REFER -> clazz = ReferRetailerActivity::class.java
            AppConstants.MENUPOS_HISTORY -> clazz = TransactionHistoryActivity::class.java
            AppConstants.MENUPOS_PROMOTION -> clazz = PromotionListActivity::class.java
        }
        if (clazz != null) {
            val starter = Intent(this@MainActivity, clazz)
            startActivity(starter)
        }
    }

    private fun registerSyncBiller() {
        val filter = IntentFilter(AppConstants.ACTION_SYNCBILLER)
        syncReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                DatabaseHelper.truncateTables(this@MainActivity)
                loadBillersCategories()
            }
        }
        registerReceiver(syncReceiver, filter)
    }

    internal inner class CyclerPagerAdapter(var billerEntities: List<BillerEntity>) : PagerAdapter() {

        var mLayoutInflater: LayoutInflater

        init {
            mLayoutInflater = LayoutInflater.from(this@MainActivity)
        }

        override fun getItemPosition(`object`: Any): Int {
            return PagerAdapter.POSITION_NONE
        }

        override fun getCount(): Int {
            return billerEntities.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object` as FrameLayout
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val itemView = mLayoutInflater.inflate(R.layout.coverflow_item, container, false)

            val billerName = itemView.findViewById(R.id.name) as TextView
            val imageView = itemView.findViewById(R.id.image) as SimpleDraweeView

            val biller = billerEntities[position]

            // viewHolder.billerImage.setImageUrl(biller.getLogoPath(),imageLoader);
            imageView.setImageURI(Uri.parse(biller.logoPath))
            billerName.text = biller.billerName

            itemView.setOnClickListener {
                val biller = billerEntities[position]
                openBillerScreen(biller)
            }

            /*  convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onRecyclerviewItemAction.onItemClicked(position);
                }
            });
*/
            container.addView(itemView)

            return itemView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as FrameLayout)
        }

        /*  public void setBillers(List<BillerEntity> billerEntities) {
            this.billerEntities = billerEntities;
        }*/
    }

    companion object {


        private val TAG = "MainActivity"
        private val BALANCE_CHECK_INTERVAL = (3 * 60000).toLong()
        private val MIN_SCALE = 0.85f
        private val MIN_ALPHA = 0.5f
    }


}
