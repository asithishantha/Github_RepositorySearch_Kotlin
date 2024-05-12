package jp.co.yumemi.android.code_check.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.yumemi.android.code_check.model.RepositoryItem
import jp.co.yumemi.android.code_check.repository.GithubRepository
import kotlinx.coroutines.launch
import javax.inject.Inject
import jp.co.yumemi.android.code_check.OpenForTesting
import org.json.JSONException
import java.io.IOException

/**
 * GitHubリポジトリの検索結果を管理するViewModel。
 * リポジトリの状態をLiveDataで公開し、UI層での監視を可能にする。
 */
@OpenForTesting
@HiltViewModel
class SearchRepositoriesViewModel @Inject constructor(
    private val repository: GithubRepository  // GitHubリポジトリにアクセスするためのリポジトリクラス
) : ViewModel() {

    // リポジトリの状態を保持するMutableLiveData
    private val _repositoryState = MutableLiveData<RepositoryState<List<RepositoryItem>>>()

    // リポジトリの状態を公開するためのLiveData
    val repositoryState: LiveData<RepositoryState<List<RepositoryItem>>> = _repositoryState

    // 最後に成功した検索クエリを保持する変数
    private var lastSuccessfulQuery: String? = null

    /**
     * 指定されたクエリでGitHubリポジトリを検索する。
     * 結果はリポジトリの状態としてLiveDataに投稿される。
     *
     * @param query 検索クエリ文字列。
     */
    fun searchRepositories(query: String) {
        // Check if the query is empty and return early if it is
        if (query.isEmpty()) {
            // Update the state to reflect that no search was performed due to empty query
            _repositoryState.value = RepositoryState.Empty
            return
        }
        // If the query is not empty, proceed with the search
        lastSuccessfulQuery = query
        _repositoryState.value = RepositoryState.Loading  // ローディング状態を設定
        viewModelScope.launch {
            // 検索が開始され、ローディング中であることをオブザーバーに通知します
            _repositoryState.postValue(RepositoryState.Loading)

            try {
                // 指定されたクエリでリポジトリを検索を試みます
                val result = repository.searchRepositories(query)
                // 成功した場合、結果をLiveDataに投稿します
                _repositoryState.postValue(result)
            } catch (e: IOException) {
                // IOExceptionが発生した場合、例外を含むエラー状態を投稿します
                // これは通常、ネットワークエラーや類似の接続問題を示します
                _repositoryState.postValue(RepositoryState.Error(e))
            } catch (e: JSONException) {
                // JSONExceptionが発生した場合、JsonParsingError状態を投稿します
                // これはJSONレスポンスの解析中にエラーが発生したことを示します
                _repositoryState.postValue(RepositoryState.JsonParsingError)
            } catch (e: Exception) {
                // その他の種類のExceptionが発生した場合、例外を含むエラー状態を投稿します
                // これはその他の予期せぬエラーに対するキャッチオールです
                _repositoryState.postValue(RepositoryState.Error(e))
            }
        }
    }

    /**
     * 最後に成功した検索を再試行する。
     * lastSuccessfulQueryがnullでない場合に検索を実行する。
     */
    fun retryLastFetch() {
        lastSuccessfulQuery?.let {
            searchRepositories(it)
        }
    }
}