package com.uc3m.whatthepass.views.passAndFiles


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.uc3m.whatthepass.R
import com.uc3m.whatthepass.databinding.FragmentPasswordViewBinding
import com.uc3m.whatthepass.viewModels.PasswordViewModel

class PasswordView : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var binding: FragmentPasswordViewBinding
    private lateinit var passwordViewModel: PasswordViewModel
    lateinit var comm: Comunicator

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPasswordViewBinding.inflate(inflater, container, false)
        val view = binding.root



        passwordViewModel = ViewModelProvider(this).get(PasswordViewModel::class.java)
        val adapter = ListAdapter()
        val recyclerView = binding.recyclerView2
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        passwordViewModel.readAll.observe(viewLifecycleOwner, {
            password -> adapter.setData(password)
        })

        binding.addButton.setOnClickListener{
            findNavController().navigate(R.id.action_passwordView_to_passwordInfoFragment)
        }




        return view
    }
}