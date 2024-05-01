package jp.co.yumemi.android.code_check

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import coil.load
import jp.co.yumemi.android.code_check.TopActivity.Companion.lastSearchDate
import jp.co.yumemi.android.code_check.databinding.FragmentTwoBinding

/**
 * TwoFragment は、リポジトリの詳細を表示するFragmentです。
 * リポジトリの詳細情報を表示し、検索した日時をログに出力します。
 */
class TwoFragment : Fragment(R.layout.fragment_two) {

    private val args: TwoFragmentArgs by navArgs()
    private var binding: FragmentTwoBinding? = null

    /**
     * FragmentのViewが生成された時に呼ばれます。
     * レイアウトのバインディングやリポジトリの詳細情報の表示を行います。
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val item = args.item
        binding = FragmentTwoBinding.bind(view)

        // リポジトリの詳細情報をビューに表示します。
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
     * Fragmentが破棄される際に呼ばれます。
     * バインディングの解除を行います。
     */
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}