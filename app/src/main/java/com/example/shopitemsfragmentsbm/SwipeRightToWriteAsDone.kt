package com.example.shopitemsfragmentsbm

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.shopitemsfragmentsbm.fragments.shop_items.AdapterShopItem

class SwipeRightToWriteAsDone (var adapter: AdapterShopItem): ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        TODO("Not yet implemented")
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val pos: Int = viewHolder.adapterPosition
        adapter.makeItemAsDone(pos)
    }


}