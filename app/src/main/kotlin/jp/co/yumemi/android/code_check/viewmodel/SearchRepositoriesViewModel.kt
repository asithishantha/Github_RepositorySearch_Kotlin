// File: viewmodel/SearchRepositoriesViewModel.kt

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
 * GitHubリポジトリの検索を行い、その結果を管理するViewModel。
 */
@HiltViewModel
class SearchRepositoriesViewModel @Inject constructor(
    private val repository: GithubRepository // Hiltによる依存注入でGitHubリポジトリを注入
) : ViewModel() {

    // リポジトリの状態を保持するLiveData
    private val _repositoryState = MutableLiveData<RepositoryState<List<RepositoryItem>>>()
    val repositoryState: LiveData<RepositoryState<List<RepositoryItem>>> = _repositoryState

    /**
     * 指定されたクエリでGitHubリポジトリを検索し、結果をLiveDataに投稿する。
     * @param query 検索クエリ文字列。
     */
    fun searchRepositories(query: String) {
        _repositoryState.value = RepositoryState.Loading // 検索開始時にローディング状態を設定
        viewModelScope.launch {
            val result = repository.searchRepositories(query) // リポジトリ検索の実行
            _repositoryState.postValue(result) // 結果をLiveDataに投稿
        }
    }
}