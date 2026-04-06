package com.mymuslem.sarrawi

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity

class  SharedPref(var con: Context) {
    var isDark: Boolean= false


    private val sharedPreferences =
    con.getSharedPreferences("MyPrefss", AppCompatActivity.MODE_PRIVATE)

    fun saveThemeStatePref(isDark: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("is_dark_mode", isDark)
        editor.apply()
    }

    fun getThemeStatePref(): Boolean {
        return sharedPreferences.getBoolean("is_dark_mode", false)
    }



    fun saveFontSize(fontSize: Int) {
        with(sharedPreferences.edit()) {
            putInt("font_size", fontSize)
            apply()
        }
    }

    fun getFontSize(): Int {
        return sharedPreferences.getInt("font_size", 14)
    }

    fun saveFontType(fontType: Int) {
        with(sharedPreferences.edit()) {
            putInt("font", fontType)
            apply()
        }
    }

    fun getFontType(): Int {
        return sharedPreferences.getInt("font", 0)
    }
}