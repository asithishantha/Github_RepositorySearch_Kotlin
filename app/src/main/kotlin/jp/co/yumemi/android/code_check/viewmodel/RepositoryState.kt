package jp.co.yumemi.android.code_check.viewmodel

/**
 * リポジトリの状態を表すシールドクラス。
 * 成功、ローディング、エラーの状態を表現する。
 */
sealed class RepositoryState<out T> {

    /**
     * データのローディング中を表す状態。
     */
    object Loading : RepositoryState<Nothing>()

    /**
     * データの取得に成功した状態。
     * @param data 取得したデータ。
     */
    data class Success<out T>(val data: T) : RepositoryState<T>()

    /**
     * データの取得に失敗した状態。
     * @param exception 発生した例外。
     */
    data class Error(val exception: Throwable) : RepositoryState<Nothing>()
}