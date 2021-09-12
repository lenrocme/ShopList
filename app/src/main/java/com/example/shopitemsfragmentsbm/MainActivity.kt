package com.example.shopitemsfragmentsbm

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopitemsfragmentsbm.databinding.ActivityMainBinding
import com.example.shopitemsfragmentsbm.fragments.info_guide.FragmentInfoGuide
import com.example.shopitemsfragmentsbm.fragments.shop_items.FragmentShopItems
import com.example.shopitemsfragmentsbm.fragments.shop_items.ShopItemData
import com.example.shopitemsfragmentsbm.fragments.shop_list.*

var TEMP_SHOP_LIST_NAME: String? = null
var LAST_SELECTED_FRAGMENTS: ArrayList<Int> = arrayListOf(5,5)

open class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var temporArr: ArrayList<ShopItemData>
    private lateinit var glbSharedPref: GlobalSharedPreferences
    private var indexLastSelectedBtmMenuItem: Int = R.id.shop_list
    private var selectedShopList: String? = null
    var isSecond: Boolean = false
    //var selectorTheme: Int = 0 // 0 for default, 1 for Light Theme, -1 for Dark Theme

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
       //setDarkLightDefaultThemeMode()
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.shop_list -> {
                    LAST_SELECTED_FRAGMENTS.add(-1)
                    openFragment(R.id.place_holder, FragmentShopLists.newInstance())
                    indexLastSelectedBtmMenuItem = R.id.shop_list
                }
                R.id.shop_items -> {
                    LAST_SELECTED_FRAGMENTS.add(0)
                    startFromCache()
                    //openFragment(R.id.place_holder, FragmentShopItems.newInstance())
                    //indexLastSelectedBtmMenuItem = R.id.shop_items
                }
                R.id.info_guide -> {
                    LAST_SELECTED_FRAGMENTS.add(1)
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
        val setAnimationDirection = when{
            LAST_SELECTED_FRAGMENTS.isEmpty() -> 0
            LAST_SELECTED_FRAGMENTS.last() < LAST_SELECTED_FRAGMENTS[(LAST_SELECTED_FRAGMENTS.size - 2)]  -> R.anim.slide_in_from_left
            LAST_SELECTED_FRAGMENTS.last() > LAST_SELECTED_FRAGMENTS[(LAST_SELECTED_FRAGMENTS.size - 2)]  -> R.anim.slide_in_from_right
            LAST_SELECTED_FRAGMENTS.last() == LAST_SELECTED_FRAGMENTS[(LAST_SELECTED_FRAGMENTS.size - 2)] -> 0
            else -> 0
        }
        if(setAnimationDirection != 0) {
            supportFragmentManager.commit {
                setCustomAnimations(
                    setAnimationDirection,      //enter
                    R.anim.fade_out,            //exit
                    R.anim.fade_in,             //popEnter
                    R.anim.slide_out            //popExit
                )
                replace(idHolder, fragment)
                //addToBackStack(null)
            }
        }
        else{
            supportFragmentManager.beginTransaction()
                .replace(idHolder, fragment)
                .commit()
        }

    }

    override fun onStart() {
        super.onStart()
        INDEX_ShopList_ARR = ShopListSharedPreference(this).loadIndexShopListArr()
        INDEX_LAST_SELECTED_SHOP_LIST = ShopListSharedPreference(this).loadIndexLastSelectedShopList()
        glbSharedPref.loadPreferedTheme() // set  saved prefered theme
        SetThemeMode().setThemeMode(binding.bottomNavigationView)
        val idBtmMenuItem: Int = glbSharedPref.loadLastBtmSelectedItemMenu()
        if(idBtmMenuItem == - 1)
            openFragment(R.id.place_holder, FragmentShopLists.newInstance())
        else{
            if(idBtmMenuItem == R.id.shop_items)
                startFromCache()
            else{
                binding.bottomNavigationView.selectedItemId = idBtmMenuItem
            }
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
                dialogBtmNavCreateNewShopList()// no shoplist, call dialog to create a shoplist
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

    //double tap back button to exit
    override fun onBackPressed() {
        if (isSecond) {
            android.os.Process.killProcess(android.os.Process.myPid())
        }
        isSecond = true
        Handler().postDelayed(Runnable { isSecond = false }, 1500)
        Toast.makeText(baseContext, "Press once again to exit!",
            Toast.LENGTH_SHORT).show()
    }
}