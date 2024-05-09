// RepositoryNavigator.kt
package jp.co.yumemi.android.code_check.ui.navigation

import androidx.navigation.NavController
import jp.co.yumemi.android.code_check.model.RepositoryItem
import jp.co.yumemi.android.code_check.ui.search.SearchRepositoriesFragmentDirections

/**
 * リポジトリ関連の画面遷移を担当するクラス。
 * NavControllerを使用して、アプリ内のナビゲーションアクションを実行する。
 */
class RepositoryNavigator(private val navController: NavController) {
    /**
     * 指定されたリポジトリアイテムの詳細画面へナビゲートする。
     * SearchRepositoriesFragmentからRepositoryDetailFragmentへの遷移を行う。
     *
     * @param item ナビゲーションに渡すリポジトリアイテムのデータ。
     */
    fun navigateToDetail(item: RepositoryItem) {
        // SearchRepositoriesFragmentからRepositoryDetailFragmentへのナビゲーションアクションを定義
        val action = SearchRepositoriesFragmentDirections.actionRepositoriesFragmentToRepositoryFragment(item)
        // NavControllerを使用してアクションを実行し、詳細画面へ遷移する
        navController.navigate(action)
    }
}