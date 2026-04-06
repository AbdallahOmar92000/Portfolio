package com.mymuslem.sarrawi.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mymuslem.sarrawi.R
import com.mymuslem.sarrawi.databinding.ZekerTypesDeBinding
import com.mymuslem.sarrawi.db.viewModel.SettingsViewModel
import com.mymuslem.sarrawi.models.Letters
import com.mymuslem.sarrawi.models.ZekerTypes

class ZekerTypes_Adapter(val con: Context,private val fragment: Fragment,var fontFamily: Typeface?=null):RecyclerView.Adapter<ZekerTypes_Adapter.MyViewHolder>() {

    var onFavClick: ((item:Letters,position:Int) -> Unit)? = null
//    var onItemClick: ((Int,String) -> Unit)? = null
    var onItemClick: ((Int) -> Unit)? = null
    var settingsViewModel = ViewModelProvider(fragment).get(SettingsViewModel::class.java)
    private var selectedTypeface: Typeface = Typeface.DEFAULT // نوع الخط الافتراضي


    @SuppressLint("NotifyDataSetChanged")
    inner class MyViewHolder(val binding: ZekerTypesDeBinding): RecyclerView.ViewHolder(binding.root) {


        init {
            binding.root.setOnClickListener {
                onItemClick?.invoke(zekerTypes_list[layoutPosition].ID?:0)
            }
        }

        fun bind(position: Int) {

            val current_zekerTypes_list = zekerTypes_list[position]
            binding.apply {
                titleDoaa.text=current_zekerTypes_list.Name
                titleDoaa.textSize = settingsViewModel.fontSize.toFloat()
                binding.titleDoaa.textSize = settingsViewModel.fontSize.toFloat()
//                titleDoaa.typeface = Typeface.create(settingsViewModel.fontType, Typeface.NORMAL)

                binding.titleDoaa.typeface = selectedTypeface

                if (current_zekerTypes_list!!.Fav==0) {
                    imgFav.setImageResource(R.mipmap.nf)
                } else {
                    imgFav.setImageResource(R.mipmap.f)
                }

                fontFamily?.let {
                    titleDoaa.typeface = it
                }
            }


            binding.imgFav.setOnClickListener{
                onFavClick?.invoke(zekerTypes_list[position], position)
            }


        }

    }

    private val diffCallback = object : DiffUtil.ItemCallback<Letters>(){
        override fun areItemsTheSame(oldItem: Letters, newItem: Letters): Boolean {
            return oldItem.ID == newItem.ID
        }

        override fun areContentsTheSame(oldItem: Letters, newItem: Letters): Boolean {
            return newItem == oldItem
        }

    }

    private val differ = AsyncListDiffer(this, diffCallback)
    var zekerTypes_list: List<Letters>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ZekerTypesDeBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.bind(position)
//        holder.binding.apply {
//            titleDoaa.text=current_zekerTypes_list.Name
////            tvMsgM.text=current_zekerTypes_list.MessageName
//        }
    }

    override fun getItemCount(): Int {
        return zekerTypes_list.size
    }
    fun setFont(font: Typeface?) {
        this.fontFamily = font
        notifyDataSetChanged()
    }

    fun setLetters(zekerTypes_list:List<Letters>){
        this.zekerTypes_list = zekerTypes_list
        notifyDataSetChanged()
    }

}