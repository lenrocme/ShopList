package com.example.shopitemsfragmentsbm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.shopitemsfragmentsbm.databinding.ActivityMainBinding
import com.example.shopitemsfragmentsbm.fragments.info_guide.FragmentInfoGuide
import com.example.shopitemsfragmentsbm.fragments.shop_items.FragmentShopItems
import com.example.shopitemsfragmentsbm.fragments.shop_list.FragmentShopLists
import com.example.shopitemsfragmentsbm.fragments.shop_items.ShopItemData
import kotlin.collections.ArrayList

open class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var temporArr: ArrayList<ShopItemData>
    //var selectorTheme: Int = 0 // 0 for default, 1 for Light Theme, -1 for Dark Theme

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
       //setDarkLightDefaultThemeMode()
        openFragment(R.id.place_holder, FragmentShopLists.newInstance())
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.shop_list -> {openFragment(R.id.place_holder, FragmentShopLists.newInstance())}
                R.id.shop_items -> {openFragment(R.id.place_holder, FragmentShopItems.newInstance())}
                R.id.info_guide -> {openFragment(R.id.place_holder, FragmentInfoGuide.newInstance())}
                R.id.dark_light_mode -> {
                    SetThemeMode().changeThemeMode(binding.bottomNavigationView)}
            }
            true }
    }

    private fun openFragment(idHolder: Int, fragment: Fragment){
        supportFragmentManager
            .beginTransaction()
            .replace(idHolder, fragment)
            .commit()
    }

    override fun onStart() {
        super.onStart()
        GlobalSharedPreferences().loadPreferedTheme(this) // set  saved prefered theme
        SetThemeMode().setThemeMode(binding.bottomNavigationView)
    }

    override fun onStop() {
        super.onStop()
        GlobalSharedPreferences().savePreferedTheme(this)
    }
}