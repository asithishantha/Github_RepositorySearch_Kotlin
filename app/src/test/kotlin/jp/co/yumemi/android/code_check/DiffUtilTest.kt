import jp.co.yumemi.android.code_check.model.RepositoryItem
import jp.co.yumemi.android.code_check.util.diffUtil
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DiffUtilTest {

    private val baseItem = RepositoryItem(
        name = "repo1",
        ownerIconUrl = "url1",
        language = "Kotlin",
        stargazersCount = 42,
        watchersCount = 42,
        forksCount = 42,
        openIssuesCount = 42
    )

    @Test
    fun `areItemsTheSame returns true for items with the same name`() {
        val itemCopy = baseItem.copy()
        assertTrue(diffUtil.areItemsTheSame(baseItem, itemCopy))
    }

    @Test
    fun `areItemsTheSame returns false for items with different names`() {
        val itemWithDifferentName = baseItem.copy(name = "repo2")
        assertFalse(diffUtil.areItemsTheSame(baseItem, itemWithDifferentName))
    }

    @Test
    fun `areContentsTheSame returns true for identical items`() {
        val itemCopy = baseItem.copy()
        assertTrue(diffUtil.areContentsTheSame(baseItem, itemCopy))
    }

    @Test
    fun `areContentsTheSame returns false for items with different fields`() {
        val itemWithDifferentOwnerIconUrl = baseItem.copy(ownerIconUrl = "url2")
        val itemWithDifferentLanguage = baseItem.copy(language = "Java")
        val itemWithDifferentStargazersCount = baseItem.copy(stargazersCount = 100)
        val itemWithDifferentWatchersCount = baseItem.copy(watchersCount = 100)
        val itemWithDifferentForksCount = baseItem.copy(forksCount = 100)
        val itemWithDifferentOpenIssuesCount = baseItem.copy(openIssuesCount = 100)

        assertFalse(diffUtil.areContentsTheSame(baseItem, itemWithDifferentOwnerIconUrl))
        assertFalse(diffUtil.areContentsTheSame(baseItem, itemWithDifferentLanguage))
        assertFalse(diffUtil.areContentsTheSame(baseItem, itemWithDifferentStargazersCount))
        assertFalse(diffUtil.areContentsTheSame(baseItem, itemWithDifferentWatchersCount))
        assertFalse(diffUtil.areContentsTheSame(baseItem, itemWithDifferentForksCount))
        assertFalse(diffUtil.areContentsTheSame(baseItem, itemWithDifferentOpenIssuesCount))
    }
}