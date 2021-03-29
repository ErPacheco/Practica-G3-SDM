package com.uc3m.whatthepass.views.passAndFiles

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.uc3m.whatthepass.R
import com.uc3m.whatthepass.databinding.RecyclerViewItemBinding
import com.uc3m.whatthepass.models.Password
import com.uc3m.whatthepass.viewModels.PasswordViewModel


class ListAdapter(passwordViewModel: PasswordViewModel): RecyclerView.Adapter<ListAdapter.MyViewHolder>() {
    private val passwordViewModel= passwordViewModel
    private var passwordList = emptyList<Password>()
    private val passwordListOnline = ArrayList<Password>()

    class MyViewHolder(val binding: RecyclerViewItemBinding): RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = RecyclerViewItemBinding.inflate(LayoutInflater.from(parent.context), parent,
                false)
        return MyViewHolder(binding)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = passwordList[position]
        with(holder){
            binding.textView.text = currentItem.name
            binding.textView2.text = currentItem.url
        }


    }

    override fun getItemCount(): Int {
        return passwordList.size
    }

    fun setData(passwordList: List<Password>){
        this.passwordList = passwordList
        notifyDataSetChanged()
    }

    fun addData(password: Password){
        passwordListOnline.add(password)
        setData(passwordListOnline.toList())

    }
    fun deleteItem(index: Int):Password{
        val pas=this.passwordList[index]
        this.passwordList.drop(index)
        notifyItemRemoved(index)
        return pas

    }

    fun getData(index: Int):Password{
        return this.passwordList[index]
    }



}

