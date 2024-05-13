package jp.co.yumemi.android.code_check.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.AndroidEntryPoint
import jp.co.yumemi.android.code_check.R
import java.util.*
import jp.co.yumemi.android.code_check.databinding.ActivityTopBinding
/**
 * アプリケーションのメインアクティビティです。
 * アプリケーションのエントリーポイントとして機能し、アプリケーション全体の機能を含みます。
 */
@AndroidEntryPoint
class TopActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTopBinding

    companion object {
        private var lastSearchDateInternal: Date? = null

        var lastSearchDate: Date?
            get() = lastSearchDateInternal
            set(value) {
                lastSearchDateInternal = value
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Use null-safe call to set up the theme switch
        binding.themeSwitch?.apply {
            isChecked = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES

            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
                // Save the theme preference (e.g., using SharedPreferences)
                // and recreate the activity for the theme change to take effect
                recreate()
            }
        }
    }
}