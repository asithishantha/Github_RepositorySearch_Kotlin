/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import jp.co.yumemi.android.code_check.databinding.FragmentOneBinding

/**
 * OneFragmentは、リポジトリの一覧を表示するFragmentです。
 * RecyclerViewを使用してリポジトリの一覧を表示し、検索機能を提供します。
 */
class OneFragment : Fragment(R.layout.fragment_one) {

    /**
     * FragmentのViewが生成された時に呼ばれます。
     * RecyclerViewのセットアップやViewModelの初期化を行います。
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Fragmentのバインディングを取得します。
        val binding = FragmentOneBinding.bind(view)

        // ViewModelを初期化します。
        val viewModel = OneViewModel(requireContext())

        // RecyclerViewのレイアウトマネージャーを設定します。
        val layoutManager = LinearLayoutManager(requireContext())

        // RecyclerViewに区切り線を追加します。
        val dividerItemDecoration =
            DividerItemDecoration(requireContext(), layoutManager.orientation)

        // RecyclerViewのアダプターを設定します。
        val adapter = CustomAdapter(object : CustomAdapter.OnItemClickListener {
            override fun onItemClick(item: item) {
                gotoRepositoryFragment(item)
            }
        })

        // 検索ボックスのエンターキーのリスナーを設定します。
        binding.searchInputText
            .setOnEditorActionListener { editText, action, _ ->
                if (action == EditorInfo.IME_ACTION_SEARCH) {
                    // 検索ボックスに入力された文字列で検索を実行し、結果を更新します。
                    editText.text.toString().let {
                        viewModel.searchResults(it).apply {
                            adapter.submitList(this)
                        }
                    }
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }

        // RecyclerViewを設定します。
        binding.recyclerView.also {
            it.layoutManager = layoutManager
            it.addItemDecoration(dividerItemDecoration)
            it.adapter = adapter
        }
    }

    /**
     * リポジトリの詳細画面に遷移します。
     */
    fun gotoRepositoryFragment(item: item) {
        val action = OneFragmentDirections
            .actionRepositoriesFragmentToRepositoryFragment(item = item)
        findNavController().navigate(action)
    }
}

// RecyclerViewのDiffUtilコールバックを定義します。
val diffUtil = object : DiffUtil.ItemCallback<item>() {
    override fun areItemsTheSame(oldItem: item, newItem: item): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: item, newItem: item): Boolean {
        return oldItem == newItem
    }

}

/**
 * RecyclerViewのアダプターを定義します。
 */
class CustomAdapter(
    private val itemClickListener: OnItemClickListener,
) : ListAdapter<item, CustomAdapter.ItemViewHolder>(diffUtil) {

    /**
     * RecyclerViewのアイテムビューを保持するViewHolderクラスです。
     */
    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view)

    /**
     * アダプターのアイテムクリックリスナーのインターフェースを定義します。
     */
    interface OnItemClickListener {
        fun onItemClick(item: item)
    }

    /**
     * ViewHolderを生成します。
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // レイアウトからビューをインフレートします。
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_item, parent, false)
        return ItemViewHolder(view)
    }

    /**
     * ViewHolderにデータをバインドします。
     */
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        // アイテムのデータを取得します。
        val currentItem = getItem(position)
        // ビューホルダーにアイテムの名前を設定します。
        (holder.itemView.findViewById<View>(R.id.repositoryNameView) as TextView).text =
            currentItem.name
        // アイテムがクリックされた時のリスナーを設定します。
        holder.itemView.setOnClickListener {

            itemClickListener.onItemClick(currentItem)
        }
    }
}
