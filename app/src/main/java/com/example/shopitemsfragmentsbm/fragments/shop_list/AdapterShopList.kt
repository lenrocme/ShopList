package com.example.shopitemsfragmentsbm.fragments.shop_list

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shopitemsfragmentsbm.R
import com.example.shopitemsfragmentsbm.databinding.ShopItemBinding
import com.example.shopitemsfragmentsbm.databinding.ShopListRcviewItemBinding
import com.example.shopitemsfragmentsbm.fragments.shop_items.IC_CREATE
import com.example.shopitemsfragmentsbm.fragments.shop_items.IC_DELETE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class AdapterShopList(private val listener: OnItemClickListener): RecyclerView.Adapter<AdapterShopList.ItemHolder>() {
        private lateinit var rcView: RecyclerView
        var shopList = LinkedList<ShopListData>()

        inner class ItemHolder(item: View): RecyclerView.ViewHolder(item), View.OnClickListener {
            private val binding = ShopListRcviewItemBinding.bind(item)

            fun bind(shopList: ShopListData) = with(binding){
                iconCreateShopList.setImageResource(IC_CREATE)
                iconDeleteShopList.setImageResource(IC_DELETE)
                iconInfoShopList.setImageResource(R.drawable.ic_info_shop_list)
                tvNameShopList.text = shopList.itemName
            }
            init {
                binding.iconDeleteShopList.setOnClickListener(this)
                binding.iconCreateShopList.setOnClickListener(this)

            }

            override fun onClick(v: View?) {
                val position: Int = adapterPosition
                if(position != RecyclerView.NO_POSITION) {

                    if (v?.id == binding.iconDeleteShopList.id)     //in case if id is deleteIcon id
                        listener.onItemClickDelete(position)

                    else if (v?.id == binding.iconCreateShopList.id)    //same for createIcon
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
            holder.bind(shopList[position])
        }

        override fun getItemCount(): Int {
            return shopList.size
        }

        fun addShopItem(shopItem: ShopListData){
            shopList.addFirst(shopItem)
            notifyItemInserted(0)
        }

        /*fun deleteItem(pos: Int) {
            if(shopItemsList[pos].stateItem) // if text is crossover, after delete this element count--
                countItemDoneAsTrue--
            shopItemsList.removeAt(pos)
            notifyItemRemoved(pos)
        }*/
        // make text in this label cross out, and move it at the end of the list with non cross out text in rcView


        fun editShopItemInDialog(pos: Int, itemNewName: String) {
            shopList[pos].itemName = itemNewName
            notifyItemChanged(pos)
        }

        fun getShopItemName(pos: Int): String {
            return shopList[pos].itemName
        }

        fun setRecycleView(rcView: RecyclerView) {
            this.rcView = rcView
        }
}