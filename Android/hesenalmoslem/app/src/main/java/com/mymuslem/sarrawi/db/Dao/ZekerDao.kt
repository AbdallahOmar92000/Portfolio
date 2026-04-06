package com.mymuslem.sarrawi.db.Dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.mymuslem.sarrawi.models.Zekr

@Dao
interface ZekerDao {

    @Query("SELECT * FROM zekr where Name_ID=:TypeID")
    fun getAllZekerDao(TypeID:Int): LiveData<List<Zekr>>
}