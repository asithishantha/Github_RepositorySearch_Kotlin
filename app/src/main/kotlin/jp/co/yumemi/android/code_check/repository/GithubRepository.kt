// ファイル: repository/GithubRepository.kt

package jp.co.yumemi.android.code_check.repository

import android.util.Log
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.call.*
import jp.co.yumemi.android.code_check.model.RepositoryItem
import jp.co.yumemi.android.code_check.viewmodel.RepositoryState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
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
            // GitHubのAPIエンドポイントにリクエストを送信し、レスポンスを取得する。
            val response = client.get<String>("https://api.github.com/search/repositories?q=$query")
            Log.d("GithubRepository", "Response: $response")

            // レスポンスが空の"items"配列であるかどうかをチェックする。
            if (response.trim() == """{"items": []}""") {
                return@withContext RepositoryState.Empty
            }

            // JSONオブジェクトを解析してリポジトリのリストを生成する。
            val jsonBody = JSONObject(response)
            val jsonItems = jsonBody.optJSONArray("items") ?: throw JSONException("Items array could not be parsed")

            // 空でない"items"配列があるため、解析を続ける。
            val items = List(jsonItems.length()) { i ->
                jsonItems.getJSONObject(i).let { jsonItem ->
                    RepositoryItem(
                        name = jsonItem.getString("full_name"),
                        ownerIconUrl = jsonItem.getJSONObject("owner").getString("avatar_url"),
                        language = jsonItem.optString("language", "Unknown"),
                        stargazersCount = jsonItem.getLong("stargazers_count"),
                        watchersCount = jsonItem.getLong("watchers_count"),
                        forksCount = jsonItem.getLong("forks_count"),
                        openIssuesCount = jsonItem.getLong("open_issues_count")
                    )
                }
            }

            // 成功した場合、取得したリポジトリのリストを含む[RepositoryState.Success]を返す。
            RepositoryState.Success(items)
        } catch (e: IOException) {
            // ネットワークエラーが発生した場合、エラーログを出力し、[RepositoryState.Error]を返す。
            Log.e("GithubRepository", "ネットワークエラーが発生しました", e)
            RepositoryState.Error(IOException("接続できません。インターネット接続を確認してもう一度お試し下さい。"))
        } catch (e: JSONException) {
            // JSON解析エラーが発生した場合、エラーログを出力し、[RepositoryState.JsonParsingError]を返す。
            Log.e("GithubRepository", "JSONの解析エラーが発生しました", e)
            RepositoryState.JsonParsingError
        } catch (e: Exception) {
            // その他の予期せぬエラーが発生した場合、エラーログを出力し、[RepositoryState.Error]を返す。
            Log.e("GithubRepository", "予期せぬエラーが発生しました", e)
            RepositoryState.Error(e)  // その他の例外に対するキャッチオール
        }
    }

    suspend fun getRepositoriesByOwner(owner: String): RepositoryState<List<RepositoryItem>> = withContext(Dispatchers.IO) {
        // Use the searchRepositories method with the user's name as the query
        return@withContext searchRepositories("user:$owner")
    }

}