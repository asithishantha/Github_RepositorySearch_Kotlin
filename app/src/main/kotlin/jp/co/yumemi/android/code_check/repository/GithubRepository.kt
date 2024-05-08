// File: repository/GithubRepository.kt

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
 * [HttpClient]を注入し、非同期でGitHubのリポジトリ検索を行う。
 *
 * @property client GitHub APIへのHTTPリクエストを行うクライアント
 */
class GithubRepository @Inject constructor(private val client: HttpClient) {

    /**
     * GitHubのAPIを呼び出し、リポジトリを検索して結果を取得する。
     * 検索クエリに基づいてGitHubリポジトリの情報を検索し、その結果を[RepositoryState]でラップして返す。
     * 成功時は[RepositoryState.Success]、失敗時は[RepositoryState.Error]を返す。
     *
     * @param query 検索クエリ文字列
     * @return 検索結果の状態を表す[RepositoryState]オブジェクト
     */
    suspend fun searchRepositories(query: String): RepositoryState<List<RepositoryItem>> = withContext(Dispatchers.IO) {
        try {
            // GitHub APIのエンドポイントにリクエストを送信し、レスポンスを文字列で取得する。
            val response = client.get<String>("https://api.github.com/search/repositories?q=$query")
            // レスポンスからJSONオブジェクトを作成する。
            val jsonBody = JSONObject(response)
            // JSONオブジェクトからリポジトリの配列を取得する。配列が存在しない場合はエラーを返す。
            val jsonItems = jsonBody.optJSONArray("items") ?: return@withContext RepositoryState.Error(Exception("No items found"))

            // JSON配列からリポジトリアイテムのリストを作成する。
            val items = List(jsonItems.length()) { i ->
                jsonItems.getJSONObject(i).let { jsonItem ->
                    // 各JSONオブジェクトからリポジトリアイテムを作成する。
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

            // リポジトリアイテムのリストが空の場合は空の状態を、それ以外の場合は成功の状態を返す。
            if (items.isEmpty()) {
                RepositoryState.Empty
            } else {
                RepositoryState.Success(items)
            }
        } catch (e: IOException) {
            // ネットワークエラーが発生した場合、ログを出力し、エラーの状態を返す。
            Log.e("GithubRepository", "ネットワークエラーが発生しました", e)
            RepositoryState.Error(IOException("接続できません。インターネット接続を確認してもう一度お試しください。"))
        } catch (e: JSONException) {
            // JSON解析エラーが発生した場合、ログを出力し、エラーの状態を返す。
            Log.e("GithubRepository", "JSONの解析エラーが発生しました", e)
            RepositoryState.Error(JSONException("データの解析中にエラーが発生しました。後でもう一度お試しください"))
        } catch (e: Exception) {
            // その他の例外が発生した場合、エラーの状態を返す。
            RepositoryState.Error(e)
        }
    }
}