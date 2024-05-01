package jp.co.yumemi.android.code_check

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jp.co.yumemi.android.code_check.databinding.FragmentOneBinding
import jp.co.yumemi.android.code_check.databinding.LayoutItemBinding

/**
 * OneFragmentは、リポジトリの一覧を表示するFragmentです。
 * RecyclerViewを使用してリポジトリの一覧を表示し、検索機能を提供します。
 */
class OneFragment : Fragment() {
    // ViewBindingのインスタンスを保持するプライベート変数です。Viewが破棄された際にはnullに設定されます。
    private var _binding: FragmentOneBinding? = null
    //    安全にBindingインスタンスにアクセスするためのプロパティです。_bindingがnullの場合、IllegalStateExceptionを投げます。
    //    これにより、Viewのライフサイクル外でのアクセスを防ぐことができます。
    private val binding get() = _binding ?: throw IllegalStateException("BindingはonCreateViewとonDestroyViewの間でのみアクセス可能です")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // ViewBindingを利用してレイアウトをインフレートし、_bindingに格納します。
        _binding = FragmentOneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewModelを初期化
        val viewModel = OneViewModel()

        // CustomAdapterを初期化、アイテムクリック時の処理を定義
        val adapter = CustomAdapter { item ->
            gotoRepositoryFragment(item)
        }

        // RecyclerViewの設定
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = adapter
        }

        // ViewModelからの検索結果を監視
        viewModel.searchResultsLiveData.observe(viewLifecycleOwner) { searchResults ->
            Log.d("OneFragment", "Search results received: ${searchResults.size}")
            adapter.submitList(searchResults)
        }

        // 検索ボックスの入力アクションをハンドル
        binding.searchInputText.setOnEditorActionListener { editText, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                editText.text?.toString()?.let { query ->
                    Log.d("OneFragment", "Search query: $query")
                    viewModel.searchResults(query)
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
                Log.d("OneFragment", "Search button clicked with query: $query")
                viewModel.searchResults(query)
            }
        }
    }
    // 指定されたitemでリポジトリの詳細画面へ遷移するためのメソッドです。
    // Navigation ComponentのActionを使用して遷移を行います。
    private fun gotoRepositoryFragment(item: RepositoryItem) {
        val action = OneFragmentDirections.actionRepositoriesFragmentToRepositoryFragment(item)
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
 * RecyclerViewのアダプターを定義します。アイテムクリック時にlambda式を使用しています。
 */
class CustomAdapter(
    private val itemClickListener: (item: RepositoryItem) -> Unit
) : ListAdapter<RepositoryItem, CustomAdapter.ItemViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = LayoutItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.binding.repositoryNameView.text = currentItem.name // Assuming repositoryNameView is the ID of your TextView in layout_item.xml
        // Bind other data to views as needed
        holder.itemView.setOnClickListener {
            itemClickListener(currentItem)
        }
    }

    class ItemViewHolder(val binding: LayoutItemBinding) : RecyclerView.ViewHolder(binding.root)
}
