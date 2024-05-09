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
    // Navigationコンポーネントから渡された引数を受け取るためのプロパティ
    private val args: RepositoryDetailFragmentArgs by navArgs()

    // View Bindingのためのプロパティ
    private var _binding: RepositoryDetailFragmentBinding? = null
    private val binding: RepositoryDetailFragmentBinding
        get() = _binding
            ?: throw IllegalStateException("FragmentのビューバインデングがonCreateViewの前、またはonDestroyViewの後にアクセスされました。")

    // FragmentのViewを生成する際に呼び出されるメソッド
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

        // 引数からリポジトリの情報を取得
        val item = args.item
        // 取得したリポジトリ情報をViewに設定
        with(binding) {
            ownerIconView.load(item.ownerIconUrl)
            nameView.text = item.name ?: "No name available"
            languageView.text = item.language ?: "Language unknown"
            starsView.text = "${item.stargazersCount} stars"
            watchersView.text = "${item.watchersCount} watchers"
            forksView.text = "${item.forksCount} forks"
            openIssuesView.text = "${item.openIssuesCount} open issues"
        }
    }

    /**
     * Viewが破棄される際に呼び出されるメソッドです。
     * View Bindingのリソースを解放します。
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // View Bindingを使用しているため、ここでバインディングをnullに設定してリソースを解放します
    }
}