package com.example.shopitemsfragmentsbm

import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.HandlerCompat.postDelayed
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.shopitemsfragmentsbm.databinding.ActivityMainBinding
import com.example.shopitemsfragmentsbm.fragments.info_guide.FragmentInfoGuide
import com.example.shopitemsfragmentsbm.fragments.shop_items.FragmentShopItems
import com.example.shopitemsfragmentsbm.fragments.shop_list.*
import kotlinx.coroutines.*
import java.lang.Runnable

var TEMP_SHOP_LIST_NAME: String? = null
var LAST_SELECTED_FRAGMENTS: ArrayList<Int> = arrayListOf(5, 5)

open class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var glbSharedPref: GlobalSharedPreferences
    private var indexLastSelectedBtmMenuItem: Int = R.id.shop_list
    private var selectedShopList: String? = null
    var isSecond: Boolean = false
    private lateinit var fromRight: Animation
    private lateinit var fromLeft: Animation
    private lateinit var exitLeft: Animation
    private lateinit var exitRight: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //set animation from xml resource
        fromRight = AnimationUtils.loadAnimation(this, R.anim.enter_from_right)
        fromLeft = AnimationUtils.loadAnimation(this, R.anim.enter_from_left)
        exitLeft = AnimationUtils.loadAnimation(this, R.anim.exit_to_left)
        exitRight = AnimationUtils.loadAnimation(this, R.anim.exit_to_right)
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.shop_list -> {
                    LAST_SELECTED_FRAGMENTS.add(-1)
                    openFragment(R.id.place_holder, FragmentShopLists.newInstance())
                    indexLastSelectedBtmMenuItem = R.id.shop_list
                    binding.tvTextHeaderEnter.text = "Shopping lists"
                }
                R.id.shop_items -> {
                    LAST_SELECTED_FRAGMENTS.add(0)
                    indexLastSelectedBtmMenuItem = R.id.shop_items
                    startFromCache()
                    binding.tvTextHeaderEnter.text = "Products list"
                    //openFragment(R.id.place_holder, FragmentShopItems.newInstance())

                }
                R.id.info_guide -> {
                    LAST_SELECTED_FRAGMENTS.add(1)
                    openFragment(R.id.place_holder, FragmentInfoGuide.newInstance())
                    indexLastSelectedBtmMenuItem = R.id.info_guide
                    binding.tvTextHeaderEnter.text = "Info guide"
                }
                R.id.dark_light_mode -> {
                    SetThemeMode().changeThemeMode(binding.bottomNavigationView)
                }
            }
            true }
        glbSharedPref = GlobalSharedPreferences(this)
    }

    private fun openFragment(idHolder: Int, fragment: Fragment) = with(binding){
        var setAnimationDirection = 0
        when{
            LAST_SELECTED_FRAGMENTS.isEmpty() -> 0
            //anim animation left to right
            LAST_SELECTED_FRAGMENTS.last() < LAST_SELECTED_FRAGMENTS[(LAST_SELECTED_FRAGMENTS.size - 2)]  -> {
                setAnimationDirection = R.anim.slide_in_from_left
                tvTextHeaderEnter.startAnimation(fromLeft)
                tvTextHeaderExit.isVisible = true
                tvTextHeaderExit.startAnimation(exitRight)
                Handler().postDelayed(Runnable { binding.tvTextHeaderExit.visibility = View.GONE}, 700)
            }
            //anim animation left to right
            LAST_SELECTED_FRAGMENTS.last() > LAST_SELECTED_FRAGMENTS[(LAST_SELECTED_FRAGMENTS.size - 2)]  -> {
                setAnimationDirection = R.anim.slide_in_from_right
                tvTextHeaderEnter.startAnimation(fromRight)
                tvTextHeaderExit.isVisible = true
                tvTextHeaderExit.startAnimation(exitLeft)
                Handler().postDelayed(Runnable { binding.tvTextHeaderExit.visibility = View.GONE}, 700)

            }
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
        tvTextHeaderExit.text = binding.tvTextHeaderEnter.text
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
                    Log.i("jora", "dadada" )
                    //openFragment(R.id.place_holder, FragmentShopItems.newInstance())
                    binding.bottomNavigationView.selectedItemId = R.id.shop_items

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
        Toast.makeText(
            baseContext, "Press once again to exit!",
            Toast.LENGTH_SHORT
        ).show()
    }
}