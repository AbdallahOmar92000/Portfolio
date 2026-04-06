package com.mymuslem.sarrawi.db.viewModel

import android.app.Application
import androidx.lifecycle.*
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModel
import android.content.Context
import android.graphics.Typeface

class SettingsViewModel(application: Application): AndroidViewModel(application) {
    var fontSize: Int = 20 // قيمة افتراضية لحجم الخط

    private var selectedFontPosition = 0

    fun getSelectedFontPosition(): Int {
        return selectedFontPosition
    }

    fun setSelectedFontPosition(position: Int) {
        selectedFontPosition = position
    }



    private val _selectedTypeface = MutableLiveData<Typeface>()
    val selectedTypeface: LiveData<Typeface> get() = _selectedTypeface

    fun setSelectedTypeface(typeface: Typeface) {
        _selectedTypeface.value = typeface
    }

    class SettingsViewModelFactory(private val application: Application): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
                return SettingsViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}