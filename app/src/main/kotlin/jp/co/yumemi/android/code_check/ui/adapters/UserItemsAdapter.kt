package jp.co.yumemi.android.code_check.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import jp.co.yumemi.android.code_check.databinding.ItemRepositoryBinding
import jp.co.yumemi.android.code_check.model.RepositoryItem

class UserItemsAdapter(
    private val items: List<RepositoryItem>,
    private val onItemSelected: (RepositoryItem) -> Unit
) : RecyclerView.Adapter<UserItemsAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ItemRepositoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RepositoryItem, onItemSelected: (RepositoryItem) -> Unit) {
            binding.apply {
                repositoryName.text = item.name
                starCount.text = item.stargazersCount.toString() // Bind the star count to the TextView
                root.setOnClickListener { onItemSelected(item) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRepositoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, onItemSelected)
    }

    override fun getItemCount(): Int = items.size
}