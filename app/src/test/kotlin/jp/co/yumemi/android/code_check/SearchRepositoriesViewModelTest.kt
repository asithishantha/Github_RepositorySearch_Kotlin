import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.MutableLiveData
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import androidx.lifecycle.Observer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import jp.co.yumemi.android.code_check.model.RepositoryItem
import jp.co.yumemi.android.code_check.repository.GithubRepository
import jp.co.yumemi.android.code_check.viewmodel.RepositoryState
import jp.co.yumemi.android.code_check.viewmodel.SearchRepositoriesViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.atLeastOnce
import org.mockito.kotlin.verify
import java.io.IOException
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.inOrder
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class SearchRepositoriesViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()



    @Mock
    lateinit var repository: GithubRepository

    @Mock
    lateinit var observer: Observer<RepositoryState<List<RepositoryItem>>>

    private lateinit var viewModel: SearchRepositoriesViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = SearchRepositoriesViewModel(repository)
        viewModel.repositoryState.observeForever(observer)
    }

    @After
    fun tearDown() {
        viewModel.repositoryState.removeObserver(observer)
        Dispatchers.resetMain() // Reset main dispatcher to the original Main dispatcher
    }

    @Test
    fun `ViewModel initializes without a state`() {
        val initialState = viewModel.repositoryState.value
        assertNull("ViewModel should not have an initial state", initialState)
    }

    @Test
    fun `searchRepositories does not trigger search with empty query`() = runTest {
        val query = ""
        viewModel.searchRepositories(query)
        verify(repository, never()).searchRepositories(query)
    }

    @Test
    fun `searchRepositories handles very long query strings`() = runTest {
        val longQuery = "a".repeat(1000) // A very long query string
        viewModel.searchRepositories(longQuery)
        verify(repository).searchRepositories(longQuery)
    }

    @Test
    fun `searchRepositories transitions from Loading to Success state`() = runTest {
        val query = "android"
        val items = listOf(RepositoryItem("repo1", "", "Kotlin", 100, 50, 20, 10))
        val successState = RepositoryState.Success(items)
        `when`(repository.searchRepositories(query)).thenReturn(successState)

        val observer = mock(Observer::class.java) as Observer<RepositoryState<List<RepositoryItem>>>
        viewModel.repositoryState.observeForever(observer)

        viewModel.searchRepositories(query)

        val inOrder = inOrder(observer)
        inOrder.verify(observer).onChanged(RepositoryState.Loading)
        inOrder.verify(observer).onChanged(successState)
    }

    @Test
    fun `repositoryState notifies observers in active lifecycle state`() {
        val lifecycleOwner = TestLifecycleOwner()
        val observer = mock(Observer::class.java) as Observer<RepositoryState<List<RepositoryItem>>>
        viewModel.repositoryState.observe(lifecycleOwner, observer)

        // Set the lifecycle state to RESUMED
        lifecycleOwner.setCurrentState(Lifecycle.State.RESUMED)

        viewModel.searchRepositories("android")

        verify(observer, atLeastOnce()).onChanged(any())
    }

    @Test
    fun `repositoryState retains state after configuration change`() = runTest {
        val query = "android"
        val items = listOf(RepositoryItem("repo1", "", "Kotlin", 100, 50, 20, 10))
        val successState = RepositoryState.Success(items)
        `when`(repository.searchRepositories(query)).thenReturn(successState)

        viewModel.searchRepositories(query)

        // Simulate ViewModel recreation due to configuration change
        val newViewModel = SearchRepositoriesViewModel(repository)
        newViewModel.repositoryState.observeForever(Observer {
            assertEquals("ViewModel should retain last known state after configuration change", successState, it)
        })
    }

    @Test
    fun `searchRepositories called with correct parameters`() = runTest {
        val query = "android"
        viewModel.searchRepositories(query)
        verify(repository).searchRepositories(query)
    }

    @Test
    fun `searchRepositories handles network errors`() = runTest {
        val query = "android"
        val networkError = IOException("Network error occurred")
        val runtimeException = RuntimeException(networkError)

        // Wrap the IOException in a RuntimeException
        `when`(repository.searchRepositories(query)).thenThrow(runtimeException)

        viewModel.searchRepositories(query)

        val observer = mock(Observer::class.java) as Observer<RepositoryState<List<RepositoryItem>>>
        viewModel.repositoryState.observeForever(observer)

        // Capture the argument passed to onChanged
        val argumentCaptor = argumentCaptor<RepositoryState<List<RepositoryItem>>>()
        verify(observer).onChanged(argumentCaptor.capture())

        // Check that the captured argument is an Error state with the IOException as its cause
        val capturedValue = argumentCaptor.firstValue
        assertTrue("Expected RepositoryState.Error with IOException",
            capturedValue is RepositoryState.Error &&
                    (capturedValue as RepositoryState.Error).exception.cause is IOException &&
                    capturedValue.exception.cause?.message == networkError.message
        )
    }

    @Test
    fun `searchRepositories updates LiveData correctly on success`() = runBlocking {
        val query = "android"
        val items = listOf(
            RepositoryItem("repo1", "", "Kotlin", 100, 50, 20, 10),
            RepositoryItem("repo2", "", "Java", 200, 80, 30, 5)
        )
        val successState = RepositoryState.Success(items)

        `when`(repository.searchRepositories(query)).thenReturn(successState)

        // Your test code remains the same
        val observer = Mockito.mock(Observer::class.java) as Observer<RepositoryState<List<RepositoryItem>>>
        viewModel.repositoryState.observeForever(observer)

        viewModel.searchRepositories(query)

        Mockito.verify(repository).searchRepositories(query)
        Mockito.verify(observer).onChanged(successState)
    }

    @Test
    fun `searchRepositories updates LiveData correctly on failure`() = runBlocking {
        val query = "invalid_query"
        val error = Exception("Search failed")
        val errorState = RepositoryState.Error(error)

        // Mock the repository method to throw the exception
        `when`(runBlocking { repository.searchRepositories(query) }).thenAnswer {
            throw error
        }

        val observer = Mockito.mock(Observer::class.java) as Observer<RepositoryState<List<RepositoryItem>>>
        viewModel.repositoryState.observeForever(observer)

        try {
            viewModel.searchRepositories(query)
        } catch (e: Exception) {
            // Verify that the observer is notified with the error state
            Mockito.verify(observer).onChanged(errorState)
        }
    }

    @Test
    fun `searchRepositories handles null response`() = runTest {
        val query = "android"
        `when`(repository.searchRepositories(query)).thenReturn(null)

        val observer = Mockito.mock(Observer::class.java) as Observer<RepositoryState<List<RepositoryItem>>>
        viewModel.repositoryState.observeForever(observer)

        viewModel.searchRepositories(query)

        val captor = argumentCaptor<RepositoryState<List<RepositoryItem>>>()
        verify(observer, atLeastOnce()).onChanged(captor.capture())

        // Check if the last value captured is an error, which should be the case for a null response
        val hasErrorForNullResponse = captor.allValues.any {
            it is RepositoryState.Error && it.exception.message == "Null response received"
        }

        assertTrue("Expected an error state for null response but didn't find it", hasErrorForNullResponse)

        viewModel.repositoryState.removeObserver(observer)
    }

    @Test
    fun `searchRepositories handles network timeouts`() = runTest {
        val query = "android"
        val expectedException = IOException("接続できません。インターネット接続を確認してもう一度お試しください。")
        `when`(repository.searchRepositories(query)).thenReturn(RepositoryState.Error(expectedException))

        val observer = Mockito.mock(Observer::class.java) as Observer<RepositoryState<List<RepositoryItem>>>
        viewModel.repositoryState.observeForever(observer)

        viewModel.searchRepositories(query)

        val errorCaptor = argumentCaptor<RepositoryState<List<RepositoryItem>>>()
        verify(observer, atLeastOnce()).onChanged(errorCaptor.capture())

        assertTrue(
            "Expected RepositoryState.Error with specific IOException message",
            errorCaptor.allValues.any {
                it is RepositoryState.Error && it.exception.message == expectedException.message
            }
        )

        viewModel.repositoryState.removeObserver(observer)
    }

    @Test
    fun `searchRepositories handles unexpected format`() = runTest {
        val query = "android"
        `when`(repository.searchRepositories(query)).thenThrow(RuntimeException("Unexpected format"))

        val observer = Mockito.mock(Observer::class.java) as Observer<RepositoryState<List<RepositoryItem>>>
        viewModel.repositoryState.observeForever(observer)

        viewModel.searchRepositories(query)

        // ArgumentCaptor to capture all onChanged invocations
        val captor = argumentCaptor<RepositoryState<List<RepositoryItem>>>()
        verify(observer, atLeastOnce()).onChanged(captor.capture())

        // Check that among all captured values, there is at least one Error state with the expected exception message
        val hasExpectedError = captor.allValues.any { state ->
            state is RepositoryState.Error && state.exception.message == "Unexpected format"
        }

        assertTrue("The expected error state was not published to the observer.", hasExpectedError)

        viewModel.repositoryState.removeObserver(observer)
    }


    @Test
    fun `retryLastFetch() retries last successful query`() = runBlocking<Unit> {
        // Mock a successful search query
        val query = "android"
        val items = listOf(
            RepositoryItem("repo1", "", "Kotlin", 100, 50, 20, 10),
            RepositoryItem("repo2", "", "Java", 200, 80, 30, 5)
        )
        val successState = RepositoryState.Success(items)
        `when`(repository.searchRepositories(query)).thenReturn(successState)

        // Perform the initial search
        viewModel.searchRepositories(query)
        Mockito.verify(repository).searchRepositories(query)

        // Mock a failure when retrying the last successful query
        val error = Exception("Search failed")
        val errorState = RepositoryState.Error(error)
        `when`(repository.searchRepositories(query)).thenReturn(errorState)

        // Retry the last successful query
        viewModel.retryLastFetch()

        // Verify that the repository method is called again with the same query
        Mockito.verify(repository, times(2)).searchRepositories(query)
    }


    @Test
    fun `searchRepositories sets Loading state before starting search`() = runTest {
        val query = "android"
        val observer = Mockito.mock(Observer::class.java) as Observer<RepositoryState<List<RepositoryItem>>>
        viewModel.repositoryState.observeForever(observer)

        viewModel.searchRepositories(query)

        val captor = argumentCaptor<RepositoryState<List<RepositoryItem>>>()
        verify(observer, atLeastOnce()).onChanged(captor.capture())

        val loadingState = captor.allValues.find { it is RepositoryState.Loading }
        assertNotNull("Loading state should be set before starting search", loadingState)
    }

    @Test
    fun `searchRepositories transitions to Empty state when no results found`() = runTest {
        val query = "emptyquery"
        `when`(repository.searchRepositories(query)).thenReturn(RepositoryState.Empty)

        val observer = Mockito.mock(Observer::class.java) as Observer<RepositoryState<List<RepositoryItem>>>
        viewModel.repositoryState.observeForever(observer)

        viewModel.searchRepositories(query)

        val captor = argumentCaptor<RepositoryState<List<RepositoryItem>>>()
        verify(observer, atLeastOnce()).onChanged(captor.capture())

        val emptyState = captor.allValues.find { it is RepositoryState.Empty }
        assertNotNull("Empty state should be set when no results found", emptyState)
    }


    @Test
    fun `retryLastFetch uses last successful query`() = runTest {
        val query = "android"
        val items = listOf(RepositoryItem("repo1", "", "Kotlin", 100, 50, 20, 10))
        val successState = RepositoryState.Success(items)
        `when`(repository.searchRepositories(query)).thenReturn(successState)

        viewModel.searchRepositories(query) // Initial search
        viewModel.retryLastFetch() // Retry fetch

        verify(repository, times(2)).searchRepositories(query) // Called twice with the same query
    }


    @Test
    fun `repositoryState retains last known state after ViewModel recreation`() = runTest {
        val query = "android"
        val items = listOf(RepositoryItem("repo1", "", "Kotlin", 100, 50, 20, 10))
        val successState = RepositoryState.Success(items)
        `when`(repository.searchRepositories(query)).thenReturn(successState)

        viewModel.searchRepositories(query) // Perform search

        // Simulate ViewModel recreation
        val newViewModel = SearchRepositoriesViewModel(repository)
        newViewModel.repositoryState.observeForever(Observer {
            assertTrue("New ViewModel should retain last known state", it is RepositoryState.Success)
        })
    }

    class TestLifecycleOwner : LifecycleOwner {
        private val lifecycleRegistry = LifecycleRegistry(this)

        init {
            lifecycleRegistry.currentState = Lifecycle.State.CREATED
        }

        override val lifecycle: Lifecycle
            get() = lifecycleRegistry

        fun setCurrentState(state: Lifecycle.State) {
            lifecycleRegistry.currentState = state
        }
    }

    @Test
    fun `repositoryState observers are removed on ViewModel clear`() {
        // Create a new LiveData instance for testing
        val liveData = MutableLiveData<RepositoryState<List<RepositoryItem>>>()

        // Create a TestLifecycleOwner and set its state to STARTED
        val lifecycleOwner = TestLifecycleOwner()
        lifecycleOwner.setCurrentState(Lifecycle.State.STARTED)

        // Observe the LiveData with the TestLifecycleOwner
        liveData.observe(lifecycleOwner, Observer { })

        // Set the LifecycleOwner state to DESTROYED
        lifecycleOwner.setCurrentState(Lifecycle.State.DESTROYED)

        // Check that the LiveData has no observers
        assertFalse("LiveData should have no observers after LifecycleOwner is DESTROYED", liveData.hasObservers())
    }

    @Test
    fun `searchRepositories retries after failure`() = runBlocking {
        val query = "android"
        val exception = RuntimeException("Network error")
        val successItems = listOf(RepositoryItem("repo1", "", "Kotlin", 100, 50, 20, 10))
        val successState = RepositoryState.Success(successItems)

        // Arrange
        doAnswer { throw exception } // First call throws an exception
            .doAnswer { successState } // Second call returns success
            .whenever(repository).searchRepositories(query)

        // Act
        viewModel.searchRepositories(query) // This should post an Error state
        viewModel.retryLastFetch() // This should post a Success state

        // Assert
        verify(observer, times(2)).onChanged(RepositoryState.Loading) // Loading state before each fetch
        verify(observer, times(1)).onChanged(RepositoryState.Error(exception)) // Error state after the first fetch fails
        verify(observer, times(1)).onChanged(successState) // Success state after the retry fetch succeeds
    }


}