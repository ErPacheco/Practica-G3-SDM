package com.uc3m.whatthepass.views.passAndFiles

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.uc3m.whatthepass.databinding.RecyclerViewItemBinding
import com.uc3m.whatthepass.models.Password
import com.uc3m.whatthepass.viewModels.PasswordViewModel



class ListAdapter: RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    private var passwordList = emptyList<Password>()
    lateinit var comm: Comunicator
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
            binding.delPasswordButton.setOnClickListener{

                deleteItem(position)
            }

        }
    }

    override fun getItemCount(): Int {
        return passwordList.size
    }

    fun setData(passwordList: List<Password>){
        this.passwordList = passwordList
        notifyDataSetChanged()
    }

    fun deleteItem(index: Int){
        this.passwordList.drop(index)
        notifyDataSetChanged()
    }

}