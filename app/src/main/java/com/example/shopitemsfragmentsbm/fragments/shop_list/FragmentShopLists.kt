package com.example.shopitemsfragmentsbm.fragments.shop_list

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopitemsfragmentsbm.R
import com.example.shopitemsfragmentsbm.SwipeLeftToUndo
import com.example.shopitemsfragmentsbm.SwipeRightToWriteAsDone
import com.example.shopitemsfragmentsbm.databinding.FragmentShopListsBinding
import com.example.shopitemsfragmentsbm.fragments.shop_items.FragmentShopItems
import com.example.shopitemsfragmentsbm.fragments.shop_items.ShopItemData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
import kotlin.collections.ArrayList


class FragmentShopLists : Fragment(), AdapterShopList.OnItemClickListenerShopList {
    lateinit var binding: FragmentShopListsBinding
    lateinit var arrListShopLists: ArrayList<ShopListData>
    val adapterShopList = AdapterShopList(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
        ): View? {
            binding = FragmentShopListsBinding.inflate(inflater)
            return binding.root//inflater.inflate(R.layout.fragment_shop_lists, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        binding.imgCreateNewShopList.setOnClickListener{
            onClickAddNewShopItem()
        }
        initRcView()
    }

    override fun onStart() {
        super.onStart()
        //loadData()
       // adapterShopList.shopList = LinkedList(arrListShopLists) //make a linkedLIst from add list and load in adapter
       // adapterShopList.setRecycleView(binding.rcViewShopList)      //send rcView object to Adapter
    }

    override fun onStop() {
        super.onStop()
        //saveArrays()
    }

    private fun initRcView() = with(binding){
        apply{
            rcViewShopList.layoutManager = LinearLayoutManager(FragmentShopItems.newInstance().context, LinearLayoutManager.VERTICAL, false)
            rcViewShopList.adapter = adapterShopList
        }
    }

    private fun onClickAddNewShopItem() = with(binding){
        if(edTCreateNewShopList.text.isNotEmpty()) {
            val shopItem = ShopListData(edTCreateNewShopList.text.toString())
            adapterShopList.addShopItem(shopItem)
            edTCreateNewShopList.text.clear()
            rcViewShopList.smoothScrollToPosition(0)   // scroll to first element, after new element is added
        }
    }

    override fun onItemClickDelete(position: Int) {
        adapterShopList.deleteItem(position)
    }

    //override fun onItemClickCreate(position: Int) {}
    override fun onItemClickCreate(position: Int) {
        val itemName = adapterShopList.getShopItemName(position)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.edit_shop_item_dialog, null)
        val builder = AlertDialog.Builder(requireContext())
        val editText = dialogView.findViewById<EditText>(R.id.etEditShopItem)
        editText.setText(itemName)

        with(builder){
            setTitle("Edit product name")
            editText.selectAll()
            editText.requestFocus()
            setPositiveButton("OK"){ dialog, which ->
                adapterShopList.editShopItemInDialog(position,
                        editText.text.toString())
            }
            setNegativeButton("Cancel"){ dialog, which->
            }
            setView(dialogView)
            show()
        }
    }

    private fun saveArrays() {
        val sharedPre: SharedPreferences = requireContext().getSharedPreferences("shared pre", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPre.edit()
        val json =
                Gson().toJson(adapterShopList.shopList) // list from ShopItemAdapter with all elements
        editor.putString("shopItemsList", json)
        editor.apply()
    }

    private fun loadData() {
        val sharedPre: SharedPreferences = requireContext().getSharedPreferences("shared pre", Context.MODE_PRIVATE)
        val json: String? = sharedPre.getString("shopItemsList", null)
        val type = object : TypeToken<ArrayList<ShopItemData?>?>() {}.type
        arrListShopLists = Gson().fromJson(json, type)
        if(arrListShopLists.isNullOrEmpty())
            arrListShopLists = ArrayList()
    }


    //singleton
    companion object {
        @JvmStatic
        fun newInstance() = FragmentShopLists()
    }
}