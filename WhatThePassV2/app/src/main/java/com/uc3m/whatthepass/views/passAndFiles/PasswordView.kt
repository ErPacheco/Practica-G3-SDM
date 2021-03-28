package com.uc3m.whatthepass.views.passAndFiles


import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.uc3m.whatthepass.R
import com.uc3m.whatthepass.databinding.FragmentPasswordViewBinding
import com.uc3m.whatthepass.models.Password
import com.uc3m.whatthepass.models.PasswordOnline
import com.uc3m.whatthepass.viewModels.PasswordViewModel
import com.uc3m.whatthepass.views.passwordGeneration.PasswordGeneratorActivity


class PasswordView : Fragment(){

    private lateinit var binding: FragmentPasswordViewBinding
    private  val passwordViewModel:PasswordViewModel by activityViewModels()
    private lateinit var deleteIcon: Drawable
    private lateinit var  editIcon: Drawable
    private lateinit var auth: FirebaseAuth


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPasswordViewBinding.inflate(inflater, container, false)
        val view = binding.root

        auth = FirebaseAuth.getInstance()
        if(auth.currentUser!=null){
            val database = FirebaseDatabase.getInstance()
            val myRef = database.getReference("Users/"+auth.currentUser.uid+"passwords")

            val postListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Get Post object and use the values to update the UI
                  val pass = dataSnapshot.getValue() as HashMap<String,Password>?
                    //Log.d("QUE COJONES HAY", pass.toString()!!)
                    if(pass!=null){
                        /*val adapter = ListAdapter(passwordViewModel)
                        deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_delete_24)!!
                        editIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_edit_24)!!
                        val recyclerView= binding.recyclerView2
                        recyclerView.adapter = adapter
                        val l= listOf<MutableCollection<Password>>(pass.values)
                        recyclerView.layoutManager = LinearLayoutManager(requireContext())
                        if(pass!=null){
                            adapter.setData(l as List<Password>)
                        }


                        recyclerView.addOnItemTouchListener(RecyclerItemClickListener(requireContext(), recyclerView, object : RecyclerItemClickListener.OnItemClickListener {

                            override fun onItemClick(view: View, position: Int) {
                                passwordViewModel.sentPassword(adapter.getData(position))
                                findNavController().navigate(R.id.action_passwordView_to_passDetailFragment)
                            }

                            override fun onItemLongClick(view: View?, position: Int) {
                                TODO("do nothing")
                            }
                        }))*/
                    }


                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
                }
            }
            myRef.addValueEventListener(postListener)

        }else { // inicio de sesión local

            val adapter = ListAdapter(passwordViewModel)
            deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_delete_24)!!
            editIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_edit_24)!!
            val recyclerView= binding.recyclerView2
            recyclerView.adapter = adapter

            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            passwordViewModel.readAll.observe(viewLifecycleOwner, { password ->
                adapter.setData(password)
            })

            recyclerView.addOnItemTouchListener(RecyclerItemClickListener(requireContext(), recyclerView, object : RecyclerItemClickListener.OnItemClickListener {

                override fun onItemClick(view: View, position: Int) {
                    passwordViewModel.sentPassword(adapter.getData(position))
                    findNavController().navigate(R.id.action_passwordView_to_passDetailFragment)
                }

                override fun onItemLongClick(view: View?, position: Int) {
                    TODO("do nothing")
                }
            }))

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
                                passwordViewModel.deletePassword(pas)
                            }else{

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
        }
        binding.addButton.setOnClickListener{
            findNavController().navigate(R.id.action_passwordView_to_passwordInfoFragment)
        }
        binding.floatingActionButton.setOnClickListener {
            val intent = Intent(this@PasswordView.context, PasswordGeneratorActivity::class.java)
            activity?.startActivity(intent)
        }




        return view
    }



}