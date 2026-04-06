package com.mymuslem.sarrawi.db.repository


import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import com.mymuslem.sarrawi.db.AzkarDB
import com.mymuslem.sarrawi.db.Dao.FavoriteDao
import com.mymuslem.sarrawi.db.Dao.ZekerTypesDao
import com.mymuslem.sarrawi.db.LocaleSource
import com.mymuslem.sarrawi.models.FavoriteModel
import com.mymuslem.sarrawi.models.Letters
import com.mymuslem.sarrawi.models.ZekerTypes

class ZekerTypesRepository(app:Application) {

    private var zekerTypesDao:ZekerTypesDao
    private var fav_Dao: FavoriteDao?

    private var allZeker: LiveData<List<Letters>>


    init{
        val database = AzkarDB.getInstance(app)
        zekerTypesDao = database.getTypesDao()
        allZeker = zekerTypesDao.getAllZekerTypesDao()
        fav_Dao = database.FavoriteDao()
    }

    fun getAllZekerTypes(): LiveData<List<Letters>> {
        return zekerTypesDao.getAllZekerTypesDao()
    }

    suspend fun add_fav(fav: FavoriteModel){
        fav_Dao?.add_fav(fav)
    }

    fun getAllFav(): LiveData<List<FavoriteModel>>{
        Log.e("tessst","entred666")
        return fav_Dao?.getAllFav()!!
    }

    // delete favorite item from db
    suspend fun delete_fav(fav:FavoriteModel) {
        fav_Dao?.deletefav(fav)!!
    }

    // update msg_table items favorite state
    suspend fun update_fav(id: Int,state:Int) {
        zekerTypesDao.update_fav(id,state)
    }

    fun searchRepo(searchQuery: String): LiveData<List<Letters>> {
        return zekerTypesDao.searchZekerTypes(searchQuery)
    }
}