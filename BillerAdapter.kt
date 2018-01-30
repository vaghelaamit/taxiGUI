package com.easypay.easypaypos.adapter

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import android.widget.TextView

import com.easypay.easypaypos.entities.CategoryEntity
import com.example.admin.easypaypos.R
import com.facebook.drawee.view.SimpleDraweeView

/**
 * Created by ADMIN on 7/15/2016.
 */
class BillerAdapter(private val context: Context, private val billerCategories: List<CategoryEntity>, private val onBillerClick: OnBillerClick) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var selectedItem = 0
    private val inflater: LayoutInflater
    private val scaleUp: Animation
    private val scaleDown: Animation

    init {
        inflater = LayoutInflater.from(context)
        scaleDown = AnimationUtils.loadAnimation(context, R.anim.scale_down)
        scaleUp = AnimationUtils.loadAnimation(context, R.anim.scale_up)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return BillerHolder(inflater.inflate(R.layout.biller_item, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val billerHolder = holder as BillerHolder

        val category = billerCategories[position]

        billerHolder.txtName.text = category.categoryName

        billerHolder.imgView.setImageURI(Uri.parse(category.catImagePath))

        billerHolder.baseView.setOnClickListener {
            onBillerClick.billerClicked(position)
            notifyItemChanged(selectedItem)
            selectedItem = position
            notifyItemChanged(selectedItem)
        }

        if (selectedItem == position) {
            //billerHolder.layoutImage.setBackgroundResource(R.drawable.red_stroke);
            billerHolder.imgView.startAnimation(scaleUp)
        }
        /*else {
            billerHolder.imgView.setBackgroundResource(R.drawable.bg_component);
        }*/

    }

    override fun getItemCount(): Int {
        return billerCategories.size
    }

    interface OnBillerClick {
        fun billerClicked(position: Int)
    }

    private class BillerHolder(internal var baseView: View) : RecyclerView.ViewHolder(baseView) {
        internal var imgView: SimpleDraweeView
        internal var txtName: TextView
        internal var layoutImage: RelativeLayout

        init {
            layoutImage = baseView.findViewById(R.id.billeritem_layoutimgbiller) as RelativeLayout
            imgView = baseView.findViewById(R.id.billeritem_imgbiller) as SimpleDraweeView
            txtName = baseView.findViewById(R.id.billeritem_txtname) as TextView
        }
    }
}
