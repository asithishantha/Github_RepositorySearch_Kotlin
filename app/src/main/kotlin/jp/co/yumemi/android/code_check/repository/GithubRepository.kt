// File: repository/GithubRepository.kt

package jp.co.yumemi.android.code_check.repository

import io.ktor.client.*
import io.ktor.client.request.*
import jp.co.yumemi.android.code_check.model.RepositoryItem
import jp.co.yumemi.android.code_check.viewmodel.RepositoryState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject

/**
 * GitHub APIを通じてリポジトリ情報を取得するクラス。
 * Hiltによる依存注入でHttpClientを注入。
 *
 * @property client HTTPクライアント
 */
class GithubRepository @Inject constructor(private val client: HttpClient) {

    /**
     * 指定されたクエリに基づいてGitHubリポジトリを検索し、結果を返す。
     *
     * @param query 検索クエリ文字列
     * @return RepositoryState<List<RepositoryItem>> 検索結果の状態を表すRepositoryState
     */
    suspend fun searchRepositories(query: String): RepositoryState<List<RepositoryItem>> = withContext(Dispatchers.IO) {
        try {
            // GitHub APIからリポジトリを検索するリクエストを送信
            val response = client.get<String>("https://api.github.com/search/repositories?q=$query")
            // レスポンスからJSONオブジェクトを生成
            val jsonBody = JSONObject(response)
            // "items"キーに対応するJSON配列を取得
            val jsonItems = jsonBody.optJSONArray("items") ?: return@withContext RepositoryState.Error(Exception("No items found"))

            // JSON配列からリポジトリアイテムのリストを生成
            val items = List(jsonItems.length()) { i ->
                jsonItems.getJSONObject(i).let { jsonItem ->
                    RepositoryItem(
                        name = jsonItem.optString("full_name"),
                        ownerIconUrl = jsonItem.optJSONObject("owner").optString("avatar_url"),
                        language = jsonItem.optString("language"),
                        stargazersCount = jsonItem.optLong("stargazers_count"),
                        watchersCount = jsonItem.optLong("watchers_count"),
                        forksCount = jsonItem.optLong("forks_count"),
                        openIssuesCount = jsonItem.optLong("open_issues_count")
                    )
                }
            }

            // 成功時に取得したリポジトリアイテムのリストを含むRepositoryState.Successを返す
            RepositoryState.Success(items)
        } catch (e: Exception) {
            // 例外発生時にエラーを含むRepositoryState.Errorを返す
            RepositoryState.Error(e)
        }
    }
}