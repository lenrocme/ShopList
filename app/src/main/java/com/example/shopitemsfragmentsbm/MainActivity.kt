package com.example.shopitemsfragmentsbm

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.shopitemsfragmentsbm.databinding.ActivityMainBinding
import com.example.shopitemsfragmentsbm.fragments.info_guide.FragmentInfoGuide
import com.example.shopitemsfragmentsbm.fragments.shop_items.FragmentShopItems
import com.example.shopitemsfragmentsbm.fragments.shop_list.FragmentShopLists
import com.example.shopitemsfragmentsbm.fragments.shop_items.ShopItem
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var temporArr: ArrayList<ShopItem>
    private var selectorTheme: Int = 0 // 0 for default, 1 for Light Theme, -1 for Dark Theme

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
       //setDarkLightDefaultThemeMode()
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.shop_list -> {openFragment(R.id.place_holder, FragmentShopLists.newInstance())}
                R.id.shop_items -> {openFragment(R.id.place_holder, FragmentShopItems.newInstance())}
                R.id.info_guide -> {openFragment(R.id.place_holder, FragmentInfoGuide.newInstance())}
                R.id.dark_light_mode -> {changeDarkLightDefaultThemeMode()}
            }
            true }
    }



    private fun changeDarkLightDefaultThemeMode(){
        selectorTheme++
        if(selectorTheme == 2)
            selectorTheme = -1
        setDarkLightDefaultThemeMode()
    }

    private fun setDarkLightDefaultThemeMode(){
        //binding.bottomNavigationView.itemIconTintList = null
        val menu: MenuItem? = binding.bottomNavigationView.menu.getItem(3)
        when(selectorTheme){
            -1 -> {AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                menu?.setIcon(R.drawable.ic_mode_night)
                menu?.title = "Dark"
            }
            0  -> {AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                menu?.setIcon(R.drawable.ic_mode_default)
                menu?.title = "Default"
            }
            1  -> {AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                menu?.setIcon(R.drawable.ic_mode_light)
                menu?.title = "Light"
            }
        }
    }

    private fun openFragment(idHolder: Int, fragment: Fragment){
        supportFragmentManager
            .beginTransaction()
            .replace(idHolder, fragment)
            .commit()
    }

    override fun onStart() {
        super.onStart()
        loadPreferedThemeIndex()
        setDarkLightDefaultThemeMode()
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        //binding.bottomNavigationView.menu.getItem(3)
    }

    override fun onStop() {
        super.onStop()
        savePreferedThemeIndex()
    }

    private fun savePreferedThemeIndex() {
        val sharedPre: SharedPreferences = this.getSharedPreferences("shared pre", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPre.edit()
        editor.putInt("ThemeMode", selectorTheme)
        editor.apply()
    }

    private fun loadPreferedThemeIndex() {
        val sharedPre: SharedPreferences = this.getSharedPreferences("shared pre", MODE_PRIVATE)
        selectorTheme = sharedPre.getInt("ThemeMode", 0)
    }

}