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
class RepositoryDetailFragment : Fragment(R.layout.repository_detail_fragment) {
    private val args: RepositoryDetailFragmentArgs by navArgs()
    private var _binding: RepositoryDetailFragmentBinding? = null
    private val binding: RepositoryDetailFragmentBinding
        get() = _binding
            ?: throw IllegalStateException("FragmentのビューバインデングがonCreateViewの前、またはonDestroyViewの後にアクセスされました。")


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = RepositoryDetailFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }
    /**
     * onViewCreatedは、Viewが生成された直後に呼び出されます。
     * レイアウトのバインディングやリポジトリの詳細情報の表示を行います。
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val item = args.item
        with(binding) {
            if (item.ownerIconUrl.isEmpty()) {
                ownerIconView.setImageResource(R.drawable.unknown)
            } else {
                ownerIconView.load(item.ownerIconUrl)
            }
            nameView.text = item.name.ifEmpty { getString(R.string.no_name_available) }
            languageView.text = item.language.ifEmpty { getString(R.string.language_unknown) }
            starsView.text = resources.getQuantityString(
                R.plurals.stars_count,
                item.stargazersCount.toInt(),
                item.stargazersCount.toInt()
            )
            watchersView.text = resources.getQuantityString(
                R.plurals.watchers_count,
                item.watchersCount.toInt(),
                item.watchersCount.toInt()
            )
            forksView.text = resources.getQuantityString(
                R.plurals.forks_count,
                item.forksCount.toInt(),
                item.forksCount.toInt()
            )
            openIssuesView.text = resources.getQuantityString(
                R.plurals.open_issues_count,
                item.openIssuesCount.toInt(),
                item.openIssuesCount.toInt()
            )
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        val repositoryUrl = "https://github.com/${args.item.name}"

        binding.openInWebButton.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(repositoryUrl))
            startActivity(browserIntent)
        }

        binding.copyUrlButton.setOnClickListener {
            val clipboard =
                requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Repository URL", repositoryUrl)
            clipboard.setPrimaryClip(clip)

            // Display Snackbar with confirmation message
            val snackbar = Snackbar.make(it, "URL copied to clipboard", Snackbar.LENGTH_SHORT)

            snackbar.setBackgroundTint(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.snackbar_background_color
                )
            ) // Replace with your desired color

            // Set the text color of the Snackbar
            snackbar.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            ) // Replace with your desired color

            // Show the Snackbar
            snackbar.show()
        }

        binding.shareButton.setOnClickListener {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, repositoryUrl)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, "Share URL via"))
        }

        val owner = args.item.name.substringBefore('/')
        lifecycleScope.launch {
            val userRepositories = getUserRepositories(owner)
            val userItemsAdapter = UserItemsAdapter(userRepositories) { selectedItem ->
                updateDetails(selectedItem)
            }

            binding.userItemsRecyclerView.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = userItemsAdapter
                val itemDecoration = SpaceItemDecoration(16)
                addItemDecoration(itemDecoration)
            }
        }

    }

    class SpaceItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect, view: View,
            parent: RecyclerView, state: RecyclerView.State
        ) {
            outRect.left = space
            outRect.right = space
            outRect.top = space
            outRect.bottom = space
        }
    }

    private fun updateDetails(item: RepositoryItem) {
        binding.apply {
            // Update the owner icon
            if (item.ownerIconUrl.isEmpty()) {
                ownerIconView.setImageResource(R.drawable.unknown)
            } else {
                ownerIconView.load(item.ownerIconUrl)
            }

            // Update the repository name
            nameView.text = item.name.ifEmpty { getString(R.string.no_name_available) }

            // Update the repository language
            languageView.text = item.language.ifEmpty { getString(R.string.language_unknown) }

            // Update the stars count
            starsView.text = resources.getQuantityString(
                R.plurals.stars_count,
                item.stargazersCount.toInt(), // Convert Long to Int
                item.stargazersCount.toInt()
            )

            // Update the watchers count
            watchersView.text = resources.getQuantityString(
                R.plurals.watchers_count,
                item.watchersCount.toInt(),
                item.watchersCount.toInt()
            )

            // Update the forks count
            forksView.text = resources.getQuantityString(
                R.plurals.forks_count,
                item.forksCount.toInt(),
                item.forksCount.toInt()
            )

            // Update the open issues count
            openIssuesView.text = resources.getQuantityString(
                R.plurals.open_issues_count,
                item.openIssuesCount.toInt(),
                item.openIssuesCount.toInt()
            )

            val repositoryUrl = "https://github.com/${item.name}"

            copyUrlButton.setOnClickListener {
                val clipboard =
                    requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Repository URL", repositoryUrl)
                clipboard.setPrimaryClip(clip)
                Snackbar.make(it, "URL copied to clipboard", Snackbar.LENGTH_SHORT).show()
            }

            shareButton.setOnClickListener {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, repositoryUrl)
                    type = "text/plain"
                }
                startActivity(Intent.createChooser(shareIntent, "Share URL via"))
            }

            openInWebButton.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(repositoryUrl))
                startActivity(browserIntent)
            }
        }
    }

    // Add a property for GithubRepository in RepositoryDetailFragment
    private val githubRepository: GithubRepository by lazy {
        // Initialize GithubRepository with an HttpClient instance
        // This is just an example, you should provide the HttpClient instance according to your DI setup
        GithubRepository(HttpClient())
    }

    // Modify getUserRepositories to use GithubRepository
    private suspend fun getUserRepositories(owner: String): List<RepositoryItem> {
        return when (val result = githubRepository.getRepositoriesByOwner(owner)) {
            is RepositoryState.Success -> result.data
            is RepositoryState.Error -> {
                // Handle error, show message to user
                emptyList()
            }

            is RepositoryState.Empty -> emptyList()
            else -> emptyList()
        }
    }

    /**
     * onDestroyViewは、Viewが破棄される際に呼び出されます。
     * バインディングの解除を行います。
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding =
            null  // Kotlinのsyntheticなどを使用している場合は、nullに設定します。View Bindingを使用している場合、通常は必要ありません。
    }
}