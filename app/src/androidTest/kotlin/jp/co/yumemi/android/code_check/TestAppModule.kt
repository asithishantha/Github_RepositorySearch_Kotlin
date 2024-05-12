// TestAppModule.kt
package jp.co.yumemi.android.code_check

import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import jp.co.yumemi.android.code_check.di.AppModule
import jp.co.yumemi.android.code_check.repository.GithubRepository
import org.mockito.Mockito

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppModule::class]
)
object TestAppModule {

    @Provides
    fun provideGithubRepository(): GithubRepository {
        // Return a mock GithubRepository here
        return Mockito.mock(GithubRepository::class.java)
    }
}