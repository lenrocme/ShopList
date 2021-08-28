package com.example.shopitemsfragmentsbm.fragments.shop_items

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shopitemsfragmentsbm.R
import com.example.shopitemsfragmentsbm.databinding.ShopItemBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import java.util.*


class AdapterShopItem(private val listener: OnItemClickListener): RecyclerView.Adapter<AdapterShopItem.ItemHolder>() {
    private lateinit var rcView: RecyclerView
    var shopItemsList = LinkedList<ShopItemData>()
    var currentPosition: Int = 0

    private var countItemDoneAsTrue: Int = 0 // count how many shopItem in Array have status False
    // with this number we can put "done" items in right place in the recycleView

    inner class ItemHolder(item: View):RecyclerView.ViewHolder(item), View.OnClickListener {
        private val binding = ShopItemBinding.bind(item)

        fun bind(shopItem: ShopItemData) = with(binding){
            iconCreate.setImageResource(IC_CREATE)
            iconDeleteItem.setImageResource(IC_DELETE)
            tvItemName.text = shopItem.itemName
            if(shopItem.stateItem)
                tvItemName.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG // add cross out text(on swipe right)
            else
                tvItemName.paintFlags = 0   // remove cross out text ( on swipe left)
        }
        init {
            binding.iconDeleteItem.setOnClickListener(this)
            binding.iconCreate.setOnClickListener(this)

        }

        override fun onClick(v: View?) {
            val position: Int = adapterPosition
            if(position != RecyclerView.NO_POSITION) {

                if (v?.id == binding.iconDeleteItem.id)     //in case if id is deleteIcon id
                    listener.onItemClickDelete(position)

                else if (v?.id == binding.iconCreate.id)    //same for createIcon
                    listener.onItemClickCreate(position)
            }
        }
    }

    interface OnItemClickListener{
        fun onItemClickDelete(position: Int)    // delete selectedItem from shop items
        fun onItemClickCreate(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.shop_item, parent, false)
        return ItemHolder(view)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.bind(shopItemsList[position])
    }

    override fun getItemCount(): Int {
        return shopItemsList.size
    }

    fun addShopItem(shopItem: ShopItemData){
        shopItemsList.addFirst(shopItem)
        notifyItemInserted(0)
    }

    fun deleteItem(pos: Int) {
        if(shopItemsList[pos].stateItem) // if text is crossover, after delete this element count--
            countItemDoneAsTrue--
        shopItemsList.removeAt(pos)
        notifyItemRemoved(pos)
    }
    // make text in this label cross out, and move it at the end of the list with non cross out text in rcView
    fun makeItemAsDone(pos: Int){
        countDoneItemsInSharedPreferencesArray(shopItemsList)
        val itemShop: ShopItemData = shopItemsList[pos]
        if(!shopItemsList[pos].stateItem){
            shopItemsList.removeAt(pos)
            itemShop.stateItem = true
            if(countItemDoneAsTrue > 0)
                shopItemsList.add(shopItemsList.size - countItemDoneAsTrue, itemShop)
            else
                shopItemsList.addLast(itemShop)
            notifyItemChanged(pos)
            notifyItemMoved(
                pos, shopItemsList.size - countItemDoneAsTrue - 1
            ) // put in top of lists with "done" items
        }
        else {
            notifyItemChanged(pos)
        }
    }

    fun undoShopItem(pos: Int) {
        countDoneItemsInSharedPreferencesArray(shopItemsList)
        val itemShop: ShopItemData = shopItemsList[pos]
        shopItemsList.removeAt(pos)
        itemShop.stateItem = false
        shopItemsList.addFirst(itemShop)
        //rcView.scrollToPosition(0)
       /* notifyItemChanged(pos)
        notifyItemMoved(pos, 0)
        Handler().postDelayed({
            (rcView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset( 0, 0)

        }, 100)*/

        CoroutineScope(IO).launch {
            withContext(Main) {
                launch {
                    rcView.scrollToPosition(0)  // scroll to first element, after new element is added
                }
                launch {
                    notifyItemChanged(pos)
                    notifyItemMoved(pos, 0)
                }
            }
        }
    }

    fun editShopItemInDialog(pos: Int, itemNewName: String) {
        shopItemsList[pos].itemName = itemNewName
        notifyItemChanged(pos)
    }

    fun getShopItemName(pos: Int): String {
        return shopItemsList[pos].itemName
    }

    //count "true"(items done mean with cross over text) in array from sharedPreference,
    fun countDoneItemsInSharedPreferencesArray(arr: LinkedList<ShopItemData>){
        countItemDoneAsTrue = 0
        for(element in arr){
            if(element.stateItem)
                this.countItemDoneAsTrue++
        }
    }

    fun setRecycleView(rcView: RecyclerView) {
        this.rcView = rcView
    }
}