package jp.co.yumemi.android.code_check.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import io.ktor.client.engine.android.*
import jp.co.yumemi.android.code_check.repository.GithubRepository
import javax.inject.Singleton

/**
 * アプリケーション全体で使用する依存関係を提供するためのDagger Hiltモジュール。
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * シングルトンとしてHttpClientのインスタンスを提供。
     *
     * @return HttpClientのインスタンス
     */
    @Singleton
    @Provides
    fun provideHttpClient(): HttpClient {
        return HttpClient(Android)
    }

    /**
     * GithubRepositoryのインスタンスをシングルトンとして提供。
     *
     * @param httpClient HttpClientのインスタンス
     * @return GithubRepositoryのインスタンス
     */
    @Singleton
    @Provides
    fun provideRepositoryRepository(httpClient: HttpClient): GithubRepository {
        return GithubRepository(httpClient)
    }
}