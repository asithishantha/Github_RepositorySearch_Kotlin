package jp.co.yumemi.android.code_check.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import coil.load
import jp.co.yumemi.android.code_check.R
import jp.co.yumemi.android.code_check.databinding.RepositoryDetailFragmentBinding

/**
 * リポジトリの詳細を表示するFragment。
 * リポジトリの詳細情報を表示し、検索した日時をログに出力する。
 */
class RepositoryDetailFragment : Fragment(R.layout.repository_detail_fragment) {

    private val args: RepositoryDetailFragmentArgs by navArgs()
    private var binding: RepositoryDetailFragmentBinding? = null

    /**
     * FragmentのViewが生成された時に呼ばれる。
     * レイアウトのバインディングとリポジトリの詳細情報の表示を行う。
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val item = args.item
        binding = RepositoryDetailFragmentBinding.bind(view)

        // リポジトリの詳細情報をビューに表示する
        binding?.apply {
            ownerIconView.load(item.ownerIconUrl)
            nameView.text = item.name
            languageView.text = item.language
            starsView.text = "${item.stargazersCount} stars"
            watchersView.text = "${item.watchersCount} watchers"
            forksView.text = "${item.forksCount} forks"
            openIssuesView.text = "${item.openIssuesCount} open issues"
        }
    }

    /**
     * Fragmentが破棄される際に呼ばれる。
     * バインディングの解除を行う。
     */
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}