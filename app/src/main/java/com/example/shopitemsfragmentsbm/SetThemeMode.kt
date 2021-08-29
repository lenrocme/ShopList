package com.example.shopitemsfragmentsbm

import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.bottomnavigation.BottomNavigationView

var SELECTOR_THEME = 0
open class SetThemeMode: MainActivity() {
    private var selectorTheme: Int = 0 // 0 for default, 1 for Light Theme, -1 for Dark Theme

    fun changeThemeMode(bottomNavigationView: BottomNavigationView) {
        SELECTOR_THEME++
        if(SELECTOR_THEME == 2)
            SELECTOR_THEME = -1
        setThemeMode(bottomNavigationView)
    }

    fun setThemeMode(bottomNavigationView: BottomNavigationView){
        //binding.bottomNavigationView.itemIconTintList = null
        val menu: MenuItem? = bottomNavigationView.menu.getItem(3)
        when(SELECTOR_THEME){
            -1 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                menu?.setIcon(R.drawable.ic_mode_night)
                menu?.title = "Dark"
            }
            0  -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                menu?.setIcon(R.drawable.ic_mode_default)
                menu?.title = "Default"
            }
            1  -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                menu?.setIcon(R.drawable.ic_mode_light)
                menu?.title = "Light"
            }
        }
    }
}