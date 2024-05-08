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
 * SearchRepositoriesFragmentは、リポジトリの一覧を表示するFragmentです。
 * RecyclerViewを使用してリポジトリの一覧を表示し、検索機能を提供します。
 */
@AndroidEntryPoint
class SearchRepositoriesFragment : BaseFragment() {
    private lateinit var navigator: RepositoryNavigator
    // ViewBindingのインスタンスを保持するプライベート変数です。Viewが破棄された際にはnullに設定されます。
    private var _binding: SearchRepositoriesFragmentBinding? = null
    //    安全にBindingインスタンスにアクセスするためのプロパティです。_bindingがnullの場合、IllegalStateExceptionを投げます。
    //    これにより、Viewのライフサイクル外でのアクセスを防ぐことができます。
    private val binding get() = _binding ?: throw IllegalStateException("BindingはonCreateViewとonDestroyViewの間でのみアクセス可能です")

    // Move ViewModel declaration to a property of the fragment for broader scope
    private val viewModel: SearchRepositoriesViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = SearchRepositoriesFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun onItemClicked(item: RepositoryItem) {
        navigator.navigateToDetail(item)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigator = RepositoryNavigator(findNavController())
        setupRecyclerView()
        observeViewModel()
        setupSearchListeners()
    }

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

    private fun observeViewModel() {
        viewModel.repositoryState.observe(viewLifecycleOwner) { state ->
            updateUIState(state)
        }
    }

    private fun setupSearchListeners() {
        binding.searchInputText.setOnEditorActionListener { editText, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                editText.text?.toString()?.let { query ->
                    Log.d("SearchRepositoriesFragment", "Search query: $query")
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
                    showMessage("Search failed: ${e.localizedMessage}", isError = true)
                }
            } else {
                showMessage("Please enter a search query", isError = false)
            }
        }

    }
    private fun showSuccess(items: List<RepositoryItem>) {
        showLoading(false) // Turn off loading indicator
        if (items.isEmpty()) {
            showEmptyState(true)
        } else {
            binding.recyclerView.visibility = View.VISIBLE
            (binding.recyclerView.adapter as CustomAdapter).submitList(items)
            showEmptyState(false)
            binding.retryButton.visibility = View.GONE // Ensure the retry button is hidden on success
        }
    }


    // Consolidate the visibility toggles for UI elements
    override fun showLoading(show: Boolean) {
        super.showLoading(show)
        binding.recyclerView.visibility = if (show) View.GONE else View.VISIBLE
        binding.emptyStateTextView.visibility = if (show) View.GONE else View.VISIBLE
        binding.retryButton.visibility = if (show) View.GONE else View.VISIBLE
    }

    override fun showError(message: String) {
        super.showError(message)
        if (binding.recyclerView.visibility == View.GONE) {
            // Only show the retry button if the RecyclerView is hidden (no search results)
            binding.retryButton.visibility = View.VISIBLE
        }
    }

    override fun showEmptyState(show: Boolean) {
        super.showEmptyState(show)
        binding.recyclerView.visibility = if (show) View.GONE else View.VISIBLE
        // Hide the retry button when showing the empty state
        binding.retryButton.visibility = if (show) View.GONE else View.VISIBLE
    }



    private fun updateUIState(state: RepositoryState<List<RepositoryItem>>) {
        when (state) {
            is RepositoryState.Loading -> showLoading(true)
            is RepositoryState.Success -> showSuccess(state.data)
            is RepositoryState.Error -> showError(state.exception.localizedMessage ?: "An unknown error occurred")
            is RepositoryState.Empty -> showEmptyState(true)
        }
    }




    private fun showMessage(message: String, isError: Boolean) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
        binding.retryButton.visibility = if (isError) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clean up any observers to prevent memory leaks
        viewModel.repositoryState.removeObservers(viewLifecycleOwner)
        // Viewが破棄される際に、Bindingの参照をクリアします。これによりメモリリークを防ぎます。
        _binding = null
    }
}