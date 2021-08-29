package com.example.shopitemsfragmentsbm

import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.bottomnavigation.BottomNavigationView

var SELECTOR_THEME = 0
open class SetThemeMode: MainActivity() {

    fun changeThemeMode(bottomNavigationView: BottomNavigationView) {
        SELECTOR_THEME++
        if(SELECTOR_THEME == 2)
            SELECTOR_THEME = -1
        setThemeMode(bottomNavigationView)
    }

    fun setThemeMode(bottomNavigationView: BottomNavigationView){
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