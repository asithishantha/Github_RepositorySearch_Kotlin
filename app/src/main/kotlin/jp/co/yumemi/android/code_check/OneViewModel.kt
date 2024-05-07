/*
 * 著作権 © 2021 YUMEMI Inc. すべての権利が保護されています。
 */
package jp.co.yumemi.android.code_check

import android.os.Parcelable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
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
 * OneViewModelは、GitHubのAPIを通じてリポジトリの検索結果を取得し、
 * それをLiveDataを通じて公開するViewModelです。
 */
class OneViewModel : ViewModel() {

    // 検索結果を公開するためのLiveDataです。
    val searchResultsLiveData = MutableLiveData<List<RepositoryItem>>()
    // エラーメッセージを公開するためのLiveDataです。
    val errorLiveData = MutableLiveData<String>()

    // HTTPクライアントの初期化。APIリクエストに使用します。
    private val client = HttpClient(Android)

    /**
     * GitHubリポジトリを検索し、結果を取得するメソッドです。
     * @param inputText 検索クエリ文字列
     */
    fun searchResults(inputText: String) {
        // ViewModelのライフサイクルに紐づいたCoroutineスコープで非同期処理を開始します。
        viewModelScope.launch {
            try {
                // GitHub APIからリポジトリを検索します。
                val response: HttpResponse =
                    client.get("https://api.github.com/search/repositories") {
                        header("Accept", "application/vnd.github.v3+json") // GitHub API v3を指定します。
                        parameter("q", inputText) // 検索クエリパラメータを追加します。
                    }

                // レスポンスからJSONオブジェクトを生成します。
                val jsonBody = JSONObject(response.receive<String>())
                // "items"キーに対応するJSON配列を取得します。存在しなければ終了します。
                val jsonItems =
                    jsonBody.optJSONArray("items")
                        ?: return@launch

                // 検索結果を格納するためのリストを作成します。
                val items = mutableListOf<RepositoryItem>()
                for (i in 0 until jsonItems.length()) {
                    // 配列内の各要素をJSONObjectとして取得します。nullの場合はスキップします。
                    val jsonItem =
                        jsonItems.optJSONObject(i)
                            ?: continue
                    // JSONObjectから必要な情報を取り出します。
                    val name = jsonItem.optString("full_name")
                    val ownerIconUrl =
                        jsonItem.optJSONObject("owner")?.optString("avatar_url") ?: ""
                    val language = jsonItem.optString("language")
                    val stargazersCount = jsonItem.getLong("stargazers_count")
                    val watchersCount = jsonItem.getLong("watchers_count")
                    val forksCount = jsonItem.getLong("forks_count")
                    val openIssuesCount = jsonItem.getLong("open_issues_count")
                    // 取得したデータをもとにRepositoryItemオブジェクトを生成し、リストに追加します。
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
                // LiveDataを更新し、検索結果を公開します。
                searchResultsLiveData.postValue(items)
                // 最後の検索日時を更新します。
                lastSearchDate = Date()

            } catch (e: Exception) {
                // 例外が発生した場合、ログにエラーメッセージを出力します。
                Log.e("API_REQUEST_ERROR", "APIリクエスト中にエラーが発生しました：${e.message}", e)
                // エラーメッセージをLiveDataに投稿し、UIに表示させます。
                errorLiveData.postValue("エラーが発生しました。詳細はログを参照してください。")
            }
        }
    }
}

/**
 * GitHubリポジトリの情報を格納するためのデータクラスです。
 * Parcelableインターフェースを実装しており、インテント間でのデータの受け渡しが可能です。
 */
@Parcelize
data class RepositoryItem(
    val name: String, // リポジトリ名
    val ownerIconUrl: String, // オーナーのアイコンURL
    val language: String, // 使用言語
    val stargazersCount: Long, // スターの数
    val watchersCount: Long, // ウォッチャーの数
    val forksCount: Long, // フォークの数
    val openIssuesCount: Long, // オープンされているイシューの数
) : Parcelable