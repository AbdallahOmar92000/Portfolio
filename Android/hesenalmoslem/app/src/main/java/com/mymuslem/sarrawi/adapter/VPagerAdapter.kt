package com.mymuslem.sarrawi.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.mymuslem.sarrawi.R
import com.mymuslem.sarrawi.db.viewModel.SettingsViewModel
import com.mymuslem.sarrawi.models.Zekr


class VPagerAdapter(val con: Context, private var zeker_list:List<Zekr>, val fragment: Fragment, var fontFamily: Typeface?=null): RecyclerView.Adapter<VPagerAdapter.Pager2View>() {

    var settingsViewModel = ViewModelProvider(fragment).get(SettingsViewModel::class.java)
    private var selectedTypeface: Typeface = Typeface.DEFAULT // نوع الخط الافتراضي


    inner class Pager2View(itemView: View): RecyclerView.ViewHolder(itemView) {
        var adView: AdView?=null
        var tv_zeker:TextView=itemView.findViewById(R.id.tv_zeker)
        var tv_count:TextView=itemView.findViewById(R.id.tv_count)
        var tv_D:TextView=itemView.findViewById(R.id.tv_d)
        var btn_count:Button=itemView.findViewById(R.id.button1)
        init {
//            adView= itemView.findViewById(R.id.adView)
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Pager2View {
       return Pager2View(LayoutInflater.from(parent.context).inflate(R.layout.item_pager,parent,false))
    }

    override fun onBindViewHolder(holder: Pager2View, position: Int) {
        val zekr: Zekr = zeker_list.get(position)
//        holder.title.setText(m.getName())
        holder.tv_zeker.text = zekr.Description
        holder.tv_zeker.textSize = settingsViewModel.fontSize.toFloat()
        holder.tv_count.text = zekr.couner
        holder.tv_D.text = zekr.hint
        holder.tv_zeker.typeface = selectedTypeface
        fontFamily?.let {
            holder.tv_zeker.typeface = it
        }
//        holder.btn_count.setOnClickListener{
//            counterr++
//            holder.btn_count.setText(Integer.toString(counterr))
//            holder.btn_count.setTextColor(Color.BLUE)
//            0
//
//        }

        var counterr = 0 // تعيين المتغير الذي يحمل قيمة العداد
        val number = convertWordToNumber(holder.tv_count.text.toString()) // تحويل النص إلى رقم

        holder.btn_count.setOnClickListener { v ->
            if (counterr < number) {
                // زيادة العداد بمقدار واحد في كل نقرة على الزر حتى يصل إلى العدد المكتوب
                counterr++

                // قم بأداء الإجراءات التي ترغب فيها هنا، مثل النقر على الزر
                // يمكنك استدعاء الدالة التي تتعامل مع النقر على الزر هنا

                // بعد الانتهاء من النقر، قم بتحديث نص الزر والعداد
                holder.btn_count.text = counterr.toString()
                holder.tv_count.text = counterr.toString()
                holder.btn_count.setTextColor(Color.BLUE)
            } else {
                // عندما يصل العداد إلى العدد المكتوب، لن يتم زيادة العداد بعد ذلك
                // يمكنك تنفيذ سياسة خاصة للتعامل مع هذه الحالة هنا
            }
        }

//        holder.adView?.loadAd(AdRequest.Builder().build())  // تحميل الإعلان



    }

    override fun getItemCount(): Int {
        return zeker_list.size
    }

    fun updateData(newZekerList: List<Zekr>) {
        zeker_list = newZekerList
        notifyDataSetChanged()
    }

    fun setFont(font: Typeface?) {
        this.fontFamily = font
        notifyDataSetChanged()
    }

    fun convertWordToNumber(word: String): Int {
        val numbers = listOf(

            Pair("",1),
            Pair("ثلاث مرَّاتٍ",3),
            Pair("ثلاث مرَّاتٍ",3),
            Pair("بَعْدَ السّلامِ مِنْ صَلاَةِ الفَجْرِ",1),
            Pair("عَشْرَ مَرّاتٍ بَعْدَ صَلاةِ الْمَغْرِبِ وَالصُّبْحِ",10),
            Pair("عشرَ مرَّاتٍ",10),
            Pair("مِائَةَ مَرَّةٍ فِي الْيَوْمِ",100),
            Pair("ثلاثَ مرَّاتٍ",3),
            Pair("مائةَ مرَّةٍ",100),
            Pair("عشرَ مرَّات أَو مرَّةً واحدةً عندَ الكَسَل",10),
            Pair("مائة مرَّةٍ",100),
            Pair("ثلاثَ مرَّاتٍ",3),
            Pair("ثلاثَ مرَّاتٍ",3),
            Pair("سَبْعَ مَرّاتٍ",7),
            Pair("ثلاثَ مرَّاتٍ",3),
            Pair("أربعَ مَرَّاتٍ",4),
            Pair("ثلاثَ مرَّاتٍ",3),
            Pair("عشرَ مرَّاتٍ",10),
            Pair("ثلاثَ مرَّاتٍ",3),
            Pair("مِائَةَ مَرَّةٍ فِي الْيَوْمِ",100),
            Pair("عشرَ مرَّات أَو مرَّةً واحدةً عندَ الكَسَل",10),
            Pair("مائة مرَّةٍ",100),
            Pair("ثلاثَ مرَّاتٍ",3),
            Pair("ثلاثَ مرَّاتٍ",3),
            Pair("سَبْعَ مَرّاتٍ",7),
            Pair("ثلاثَ مرَّاتٍ",3),
            Pair("أربعَ مَرَّاتٍ",4),
            Pair("ثلاثَ مرَّاتٍ",3),
            Pair("أربعاً وثلاثينَ",34),
            Pair("ثلاثاً وثلاثين",33),
            Pair("ثلاثاً وثلاثين",33),
            Pair("يفعلُ ذلك ثلاثَ مرَّاتٍ",3),
            Pair("ثَلاَثَ مَرَّاتٍ",3),
            Pair("ثلاثاً",3),
            Pair("ثلاثَ مرَّاتٍ والثَّالِثَةُ يَجْهَرُ بها ويَمُدُّ بها صَوتَهُ يقولُ: [رَبِّ الْمَلاَئِكَةِ وَالرُّوحِ]",3),
            Pair("ثلاثاً",3),
            Pair("سبع مرات",7)

            // استكمل الأعداد الباقية حسب حاجتك
        )

        return numbers.firstOrNull { it.first == word }?.second ?: 0
    }

}