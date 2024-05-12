import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.testing.launchFragmentInContainer
import jp.co.yumemi.android.code_check.R
import jp.co.yumemi.android.code_check.ui.base.BaseFragment
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BaseFragmentTest {

    @Test
    fun `showLoading true should make progress bar visible`() {
        val scenario = launchFragmentInContainer<TestBaseFragment>()
        scenario.onFragment { fragment ->
            fragment.showLoading(true)
            assertEquals(View.VISIBLE, fragment.view?.findViewById<ProgressBar>(R.id.progressBar)?.visibility)
        }
    }

    @Test
    fun `showLoading false should make progress bar gone`() {
        val scenario = launchFragmentInContainer<TestBaseFragment>()
        scenario.onFragment { fragment ->
            fragment.showLoading(false)
            assertEquals(View.GONE, fragment.view?.findViewById<ProgressBar>(R.id.progressBar)?.visibility)
        }
    }

    @Test
    fun `showEmptyState true should make empty state text view visible`() {
        val scenario = launchFragmentInContainer<TestBaseFragment>()
        scenario.onFragment { fragment ->
            fragment.showEmptyState(true)
            assertEquals(View.VISIBLE, fragment.view?.findViewById<TextView>(R.id.emptyStateTextView)?.visibility)
        }
    }

    @Test
    fun `showEmptyState false should make empty state text view gone`() {
        val scenario = launchFragmentInContainer<TestBaseFragment>()
        scenario.onFragment { fragment ->
            fragment.showEmptyState(false)
            assertEquals(View.GONE, fragment.view?.findViewById<TextView>(R.id.emptyStateTextView)?.visibility)
        }
    }
}

class TestBaseFragment : BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the test layout that contains a ProgressBar with the ID R.id.progressBar
        return inflater.inflate(R.layout.test_fragment_base, container, false)
    }
}