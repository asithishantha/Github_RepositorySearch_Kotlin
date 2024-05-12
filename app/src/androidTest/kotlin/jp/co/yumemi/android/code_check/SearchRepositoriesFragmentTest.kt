import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import jp.co.yumemi.android.code_check.R
import jp.co.yumemi.android.code_check.ui.TopActivity
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import jp.co.yumemi.android.code_check.model.RepositoryItem
import jp.co.yumemi.android.code_check.ui.search.SearchRepositoriesFragment
import jp.co.yumemi.android.code_check.viewmodel.RepositoryState
import jp.co.yumemi.android.code_check.viewmodel.SearchRepositoriesViewModel
import org.hamcrest.Matcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.io.IOException
import java.util.concurrent.TimeoutException
@RunWith(AndroidJUnit4::class)
@LargeTest
class SearchRepositoriesFragmentTest {

    @get:Rule
    val activityRule = ActivityTestRule(TopActivity::class.java)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: SearchRepositoriesViewModel

    //IdlingResourceなどの同期技術を利用して、目的のUI状態が到来するまで待機しようとしましたが、
    // うまくいきませんでした。そのため、独自の方法を使用しました。
    fun waitForView(viewMatcher: Matcher<View>, timeoutMillis: Long) {
        val endTime = System.currentTimeMillis() + timeoutMillis
        while (System.currentTimeMillis() < endTime) {
            try {
                onView(viewMatcher).check(matches(isDisplayed()))
                return  // View is displayed, exit the loop
            } catch (e: Throwable) {}
        }
        throw TimeoutException("Timed out waiting for view matching: $viewMatcher")
    }

//    @Test
//    fun testSearchBarVisibility() {
//        // Check if the search bar is visible
//        onView(withId(R.id.searchBar))
//            .check(matches(isDisplayed()))
//    }
//
//     @Test
//     fun testSearchButtonWithoutInput() {
//         // Click the search button without entering anything in the search input field
//         onView(withId(R.id.searchButton)).perform(click())
//         // Check if the Snackbar with the expected text is displayed
//         onView(withText("検索クエリを入力してください")).check(matches(isDisplayed()))
//     }
//
//    @Test
//    fun searchRepositories_DisplayResults() {
//        // Type the search query
//        onView(withId(R.id.searchInputText))
//            .perform(typeText("android"))
//
//        // Perform a click on the search button
//        onView(withId(R.id.searchButton))
//            .perform(click())
//
//        // Wait for the RecyclerView to be displayed, allowing a timeout of 4000 milliseconds
//        waitForView(withId(R.id.recyclerView), 4000)
//
//        // Now that we've waited, we can check if the RecyclerView is displayed
//        onView(withId(R.id.recyclerView))
//            .check(matches(isDisplayed()))
//
//        // Check if an item with the text "android" is displayed in the RecyclerView
//        onView(withText("android"))
//            .check(matches(isDisplayed()))
//    }
//
//    @Test
//    fun searchRepositories_DisplayNoResults() {
//        // Type a search query that does not match any repositories
//        onView(withId(R.id.searchInputText))
//            .perform(typeText("$#"))
//
//        // Perform a click on the search button
//        onView(withId(R.id.searchButton))
//            .perform(click())
//
//        waitForView(withId(R.id.emptyStateTextView), 5000)
//
//        // Wait until the empty state text view is displayed
//        onView(withId(R.id.emptyStateTextView))
//            .check(matches(isDisplayed()))
//
//        // Optionally, check the text of the empty state text view
//        onView(withId(R.id.emptyStateTextView))
//            .check(matches(withText(R.string.no_items_found)))
//    }
//
//    @Test
//    fun searchRepositories_navigateTo_detailrepositories() {
//        // Type the search query and close the keyboard
//        onView(withId(R.id.searchInputText))
//            .perform(typeText("android"), closeSoftKeyboard())
//
//        // Perform a click on the search button
//        onView(withId(R.id.searchButton))
//            .perform(click())
//
//        waitForView(withId(R.id.recyclerView), 5000)
//
//        // Check if the RecyclerView is displayed with the list of repositories
//        onView(withId(R.id.recyclerView))
//            .check(matches(isDisplayed()))
//
//        // Check if an item with the text "android" is displayed in the RecyclerView
//        onView(withText("android"))
//            .check(matches(isDisplayed()))
//
//        // Perform a click on the first item in the RecyclerView
//        onView(withId(R.id.recyclerView))
//            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
//
//        onView(withId(R.id.ownerIconView))
//            .check(matches(isDisplayed()))
//    }
//
//    @Test
//    fun testSearchFunctionalityWithValidQuery() {
//        // Type a valid search query
//        onView(withId(R.id.searchInputText))
//            .perform(typeText("android"), closeSoftKeyboard())
//
//        // Click the search button
//        onView(withId(R.id.searchButton))
//            .perform(click())
//
//        // Verify that the progress bar is displayed while loading
//        onView(withId(R.id.progressBar))
//            .check(matches(isDisplayed()))
//
//        // Wait for the RecyclerView to be displayed
//        waitForView(withId(R.id.recyclerView), 5000)
//
//        // Verify that the RecyclerView is displayed with the expected results
//        onView(withId(R.id.recyclerView))
//            .check(matches(isDisplayed()))
//
//        // Verify that at least one item is displayed in the RecyclerView
//        onView(withId(R.id.recyclerView))
//            .check(matches(hasMinimumChildCount(1)))
//    }



}