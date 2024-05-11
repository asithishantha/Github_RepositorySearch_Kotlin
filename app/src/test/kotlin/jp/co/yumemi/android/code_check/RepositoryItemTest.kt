import jp.co.yumemi.android.code_check.model.RepositoryItem
import org.junit.Assert.assertEquals
import org.junit.Test

class RepositoryItemTest {

    @Test
    fun `RepositoryItem should hold the correct values`() {
        val repositoryItem = RepositoryItem("Repo1", "url", "Kotlin", 1, 1, 1, 1)
        assertEquals("Repo1", repositoryItem.name)
        assertEquals("url", repositoryItem.ownerIconUrl)
        assertEquals("Kotlin", repositoryItem.language)
        assertEquals(1L, repositoryItem.stargazersCount)
        assertEquals(1L, repositoryItem.watchersCount)
        assertEquals(1L, repositoryItem.forksCount)
        assertEquals(1L, repositoryItem.openIssuesCount)
    }
}