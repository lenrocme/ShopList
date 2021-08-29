package com.example.shopitemsfragmentsbm

import android.app.Activity
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity

class GlobalSharedPreferences: MainActivity() {

    fun savePreferedTheme(activity: Activity) {
        val sharedPre: SharedPreferences = activity.getSharedPreferences("shared pre", AppCompatActivity.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPre.edit()
        editor.putInt("ThemeMode", SELECTOR_THEME)
        editor.apply()
    }

    fun loadPreferedTheme(activity: Activity) {
        val sharedPre: SharedPreferences = activity.getSharedPreferences("shared pre", AppCompatActivity.MODE_PRIVATE)
            // on load set index Theme from sharedpreferences
        SELECTOR_THEME = sharedPre.getInt("ThemeMode", 0)
    }
}