package com.uc3m.whatthepass.views.passAndFiles

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.uc3m.whatthepass.R
import com.uc3m.whatthepass.databinding.FragmentFilesBinding
import com.uc3m.whatthepass.databinding.FragmentPasswordViewBinding
import com.uc3m.whatthepass.viewModels.FilesViewModel
import com.uc3m.whatthepass.viewModels.PasswordViewModel
import com.uc3m.whatthepass.views.passwordGeneration.PasswordGeneratorActivity


class FilesFragment : Fragment() {
    private lateinit var binding: FragmentFilesBinding
    private  val filesViewModel:FilesViewModel by activityViewModels()
    private lateinit var deleteIcon: Drawable
    private lateinit var  editIcon: Drawable


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFilesBinding.inflate(inflater, container, false)
        val view = binding.root


        val adapter = FileAdapter(filesViewModel)
        deleteIcon=ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_delete_24)!!
        editIcon=ContextCompat.getDrawable(requireContext(),R.drawable.ic_baseline_edit_24)!!
        val recyclerView = binding.recyclerViewFiles
        recyclerView.adapter = adapter

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        filesViewModel.readAll.observe(viewLifecycleOwner, { files ->
            adapter.setData(files)
        })

        recyclerView.addOnItemTouchListener(RecyclerItemClickListener(requireContext(), recyclerView, object : RecyclerItemClickListener.OnItemClickListener {

            override fun onItemClick(view: View, position: Int) {
                //filesViewModel.sentPassword(adapter.getData(position))
                //findNavController().navigate(R.id.action_passwordView_to_passDetailFragment)
            }
            override fun onItemLongClick(view: View?, position: Int) {
                TODO("do nothing")
            }
        }))
        /*binding.addButton.setOnClickListener{
            findNavController().navigate(R.id.action_passwordView_to_passwordInfoFragment)
        }
        binding.floatingActionButton.setOnClickListener {
            val intent = Intent(this@PasswordView.context, PasswordGeneratorActivity::class.java)
            activity?.startActivity(intent)
        }*/
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
                        var pas = adapter.deleteItem(pos)
                        filesViewModel.deleteFile(pas)
                    }else{

                        /*filesViewModel.sentPassword(adapter.getData(viewHolder.adapterPosition))
                        findNavController().navigate(R.id.action_passwordView_to_passDetailFragment)*/
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
                    val itemView = viewHolder.itemView
                    val iconMarginVertical = (viewHolder.itemView.height - deleteIcon.intrinsicHeight) / 2
                    val iconMarginVerticalEdit = (viewHolder.itemView.height - editIcon.intrinsicHeight) / 2
                    if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                        if (dX > 0) {
                            swipeBackground.setBounds(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                            deleteIcon.setBounds(itemView.left + iconMarginVertical, itemView.top + iconMarginVertical,
                                itemView.left + iconMarginVertical + deleteIcon.intrinsicWidth, itemView.bottom - iconMarginVertical)
                            swipeBackground.draw(c)
                        }else {
                            swipeBackgroundEdit.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                            editIcon.setBounds(itemView.right - iconMarginVerticalEdit - editIcon.intrinsicWidth, itemView.top + iconMarginVerticalEdit,
                                itemView.right - iconMarginVerticalEdit, itemView.bottom - iconMarginVerticalEdit)
                            swipeBackgroundEdit.draw(c)

                        }

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