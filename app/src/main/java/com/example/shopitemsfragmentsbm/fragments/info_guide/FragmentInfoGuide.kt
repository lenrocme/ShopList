package com.example.shopitemsfragmentsbm.fragments.info_guide

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.transition.TransitionInflater
import com.example.shopitemsfragmentsbm.LAST_SELECTED_FRAGMENTS
import com.example.shopitemsfragmentsbm.R
import com.example.shopitemsfragmentsbm.databinding.FragmentInfoGuideBinding

class FragmentInfoGuide : Fragment() {
    lateinit var binding: FragmentInfoGuideBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        //enterTransition = inflater.inflateTransition(R.transition.slide_right)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
            binding = FragmentInfoGuideBinding.inflate(inflater)
            return binding.root//inflater.inflate(R.layout.fragment_info_guide, container, false)
    }

    override fun onStart() {
        super.onStart()
    }
    override fun onStop() {
        super.onStop()

    }

    companion object {
        @JvmStatic
        fun newInstance() = FragmentInfoGuide()
    }
}