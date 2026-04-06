package com.mymuslem.sarrawi.db.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.mymuslem.sarrawi.db.repository.ZekerTypesRepository
import com.mymuslem.sarrawi.models.FavoriteModel
import com.mymuslem.sarrawi.models.Letters
import kotlinx.coroutines.launch


class ZekerTypesViewModel constructor(application : Application) : ViewModel() {

    private var __response = MutableLiveData<List<FavoriteModel>>()
    val responseMsgsFav: MutableLiveData<List<FavoriteModel>>
        get() = __response
    private val zekerTypesRepository: ZekerTypesRepository = ZekerTypesRepository(application)


    fun getAllZekerTypes(): LiveData<List<Letters>> = zekerTypesRepository.getAllZekerTypes()

    fun SearchViewModel(searchQuery: String): LiveData<List<Letters>> = zekerTypesRepository.searchRepo(searchQuery)

    // update msg_table items favorite state
    fun update_fav(id: Int,state:Int) = viewModelScope.launch {
        zekerTypesRepository.update_fav(id,state)
    }

    fun getFav(): LiveData<List<FavoriteModel>> {
        Log.e("tessst","entered22")


        return zekerTypesRepository.getAllFav()
    }

    fun add_fav(fav: FavoriteModel)= viewModelScope.launch {
        zekerTypesRepository.add_fav(fav)
    }

    // delete favorite item from db
    fun delete_fav(fav: FavoriteModel)= viewModelScope.launch {
        zekerTypesRepository.delete_fav(fav)
    }
    /*********/

    class AzkarViewModelFactory(private val application: Application): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(ZekerTypesViewModel::class.java)){
                @Suppress("UNCHECKED_CAST")
                return ZekerTypesViewModel(application) as T
            }
            throw IllegalArgumentException("Unable Constructore View Model")
        }
    }

}

