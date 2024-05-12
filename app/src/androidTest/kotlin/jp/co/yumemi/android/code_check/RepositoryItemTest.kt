package jp.co.yumemi.android.code_check

import android.os.Parcel
import android.os.Parcelable
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import jp.co.yumemi.android.code_check.model.RepositoryItem
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.reflect.Field

@RunWith(AndroidJUnit4::class)
@SmallTest
class RepositoryItemTest {

    @Test
    fun repositoryItem_ParcelableImplementation_ShouldBeCorrect() {
        // Arrange
        val originalItem = RepositoryItem(
            name = "SampleRepo",
            ownerIconUrl = "https://example.com/avatar.png",
            language = "Kotlin",
            stargazersCount = 42,
            watchersCount = 100,
            forksCount = 7,
            openIssuesCount = 3
        )

        // Act
        val parcel = Parcel.obtain()
        originalItem.writeToParcel(parcel, originalItem.describeContents())
        parcel.setDataPosition(0)

        // Use reflection to access the CREATOR field
        val creatorField: Field = RepositoryItem::class.java.getField("CREATOR")
        @Suppress("UNCHECKED_CAST")
        val creator = creatorField.get(null) as Parcelable.Creator<RepositoryItem>
        val createdFromParcel = creator.createFromParcel(parcel)

        // Assert
        assertEquals(originalItem, createdFromParcel)

        // Clean up
        parcel.recycle()
    }
}