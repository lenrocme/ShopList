package com.example.shopitemsfragmentsbm

import android.app.Activity
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity

class GlobalSharedPreferences(mainActivity: MainActivity) : MainActivity() {
    val sharedPre: SharedPreferences = mainActivity.getSharedPreferences("shared pre", AppCompatActivity.MODE_PRIVATE)

    fun savePreferedTheme() {
        val editor: SharedPreferences.Editor = sharedPre.edit()
        editor.putInt("ThemeMode", SELECTOR_THEME)
        editor.apply()
    }

    fun loadPreferedTheme() {
        SELECTOR_THEME = sharedPre.getInt("ThemeMode", 0)
    }

    fun saveLastBtmSelectedItemMenu(last_selected_item_menu: Int) {
        val editor: SharedPreferences.Editor = sharedPre.edit()
        editor.putInt("Last_selected_item_menu", last_selected_item_menu)
        editor.apply()
    }

    fun loadLastBtmSelectedItemMenu(): Int {
        return sharedPre.getInt("Last_selected_item_menu", 0)
    }
}