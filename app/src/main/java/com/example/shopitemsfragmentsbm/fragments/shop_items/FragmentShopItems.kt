package com.example.shopitemsfragmentsbm.fragments.shop_items

import android.content.Context.MODE_PRIVATE
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
import com.example.shopitemsfragmentsbm.*
import com.example.shopitemsfragmentsbm.databinding.FragmentShopItemsBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
import kotlin.collections.ArrayList


class FragmentShopItems : Fragment(), AdapterShopItem.OnItemClickListener {
    lateinit var binding: FragmentShopItemsBinding
    val adapterShopItem = AdapterShopItem(this)
    lateinit var arrListShopItem: ArrayList<ShopItemData>//LinkedList<ShopItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
        ): View? {
            binding = FragmentShopItemsBinding.inflate(inflater)
            return binding.root//inflater.inflate(R.layout.fragment_shop_items, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        binding.imgCreateNewItem.setOnClickListener{
            onClickAddNewShopItem()
        }
        initRcView()
    }

    override fun onStart() {
        super.onStart()
        loadData()
        adapterShopItem.shopItemsList = LinkedList(arrListShopItem) //make a linkedLIst from add list and load in adapter
        adapterShopItem.setRecycleView(binding.rcView)      //send rcView object to Adapter
    }

    override fun onStop() {
        super.onStop()
        saveArrays()
    }

    private fun initRcView() = with(binding){
        apply{
            rcView.layoutManager = LinearLayoutManager(newInstance().context, LinearLayoutManager.VERTICAL, false)
            rcView.adapter = adapterShopItem
            val itemTouchHelper = ItemTouchHelper(SwipeRightToWriteAsDone(adapterShopItem))
            itemTouchHelper.attachToRecyclerView(rcView)    // delete swiped item
            val itemTouchLeft = ItemTouchHelper(SwipeLeftToUndo(adapterShopItem))
            itemTouchLeft.attachToRecyclerView(rcView) // rewrite As done
        }
    }

   private fun onClickAddNewShopItem() = with(binding){
        if(tvAddShopItem.text.isNotEmpty()) {
            val shopItem = ShopItemData(tvAddShopItem.text.toString(), false)
            adapterShopItem.addShopItem(shopItem)
            tvAddShopItem.text.clear()
            rcView.smoothScrollToPosition(0)   // scroll to first element, after new element is added
        }
    }

    override fun onItemClickDelete(position: Int) {
        adapterShopItem.deleteItem(position)
    }

    //override fun onItemClickCreate(position: Int) {}
    override fun onItemClickCreate(position: Int) {
        val itemName = adapterShopItem.getShopItemName(position)
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
                adapterShopItem.editShopItemInDialog(position,
                    editText.text.toString())
            }
            setNegativeButton("Cancel"){ dialog, which->
            }
            setView(dialogView)
            show()
        }
    }

   private fun saveArrays() {
       val sharedPre: SharedPreferences = requireContext().getSharedPreferences("shared pre", MODE_PRIVATE)
       val editor: SharedPreferences.Editor = sharedPre.edit()
       val json =
           Gson().toJson(adapterShopItem.shopItemsList) // list from ShopItemAdapter with all elements
       editor.putString("shopItemsList", json)
       editor.apply()
   }
    private fun loadData() {
        val sharedPre: SharedPreferences = requireContext().getSharedPreferences("shared pre", MODE_PRIVATE)
        val json: String? = sharedPre.getString("shopItemsList", null)
        val type = object : TypeToken<ArrayList<ShopItemData?>?>() {}.type
        arrListShopItem = Gson().fromJson(json, type)
        if(arrListShopItem.isNullOrEmpty())
            arrListShopItem = ArrayList()
    }

    companion object {
        @JvmStatic
        fun newInstance() = FragmentShopItems()
    }
}