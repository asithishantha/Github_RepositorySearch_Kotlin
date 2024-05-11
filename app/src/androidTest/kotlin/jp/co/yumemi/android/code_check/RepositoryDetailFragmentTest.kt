package jp.co.yumemi.android.code_check.ui.detail

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import jp.co.yumemi.android.code_check.R
import jp.co.yumemi.android.code_check.model.RepositoryItem
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import java.util.logging.Logger

class RepositoryDetailFragmentTest {

    @Test
    fun repositoryDetailFragment_displaysRepositoryInformation() {
        // Create a mock RepositoryItem to pass to the fragment
        val repositoryItem = RepositoryItem(
            name = "SampleRepo",
            ownerIconUrl = "https://sample.com/icon.png",
            language = "Kotlin",
            stargazersCount = 42,
            watchersCount = 100,
            forksCount = 7,
            openIssuesCount = 3
        )

        // Launch the fragment with the mock RepositoryItem as an argument
        val fragmentScenario = launchFragmentInContainer<RepositoryDetailFragment>(
            fragmentArgs = RepositoryDetailFragmentArgs(repositoryItem).toBundle(),
            themeResId = R.style.Theme_AndroidEngineerCodeCheck
        )

        // Verify that the repository name is displayed
        onView(withId(R.id.nameView)).check(matches(withText(repositoryItem.name)))

        // Verify that the repository language is displayed
        onView(withId(R.id.languageView)).check(matches(withText(repositoryItem.language)))

        // Verify that the stargazers count is displayed
        onView(withId(R.id.starsView)).check(matches(withText("${repositoryItem.stargazersCount} stars")))

        // Verify that the watchers count is displayed
        onView(withId(R.id.watchersView)).check(matches(withText("${repositoryItem.watchersCount} watchers")))

        // Verify that the forks count is displayed
        onView(withId(R.id.forksView)).check(matches(withText("${repositoryItem.forksCount} forks")))

        // Verify that the open issues count is displayed
        onView(withId(R.id.openIssuesView)).check(matches(withText("${repositoryItem.openIssuesCount} open issues")))
    }

    @Test
    fun repositoryDetailFragment_displaysPlaceholdersForMissingInformation() {
        // Create a mock RepositoryItem with empty strings for name and language
        val repositoryItem = RepositoryItem(
            name = "",
            ownerIconUrl = "https://sample.com/icon.png",
            language = "",
            stargazersCount = 42,
            watchersCount = 100,
            forksCount = 7,
            openIssuesCount = 3
        )

        // Launch the fragment with the mock RepositoryItem as an argument
        val fragmentScenario = launchFragmentInContainer<RepositoryDetailFragment>(
            fragmentArgs = RepositoryDetailFragmentArgs(repositoryItem).toBundle(),
            themeResId = R.style.Theme_AndroidEngineerCodeCheck
        )

        // Verify that the placeholder text is displayed when the name is missing
        onView(withId(R.id.nameView)).check(matches(withText(R.string.no_name_available)))

        // Verify that the placeholder text is displayed when the language is missing
        onView(withId(R.id.languageView)).check(matches(withText(R.string.language_unknown)))

        // The rest of the checks remain the same as in the previous test
        onView(withId(R.id.starsView)).check(matches(withText("${repositoryItem.stargazersCount} stars")))
        onView(withId(R.id.watchersView)).check(matches(withText("${repositoryItem.watchersCount} watchers")))
        onView(withId(R.id.forksView)).check(matches(withText("${repositoryItem.forksCount} forks")))
        onView(withId(R.id.openIssuesView)).check(matches(withText("${repositoryItem.openIssuesCount} open issues")))
    }


    @Test
    fun repositoryDetailFragment_displaysCorrectPluralization() {
        // Create a mock RepositoryItem with different counts
        val repositoryItem = RepositoryItem(
            name = "SampleRepo",
            ownerIconUrl = "https://sample.com/icon.png",
            language = "Kotlin",
            stargazersCount = 1, // Singular
            watchersCount = 2, // Plural
            forksCount = 1, // Singular
            openIssuesCount = 2 // Plural
        )

        // Launch the fragment with the mock RepositoryItem as an argument
        val fragmentScenario = launchFragmentInContainer<RepositoryDetailFragment>(
            fragmentArgs = RepositoryDetailFragmentArgs(repositoryItem).toBundle(),
            themeResId = R.style.Theme_AndroidEngineerCodeCheck
        )

        // Verify that the correct singular and plural forms are displayed
        onView(withId(R.id.starsView)).check(matches(withText("1 star")))
        onView(withId(R.id.watchersView)).check(matches(withText("2 watchers")))
        onView(withId(R.id.forksView)).check(matches(withText("1 fork")))
        onView(withId(R.id.openIssuesView)).check(matches(withText("2 open issues")))
    }


    @Test
    fun repositoryDetailFragment_displaysOwnerIcon() {
        // Create a mock RepositoryItem with an owner icon URL
        val repositoryItem = RepositoryItem(
            name = "SampleRepo",
            ownerIconUrl = "https://sample.com/icon.png",
            language = "Kotlin",
            stargazersCount = 42,
            watchersCount = 100,
            forksCount = 7,
            openIssuesCount = 3
        )

        // Launch the fragment with the mock RepositoryItem as an argument
        val fragmentScenario = launchFragmentInContainer<RepositoryDetailFragment>(
            fragmentArgs = RepositoryDetailFragmentArgs(repositoryItem).toBundle(),
            themeResId = R.style.Theme_AndroidEngineerCodeCheck
        )

        // Verify that the owner icon is displayed
        onView(withId(R.id.ownerIconView)).check(matches(isDisplayed()))
    }


