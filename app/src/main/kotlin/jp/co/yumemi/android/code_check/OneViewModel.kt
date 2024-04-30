/*
 * 著作権 © 2021 YUMEMI Inc. すべての権利が保護されています。
 */
package jp.co.yumemi.android.code_check

import android.content.Context
import android.os.Parcelable
import androidx.lifecycle.ViewModel
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import jp.co.yumemi.android.code_check.TopActivity.Companion.lastSearchDate
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.parcelize.Parcelize
import org.json.JSONObject
import java.util.*

/**
 * TwoFragment で使う
 * OneViewModelは、OneFragmentで使用されるViewModelです。
 * 検索結果を取得するメソッドを提供します。
 */
class OneViewModel(
    val context: Context
) : ViewModel() {

    /**
     * 検索結果を取得します。
     * @param inputText 検索クエリ
     * @return 検索結果のリスト
     */
    fun searchResults(inputText: String): List<item> = runBlocking {
        // HTTPクライアントを初期化します。
        val client = HttpClient(Android)

        return@runBlocking GlobalScope.async {
            // GitHub APIからリポジトリを検索します。
            val response: HttpResponse = client?.get("https://api.github.com/search/repositories") {
                header("Accept", "application/vnd.github.v3+json")
                parameter("q", inputText)
            }

            // レスポンスからJSONデータを取得します。
            val jsonBody = JSONObject(response.receive<String>())
            val jsonItems = jsonBody.optJSONArray("items")!!

            val items = mutableListOf<item>()

            /**
             * アイテムの個数分ループして検索結果をパースします。
             */
            for (i in 0 until jsonItems.length()) {
                val jsonItem = jsonItems.optJSONObject(i)!!
                val name = jsonItem.optString("full_name")
                val ownerIconUrl = jsonItem.optJSONObject("owner")!!.optString("avatar_url")
                val language = jsonItem.optString("language")
                val stargazersCount = jsonItem.optLong("stargazers_count")
                val watchersCount = jsonItem.optLong("watchers_count")
                val forksCount = jsonItem.optLong("forks_count")
                val openIssuesCount = jsonItem.optLong("open_issues_count")

                // itemオブジェクトを作成してリストに追加します。
                items.add(
                    item(
                        name = name,
                        ownerIconUrl = ownerIconUrl,
                        language = context.getString(R.string.written_language, language),
                        stargazersCount = stargazersCount,
                        watchersCount = watchersCount,
                        forksCount = forksCount,
                        openIssuesCount = openIssuesCount
                    )
                )
            }

            // 検索日時を更新します。
            lastSearchDate = Date()

            return@async items.toList()
        }.await()
    }
}

/**
 * リポジトリの情報を表すデータクラスです。
 * Parcelableを実装しています。
 */
@Parcelize
data class item(
    val name: String,
    val ownerIconUrl: String,
    val language: String,
    val stargazersCount: Long,
    val watchersCount: Long,
    val forksCount: Long,
    val openIssuesCount: Long,
) : Parcelable
