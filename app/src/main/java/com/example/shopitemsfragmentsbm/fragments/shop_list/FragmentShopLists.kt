package com.example.shopitemsfragmentsbm.fragments.shop_list

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.*
import androidx.transition.TransitionInflater
import com.example.shopitemsfragmentsbm.*
import com.example.shopitemsfragmentsbm.databinding.FragmentShopListsBinding
import com.example.shopitemsfragmentsbm.fragments.shop_items.FragmentShopItems
import com.example.shopitemsfragmentsbm.fragments.shop_items.ShopItemsSharedPreference
import kotlinx.coroutines.*
import java.lang.Runnable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import kotlin.collections.ArrayList

var INDEX_LAST_SELECTED_SHOP_LIST: Int = 0
var SHOP_LIST_Index: String? = null
var INDEX_ShopList_ARR: ArrayList<Int> = ArrayList()
//var SET_SHOPLIST_NAME: String? = null

class FragmentShopLists : Fragment(), AdapterShopList.OnItemClickListenerShopList {
    lateinit var binding: FragmentShopListsBinding
    private lateinit var arrListShopLists: ArrayList<ShopListData>
    var adapterShopList = AdapterShopList(this)
    //var indexShopListarr: ArrayList<Int> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        //exitTransition = inflater.inflateTransition(R.transition.slide_right)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
        ): View? {
            binding = FragmentShopListsBinding.inflate(inflater)
            binding.rcViewShopList.setOnTouchListener { _, _ ->
                if (!adapterShopList.shopList.isEmpty())    //edText on empty is allways visible
                    hideEditTextField()
            false
            }
            binding.imgCallButtShopList.setOnClickListener{
                showEditTextField()
            }
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
        //INDEX_ShopList_ARR = ShopListSharedPreference(requireActivity()).loadIndexShopListArr() // start on mainactiv
        arrListShopLists = ShopListSharedPreference(requireActivity()).loadShopListSharedPref()
        adapterShopList.shopList = LinkedList(arrListShopLists) //make a linkedLIst from add list and load in adapter
        if(!TEMP_SHOP_LIST_NAME.equals(null)){
            onCreateFirstShopList()
            TEMP_SHOP_LIST_NAME = null
        }
    }

    override fun onStop() {
        super.onStop()
        ShopListSharedPreference(requireActivity()).saveShopListSharedPref(adapterShopList.shopList)
        ShopListSharedPreference(requireActivity()).saveIndexShopListArr(INDEX_ShopList_ARR)
        ShopListSharedPreference(requireActivity()).saveIndexLastSelectedShopList(INDEX_LAST_SELECTED_SHOP_LIST)
        //SharedPreferenceGlobal().saveShopListSharedPref(requireActivity(), adapterShopList.shopList)
    }

    private fun initRcView() = with(binding){
        apply{
            rcViewShopList.layoutManager = LinearLayoutManager(FragmentShopItems.newInstance().context, LinearLayoutManager.VERTICAL, false)
            rcViewShopList.adapter = adapterShopList
        }
    }
    private fun getActualDate(): String{
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
            LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))
        else
            ""
    }

    //create new shoplist from dialog
    private fun onCreateFirstShopList() = with(binding){
        INDEX_LAST_SELECTED_SHOP_LIST = addIndexShopList() //create new unique index for new ShopList
        SHOP_LIST_Index = INDEX_LAST_SELECTED_SHOP_LIST.toString()
        val shopItem = ShopListData(TEMP_SHOP_LIST_NAME!!, INDEX_LAST_SELECTED_SHOP_LIST, getActualDate(), getActualDate(), "No description") //use same index to add it in created object
        adapterShopList.addShopItem(shopItem)
        //edTCreateNewShopList.text.clear()
        //rcViewShopList.smoothScrollToPosition(0)   // scroll to first element, after new element is added
        loadFragmentShopItems()
    }

    private fun onClickAddNewShopItem() = with(binding){
        if(edTCreateNewShopList.text.isNotEmpty()) {
            INDEX_LAST_SELECTED_SHOP_LIST = addIndexShopList() //create new unique index for new ShopList
            SHOP_LIST_Index = INDEX_LAST_SELECTED_SHOP_LIST.toString()
            val shopItem = ShopListData(edTCreateNewShopList.text.toString(), INDEX_LAST_SELECTED_SHOP_LIST, getActualDate(), getActualDate(), "") //use same index to add it in created object
            adapterShopList.addShopItem(shopItem)
            edTCreateNewShopList.text.clear()
            rcViewShopList.smoothScrollToPosition(0)   // scroll to first element, after new element is added
            loadFragmentShopItems()
        }else{
            hideEditTextField()
        }
    }
    // select a shoplist and laod data to new fragment with all items
    override fun onItemCLickGoToList(position: Int) {
        val shopListIndex: Int = adapterShopList.shopList[position].indexShopList
        SHOP_LIST_Index = shopListIndex.toString()
        INDEX_LAST_SELECTED_SHOP_LIST = shopListIndex
        loadFragmentShopItems()
    }

    override fun onItemClickDescription(position: Int) {
        val descriptionText = adapterShopList.shopList[position].descriptionShopList
        val shopListName = adapterShopList.shopList[position].itemName
        val dialogView = layoutInflater.inflate(R.layout.description_shop_list, null)
        val builder = AlertDialog.Builder(requireContext())
        val editText = dialogView.findViewById<EditText>(R.id.edTmlDescription)
        editText.setText(descriptionText)

        with(builder){
            setTitle("Description for: $shopListName")
            editText.requestFocus()
            setPositiveButton("OK"){ dialog, which ->
                adapterShopList.shopList[position].descriptionShopList = editText.text.toString()       // save redacted description text in the shoplist object
            }
            setNegativeButton("Cancel"){ dialog, which->
            }
            setView(dialogView)
            show()
        }
    }

    override fun onItemClickDelete(position: Int) {
        ShopItemsSharedPreference().deleteItemsShopList(requireActivity(), adapterShopList.shopList[position].indexShopList)
        SHOP_LIST_Index = null
        deleteIndexShopList(adapterShopList.shopList[position].indexShopList)
        adapterShopList.deleteItem(position)
    }

    //get change fragment, load data with variable asa name of the list
    private fun loadFragmentShopItems(){
        (activity as MainActivity).binding.bottomNavigationView.selectedItemId = R.id.shop_items    // change selected item on btm nav menu
    }

    //override fun onItemClickCreate(position: Int) {}
    override fun onItemClickEdit(position: Int) {
        val itemName = adapterShopList.getShopItemName(position)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.edit_shop_item_dialog, null)
        val builder = AlertDialog.Builder(requireContext())
        val editText = dialogView.findViewById<EditText>(R.id.etEditShopItem)
        editText.setText(itemName)

        with(builder){
            setTitle("Edit ShopList name")
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

    private fun addIndexShopList():Int{
        if(INDEX_ShopList_ARR.isNullOrEmpty())
            INDEX_ShopList_ARR.add(1) // mean all elements a deleted, so we can write as new install , first element with index 1
        else{
            INDEX_ShopList_ARR.add(INDEX_ShopList_ARR.last() + 1) //increase with 1 and add as next index
        }
        return INDEX_ShopList_ARR.last() //return new created index
    }

    // bc index is uniqe, we gonna delete first finded element
    private fun deleteIndexShopList(element: Int){
        SHOP_LIST_Index = null
        INDEX_ShopList_ARR.remove(element)
    }

    //hide editTextField & addButton
    private fun hideEditTextField() {
        animOnX(800, binding.edTCreateNewShopList, 1000f)
        Handler().postDelayed(Runnable { animOnX(400,  binding.imgCreateNewShopList, 300f)}, 600)
    }

    private fun showEditTextField(){
        animOnX(300, binding.imgCreateNewShopList, 0f)
        Handler().postDelayed(Runnable { animOnX(800, binding.edTCreateNewShopList, 0f)}, 120)
    }

    private fun animOnX(duration: Long, v: View, distance: Float){
        ObjectAnimator.ofFloat(v, "translationX", distance).apply {//10k for some tablets
            this.duration = duration
            start()
        }
    }

    //singleton
    companion object {
        @JvmStatic
        fun newInstance() = FragmentShopLists()
    }
}