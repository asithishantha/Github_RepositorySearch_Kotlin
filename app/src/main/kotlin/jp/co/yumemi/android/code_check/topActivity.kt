package jp.co.yumemi.android.code_check

import androidx.appcompat.app.AppCompatActivity
import java.util.*

/**
 * アプリケーションのメインアクティビティです。
 * アプリケーションのエントリーポイントとして機能し、アプリケーション全体の機能を含みます。
 */
class TopActivity : AppCompatActivity(R.layout.activity_top) {

    companion object {
        // アプリケーションで行われた最後の検索の日付を表します。
        private var lastSearchDateInternal: Date? = null

        /**
         * 行われた最後の検索の日付を取得します。
         * まだ日付が設定されていない場合、null を返します。
         */
        var lastSearchDate: Date
            get() = lastSearchDateInternal ?: throw UninitializedPropertyAccessException(
                "lastSearchDate プロパティが初期化されていません"
            )
            set(value) {
                lastSearchDateInternal = value
            }
    }
}
