package com.mymuslem.sarrawi.db.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.mymuslem.sarrawi.db.AzkarDB
import com.mymuslem.sarrawi.db.Dao.ZekerDao
import com.mymuslem.sarrawi.models.Letters
import com.mymuslem.sarrawi.models.Zekr

class ZekerRepo(app: Application) {

    private var zekerDao:ZekerDao
    private var allZeker: LiveData<List<Zekr>>
    val TypeID:Int = 0

    init{
        val database = AzkarDB.getInstance(app)
        zekerDao = database.geZekerDao()
        allZeker=zekerDao.getAllZekerDao(TypeID)
    }

    fun getAllZekerRepo(TypeID:Int): LiveData<List<Zekr>> {
        return zekerDao.getAllZekerDao(TypeID)
    }
}