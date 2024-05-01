package jp.co.yumemi.android.code_check

import androidx.appcompat.app.AppCompatActivity
import java.util.*

/**
 * アプリケーションのメインアクティビティです。
 * アプリケーションのエントリーポイントとして機能し、アプリケーション全体の機能を含みます。
 */
class TopActivity : AppCompatActivity(R.layout.activity_top) {

    companion object {
        private var lastSearchDateInternal: Date? = null

        // 最後の検索日時を安全に取得する
        var lastSearchDate: Date?
            get() = lastSearchDateInternal
            set(value) {
                lastSearchDateInternal = value
            }
    }
}