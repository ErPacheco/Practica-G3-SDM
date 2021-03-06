package com.uc3m.whatthepass.views.passAndFiles


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.uc3m.whatthepass.R
import com.uc3m.whatthepass.databinding.FragmentPasswordViewBinding
import com.uc3m.whatthepass.viewModels.PasswordViewModel

class PasswordView : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var binding: FragmentPasswordViewBinding
    private lateinit var passwordViewModel: PasswordViewModel


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
       /* recyclerView.setOnTouchListener(object : OnSwipeTouchListener(context) {
             fun onSwipeLeft() {
                super.onSwipeLeft()
                Toast.makeText(context, "Swipe Left gesture detected",
                        Toast.LENGTH_SHORT)
                        .show()
            }
             fun onSwipeRight() {
                super.onSwipeRight()
                Toast.makeText(
                        context,
                        "Swipe Right gesture detected",
                        Toast.LENGTH_SHORT
                ).show()
            }
             fun onSwipeUp() {
                super.onSwipeUp()
                Toast.makeText(context, "Swipe up gesture detected", Toast.LENGTH_SHORT)
                        .show()
            }
             fun onSwipeDown() {
                super.onSwipeDown()
                Toast.makeText(context, "Swipe down gesture detected", Toast.LENGTH_SHORT)
                        .show()
            }
        })*/

        binding.addButton.setOnClickListener{
            findNavController().navigate(R.id.action_passwordView_to_passwordInfoFragment)
        }

        var itemTouchHelper = ItemTouchHelper(SwipeToDelete(adapter,passwordViewModel))
        itemTouchHelper.attachToRecyclerView(recyclerView)
        return view
    }
}