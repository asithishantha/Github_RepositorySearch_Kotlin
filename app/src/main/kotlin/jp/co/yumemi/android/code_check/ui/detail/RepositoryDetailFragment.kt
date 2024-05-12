package jp.co.yumemi.android.code_check.ui.detail

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.snackbar.Snackbar
import io.ktor.client.HttpClient
import jp.co.yumemi.android.code_check.R
import jp.co.yumemi.android.code_check.databinding.RepositoryDetailFragmentBinding
import jp.co.yumemi.android.code_check.model.RepositoryItem
import jp.co.yumemi.android.code_check.repository.GithubRepository
import jp.co.yumemi.android.code_check.viewmodel.RepositoryState
import kotlinx.coroutines.launch

/**
 * RepositoryDetailFragmentは、リポジトリの詳細を表示するFragmentです。
 * リポジトリの詳細情報を表示し、検索した日時をログに出力します。
 */
class RepositoryDetailFragment : Fragment() {
    private val args: RepositoryDetailFragmentArgs by navArgs()
    private var _binding: RepositoryDetailFragmentBinding? = null
    private val binding get() = _binding!!

    // GithubRepositoryのインスタンスを遅延初期化する
    private val githubRepository: GithubRepository by lazy {
        GithubRepository(HttpClient())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = RepositoryDetailFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Viewが生成された直後に呼び出される。
     * UIの初期設定やイベントリスナーの設定を行う。
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val item = args.item
        updateUIWithRepositoryItem(item)

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // オーナー名を取得するための修正が必要です。例えば、以下のようにします。
        val owner = item.name.substringBefore('/')

        lifecycleScope.launch {
            // リポジトリの状態を取得し、適切に処理します。
            when (val result = githubRepository.getRepositoriesByOwner(owner)) {
                is RepositoryState.Success -> {
                    setupUserItemsRecyclerView(result.data)
                }
                is RepositoryState.Error -> {
                    showSnackbar("エラー: ${result.exception.localizedMessage}")
                }
                is RepositoryState.Empty -> {
                    showSnackbar("リポジトリが見つかりませんでした。")
                }
                is RepositoryState.JsonParsingError -> {
                    showSnackbar("JSON解析エラーが発生しました。")
                }
                // その他の状態に対する処理が必要な場合はここに追加します。
            }
        }
    }

    /**
     * リポジトリの詳細情報をUIに反映する。
     */
    private fun updateUIWithRepositoryItem(item: RepositoryItem) {
        with(binding) {
            ownerIconView.load(item.ownerIconUrl) {
                placeholder(R.drawable.unknown)
                error(R.drawable.unknown)
            }
            nameView.text = item.name.ifEmpty { getString(R.string.no_name_available) }
            languageView.text = item.language.ifEmpty { getString(R.string.language_unknown) }
            starsView.text = getString(R.string.stars_count, item.stargazersCount)
            watchersView.text = getString(R.string.watchers_count, item.watchersCount)
            forksView.text = getString(R.string.forks_count, item.forksCount)
            openIssuesView.text = getString(R.string.open_issues_count, item.openIssuesCount)
            setupActionButtons(item)
        }
    }

    /**
     * アクションボタンの設定を行う。
     */
    private fun setupActionButtons(item: RepositoryItem) {
        val repositoryUrl = "https://github.com/${item.name}"
        binding.apply {
            openInWebButton.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(repositoryUrl)))
            }
            copyUrlButton.setOnClickListener {
                val clipboard = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Repository URL", repositoryUrl)
                clipboard.setPrimaryClip(clip)
                showSnackbar("URLをクリップボードにコピーしました")
            }
            shareButton.setOnClickListener {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, repositoryUrl)
                    type = "text/plain"
                }
                startActivity(Intent.createChooser(shareIntent, "URLを共有"))
            }
        }
    }

    /**
     * ユーザーのリポジトリ一覧をRecyclerViewに設定する。
     */
    private fun setupUserItemsRecyclerView(userRepositories: List<RepositoryItem>) {
        val userItemsAdapter = UserItemsAdapter(userRepositories) { selectedItem ->
            updateUIWithRepositoryItem(selectedItem)
        }
        binding.userItemsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = userItemsAdapter
            addItemDecoration(SpaceItemDecoration(16))
        }
    }

    /**
     * Snackbarを表示してユーザーにメッセージを伝える。
     */
    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).apply {
            setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.snackbar_background_color))
            setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            show()
        }
    }

    /**
     * Viewが破棄される際に呼び出される。
     * バインディングの解除を行う。
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * RecyclerViewのアイテム間にスペースを追加するためのItemDecoration。
     */
    class SpaceItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
        ) {
            with(outRect) {
                left = space
                right = space
                top = space
                bottom = space
            }
        }
    }
}