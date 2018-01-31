package com.easypay.easypaypos.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView

import com.example.admin.easypaypos.R


/**
 * Created by Viral on 12-10-2017.
 */

class DialogAlert(private val mContext: Context) : Dialog(mContext) {

    private var btnCancel: Button? = null
    private var btnOk: Button? = null
    private val txtMessage: TextView? = null
    private val inflater: LayoutInflater? = null
    private val view: View

    init {
        val inflater = LayoutInflater.from(mContext)
        view = inflater.inflate(R.layout.dialog_two_button, null)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        super.setContentView(view)
    }


    fun setMessage(message: String): DialogAlert {
        val txtMessage = view.findViewById(R.id.tvMessage) as TextView
        txtMessage.text = message
        return this
    }


    fun setPositiveButton(btnText: String, yesClick: View.OnClickListener): DialogAlert {
        btnOk = view.findViewById(R.id.tvOk) as Button
        btnOk!!.visibility = View.VISIBLE
        btnOk!!.text = btnText
        btnOk!!.setOnClickListener { v ->
            yesClick.onClick(v)
            dismiss()
        }
        //dismiss();
        return this
    }

    fun setNegativeButton(btnText: String, noClick: View.OnClickListener): DialogAlert {
        btnCancel = view.findViewById(R.id.tvCancel) as Button
        btnCancel!!.visibility = View.VISIBLE
        btnCancel!!.text = btnText
        btnCancel!!.setOnClickListener { v ->
            noClick.onClick(v)
            dismiss()
        }
        //dismiss();
        return this
    }

}
