package com.mymuslem.sarrawi.db.Dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mymuslem.sarrawi.models.Letters
import com.mymuslem.sarrawi.models.ZekerTypes

@Dao
interface ZekerTypesDao {

    @Query("select * from one")
    fun getAllZekerTypesDao(): LiveData<List<Letters>>

    @Query("Update one SET Fav = :state where ID =:ID")
    suspend fun update_fav(ID:Int,state:Int)

//    @Insert
//    suspend fun insert_zekerTypes (zekerTypes: ZekerTypes)


    @Query("SELECT * FROM one WHERE ID LIKE :searchQuery " +
            "OR Name_Filter LIKE :searchQuery")
    fun searchZekerTypes(searchQuery: String): LiveData<List<Letters>>

}