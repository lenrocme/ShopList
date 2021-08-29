package com.example.shopitemsfragmentsbm.fragments.shop_list

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.example.shopitemsfragmentsbm.fragments.shop_items.ShopItemData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
import kotlin.collections.ArrayList

class SharedPreference {

    fun saveShopListSharedPref(activity: Activity, linkedList: LinkedList<ShopListData>) {
        val sharedPre: SharedPreferences = activity.getSharedPreferences("shared pre", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPre.edit()
        val json =
                Gson().toJson(linkedList) // list from ShopItemAdapter with all elements
        editor.putString("shopLists", json)
        editor.apply()
    }

    fun loadShopListSharedPref(activity: Activity) : ArrayList<ShopListData> {
        val sharedPre: SharedPreferences = activity.getSharedPreferences("shared pre", Context.MODE_PRIVATE)
        val json: String? = sharedPre.getString("shopLists", null)
        val type = object : TypeToken<ArrayList<ShopListData?>?>() {}.type
        val arr: ArrayList<ShopListData>? = Gson().fromJson(json, type)
        return if(arr.isNullOrEmpty())
            ArrayList()
        else
            arr
    }
}