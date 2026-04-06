package com.mymuslem.sarrawi.db.viewModel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mymuslem.sarrawi.db.repository.ZekerRepo
import com.mymuslem.sarrawi.models.Zekr

class ZekerViewModel constructor(application : Application) : ViewModel() {

    private val zekerRepository: ZekerRepo = ZekerRepo(application)

    fun getAllZeker(TypeID:Int): LiveData<List<Zekr>> = zekerRepository.getAllZekerRepo(TypeID)

    class AzkarViewModelFactory(private val application: Application): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(ZekerViewModel::class.java)){
                @Suppress("UNCHECKED_CAST")
                return ZekerViewModel(application) as T
            }
            throw IllegalArgumentException("Unable Constructore View Model")
        }
    }
}