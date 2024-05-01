/*
 * 著作権 © 2021 YUMEMI Inc. すべての権利が保護されています。
 */
package jp.co.yumemi.android.code_check

import android.content.Context
import android.os.Parcelable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import jp.co.yumemi.android.code_check.TopActivity.Companion.lastSearchDate
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import org.json.JSONObject
import java.util.*

/**
 * TwoFragment で使う
 * OneFragmentで使用されるViewModelです。
 * GitHubのAPIを通じてリポジトリの検索結果を取得する機能を提供します。
 */
class OneViewModel: ViewModel() {

    // Add LiveData to publish search results
    val searchResultsLiveData = MutableLiveData<List<RepositoryItem>>()

    private val client = HttpClient(Android) // HTTPクライアントの初期化。APIリクエストに使用します。

    /**
     * 指定された検索クエリに基づいてGitHubリポジトリを検索し、結果を取得します。
     * @param inputText 検索クエリ文字列
     */
    fun searchResults(inputText: String) {
        viewModelScope.launch {// ViewModelのライフサイクルに紐づいたCoroutineスコープで非同期処理を開始
            // GitHub APIからリポジトリを検索します。
            val response: HttpResponse = client.get("https://api.github.com/search/repositories") {
                header("Accept", "application/vnd.github.v3+json") // GitHub API v3を指定
                parameter("q", inputText) // 検索クエリパラメータを追加
            }

            val jsonBody = JSONObject(response.receive<String>()) // レスポンスからJSONオブジェクトを生成
            val jsonItems =
                jsonBody.optJSONArray("items") ?: return@launch // "items"キーに対応するJSON配列を取得、存在しなければ終了

            val items = mutableListOf<RepositoryItem>()  // 検索結果を格納するためのリスト
            for (i in 0 until jsonItems.length()) {
                val jsonItem =
                    jsonItems.optJSONObject(i) ?: continue // 配列内の各要素をJSONObjectとして取得、nullの場合はスキップ
                val name = jsonItem.optString("full_name")
                val ownerIconUrl = jsonItem.optJSONObject("owner")?.optString("avatar_url") ?: ""
                val language = jsonItem.optString("language")
                val stargazersCount = jsonItem.optLong("stargazers_count")
                val watchersCount = jsonItem.optLong("watchers_count")
                val forksCount = jsonItem.optLong("forks_count")
                val openIssuesCount = jsonItem.optLong("open_issues_count")
                // 取得したデータをもとにitemオブジェクトを生成し、リストに追加
                items.add(
                    RepositoryItem(
                        name,
                        ownerIconUrl,
                        language,
                        stargazersCount,
                        watchersCount,
                        forksCount,
                        openIssuesCount
                    )
                )
            }
            // Update LiveData with the results
            searchResultsLiveData.postValue(items)
            // 最後の検索日時を更新
            lastSearchDate = Date()

        }
    }
}

/**
 * リポジトリの情報を表すデータクラスです。
 * Parcelableを実装しています。
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