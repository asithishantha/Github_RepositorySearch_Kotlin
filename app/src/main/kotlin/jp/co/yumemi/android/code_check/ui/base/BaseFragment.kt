// BaseFragment.kt
package jp.co.yumemi.android.code_check.ui.base

import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import jp.co.yumemi.android.code_check.R

/**
 * 全てのFragmentに共通のUI操作やエラー表示機能を提供する基底クラス。
 * このクラスを継承することで、ローディング表示やエラーメッセージの表示などの共通機能を利用できる。
 */
abstract class BaseFragment : Fragment() {
    /**
     * ローディングインジケータの表示状態を切り替える。
     * ローディング中はインジケータを表示し、それ以外の場合は非表示にする。
     *
     * @param show ローディングインジケータを表示する場合はtrue、非表示にする場合はfalse。
     */
    open fun showLoading(show: Boolean) {
        view?.findViewById<ProgressBar>(R.id.progressBar)?.visibility = if (show) View.VISIBLE else View.GONE
    }

    /**
     * エラーメッセージをスナックバーで表示する。
     * このメソッドはエラーが発生した際にユーザーに通知するために使用される。
     *
     * @param message 表示するエラーメッセージの内容。
     */
    open fun showError(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
    }

    /**
     * 空の状態を示すテキストビューの表示状態を切り替える。
     * データが存在しない場合は空の状態を示すテキストを表示し、データがある場合は非表示にする。
     *
     * @param show 空の状態を表示する場合はtrue、非表示にする場合はfalse。
     */
    open fun showEmptyState(show: Boolean) {
        view?.findViewById<TextView>(R.id.emptyStateTextView)?.visibility = if (show) View.VISIBLE else View.GONE
    }
}