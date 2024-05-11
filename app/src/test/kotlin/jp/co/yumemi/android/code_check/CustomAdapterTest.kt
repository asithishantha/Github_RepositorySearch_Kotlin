import android.content.Context
import android.widget.FrameLayout
import androidx.test.core.app.ApplicationProvider
import jp.co.yumemi.android.code_check.model.RepositoryItem
import jp.co.yumemi.android.code_check.ui.adapters.CustomAdapter
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import android.os.Build
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class CustomAdapterTest {

    private lateinit var adapter: CustomAdapter
    private lateinit var context: Context
    private var itemClicked: RepositoryItem? = null

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext<Context>()
        adapter = CustomAdapter().apply {
            this.itemClickListener = { item ->
                itemClicked = item
            }
        }
    }

    @Test
    fun `itemViewClick should invoke clickListener`() {
        val mockItem = RepositoryItem(
            name = "mockName",
            ownerIconUrl = "mockOwnerIconUrl",
            language = "mockLanguage",
            stargazersCount = 123,
            watchersCount = 456,
            forksCount = 789,
            openIssuesCount = 101
        )
        adapter.submitList(listOf(mockItem))

        // Use Robolectric to create a real Activity instance
        val activity = Robolectric.buildActivity(android.app.Activity::class.java).create().get()
        // Use FrameLayout as the parent ViewGroup
        val parent = FrameLayout(activity)

        val viewHolder = adapter.onCreateViewHolder(parent, 0)
        adapter.onBindViewHolder(viewHolder, 0)

        // Perform click on the itemView of the ViewHolder
        viewHolder.itemView.performClick()

        // Assert that the itemClickListener was invoked with the mockItem
        assert(itemClicked == mockItem)
    }

    @Test
    fun `adapter reports correct item count`() {
        val items = listOf(
            RepositoryItem(name = "Item1", ownerIconUrl = "", language = "", stargazersCount = 0, watchersCount = 0, forksCount = 0, openIssuesCount = 0),
            RepositoryItem(name = "Item2", ownerIconUrl = "", language = "", stargazersCount = 0, watchersCount = 0, forksCount = 0, openIssuesCount = 0)
        )
        adapter.submitList(items)
        assert(adapter.itemCount == items.size)
    }


    @Test
    fun `adapter recycles views correctly`() {
        val items = listOf(
            RepositoryItem(name = "Item1", ownerIconUrl = "", language = "", stargazersCount = 0, watchersCount = 0, forksCount = 0, openIssuesCount = 0),
            RepositoryItem(name = "Item2", ownerIconUrl = "", language = "", stargazersCount = 0, watchersCount = 0, forksCount = 0, openIssuesCount = 0)
        )
        adapter.submitList(items)

        val activity = Robolectric.buildActivity(android.app.Activity::class.java).create().get()
        val parent = FrameLayout(activity)

        val viewHolder1 = adapter.onCreateViewHolder(parent, 0)
        adapter.onBindViewHolder(viewHolder1, 0)

        val viewHolder2 = adapter.onCreateViewHolder(parent, 1)
        adapter.onBindViewHolder(viewHolder2, 1)

        assert(viewHolder1.itemView != viewHolder2.itemView) // Ensure different views are used for different positions
    }

}