package com.example.shopitemsfragmentsbm.fragments.shop_list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopitemsfragmentsbm.MainActivity
import com.example.shopitemsfragmentsbm.R
import com.example.shopitemsfragmentsbm.databinding.FragmentShopListsBinding
import com.example.shopitemsfragmentsbm.fragments.shop_items.FragmentShopItems
import java.util.*
import kotlin.collections.ArrayList

var LIST_NAME: String? = null
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
        arrListShopLists =  SharedPreference().loadShopListSharedPref(requireActivity()) //SharedPreferenceGlobal().loadShopListSharedPref(requireActivity())
        adapterShopList.shopList = LinkedList(arrListShopLists) //make a linkedLIst from add list and load in adapter
    }

    override fun onStop() {
        super.onStop()
        SharedPreference().saveShopListSharedPref(requireActivity(), adapterShopList.shopList)
        //SharedPreferenceGlobal().saveShopListSharedPref(requireActivity(), adapterShopList.shopList)
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
    // select a shoplist and laod data to new fragment with all items
    override fun onItemCLickGoToList(position: Int) {
        val shopListName: String = adapterShopList.shopList[position].itemName
        LIST_NAME = shopListName
        //FragmentShopItems.newInstance().setNameList(shopListName)
        //SHOP_ITEM_ARR = SharedPreferenceGlobal().loadShopItemSharedPref(requireActivity(), shopListName)
        loadFragmentShopItems()

    }

    override fun onItemClickDelete(position: Int) {
        adapterShopList.deleteItem(position)
    }

    //get change fragment, load data with variable asa name of the list
    private fun loadFragmentShopItems(){
        (activity as MainActivity).binding.bottomNavigationView.selectedItemId = R.id.shop_items
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

    //singleton
    companion object {
        @JvmStatic
        fun newInstance() = FragmentShopLists()
    }
}