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

/**
 * GitHubリポジトリの検索結果を管理するViewModel。
 * リポジトリの状態をLiveDataで公開し、UI層での監視を可能にする。
 */
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
        lastSuccessfulQuery = query
        _repositoryState.value = RepositoryState.Loading  // ローディング状態を設定
        viewModelScope.launch {
            try {
                // リポジトリから検索結果を取得し、成功状態を投稿
                val result = repository.searchRepositories(query)
                _repositoryState.postValue(result)
            } catch (e: Exception) {
                // 例外が発生した場合はエラー状態を投稿
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