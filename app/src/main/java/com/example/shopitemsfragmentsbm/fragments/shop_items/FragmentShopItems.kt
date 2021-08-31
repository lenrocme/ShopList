package com.example.shopitemsfragmentsbm.fragments.shop_items

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
import com.example.shopitemsfragmentsbm.fragments.shop_list.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import kotlin.collections.ArrayList

class FragmentShopItems() : Fragment(), AdapterShopItem.OnItemClickListener {
    lateinit var binding: FragmentShopItemsBinding
    private val adapterShopItem = AdapterShopItem(this)
    lateinit var nameOfList: String
    private lateinit var arrListShopItem: ArrayList<ShopItemData>//LinkedList<ShopItem>
    private lateinit var arrListShopLists: ArrayList<ShopListData>

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
        arrListShopLists = ShopListSharedPreference(requireActivity()).loadShopListSharedPref()
        arrListShopItem = ShopItemsSharedPreference().loadShopItemSharedPref(requireActivity(), SHOP_LIST_Index!!)
        adapterShopItem.shopItemsList = LinkedList(arrListShopItem) //make a linkedLIst from add list and load in adapter
        adapterShopItem.setRecycleView(binding.rcView)      //send rcView object to Adapter
        binding.tvNameShopListInShopItemFr.text = getShopListName()
    }

    override fun onStop() {
        super.onStop()
        ShopItemsSharedPreference().saveShopItemsSharedPref(requireActivity(), SHOP_LIST_Index!!, adapterShopItem.shopItemsList)
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

    private fun getActualDate(): String{
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
            LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))
        else
            ""
    }

    private fun changeDateOnShopListWhenChanged(){
        val arr = ShopListSharedPreference(requireActivity()).loadShopListSharedPref()
        arr.forEach {
            if(it.indexShopList == INDEX_LAST_SELECTED_SHOP_LIST)
                it.dateChanged = getActualDate()
        }
    }

   private fun onClickAddNewShopItem() = with(binding){
        if(tvAddShopItem.text.isNotEmpty()) {
            val shopItem = ShopItemData(tvAddShopItem.text.toString(), false)
            adapterShopItem.addShopItem(shopItem)
            tvAddShopItem.text.clear()
            rcView.smoothScrollToPosition(0)   // scroll to first element, after new element is added
            changeDateOnShopListWhenChanged()
        }
    }

    override fun onItemClickDelete(position: Int) {
        changeDateOnShopListWhenChanged()
        adapterShopItem.deleteItem(position)
    }

    //override fun onItemClickCreate(position: Int) {}
    override fun onItemClickEditShopItem(position: Int) {
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
                setTitle("")
            }
            setNegativeButton("Cancel"){ dialog, which->
            }
            setView(dialogView)
            show()
        }
    }

    //get name shoplist for edit name fragment shopitems
    private fun getShopListName(): String? {
        arrListShopLists.forEach{
            if(it.indexShopList == SHOP_LIST_Index?.toInt()){
                return it.itemName
            }
        }
        return null
    }

    companion object {
        @JvmStatic
        fun newInstance() = FragmentShopItems()
    }
}