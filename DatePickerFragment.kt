package com.easypay.easypaypos.dialogs

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import com.easypay.easypaypos.common.Utils
import com.easypay.easypaypos.interfaces.OnDateSet
import com.example.admin.easypaypos.R
import java.util.*

/**
 * Created by Viral on 02-01-2017.
 */

@SuppressLint("ValidFragment")
class DatePickerFragment : DialogFragment {
    private var dateSet: OnDateSet? = null
    private var time: Long = 0
    private var checkForValid: Boolean = false


    constructor() {}

    constructor(dateSet: OnDateSet, time: Long, checkForValid: Boolean) {
        this.dateSet = dateSet
        this.time = time
        this.checkForValid = checkForValid

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        if (time > 0)
            calendar.timeInMillis = time

        val initialYear = calendar.get(Calendar.YEAR)
        val initialMonth = calendar.get(Calendar.MONTH)
        val initialDay = calendar.get(Calendar.DAY_OF_MONTH)

        return object : DatePickerDialog(activity, null, initialYear, initialMonth, initialDay) {

            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                if (checkForValid)
                    datePicker.minDate = System.currentTimeMillis()
            }

            override fun onClick(dialog: DialogInterface, which: Int) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    val year = datePicker.year
                    val month = datePicker.month
                    val day = datePicker.dayOfMonth

                    val calendar = Calendar.getInstance()

                    calendar.set(year, month, day)
                    if (checkForValid && calendar.timeInMillis < System.currentTimeMillis()) {
                        Utils.showToast(activity!!, getString(R.string.select_valid_date))
                        return
                    }
                    dateSet!!.dateSet(calendar.timeInMillis)
                }
                super.onClick(dialog, which)
            }
        }
    }

    companion object {

        private val TAG = "DatePickerFragment"
    }
}
