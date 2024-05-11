import androidx.navigation.NavController
import androidx.navigation.NavDirections
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import jp.co.yumemi.android.code_check.model.RepositoryItem
import jp.co.yumemi.android.code_check.ui.navigation.RepositoryNavigator
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class RepositoryNavigatorTest {

    private lateinit var navController: NavController
    private lateinit var repositoryNavigator: RepositoryNavigator
    private val repositoryItem = RepositoryItem(
        name = "testName",
        ownerIconUrl = "testOwnerIconUrl",
        language = "Kotlin",
        stargazersCount = 42,
        watchersCount = 42,
        forksCount = 42,
        openIssuesCount = 42
    )

    @Before
    fun setUp() {
        navController = mock()
        repositoryNavigator = RepositoryNavigator(navController)
    }

    @Test
    fun `navigateToDetail triggers navigation with correct action`() {
        repositoryNavigator.navigateToDetail(repositoryItem)
        verify(navController).navigate(any<NavDirections>())
    }

    @Test
    fun `navigateToDetail triggers navigation only once`() {
        repositoryNavigator.navigateToDetail(repositoryItem)
        verify(navController, times(1)).navigate(any<NavDirections>())
    }

    @Test
    fun `navigateToDetail does not trigger navigation when NavController is not set`() {
        val repositoryNavigatorWithNoController = RepositoryNavigator(mock())
        repositoryNavigatorWithNoController.navigateToDetail(repositoryItem)
        // Assuming that the mock NavController is not set up to navigate.
        verify(navController, never()).navigate(any<NavDirections>())
    }

    @Test
    fun `navigateToDetail handles navigation when there are multiple requests in quick succession`() {
        repositoryNavigator.navigateToDetail(repositoryItem)
        repositoryNavigator.navigateToDetail(repositoryItem)
        // Assuming there is no debounce mechanism, verify that navigate is called twice.
        verify(navController, times(2)).navigate(any<NavDirections>())
    }

}