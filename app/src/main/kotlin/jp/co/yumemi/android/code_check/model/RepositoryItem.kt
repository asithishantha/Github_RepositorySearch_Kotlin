// File: model/RepositoryItem.kt

package jp.co.yumemi.android.code_check.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * GitHubリポジトリの情報を格納するためのデータクラス。
 * Parcelableインターフェースを実装しており、インテント間でのデータの受け渡しが可能。
 *
 * @property name リポジトリ名
 * @property ownerIconUrl リポジトリのオーナーのアイコンURL
 * @property language リポジトリで使用されている主要言語
 * @property stargazersCount リポジトリのスター数
 * @property watchersCount リポジトリをウォッチしているユーザー数
 * @property forksCount リポジトリのフォーク数
 * @property openIssuesCount リポジトリのオープンされているイシュー数
 */
@Parcelize
data class RepositoryItem(
    val name: String,
    val ownerIconUrl: String,
    val language: String,
    val stargazersCount: Long,
    val watchersCount: Long,
    val forksCount: Long,
    val openIssuesCount: Long,
) : Parcelable