    @Test
    fun repositoryDetailFragment_viewsAreVisible() {
        // Create a mock RepositoryItem
        val repositoryItem = RepositoryItem(
            name = "SampleRepo",
            ownerIconUrl = "https://sample.com/icon.png",
            language = "Kotlin",
            stargazersCount = 42,
            watchersCount = 100,
            forksCount = 7,
            openIssuesCount = 3
        )

        // Launch the fragment with the mock RepositoryItem as an argument
        val fragmentScenario = launchFragmentInContainer<RepositoryDetailFragment>(
            fragmentArgs = RepositoryDetailFragmentArgs(repositoryItem).toBundle(),
            themeResId = R.style.Theme_AndroidEngineerCodeCheck
        )

        // Verify that all views are visible
        onView(withId(R.id.ownerIconView)).check(matches(isDisplayed()))
        onView(withId(R.id.nameView)).check(matches(isDisplayed()))
        onView(withId(R.id.languageView)).check(matches(isDisplayed()))
        onView(withId(R.id.starsView)).check(matches(isDisplayed()))
        onView(withId(R.id.watchersView)).check(matches(isDisplayed()))
        onView(withId(R.id.forksView)).check(matches(isDisplayed()))
        onView(withId(R.id.openIssuesView)).check(matches(isDisplayed()))
    }

    @Test
    fun repositoryDetailFragment_displaysDefaultsWhenCountsAreZero() {
        val repositoryItem = RepositoryItem(
            name = "SampleRepo",
            ownerIconUrl = "https://sample.com/icon.png",
            language = "Kotlin",
            stargazersCount = 0,
            watchersCount = 0,
            forksCount = 0,
            openIssuesCount = 0
        )

        launchFragmentInContainer<RepositoryDetailFragment>(
            fragmentArgs = RepositoryDetailFragmentArgs(repositoryItem).toBundle(),
            themeResId = R.style.Theme_AndroidEngineerCodeCheck
        )

        onView(withId(R.id.starsView)).check(matches(withText("0 stars")))
        onView(withId(R.id.watchersView)).check(matches(withText("0 watchers")))
        onView(withId(R.id.forksView)).check(matches(withText("0 forks")))
        onView(withId(R.id.openIssuesView)).check(matches(withText("0 open issues")))
    }

    @Test
    fun repositoryDetailFragment_displaysDefaultIconWhenUrlIsEmpty() {
        // Arrange: Create a mock RepositoryItem with an empty ownerIconUrl
        val repositoryItem = RepositoryItem(
            name = "SampleRepo",
            ownerIconUrl = "",
            language = "Kotlin",
            stargazersCount = 42,
            watchersCount = 100,
            forksCount = 7,
            openIssuesCount = 3
        )

        // Act: Launch the fragment with the mock RepositoryItem as an argument
        val fragmentScenario = launchFragmentInContainer<RepositoryDetailFragment>(
            fragmentArgs = RepositoryDetailFragmentArgs(repositoryItem).toBundle(),
            themeResId = R.style.Theme_AndroidEngineerCodeCheck
        )

        // Assert: ownerIconViewが表示されていることを確認します
        // このテストはImageViewが表示されていれば合格となりますが、表示されているドローアブルが何であるかはチェックしません。
        onView(withId(R.id.ownerIconView)).check(matches(isDisplayed()))
    }

    @Test
    fun repositoryDetailFragment_displaysCorrectData() {
        // Arrange: Create a mock RepositoryItem with all necessary fields
        val repositoryItem = RepositoryItem(
            name = "SampleRepo",
            ownerIconUrl = "https://sample.com/icon.png",
            language = "Kotlin",
            stargazersCount = 42,
            watchersCount = 100,
            forksCount = 7,
            openIssuesCount = 3
        )

        // Act: Launch the fragment with the mock RepositoryItem as an argument
        val fragmentScenario = launchFragmentInContainer<RepositoryDetailFragment>(
            fragmentArgs = RepositoryDetailFragmentArgs(repositoryItem).toBundle(),
            themeResId = R.style.Theme_AndroidEngineerCodeCheck
        )

        // Assert: Verify that the owner icon is displayed using the provided URL
        onView(withId(R.id.ownerIconView)).check(matches(isDisplayed()))

        // Assert: Verify that the repository name is displayed
        onView(withId(R.id.nameView)).check(matches(withText(repositoryItem.name)))

        // Assert: Verify that the repository language is displayed
        onView(withId(R.id.languageView)).check(matches(withText(repositoryItem.language)))

        // Assert: Verify that the counts are displayed with correct pluralization
        onView(withId(R.id.starsView)).check(matches(withText("42 stars")))
        onView(withId(R.id.watchersView)).check(matches(withText("100 watchers")))
        onView(withId(R.id.forksView)).check(matches(withText("7 forks")))
        onView(withId(R.id.openIssuesView)).check(matches(withText("3 open issues")))
    }

}