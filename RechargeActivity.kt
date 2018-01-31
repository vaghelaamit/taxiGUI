package com.easypay.easypaypos.acitvities

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

import com.easypay.easypaypos.fragments.RechargeStatusFragment
import com.easypay.easypaypos.fragments.RequestRechargeFragment
import com.easypay.easypaypos.interfaces.OnViewpagerPageSelected
import com.easypay.easypaypos.views.CustomViewPager
import com.example.admin.easypaypos.R

class RechargeActivity : AppCompatActivity() {

    private var pager: CustomViewPager? = null
    private var txtTabLeft: TextView? = null
    private var txtTabRight: TextView? = null
    private var baseCardView: CardView? = null
    private var btnCancel: Button? = null
    private var tabLayout: TabLayout? = null
    private var rechargeAdapter: RechargePagerAdapter? = null
    private var pageChangeListener: TabPageChangeListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recharge)

        init()
        baseCardView!!.preventCornerOverlap = false

        pageChangeListener = TabPageChangeListener(tabLayout!!)
        pager!!.addOnPageChangeListener(pageChangeListener)

        tabLayout!!.setupWithViewPager(pager)

        txtTabLeft!!.setOnClickListener {
            tabLeftSelected()
            pager!!.setCurrentItem(0, true)
        }

        txtTabRight!!.setOnClickListener {
            tabRightSelected()
            pager!!.setCurrentItem(1, true)
        }

        rechargeAdapter = RechargePagerAdapter(supportFragmentManager)
        pager!!.adapter = rechargeAdapter

        btnCancel!!.setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        pager!!.addOnPageChangeListener(pageChangeListener)
    }

    override fun onPause() {
        super.onPause()
        pager!!.removeOnPageChangeListener(pageChangeListener)
    }

    private fun tabRightSelected() {
        txtTabRight!!.setBackgroundColor(Color.WHITE)
        txtTabLeft!!.setBackgroundResource(R.drawable.black_border)

    }

    private fun tabLeftSelected() {
        txtTabLeft!!.setBackgroundColor(Color.WHITE)
        txtTabRight!!.setBackgroundResource(R.drawable.black_border)
    }

    private fun init() {
        tabLayout = findViewById(R.id.recharge_tablayout) as TabLayout
        btnCancel = findViewById(R.id.recharge_btncancel) as Button
        baseCardView = findViewById(R.id.recharge_basecardview) as CardView
        pager = findViewById(R.id.recharge_pager) as CustomViewPager
        txtTabLeft = findViewById(R.id.recharge_txttableft) as TextView
        txtTabRight = findViewById(R.id.recharge_txttabright) as TextView
    }

    private inner class TabPageChangeListener(tabLayout: TabLayout) : TabLayout.TabLayoutOnPageChangeListener(tabLayout) {

        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)

            val fragment = rechargeAdapter!!.getRegisteredFragment(position)
            (fragment as OnViewpagerPageSelected).pageSelected(position)
        }
    }

    private inner class RechargePagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        internal var registeredFragments = SparseArray<Fragment>()

        override fun getItem(position: Int): Fragment? {
            var fragment: Fragment? = null
            when (position) {
                0 -> fragment = RequestRechargeFragment()
                1 -> fragment = RechargeStatusFragment()
            }
            return fragment
        }

        override fun getCount(): Int {
            return 2
        }


        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val fragment = super.instantiateItem(container, position) as Fragment
            registeredFragments.put(position, fragment)
            return fragment
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            registeredFragments.remove(position)
            super.destroyItem(container, position, `object`)
        }

        fun getRegisteredFragment(position: Int): Fragment {
            return registeredFragments.get(position)
        }

        override fun getPageTitle(position: Int): CharSequence {
            when (position) {
                0 -> return getString(R.string.new_recharge_request)
                1 -> return getString(R.string.recharge_status)
            }
            return ""
        }
    }


}
