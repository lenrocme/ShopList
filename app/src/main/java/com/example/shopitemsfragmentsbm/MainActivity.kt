package com.example.shopitemsfragmentsbm

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.method.TextKeyListener.clear
import androidx.fragment.app.Fragment
import com.example.shopitemsfragmentsbm.databinding.ActivityMainBinding
import com.example.shopitemsfragmentsbm.fragments.info_guide.FragmentInfoGuide
import com.example.shopitemsfragmentsbm.fragments.shop_items.FragmentShopItems
import com.example.shopitemsfragmentsbm.fragments.shop_list.FragmentShopLists
import com.example.shopitemsfragmentsbm.fragments.shop_items.ShopItemData
import java.util.prefs.Preferences
import kotlin.collections.ArrayList

open class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var temporArr: ArrayList<ShopItemData>
    private lateinit var glbSharedPref: GlobalSharedPreferences
    private var indexLastSelectedBtmMenuItem: Int = R.id.shop_list
    private var selectedShopList: String? = null
    //var selectorTheme: Int = 0 // 0 for default, 1 for Light Theme, -1 for Dark Theme

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
       //setDarkLightDefaultThemeMode()
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.shop_list -> {
                    openFragment(R.id.place_holder, FragmentShopLists.newInstance())
                    indexLastSelectedBtmMenuItem = R.id.shop_list
                }
                R.id.shop_items -> {
                    openFragment(R.id.place_holder, FragmentShopItems.newInstance())
                    indexLastSelectedBtmMenuItem = R.id.shop_items
                }
                R.id.info_guide -> {
                    openFragment(R.id.place_holder, FragmentInfoGuide.newInstance())
                    indexLastSelectedBtmMenuItem = R.id.info_guide
                }
                R.id.dark_light_mode -> {
                    SetThemeMode().changeThemeMode(binding.bottomNavigationView)
                }
            }
            true }
        glbSharedPref = GlobalSharedPreferences(this)
    }

    private fun openFragment(idHolder: Int, fragment: Fragment){
        supportFragmentManager
            .beginTransaction()
            .replace(idHolder, fragment)
            .commit()
    }

    override fun onStart() {
        super.onStart()
        glbSharedPref.loadPreferedTheme() // set  saved prefered theme
        //glbSharedPref.loadLastBtmSelectedItemMenu()
        SetThemeMode().setThemeMode(binding.bottomNavigationView)
        //binding.bottomNavigationView.selectedItemId = glbSharedPref.loadLastBtmSelectedItemMenu()
        //openFragment(R.id.place_holder, FragmentShopLists.newInstance())
    }

    override fun onStop() {
        super.onStop()
        glbSharedPref.savePreferedTheme()
        glbSharedPref.saveLastBtmSelectedItemMenu(indexLastSelectedBtmMenuItem)
    }
}