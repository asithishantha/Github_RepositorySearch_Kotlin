package jp.co.yumemi.android.code_check.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import jp.co.yumemi.android.code_check.R
import jp.co.yumemi.android.code_check.viewmodel.SearchRepositoriesViewModel
import jp.co.yumemi.android.code_check.model.RepositoryItem
import jp.co.yumemi.android.code_check.databinding.SearchRepositoriesFragmentBinding
import jp.co.yumemi.android.code_check.ui.adapters.CustomAdapter
import jp.co.yumemi.android.code_check.ui.base.BaseFragment
import jp.co.yumemi.android.code_check.ui.navigation.RepositoryNavigator
import jp.co.yumemi.android.code_check.viewmodel.RepositoryState

/**
 * SearchRepositoriesFragmentは、GitHubリポジトリの検索結果を表示するFragmentです。
 * ユーザーが検索クエリを入力し、結果をRecyclerViewで表示します。
 */
@AndroidEntryPoint
class SearchRepositoriesFragment : BaseFragment() {
    // リポジトリの詳細画面へのナビゲーションを担当するクラスのインスタンス
    private lateinit var navigator: RepositoryNavigator

    // View Bindingのインスタンスを保持するプライベート変数
    private var _binding: SearchRepositoriesFragmentBinding? = null

    // View Bindingインスタンスに安全にアクセスするためのプロパティ
    private val binding
        get() = _binding
            ?: throw IllegalStateException("BindingはonCreateViewとonDestroyViewの間でのみアクセス可能です")

    // ViewModelのインスタンスを保持するプロパティ
    private val viewModel: SearchRepositoriesViewModel by viewModels()

    // FragmentのViewを生成する際に呼び出されるメソッド
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = SearchRepositoriesFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    // リポジトリアイテムがクリックされた時の処理を定義するメソッド
    private fun onItemClicked(item: RepositoryItem) {
        navigator.navigateToDetail(item)
    }


    // Viewが生成された後に呼び出されるメソッド
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigator = RepositoryNavigator(findNavController())
        setupRecyclerView()
        observeViewModel()
        setupSearchListeners()
    }

    // RecyclerViewのセットアップを行うメソッド
    private fun setupRecyclerView() {
        val adapter = CustomAdapter().apply {
            itemClickListener = { item ->
                onItemClicked(item)
            }
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = adapter
        }
    }

    // ViewModelの状態を監視し、UIを更新するメソッド
    private fun observeViewModel() {
        viewModel.repositoryState.observe(viewLifecycleOwner) { state ->
            updateUIState(state)
        }
    }

    // 検索成功時のUI表示を更新するメソッド
    private fun setupSearchListeners() {
        binding.searchInputText.setOnEditorActionListener { editText, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                editText.text?.toString()?.let { query ->
                    Log.d("SearchRepositoriesFragment", "検索クエリ: $query") // 検索クエリをデバッグログに出力
                    viewModel.searchRepositories(query)
                }
                true
            } else false
        }

        binding.searchButton.setOnClickListener {
            val query = binding.searchInputText.text.toString()
            if (query.isNotEmpty()) {
                try {
                    viewModel.searchRepositories(query)
                } catch (e: Exception) {
                    showMessage("検索に失敗しました: ${e.localizedMessage}", isError = true)
                }
            } else {
                showMessage("検索クエリを入力してください", isError = false)
            }
        }

    }

    // 検索成功時のUI表示を更新するメソッド
    private fun showSuccess(items: List<RepositoryItem>) {
        showLoading(false) // Turn off loading indicator
        if (items.isEmpty()) {
            showEmptyState(true)
        } else {
            binding.recyclerView.visibility = View.VISIBLE
            (binding.recyclerView.adapter as CustomAdapter).submitList(items)
            showEmptyState(false)
        }
    }


    // UI要素の表示を切り替えるメソッド
    override fun showLoading(show: Boolean) {
        super.showLoading(show)
        binding.recyclerView.visibility = if (show) View.GONE else View.VISIBLE
    }

    // エラー表示を行うメソッド
    override fun showError(message: String) {
        super.showError(message)
        binding.recyclerView.visibility == View.GONE
    }

    // 空の状態を表示するメソッド
    override fun showEmptyState(show: Boolean) {
        super.showEmptyState(show)
        binding.recyclerView.visibility = if (show) View.GONE else View.VISIBLE
    }


    // ViewModelの状態に応じてUIを更新するメソッド
    private fun updateUIState(state: RepositoryState<List<RepositoryItem>>) {
        when (state) {
            is RepositoryState.Loading -> showLoading(true)
            is RepositoryState.Success -> showSuccess(state.data)
            is RepositoryState.Error -> showError(
                state.exception.localizedMessage ?: "An unknown error occurred"
            )

            is RepositoryState.Empty -> showEmptyState(true)
            else -> Log.e("SearchRepositoriesFragment", "Unhandled state: $state")
        }
    }

    // メッセージを表示するメソッド
    private fun showMessage(message: String, isError: Boolean) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    // Viewが破棄される際に呼び出されるメソッド
    override fun onDestroyView() {
        super.onDestroyView()
        // Clean up any observers to prevent memory leaks
        viewModel.repositoryState.removeObservers(viewLifecycleOwner)
        // Viewが破棄される際に、Bindingの参照をクリアします。これによりメモリリークを防ぎます。
        _binding = null
    }
}