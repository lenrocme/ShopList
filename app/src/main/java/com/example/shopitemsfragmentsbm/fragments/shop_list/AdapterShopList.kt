package com.example.shopitemsfragmentsbm.fragments.shop_list

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shopitemsfragmentsbm.R
import com.example.shopitemsfragmentsbm.databinding.ShopListRcviewItemBinding
import com.example.shopitemsfragmentsbm.fragments.shop_items.IC_CREATE
import com.example.shopitemsfragmentsbm.fragments.shop_items.IC_DELETE
import com.example.shopitemsfragmentsbm.fragments.shop_items.IC_DESCRIPTION
import com.example.shopitemsfragmentsbm.fragments.shop_items.ShopItemsSharedPreference
import java.util.*

class AdapterShopList(private val listener: OnItemClickListenerShopList): RecyclerView.Adapter<AdapterShopList.ItemHolder>() {
        //private lateinit var rcView: RecyclerView
        var shopList = LinkedList<ShopListData>()

        inner class ItemHolder(item: View): RecyclerView.ViewHolder(item), View.OnClickListener {
            private val binding = ShopListRcviewItemBinding.bind(item)

            fun bind(shopList: ShopListData) = with(binding){
                iconCreateShopList.setImageResource(IC_CREATE)
                iconDeleteShopList.setImageResource(IC_DELETE)
                iconDescriptionShopList.setImageResource(IC_DESCRIPTION)
                tvNameShopList.text = shopList.itemName
                tvAddedDate.text = shopList.dateCreated
                tvChangedDate.text = shopList.dateChanged // gonna be changed, then item from this shop list will be added or deleted
                tvCountItemsShopList.text = ShopItemsSharedPreference()// fin this shoplist in sharedpreference and count how many items are there
                    .getCountNrItemsInOneShopList(iconCreateShopList.context as Activity, shopList.indexShopList)
            }
            init {
                binding.iconDeleteShopList.setOnClickListener(this)
                binding.iconCreateShopList.setOnClickListener(this)
                binding.tvNameShopList.setOnClickListener(this)
                binding.iconDescriptionShopList.setOnClickListener(this)
            }

            override fun onClick(v: View?) = with(binding) {
                val position: Int = adapterPosition
                if(position != RecyclerView.NO_POSITION) {

                    when(v?.id) {
                        iconDescriptionShopList.id -> listener.onItemClickDescription(position)
                        iconDeleteShopList.id -> listener.onItemClickDelete(position)
                        iconCreateShopList.id -> listener.onItemClickEdit(position) // edit shoplist name
                        tvNameShopList.id -> listener.onItemCLickGoToList(position)
                    }
                }
            }
        }

        interface OnItemClickListenerShopList{
            fun onItemClickDelete(position: Int)    // delete selectedItem from shop items
            fun onItemClickEdit(position: Int)
            fun onItemCLickGoToList(position: Int)
            fun onItemClickDescription(position: Int)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.shop_list_rcview_item, parent, false)
            return ItemHolder(view)
        }

        override fun onBindViewHolder(holder: ItemHolder, position: Int) {
            holder.bind(shopList[position])
        }

        override fun getItemCount(): Int {
            return shopList.size
        }

        fun addShopItem(shopListItem: ShopListData){
            shopList.addFirst(shopListItem)
            notifyItemInserted(0)
        }

        fun deleteItem(pos: Int) {
            //if(shopItemsList[pos].stateItem) // if text is crossover, after delete this element count--
           //     countItemDoneAsTrue--
            shopList.removeAt(pos)
            notifyItemRemoved(pos)
        }
        // make text in this label cross out, and move it at the end of the list with non cross out text in rcView


        fun editShopItemInDialog(pos: Int, itemNewName: String) {
            shopList[pos].itemName = itemNewName
            notifyItemChanged(pos)
        }

        fun getShopItemName(pos: Int): String {
            return shopList[pos].itemName
        }

        /*fun setRecycleView(rcView: RecyclerView) {
            this.rcView = rcView
        }*/
}