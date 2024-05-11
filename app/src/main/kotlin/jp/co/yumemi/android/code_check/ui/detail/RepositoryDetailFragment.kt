package jp.co.yumemi.android.code_check.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import coil.load
import jp.co.yumemi.android.code_check.R
import jp.co.yumemi.android.code_check.databinding.RepositoryDetailFragmentBinding

/**
 * RepositoryDetailFragmentは、リポジトリの詳細を表示するFragmentです。
 * リポジトリの詳細情報を表示し、検索した日時をログに出力します。
 */
class RepositoryDetailFragment : Fragment(R.layout.repository_detail_fragment) {
    // Navigationコンポーネントから渡された引数を受け取るためのプロパティです。
    private val args: RepositoryDetailFragmentArgs by navArgs()

    // View Bindingのためのプロパティです。
    private var _binding: RepositoryDetailFragmentBinding? = null
    private val binding: RepositoryDetailFragmentBinding
        get() = _binding ?: throw IllegalStateException(
            "FragmentのビューバインディングがonCreateViewの前、またはonDestroyViewの後にアクセスされました。"
        )

    // FragmentのViewを生成する際に呼び出されるメソッドです。
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = RepositoryDetailFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Viewが生成された後に呼び出されるメソッドです。
     * ここでレイアウトのバインディングを行い、リポジトリの詳細情報を画面に表示します。
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 引数からリポジトリの情報を取得します。
        val item = args.item
        // 取得したリポジトリ情報をViewに設定します。
        with(binding) {
            // オーナーのアイコンURLがnullまたは空の場合、デフォルトの画像を設定します。
            if (item.ownerIconUrl.isNullOrEmpty()) {
                ownerIconView.setImageResource(R.drawable.jetbrains)
            } else {
                ownerIconView.load(item.ownerIconUrl)
            }
            // リポジトリ名がnullまたは空の場合、デフォルトのテキストを表示します。
            nameView.text = item.name.ifNullOrEmpty { getString(R.string.no_name_available) }
            // 使用言語がnullまたは空の場合、デフォルトのテキストを表示します。
            languageView.text = item.language.ifNullOrEmpty { getString(R.string.language_unknown) }
            // スター数、ウォッチャー数、フォーク数、オープンイシュー数を表示します。
            // 数量に応じた複数形のテキストを取得し、フォーマット引数として使用します。
            starsView.text = resources.getQuantityString(
                R.plurals.stars_count, item.stargazersCount.toInt(), item.stargazersCount.toInt()
            )
            watchersView.text = resources.getQuantityString(
                R.plurals.watchers_count, item.watchersCount.toInt(), item.watchersCount.toInt()
            )
            forksView.text = resources.getQuantityString(
                R.plurals.forks_count, item.forksCount.toInt(), item.forksCount.toInt()
            )
            openIssuesView.text = resources.getQuantityString(
                R.plurals.open_issues_count, item.openIssuesCount.toInt(), item.openIssuesCount.toInt()
            )
        }
    }

    /**
     * Viewが破棄される際に呼び出されるメソッドです。
     * View Bindingのリソースを解放します。
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // View Bindingを使用しているため、ここでバインディングをnullに設定してリソースを解放します。
    }
}

/**
 * nullまたは空の文字列の場合にデフォルト値を返す拡張関数です。
 */
private fun String?.ifNullOrEmpty(defaultValue: () -> String): String {
    return if (this.isNullOrEmpty()) defaultValue() else this
}