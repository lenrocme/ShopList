package com.example.shopitemsfragmentsbm.fragments.shop_items

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.example.shopitemsfragmentsbm.fragments.shop_list.ShopListData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
import kotlin.collections.ArrayList

class SharedPreference {

    fun loadShopItemSharedPref(activity: Activity, listName:String) : ArrayList<ShopItemData> {
        val sharedPre: SharedPreferences = activity.getSharedPreferences("shared pre", Context.MODE_PRIVATE)
        val json: String? = sharedPre.getString(listName, null)
        val type = object : TypeToken<ArrayList<ShopItemData?>?>() {}.type
        val arr: ArrayList<ShopItemData>? = Gson().fromJson(json, type)
        return if(arr.isNullOrEmpty())
            ArrayList()
        else
            arr
    }

    fun saveShopItemsSharedPref(activity: Activity, listName: String, linkedList: LinkedList<ShopItemData>) {
        val sharedPre: SharedPreferences = activity.getSharedPreferences("shared pre", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPre.edit()
        val json =
                Gson().toJson(linkedList) // list from ShopItemAdapter with all elements
        editor.putString(listName, json)
        editor.apply()
    }
}