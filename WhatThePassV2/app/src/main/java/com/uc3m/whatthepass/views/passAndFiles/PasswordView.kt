package com.uc3m.whatthepass.views.passAndFiles


import android.content.ContentValues.TAG
import android.content.Context
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
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.uc3m.whatthepass.R
import com.uc3m.whatthepass.databinding.FragmentPasswordViewBinding
import com.uc3m.whatthepass.viewModels.PasswordViewModel
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.uc3m.whatthepass.models.Password
import com.uc3m.whatthepass.views.passwordGeneration.PasswordGeneratorActivity
import kotlinx.coroutines.launch


class PasswordView : Fragment(){

    private lateinit var binding: FragmentPasswordViewBinding
    private  val passwordViewModel:PasswordViewModel by activityViewModels()
    private lateinit var deleteIcon: Drawable
    private lateinit var  editIcon: Drawable
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentPasswordViewBinding.inflate(inflater, container, false)
        val view = binding.root
        // Usuario de Firebase si se ha conectado Online
        auth = FirebaseAuth.getInstance()
        val user = Firebase.auth.currentUser
        // Adapter para la lista de contraseñas
        val adapter = ListAdapter(passwordViewModel)
        deleteIcon=ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_delete_24)!!
        editIcon=ContextCompat.getDrawable(requireContext(),R.drawable.ic_baseline_edit_24)!!
        val recyclerView = binding.recyclerView2
        recyclerView.adapter = adapter

        // Obtenemos el email del usuario logueado, para crear la lista de sus contraseñas
        val sp = requireActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE)
        val email = sp.getString("loginEmail", null)
        if (!email.equals("Online")) {
            lifecycleScope.launch{
                // Ejecutamos la función del viewModel para crear la lista de entradas
                if (email != null) {
                    passwordViewModel.findPasswordsByUser(email)
                }
            }
        } else if(email.equals("Online")) {
            val database = FirebaseDatabase.getInstance()
            val myRef = database.getReference("Users/" + user.uid + "/passwords")

            val childEventListener = object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                    Log.d(TAG, "onChildAdded:" + dataSnapshot.key!!)

                    // A new comment has been added, add it to the displayed list
                    val pass = dataSnapshot!!.getValue(Password::class.java)

                    if (pass != null) {
                        Log.d(TAG, "QUE SALIO:" + pass.toString())
                        adapter.addData(pass)
                    }

                }

                override fun onChildChanged(
                    dataSnapshot: DataSnapshot,
                    previousChildName: String?
                ) {
                    Log.d(TAG, "onChildChanged: ${dataSnapshot.key}")

                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so displayed the changed comment.
                    // val newComment = dataSnapshot.getValue<Password>()
                    //val commentKey = dataSnapshot.key

                    // ...
                }

                override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                    Log.d(TAG, "onChildRemoved:" + dataSnapshot.key!!)
                    val pass = dataSnapshot!!.getValue(Password::class.java)
                    if (pass != null) {
                        adapter.deletePasswordFromFirebase(pass.id)
                    }


                }

                override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                    Log.d(TAG, "onChildMoved:" + dataSnapshot.key!!)

                    // A comment has changed position, use the key to determine if we are
                    // displaying this comment and if so move it.
                    //val movedComment = dataSnapshot.getValue<Password>()
                    //val commentKey = dataSnapshot.key

                    // ...
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w(TAG, "postComments:onCancelled", databaseError.toException())

                }
            }
            myRef.addChildEventListener(childEventListener)
        }
        else
        { // Si no encuentra el email en el almacenamiento clave/valor, error
            Toast.makeText(requireContext(), "An error has occurred!", Toast.LENGTH_LONG).show()
            return view
        }

        // Mostramos en el adaptare la lista de contraseñas del usuario logueado
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        passwordViewModel.readUserPasswords.observe(viewLifecycleOwner, { password ->
            adapter.setData(password)
        })

        recyclerView.addOnItemTouchListener(RecyclerItemClickListener(requireContext(), recyclerView, object : RecyclerItemClickListener.OnItemClickListener {

            override fun onItemClick(view: View, position: Int) {
                passwordViewModel.sentPassword(adapter.getData(position))
                findNavController().navigate(R.id.action_passwordView_to_passDetailFragment)
            }
            override fun onItemLongClick(view: View?, position: Int) {
                // Do something
            }
        }))
        binding.addButton.setOnClickListener{
            findNavController().navigate(R.id.action_passwordView_to_passwordInfoFragment)
        }
        binding.floatingActionButton.setOnClickListener {
            val intent = Intent(this@PasswordView.context, PasswordGeneratorActivity::class.java)
            activity?.startActivity(intent)
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
                            val pos = viewHolder.adapterPosition
                            val pas = adapter.deleteItem(pos)
                            if(!email.equals("Online")){
                                passwordViewModel.deletePassword(pas)
                            }else{
                                val database = FirebaseDatabase.getInstance()
                                val myRef = database.getReference("Users/" + user.uid + "/passwords/"+pas.id)
                                myRef.removeValue()
                            }

                        } else {
                            passwordViewModel.sentPassword(adapter.getData(viewHolder.adapterPosition))
                            findNavController().navigate(R.id.action_passwordView_to_passEditFragment)
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