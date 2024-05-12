// ファイル: repository/GithubRepository.kt

package jp.co.yumemi.android.code_check.repository

import android.util.Log
import io.ktor.client.*
import io.ktor.client.request.*
import org.json.JSONException
import org.json.JSONObject
import jp.co.yumemi.android.code_check.model.RepositoryItem
import jp.co.yumemi.android.code_check.viewmodel.RepositoryState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

/**
 * GitHub APIを利用してリポジトリ情報を取得するリポジトリ層クラス。
 */
class GithubRepository @Inject constructor(private val client: HttpClient) {

    /**
     * GitHubのAPIを呼び出してリポジトリを検索し、結果を取得する。
     * @param query 検索クエリ文字列
     * @return 検索結果の状態を表す[RepositoryState]を返す。
     */
    suspend fun searchRepositories(query: String): RepositoryState<List<RepositoryItem>> = withContext(Dispatchers.IO) {
        try {
            // GitHub APIエンドポイントにリクエストを送信し、レスポンスを取得します。
            val response = client.get<String>("https://api.github.com/search/repositories?q=$query")
            Log.d("GithubRepository", "Response: $response")

            // レスポンスが空の"items"配列であるかどうかをチェックします。
            if (response.trim() == """{"items": []}""") {
                RepositoryState.Empty
            } else {
                // レスポンスからリポジトリアイテムのリストを解析します。
                val items = parseRepositoryItems(response)
                // 成功した場合、取得したリポジトリのリストを含む[RepositoryState.Success]を返します。
                RepositoryState.Success(items)
            }
        } catch (e: IOException) {
            // ネットワークエラーが発生した場合、エラーログを出力し、[RepositoryState.Error]を返します。
            Log.e("GithubRepository", "ネットワークエラーが発生しました", e)
            RepositoryState.Error(IOException("接続できません。インターネット接続を確認してもう一度お試し下さい。"))
        } catch (e: JSONException) {
            // JSON解析エラーが発生した場合、エラーログを出力し、[RepositoryState.JsonParsingError]を返します。
            Log.e("GithubRepository", "JSONの解析エラーが発生しました", e)
            RepositoryState.JsonParsingError
        } catch (e: Exception) {
            // その他の予期せぬエラーが発生した場合、エラーログを出力し、[RepositoryState.Error]を返します。
            Log.e("GithubRepository", "予期せぬエラーが発生しました", e)
            RepositoryState.Error(e)
        }
    }

    /**
     * 指定されたオーナー名を使用してリポジトリを検索し、結果を取得する。
     * @param owner オーナー名
     * @return 検索結果の状態を表す[RepositoryState]を返す。
     */
    suspend fun getRepositoriesByOwner(owner: String): RepositoryState<List<RepositoryItem>> = withContext(Dispatchers.IO) {
        // 指定されたオーナー名をクエリとしてsearchRepositoriesメソッドを使用します。
        return@withContext searchRepositories("user:$owner")
    }

    private fun parseRepositoryItems(response: String): List<RepositoryItem> {
        // JSONレスポンスからリポジトリアイテムのリストを生成します。
        val jsonBody = JSONObject(response)
        val jsonItems = jsonBody.getJSONArray("items")
        return (0 until jsonItems.length()).map { i ->
            jsonItems.getJSONObject(i).toRepositoryItem()
        }
    }

    private fun JSONObject.toRepositoryItem(): RepositoryItem {
        // JSONObjectからRepositoryItemオブジェクトに変換します。
        return RepositoryItem(
            name = getString("full_name"),
            ownerIconUrl = getJSONObject("owner").getString("avatar_url"),
            language = optString("language", "Unknown"),
            stargazersCount = getLong("stargazers_count"),
            watchersCount = getLong("watchers_count"),
            forksCount = getLong("forks_count"),
            openIssuesCount = getLong("open_issues_count")
        )
    }
}