package com.mymuslem.sarrawi.db

import android.content.Context
import androidx.lifecycle.LiveData
import com.mymuslem.sarrawi.db.Dao.ZekerTypesDao
import com.mymuslem.sarrawi.models.ZekerTypes

class LocaleSource(context: Context) {

//    private var  zekerTypesDao: ZekerTypesDao?
//
//    init{
//        val dataBase = AzkarDB.getInstance(context.applicationContext)
//        zekerTypesDao = dataBase.TypesDao()
//    }
//
//    companion object {
//        private var sourceConcreteClass: LocaleSource? = null
//        fun getInstance(context: Context): LocaleSource {
//            if (sourceConcreteClass == null)
//                sourceConcreteClass = LocaleSource(context)
//            return sourceConcreteClass as LocaleSource
//        }
//    }
//
//    fun getzekerTypesDao(): LiveData<List<ZekerTypes>> {
//        return zekerTypesDao?.getAllZekerTypesDao()!!
//    }
}