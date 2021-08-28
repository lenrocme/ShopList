package com.example.shopitemsfragmentsbm.fragments.shop_list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.shopitemsfragmentsbm.databinding.FragmentShopListsBinding


class FragmentShopLists : Fragment() {
    lateinit var binding: FragmentShopListsBinding

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




    //singleton
    companion object {
        @JvmStatic
        fun newInstance() = FragmentShopLists()
    }
}