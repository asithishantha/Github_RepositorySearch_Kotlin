import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.http.*
import jp.co.yumemi.android.code_check.model.RepositoryItem
import jp.co.yumemi.android.code_check.repository.GithubRepository
import jp.co.yumemi.android.code_check.viewmodel.RepositoryState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class GithubRepositoryTest {

    private lateinit var repository: GithubRepository

    @Before
    fun setup() {
        val mockEngine = MockEngine { request ->
            respond(
                content = """{
                "items": [
                    {
                        "full_name": "owner/repo",
                        "owner": {
                            "avatar_url": "https://example.com/avatar.png"
                        },
                        "language": "Kotlin",
                        "stargazers_count": 42,
                        "watchers_count": 42,
                        "forks_count": 42,
                        "open_issues_count": 42
                    }
                ]
            }""",
                headers = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString())),
                status = HttpStatusCode.OK
            )
        }
        val mockHttpClient = HttpClient(mockEngine) {
            install(JsonFeature)
        }
        repository = GithubRepository(mockHttpClient)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset the Main dispatcher to the original one after the test
    }

    private fun executeSearchRepositories(
        name: String,
        ownerIconUrl: String,
        language: String,
        stargazersCount: Long,
        watchersCount: Long,
        forksCount: Long,
        openIssuesCount: Long
    ): RepositoryState<List<RepositoryItem>> {
        return RepositoryState.Success(
            listOf(
                RepositoryItem(
                    name = name,
                    ownerIconUrl = ownerIconUrl,
                    language = language,
                    stargazersCount = stargazersCount,
                    watchersCount = watchersCount,
                    forksCount = forksCount,
                    openIssuesCount = openIssuesCount
                )
            )
        )
    }

    @Test
    fun `searchRepositories returns success on valid response`() = runTest {
        val result = executeSearchRepositories(
            name = "owner/repo",
            ownerIconUrl = "https://example.com/avatar.png",
            language = "Kotlin",
            stargazersCount = 42,
            watchersCount = 42,
            forksCount = 42,
            openIssuesCount = 42
        )

        assertTrue(result is RepositoryState.Success, "Expected result to be a success state")
        // ... rest of the assertions
    }

    @Test
    fun `searchRepositories returns error on network failure`() = runTest {
        // Setup the mock engine to return an error response
        val errorMockEngine = MockEngine { request ->
            respondError(HttpStatusCode.InternalServerError)
        }
        val errorMockHttpClient = HttpClient(errorMockEngine) {
            install(JsonFeature)
        }
        val errorRepository = GithubRepository(errorMockHttpClient)

        // Call the searchRepositories method and expect an error result
        val result = errorRepository.searchRepositories("query")

        // Assert that the result is an error state
        assertTrue(result is RepositoryState.Error, "Expected result to be an error state")
    }


    @Test
    fun `searchRepositories returns empty state when no repositories found`() = runTest {
        // Setup the mock engine to return an empty list of repositories
        val emptyListMockEngine = MockEngine { request ->
            respond(
                content = """{"items": []}""",
                headers = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString())),
                status = HttpStatusCode.OK
            )
        }
        val emptyListMockHttpClient = HttpClient(emptyListMockEngine) {
            install(JsonFeature)
        }
        val emptyListRepository = GithubRepository(emptyListMockHttpClient)

        val result = emptyListRepository.searchRepositories("query")

        // Log the actual result for debugging purposes
        println("Actual result: $result")

        // Assert that the result is an empty state
        assertTrue(result is RepositoryState.Empty, "Expected result to be an empty state, but was $result")
    }

    @Test
    fun `searchRepositories returns error on JSON parsing failure`() = runTest {
        // Set up a mock engine to return a structurally invalid JSON response
        val invalidJsonMockEngine = MockEngine { request ->
            respond(
                content = "{\"invalid\": true", // Structurally invalid JSON (missing closing brace)
                headers = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString())),
                status = HttpStatusCode.OK
            )
        }

        // Create an HttpClient with the mock engine
        val invalidJsonMockHttpClient = HttpClient(invalidJsonMockEngine) {
            install(JsonFeature) {
                serializer = KotlinxSerializer() // Make sure to use a serializer that throws on invalid JSON
            }
        }

        // Create an instance of GithubRepository with the mock HttpClient
        val invalidJsonRepository = GithubRepository(invalidJsonMockHttpClient)

        // Call the searchRepositories method and expect a JSON parsing error result
        val result = invalidJsonRepository.searchRepositories("query")

        // Log the result for diagnostic purposes
        println("Result of searchRepositories: $result")

        // Assert that the result is a JSON parsing error state
        assertTrue(
            result is RepositoryState.JsonParsingError,
            "Expected result to be a JSON parsing error state, but was $result"
        )
    }


}