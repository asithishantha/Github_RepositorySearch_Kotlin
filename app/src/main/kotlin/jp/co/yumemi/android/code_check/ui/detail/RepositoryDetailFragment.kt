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

    private val args: RepositoryDetailFragmentArgs by navArgs()
    private var _binding: RepositoryDetailFragmentBinding? = null
    private val binding: RepositoryDetailFragmentBinding
        get() = _binding ?: throw IllegalStateException("FragmentのビューバインデングがonCreateViewの前、またはonDestroyViewの後にアクセスされました。")


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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
     * onDestroyViewは、Viewが破棄される際に呼び出されます。
     * バインディングの解除を行います。
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // Kotlinのsyntheticなどを使用している場合は、nullに設定します。View Bindingを使用している場合、通常は必要ありません。
    }
}