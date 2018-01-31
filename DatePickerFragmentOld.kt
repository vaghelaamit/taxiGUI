package com.easypay.easypaypos.dialogs

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log

import com.easypay.easypaypos.acitvities.DashBoardActivity

import java.util.Calendar

/**
 * Created by ADMIN on 7/26/2016.
 */
class DatePickerFragmentOld : DialogFragment() {
    private var isStartDate: Boolean = false
    private var dashActivity: DashBoardActivity? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            dashActivity = context as DashBoardActivity
        } catch (e: ClassCastException) {
            Log.e(TAG, "onAttach: ", e)
        }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val initialYear = c.get(Calendar.YEAR)
        val initialMonth = c.get(Calendar.MONTH)
        val initialDay = c.get(Calendar.DAY_OF_MONTH)

        isStartDate = arguments.getBoolean("isStartDate")

        return object : DatePickerDialog(activity, null, initialYear, initialMonth, initialDay) {
            override fun onClick(dialog: DialogInterface, which: Int) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    val year = datePicker.year
                    val month = datePicker.month
                    val day = datePicker.dayOfMonth

                    val calendar = Calendar.getInstance()
                    calendar.set(year, month, day)

                    dashActivity!!.setDate(calendar.timeInMillis, isStartDate)
                }
                super.onClick(dialog, which)
            }
        }
    }

    companion object {

        private val TAG = "DatePickerFragmentOld"
    }

}