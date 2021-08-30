package com.example.shopitemsfragmentsbm

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.method.TextKeyListener.clear
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.shopitemsfragmentsbm.databinding.ActivityMainBinding
import com.example.shopitemsfragmentsbm.fragments.info_guide.FragmentInfoGuide
import com.example.shopitemsfragmentsbm.fragments.shop_items.FragmentShopItems
import com.example.shopitemsfragmentsbm.fragments.shop_items.ShopItemData
import com.example.shopitemsfragmentsbm.fragments.shop_list.*
import java.util.prefs.Preferences
import kotlin.collections.ArrayList

var TEMP_SHOP_LIST_NAME: String? = null

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
                    startFromCache()
                    //openFragment(R.id.place_holder, FragmentShopItems.newInstance())
                    //indexLastSelectedBtmMenuItem = R.id.shop_items
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
        INDEX_ShopList_ARR = ShopListSharedPreference(this).loadIndexShopListArr()
        INDEX_LAST_SELECTED_SHOP_LIST = ShopListSharedPreference(this).loadIndexLastSelectedShopList()
        glbSharedPref.loadPreferedTheme() // set  saved prefered theme
        //glbSharedPref.loadLastBtmSelectedItemMenu()
        SetThemeMode().setThemeMode(binding.bottomNavigationView)
        //binding.bottomNavigationView.selectedItemId = glbSharedPref.loadLastBtmSelectedItemMenu()
        val idBtmMenuItem: Int = glbSharedPref.loadLastBtmSelectedItemMenu()
        if(idBtmMenuItem == - 1)
            openFragment(R.id.place_holder, FragmentShopLists.newInstance())
        else{
            if(idBtmMenuItem == R.id.shop_items)
                startFromCache()
            else
                binding.bottomNavigationView.selectedItemId = idBtmMenuItem
        }
    }

    override fun onStop() {
        super.onStop()
        glbSharedPref.savePreferedTheme()
        glbSharedPref.saveLastBtmSelectedItemMenu(indexLastSelectedBtmMenuItem)
    }

    private fun startFromCache(){
        if (SHOP_LIST_Index.equals(null)){ // only on first start is null
            if(INDEX_LAST_SELECTED_SHOP_LIST == 0){ // default value, only whe sharedpreference are empty, only first time
                dialogBtmNavCreateNewShopList()
            }
            else{
                if(INDEX_ShopList_ARR.contains(INDEX_LAST_SELECTED_SHOP_LIST)) {    // index has been found, use it, open fragment with ShopItems from ShopList with this index
                    SHOP_LIST_Index = INDEX_LAST_SELECTED_SHOP_LIST.toString()
                    indexLastSelectedBtmMenuItem = R.id.shop_items
                    binding.bottomNavigationView.selectedItemId = R.id.shop_items
                    //openFragment(R.id.place_holder, FragmentShopItems.newInstance())
                }
                else{
                    if(INDEX_ShopList_ARR.isNotEmpty()){        // index wasn't been found, but array with all indexes are not empty, mean ShopList with that index was deleted
                        INDEX_LAST_SELECTED_SHOP_LIST = INDEX_ShopList_ARR.last()       // because ShopList with that index was deleted, we ShopList with position 0 in recycleView
                        SHOP_LIST_Index = INDEX_LAST_SELECTED_SHOP_LIST.toString()      // found from last element as index form saved array
                        indexLastSelectedBtmMenuItem = R.id.shop_items
                        Toast.makeText(this.applicationContext, SHOP_LIST_Index, Toast.LENGTH_SHORT).show()
                        //binding.bottomNavigationView.selectedItemId = R.id.shop_items
                        openFragment(R.id.place_holder, FragmentShopItems.newInstance())
                    }
                    else{
                        dialogBtmNavCreateNewShopList()
                    }
                }
            }
        }
        else{
            SHOP_LIST_Index = INDEX_LAST_SELECTED_SHOP_LIST.toString()
            indexLastSelectedBtmMenuItem = R.id.shop_items
            //binding.bottomNavigationView.selectedItemId = R.id.shop_items
            openFragment(R.id.place_holder, FragmentShopItems.newInstance())
        }
    }

    private fun dialogBtmNavCreateNewShopList(){
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.edit_shop_item_dialog, null)
        val builder = AlertDialog.Builder(this)
        val editText = dialogView.findViewById<EditText>(R.id.etEditShopItem)
        //editText.setText(itemName)

        with(builder){
            setTitle("Please enter the name of your new shop list")
            editText.selectAll()
            editText.requestFocus()
            setPositiveButton("OK"){ dialog, which ->

                TEMP_SHOP_LIST_NAME = editText.text.toString()
                indexLastSelectedBtmMenuItem = R.id.shop_list
                openFragment(R.id.place_holder, FragmentShopLists.newInstance())

            }
            setNegativeButton("Cancel"){ dialog, which->
                binding.bottomNavigationView.selectedItemId = R.id.shop_list
            }
            setView(dialogView)
            setCancelable(false)
            show()
        }
    }
}