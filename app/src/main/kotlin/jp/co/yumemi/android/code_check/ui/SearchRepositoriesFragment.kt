package jp.co.yumemi.android.code_check.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import jp.co.yumemi.android.code_check.viewmodel.SearchRepositoriesViewModel
import jp.co.yumemi.android.code_check.model.RepositoryItem
import jp.co.yumemi.android.code_check.databinding.SearchRepositoriesFragmentBinding
import jp.co.yumemi.android.code_check.databinding.LayoutItemBinding
import jp.co.yumemi.android.code_check.viewmodel.RepositoryState

/**
 * SearchRepositoriesFragmentは、リポジトリの一覧を表示するFragmentです。
 * RecyclerViewを使用してリポジトリの一覧を表示し、検索機能を提供します。
 */
@AndroidEntryPoint
class SearchRepositoriesFragment : Fragment() {
    // ViewBindingのインスタンスを保持するプライベート変数です。Viewが破棄された際にはnullに設定されます。
    private var _binding: SearchRepositoriesFragmentBinding? = null
    //    安全にBindingインスタンスにアクセスするためのプロパティです。_bindingがnullの場合、IllegalStateExceptionを投げます。
    //    これにより、Viewのライフサイクル外でのアクセスを防ぐことができます。
    private val binding get() = _binding ?: throw IllegalStateException("BindingはonCreateViewとonDestroyViewの間でのみアクセス可能です")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // ViewBindingを利用してレイアウトをインフレートし、_bindingに格納します。
        _binding = SearchRepositoriesFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewModelを初期化
        val viewModel: SearchRepositoriesViewModel by viewModels()


        // CustomAdapterを初期化、アイテムクリック時の処理を定義
        val adapter = CustomAdapter { item ->
            gotoRepositoryFragment(item)
        }

        // RecyclerViewの設定
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = adapter
        }

        // UI状態管理関数
        fun showLoading() {
            binding.progressBar.visibility = View.VISIBLE
        }

        fun hideLoading() {
            binding.progressBar.visibility = View.GONE
        }

        fun showError(message: String?) {
            Snackbar.make(binding.root, "Error: $message", Snackbar.LENGTH_LONG).show()
        }

        fun showEmptyState() {
            binding.emptyStateTextView.visibility = View.VISIBLE
            binding.emptyStateTextView.text = "No items found"
        }

        fun updateUI(items: List<RepositoryItem>) {
            if (items.isEmpty()) {
                showEmptyState()
            } else {
                adapter.submitList(items)
            }
        }

        // ViewModelの状態監視
        viewModel.repositoryState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is RepositoryState.Loading -> showLoading()
                is RepositoryState.Success -> {
                    hideLoading()
                    updateUI(state.data)
                }
                is RepositoryState.Error -> {
                    hideLoading()
                    showError(state.exception.message)
                }
            }
        }

        // 検索ボックスの入力アクションをハンドル
        binding.searchInputText.setOnEditorActionListener { editText, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                editText.text?.toString()?.let { query ->
                    Log.d("SearchRepositoriesFragment", "Search query: $query")
                    viewModel.searchRepositories(query)
                }
                true // アクションをここで処理
            } else {
                false // その他のアクションは無視
            }
        }
        // 検索ボタンに OnClickListener を設定
        binding.searchButton.setOnClickListener {
            val query = binding.searchInputText.text.toString()
            if (query.isNotEmpty()) {
                Log.d("SearchRepositoriesFragment", "Search button clicked with query: $query")
                viewModel.searchRepositories(query)
            }
        }
    }
    // 指定されたitemでリポジトリの詳細画面へ遷移するためのメソッドです。
    // Navigation ComponentのActionを使用して遷移を行います。
    private fun gotoRepositoryFragment(item: RepositoryItem) {
        val action = SearchRepositoriesFragmentDirections.actionRepositoriesFragmentToRepositoryFragment(item)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Viewが破棄される際に、Bindingの参照をクリアします。これによりメモリリークを防ぎます。
        _binding = null
    }
}
/**
 * DiffUtilコールバックは、リスト内のアイテムが同じかどうかを判断します。
 */
val diffUtil = object : DiffUtil.ItemCallback<RepositoryItem>() {
    override fun areItemsTheSame(oldItem: RepositoryItem, newItem: RepositoryItem): Boolean =
        oldItem.name == newItem.name
    override fun areContentsTheSame(oldItem: RepositoryItem, newItem: RepositoryItem): Boolean = oldItem == newItem
}

/**
 * リポジトリアイテムのリストを表示するためのRecyclerViewアダプター。
 * @param itemClickListener リポジトリアイテムがクリックされた時のコールバック関数。
 */
class CustomAdapter(
    private val itemClickListener: (item: RepositoryItem) -> Unit
) : ListAdapter<RepositoryItem, CustomAdapter.ItemViewHolder>(diffUtil) {

    /**
     * 新しいViewHolderを生成する。
     * @param parent ViewHolderが存在する親のViewGroup。
     * @param viewType 新しいViewのビュータイプ。
     * @return 新しいItemViewHolderインスタンス。
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = LayoutItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    /**
     * ViewHolderにデータをバインドする。
     * @param holder バインドされるViewHolder。
     * @param position リスト内のアイテムの位置。
     */
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.binding.repositoryNameView.text = currentItem.name
        holder.itemView.setOnClickListener {
            itemClickListener(currentItem)
        }
    }

    /**
     * リポジトリアイテムのビューを保持するためのViewHolder。
     * @param binding ビューバインディング。
     */
    class ItemViewHolder(val binding: LayoutItemBinding) : RecyclerView.ViewHolder(binding.root)
}