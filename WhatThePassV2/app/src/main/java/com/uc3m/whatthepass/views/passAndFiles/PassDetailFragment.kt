package com.uc3m.whatthepass.views.passAndFiles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.uc3m.whatthepass.databinding.FragmentPassDetailBinding


class PassDetailFragment : Fragment() {
    private lateinit var binding: FragmentPassDetailBinding
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPassDetailBinding.inflate(inflater, container, false)
        val view = binding.root


        return view
    }
}