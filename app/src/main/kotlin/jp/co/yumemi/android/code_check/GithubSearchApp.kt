package jp.co.yumemi.android.code_check

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * アプリケーションのメインクラスで、Hiltの依存注入フレームワークを初期化するために使用されます。
 */
@HiltAndroidApp
class GithubSearchApp : Application()