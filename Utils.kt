package com.easypay.easypaypos.common

import android.content.Context
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.easypay.easypaypos.dialogs.DialogAlert
import com.example.admin.easypaypos.R
import java.text.NumberFormat
import java.util.*


/**
 * Created by ADMIN on 7/18/2016.
 */
object Utils {
    private val UNIQ_DIGIT_LENGTH = 14
    private val indianLocal = Locale("en", "IN")

    @JvmOverloads
    fun showAlert(context: Context, message: String?, btnClick: View.OnClickListener = View.OnClickListener { }) {
        /* new AlertDialog.Builder(context).setMessage(message).setTitle("").setView(R.layout.dialog_single_button)
                .setNegativeButton(context.getString(R.string.ok), btnClick).show();*/
        val dialog=DialogAlert(context)
        message?.let{dialog.setMessage(it)}
        dialog.setNegativeButton(context.getString(R.string.ok), btnClick).show()

    }

    fun showAlert(context: Context, message: String, title: String, yesClick: View.OnClickListener) {
        DialogAlert(context).setMessage(message)
                .setPositiveButton(context.getString(R.string.yes), yesClick)
                .setNegativeButton(context.getString(R.string.no), View.OnClickListener {  }).show()
    }


    fun spToPixel(context: Context, sp: Int): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp.toFloat(), context.resources.displayMetrics)
    }

    fun dpToPixel(context: Context, px: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, context.resources.displayMetrics).toInt()
    }

    fun showToast(context: Context, message: String) {
        val inflater = LayoutInflater.from(context)
        val customToastView = inflater.inflate(R.layout.toastview, null)
        val textView = customToastView!!.findViewById(R.id.toast_txtview) as TextView
        textView.text = message
        val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.view = customToastView
        toast.show()
    }


    fun formatAmount(amount: String?): String {
        return if (!TextUtils.isEmpty(amount)) {
            try {
                formatAmount(java.lang.Double.parseDouble(amount))
            } catch (e: NumberFormatException) {
                amount?:"0.0"
            }

        } else
            formatAmount(0.0)
    }

    fun formatAmount(amount: Double): String {
        //return AppConstants.RUPEE_SYMBOL + String.format(" %.2f", Amount);
        val formatter = NumberFormat.getCurrencyInstance(indianLocal)
        return formatter.format(amount).trim { it <= ' ' }
    }

    val uniqueOrderId: String
        get() {
            val random = Random()
            val digits = StringBuilder()
            digits.append(random.nextInt(9) )
            for (i in 1 until UNIQ_DIGIT_LENGTH) {
                digits.append(random.nextInt(10) + 0)
            }
            return digits.toString()
        }

    fun appendZero(data: String): String {
        var number: String = ""
        number = data
        if (data.length < 2) {
            number = "0" + data
            return number
        } else
            return data

    }
}
