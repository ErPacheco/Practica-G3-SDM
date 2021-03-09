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


class ListAdapter(/*val context: Context*/): RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    private var passwordList = emptyList<Password>()
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