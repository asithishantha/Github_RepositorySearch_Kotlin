package jp.co.yumemi.android.code_check.viewmodel

/**
 * リポジトリの状態を表すシールドクラス。
 * ローディング、成功、エラー、空状態を表現するために使用される。
 * ジェネリック型Tは成功状態のデータ型を表す。
 */
sealed class RepositoryState<out T> {
    /**
     * ローディング状態を表すオブジェクト。
     * データの読み込み中を示す。
     */
    object Loading : RepositoryState<Nothing>()

    /**
     * 成功状態を表すデータクラス。
     * データの読み込みが成功したことと、読み込まれたデータを保持する。
     *
     * @param data 読み込まれたデータ。
     */
    data class Success<out T>(val data: T) : RepositoryState<T>()

    /**
     * エラー状態を表すデータクラス。
     * エラーが発生したことと、その例外を保持する。
     *
     * @param exception 発生した例外。
     */
    data class Error(val exception: Throwable) : RepositoryState<Nothing>()

    /**
     * 空状態を表すオブジェクト。
     * データが見つからなかったことを示す。
     */
    object Empty : RepositoryState<Nothing>()  // データが見つからないことを表す
}