package com.uc3m.whatthepass.views.passAndFiles


import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.uc3m.whatthepass.R
import com.uc3m.whatthepass.databinding.FragmentPasswordViewBinding
import com.uc3m.whatthepass.viewModels.PasswordViewModel
import androidx.fragment.app.FragmentManager


class PasswordView : Fragment(){

    private lateinit var binding: FragmentPasswordViewBinding
    private  val passwordViewModel:PasswordViewModel by activityViewModels()



    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPasswordViewBinding.inflate(inflater, container, false)
        val view = binding.root


        val adapter = ListAdapter()

        val recyclerView = binding.recyclerView2
        recyclerView.adapter = adapter

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
                passwordViewModel.readAll.observe(viewLifecycleOwner, { password ->
                    adapter.setData(password)
                })
        

        binding.addButton.setOnClickListener{
            findNavController().navigate(R.id.action_passwordView_to_passwordInfoFragment)
        }
        val itemTouchHelperCallback =
                object :
                        ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                    private var swipeBackground: ColorDrawable = ColorDrawable(Color.parseColor("#FF0000"))
                    private var swipeBackgroundEdit: ColorDrawable = ColorDrawable(Color.parseColor("#0000FF"))
                    override fun onMove(
                            recyclerView: RecyclerView,
                            viewHolder: RecyclerView.ViewHolder,
                            target: RecyclerView.ViewHolder
                    ): Boolean {

                        return false
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        if (direction == ItemTouchHelper.RIGHT) {
                            var pos = viewHolder.adapterPosition
                            var pas = adapter.deleteItem(direction)
                            passwordViewModel.deletePassword(pas)
                        }else{

                            // código para llamar a editarp
                                passwordViewModel.sentPassword(adapter.getData(viewHolder.adapterPosition))
                            findNavController().navigate(R.id.action_passwordView_to_passDetailFragment)
                        }

                    }
                    override fun onChildDraw(
                            c: Canvas,
                            recyclerView: RecyclerView,
                            viewHolder: RecyclerView.ViewHolder,
                            dX: Float,
                            dY: Float,
                            actionState: Int,
                            isCurrentlyActive: Boolean
                    ) {
                        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                            val itemView = viewHolder.itemView
                            if (dX > 0) {
                                swipeBackground.setBounds(itemView.left, itemView.top, dX.toInt(), itemView.bottom)

                            }

                            swipeBackground.draw(c)
                        }else{
                            val itemView = viewHolder.itemView
                             if (dX > 0) {
                                 swipeBackgroundEdit.setBounds(dX.toInt(), itemView.top,itemView.right , itemView.bottom)
                             }
                                 swipeBackgroundEdit.draw(c)

                        }
                        super.onChildDraw(c, recyclerView, viewHolder,
                                dX, dY, actionState, isCurrentlyActive)
                    }

                }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)



        return view
    }



}