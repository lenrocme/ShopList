package com.example.shopitemsfragmentsbm.fragments.shop_list

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.example.shopitemsfragmentsbm.fragments.shop_items.ShopItemData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
import kotlin.collections.ArrayList

class ShopListSharedPreference(activity: Activity) {
    val sharedPre: SharedPreferences = activity.getSharedPreferences("shared pre", Context.MODE_PRIVATE)
    val editor: SharedPreferences.Editor = sharedPre.edit()

    fun saveShopListSharedPref(linkedList: LinkedList<ShopListData>) {
        val editor: SharedPreferences.Editor = sharedPre.edit()
        val json =
                Gson().toJson(linkedList) // list from ShopItemAdapter with all elements
        editor.putString("shopLists", json)
        editor.apply()
    }

    fun loadShopListSharedPref() : ArrayList<ShopListData> {
        val json: String? = sharedPre.getString("shopLists", null)
        val type = object : TypeToken<ArrayList<ShopListData?>?>() {}.type
        val arr: ArrayList<ShopListData>? = Gson().fromJson(json, type)
        return if(arr.isNullOrEmpty())
            ArrayList()
        else
            arr
    }

    fun saveIndexShopListArr(indexShopListArr: ArrayList<Int>){
        val json =
                Gson().toJson(indexShopListArr)
        editor.putString("IndexesOfShopListsArr", json)
        editor.apply()
    }

    fun loadIndexShopListArr(): ArrayList<Int>{
        val json: String? = sharedPre.getString("IndexesOfShopListsArr", null)
        val type = object : TypeToken<ArrayList<Int?>?>() {}.type
        val arr: ArrayList<Int>? = Gson().fromJson(json, type)
        return if(arr.isNullOrEmpty())
            ArrayList()
        else
            arr
    }


}