package com.uc3m.whatthepass.views.passAndFiles

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.uc3m.whatthepass.databinding.RecyclerViewItemBinding
import com.uc3m.whatthepass.models.File
import com.uc3m.whatthepass.models.Password
import com.uc3m.whatthepass.viewModels.FilesViewModel


class FileAdapter(fileViewModel: FilesViewModel): RecyclerView.Adapter<FileAdapter.MyViewHolder>() {
    private val fileViewModel= fileViewModel
    private var fileList = emptyList<File>()


    class MyViewHolder(val binding: RecyclerViewItemBinding): RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = RecyclerViewItemBinding.inflate(
            LayoutInflater.from(parent.context), parent,
            false)
        return MyViewHolder(binding)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = fileList[position]
        with(holder){
            binding.textView.text = currentItem.name
            binding.textView2.text = currentItem.path
        }


    }

    override fun getItemCount(): Int {
        return fileList.size
    }

    fun setData(fileList: List<File>){
        this.fileList = fileList
        notifyDataSetChanged()
    }

    fun deleteItem(index: Int): File {
        val pas=this.fileList[index]
        this.fileList.drop(index)
        notifyItemRemoved(index)
        return pas

    }

    fun getData(index: Int): File {
        return this.fileList[index]
    }


}