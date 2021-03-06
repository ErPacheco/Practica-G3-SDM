package com.uc3m.whatthepass.views.passAndFiles

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.uc3m.whatthepass.viewModels.PasswordViewModel

class SwipeToDelete(var adapter: ListAdapter, var passwordViewModel: PasswordViewModel):ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {

    override fun onMove(recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder): Boolean {
        TODO("Not yet implemented")
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        var pos = viewHolder.adapterPosition
        var pas=adapter.deleteItem(direction)
        passwordViewModel.deletePassword(pas)




    }
}