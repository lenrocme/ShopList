package com.example.shopitemsfragmentsbm.fragments.shop_items

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
import kotlin.collections.ArrayList

class ShopItemsSharedPreference() {

    fun loadShopItemSharedPref(activity: Activity, listIndex: String) : ArrayList<ShopItemData> {
        val sharedPre: SharedPreferences = activity.getSharedPreferences(
            "shared pre",
            Context.MODE_PRIVATE
        )
        val json: String? = sharedPre.getString(listIndex, null)
        val type = object : TypeToken<ArrayList<ShopItemData?>?>() {}.type
        val arr: ArrayList<ShopItemData>? = Gson().fromJson(json, type)
        return if(arr.isNullOrEmpty())
            ArrayList()
        else
            arr
    }

    fun saveShopItemsSharedPref(
        activity: Activity,
        listIndex: String,
        linkedList: LinkedList<ShopItemData>
    ) {
        val sharedPre: SharedPreferences = activity.getSharedPreferences(
            "shared pre",
            Context.MODE_PRIVATE
        )
        val editor: SharedPreferences.Editor = sharedPre.edit()
        val json =
                Gson().toJson(linkedList) // list from ShopItemAdapter with all elements
        editor.putString(listIndex, json)
        editor.apply()
    }

    fun deleteItemsShopList(activity: Activity, indexShopList: Int){
        val preferences: SharedPreferences = activity.getSharedPreferences("shared pre", 0)
        preferences.edit().remove(indexShopList.toString()).apply()
    }

    fun getCountNrItemsInOneShopList(activity: Activity, indexShopList: Int): String{
        var countItemsDone: Int = 0 //false
        var countItemsNotDone: Int = 0  //true
        loadShopItemSharedPref(activity, indexShopList.toString()).forEach {
            if(it.stateItem)
                countItemsDone++
            else
                countItemsNotDone++
        }
        return if(countItemsDone + countItemsNotDone == 0)
            "Empty"
        else if(countItemsDone == countItemsNotDone + countItemsDone)
            "Done($countItemsDone)"
        else
            "$countItemsDone / " + (countItemsNotDone + countItemsDone)
    }
}