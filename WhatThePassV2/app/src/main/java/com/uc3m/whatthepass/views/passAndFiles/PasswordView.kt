package com.uc3m.whatthepass.views.passAndFiles

import android.content.ContentValues.TAG
import android.content.Context
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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.uc3m.whatthepass.R
import com.uc3m.whatthepass.databinding.FragmentPasswordViewBinding
import com.uc3m.whatthepass.models.Password
import com.uc3m.whatthepass.viewModels.PasswordViewModel
import kotlinx.coroutines.launch

class PasswordView : Fragment() {

    private lateinit var binding: FragmentPasswordViewBinding
    private val passwordViewModel: PasswordViewModel by activityViewModels()
    private lateinit var auth: FirebaseAuth
    private lateinit var childEventListener: ChildEventListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPasswordViewBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.progressBar.visibility = View.VISIBLE
        // Usuario de Firebase si se ha conectado Online
        auth = FirebaseAuth.getInstance()
        val user = Firebase.auth.currentUser

        // Adapter para la lista de contraseñas
        val adapter = ListAdapter(passwordViewModel)
        val recyclerView = binding.recyclerView2
        recyclerView.adapter = adapter

        // Obtenemos el email del usuario logueado, para crear la lista de sus contraseñas
        val sp = requireActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE)
        val email = sp.getString("loginEmail", null)

        if (!email.equals("Online")) {
            lifecycleScope.launch {
                // Ejecutamos la función del viewModel para crear la lista de entradas
                if (email != null) {
                    passwordViewModel.findPasswordsByUser(email)
                }
            }
        } else if (email.equals("Online")) {
            if (user != null) {
                val database = FirebaseDatabase.getInstance()
                val myRef = database.getReference("Users/" + user.uid + "/passwords")
                childEventListener = object : ChildEventListener {
                    override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                        Log.d(TAG, "onChildAdded:" + dataSnapshot.key!!)

                        val pass = dataSnapshot.getValue(Password::class.java)

                        if (pass != null) {
                            adapter.addData(pass)
                        }
                    }

                    override fun onChildChanged(
                        dataSnapshot: DataSnapshot,
                        previousChildName: String?
                    ) {
                        Log.d(TAG, "onChildChanged: ${dataSnapshot.key}")
                    }

                    override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                        Log.d(TAG, "onChildRemoved:" + dataSnapshot.key!!)
                        val pass = dataSnapshot.getValue(Password::class.java)
                        if (pass != null) {
                            adapter.deletePasswordFromFirebase(pass.id)
                        }
                    }

                    override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                        Log.d(TAG, "onChildMoved:" + dataSnapshot.key!!)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.w(TAG, "postComments:onCancelled", databaseError.toException())
                    }
                }
                myRef.addChildEventListener(childEventListener)
            }
        } else { // Si no encuentra el email en el almacenamiento clave/valor, error
            Toast.makeText(requireContext(), "An error has occurred!", Toast.LENGTH_LONG).show()
            return view
        }

        // Mostramos en el adaptare la lista de contraseñas del usuario logueado
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        passwordViewModel.readUserPasswords.observe(
            viewLifecycleOwner,
            { password ->
                adapter.setData(password)
            }
        )

        recyclerView.addOnItemTouchListener(
            RecyclerItemClickListener(
                requireContext(), recyclerView,
                object : RecyclerItemClickListener.OnItemClickListener {

                    override fun onItemClick(view: View, position: Int) {
                        passwordViewModel.sentPassword(adapter.getData(position))
                        findNavController().navigate(R.id.action_passwordView_to_passDetailFragment)
                    }
                    override fun onItemLongClick(view: View?, position: Int) {
                        // Do something
                    }
                }
            )
        )
        binding.addButton.setOnClickListener {
            findNavController().navigate(R.id.action_passwordView_to_passwordInfoFragment)
        }

        // Objeto que habilita las funciones de deslizar en cada uno de los items de la recycler view
        val itemTouchHelperCallback =
            object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                private var swipeBackground: ColorDrawable = ColorDrawable(Color.parseColor("#FF6666"))
                private var swipeBackgroundEdit: ColorDrawable = ColorDrawable(Color.parseColor("#598BFF"))

                private var deleteIcon: Drawable? = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_delete_24)
                private var editIcon: Drawable? = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_edit_24)

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
                        if (!email.equals("Online")) {
                            passwordViewModel.deletePassword(pas)
                        } else {
                            if (user != null) {
                                val database = FirebaseDatabase.getInstance()
                                val myRef = database.getReference("Users/" + user.uid + "/passwords/" + pas.id)
                                myRef.removeValue()
                            }
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
                    val iconMarginVertical = (viewHolder.itemView.height - deleteIcon!!.intrinsicHeight) / 2
                    val iconMarginVerticalEdit = (viewHolder.itemView.height - editIcon!!.intrinsicHeight) / 2
                    if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                        if (dX > 0) {
                            swipeBackground.setBounds(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                            deleteIcon?.setBounds(
                                itemView.left + iconMarginVertical, itemView.top + iconMarginVertical,
                                itemView.left + iconMarginVertical + deleteIcon!!.intrinsicWidth,
                                itemView.bottom - iconMarginVertical
                            )
                            swipeBackground.draw(c)
                            deleteIcon?.draw(c)
                        } else {
                            swipeBackgroundEdit.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                            editIcon?.setBounds(
                                itemView.right - iconMarginVerticalEdit - editIcon!!.intrinsicWidth, itemView.top + iconMarginVerticalEdit,
                                itemView.right - iconMarginVerticalEdit, itemView.bottom - iconMarginVerticalEdit
                            )
                            swipeBackgroundEdit.draw(c)
                            editIcon?.draw(c)
                        }
                    }

                    super.onChildDraw(
                        c, recyclerView, viewHolder,
                        dX, dY, actionState, isCurrentlyActive
                    )
                }
            }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        binding.progressBar.visibility = View.GONE

        return view
    }
}
