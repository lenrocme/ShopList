package com.example.shopitemsfragmentsbm.fragments.info_guide

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.shopitemsfragmentsbm.databinding.FragmentInfoGuideBinding

class FragmentInfoGuide : Fragment() {
    lateinit var binding: FragmentInfoGuideBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
            binding = FragmentInfoGuideBinding.inflate(inflater)
            return binding.root//inflater.inflate(R.layout.fragment_info_guide, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() = FragmentInfoGuide()
    }
}