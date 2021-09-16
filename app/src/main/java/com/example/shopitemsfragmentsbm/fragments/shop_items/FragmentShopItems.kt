package com.example.shopitemsfragmentsbm.fragments.shop_items

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionInflater
import com.example.shopitemsfragmentsbm.*
import com.example.shopitemsfragmentsbm.databinding.FragmentShopItemsBinding
import com.example.shopitemsfragmentsbm.fragments.shop_list.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*


class FragmentShopItems() : Fragment(), AdapterShopItem.OnItemClickListener {
    lateinit var binding: FragmentShopItemsBinding
    private val adapterShopItem = AdapterShopItem(this)
    lateinit var nameOfList: String
    private lateinit var arrListShopItem: ArrayList<ShopItemData>//LinkedList<ShopItem>
    private lateinit var arrListShopLists: ArrayList<ShopListData>
    private var checkOnEmptyEdText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        //enterTransition = inflater.inflateTransition(R.transition.slide_right)
        //exitTransition = inflater.inflateTransition(R.transition.fade)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
            binding = FragmentShopItemsBinding.inflate(inflater)
            binding.rcView.setOnTouchListener { _, _ ->
                if (!adapterShopItem.shopItemsList.isEmpty()) //edText on empty is allways visible
                    hideEditTextField()
            false
            }
            binding.scrollRcView.setOnTouchListener { _, _ ->
                if (!adapterShopItem.shopItemsList.isEmpty()) //edText on empty is allways visible
                    hideEditTextField()
                false
            }
            binding.imgCallButtItemList.setOnClickListener{
                showEditTextField()
            }
            binding.imgCreateNewItem.setOnClickListener{
                onClickAddNewShopItem()
            }
            binding.edTxtShopItems.setOnKeyListener(object : View.OnKeyListener {
                override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                    if (event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {    //on click enter, use function add new item, and refocus on editText
                        onClickAddNewShopItem()
                        return true
                    }
                    return true
                }
            })
            return binding.root//inflater.inflate(R.layout.fragment_shop_items, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRcView()
    }

    override fun onStart() {
        super.onStart()
        onStartHideEditTextField()
        arrListShopLists = ShopListSharedPreference(requireActivity()).loadShopListSharedPref()
        arrListShopItem = ShopItemsSharedPreference().loadShopItemSharedPref(requireActivity(), SHOP_LIST_Index!!)
        adapterShopItem.shopItemsList = LinkedList(arrListShopItem) //make a linkedLIst from add list and load in adapter
        adapterShopItem.setRecycleView(binding.rcView)      //send rcView object to Adapter
        (activity as MainActivity).binding.tvTextHeaderEnter.text = "List: ${getShopListName()}"
        Handler().postDelayed(Runnable { showEditTextField() }, 350)
    }

    override fun onStop() {
        super.onStop()
        ShopItemsSharedPreference().saveShopItemsSharedPref(requireActivity(), SHOP_LIST_Index!!, adapterShopItem.shopItemsList)
        hideKeyboard()
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

    //if we add new item in the shoplist, date gonna be changed
    private fun changeDateOnShopListWhenChanged(){
        val linkedlist: LinkedList<ShopListData> = LinkedList()
        val arr = ShopListSharedPreference(requireActivity()).loadShopListSharedPref()
        for (i in 0 until arr.size){
            //Log.i("jora","arr size: ${arr.size}" ) // ? showing right size, but repeat only 2 times
                if(arr[i].indexShopList == INDEX_LAST_SELECTED_SHOP_LIST)//find right list, change date
                    arr[i].dateChanged = getActualDate()
            linkedlist.add(arr[i])      // write every item in linked list
        }
        ShopListSharedPreference(requireActivity()).saveShopListSharedPref(linkedlist)
    }

   private fun onClickAddNewShopItem() = with(binding) {
       val valueTxt: String = edTxtShopItems.text.toString()
       if (valueTxt.isNotEmpty()) {
           val shopItem = ShopItemData(valueTxt, false)
           adapterShopItem.addShopItem(shopItem)
           rcView.smoothScrollToPosition(0)   // scroll to first element, after new element is added
           changeDateOnShopListWhenChanged()
           edTxtShopItems.text.clear()
           animatedOnEnterorAdd()
       }
       else{
           if (!adapterShopItem.shopItemsList.isEmpty()) {   //edText on empty is allways visible
               hideEditTextField()
               hideKeyboard()
           }
           else
               animatedOnEnterorAdd()
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
        showKeyboard()
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

    //hide editTextField & addButton
    private fun hideEditTextField() {
        Log.i("jroa", 1.toString())
        animOnX(800, binding.edTxtShopItems, 1000f)
        Handler().postDelayed(Runnable { animOnX(400, binding.imgCreateNewItem, 300f)}, 600)
    }

    private fun showEditTextField(){
        animOnX(300, binding.imgCreateNewItem, 0f)
        Handler().postDelayed(Runnable { animOnX(800, binding.edTxtShopItems, 0f)}, 120)
    }

    private fun animOnX(duration: Long, v:View, distance: Float){
        ObjectAnimator.ofFloat(v, "translationX", distance).apply {//10k for some tablets
            this.duration = duration
            start()
        }
    }
    private fun onStartHideEditTextField(){
        animOnX(0, binding.edTxtShopItems, 1000f)
        animOnX(0, binding.imgCreateNewItem, 300f)
    }
    private fun animatedOnEnterorAdd(){
        Handler().postDelayed(Runnable { animOnX(200, binding.edTxtShopItems, 200f)}, 50)
        Handler().postDelayed(Runnable { animOnX(200, binding.edTxtShopItems, 0f)}, 210)
    }

    companion object {
        @JvmStatic
        fun newInstance() = FragmentShopItems()
    }
}