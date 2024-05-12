package jp.co.yumemi.android.code_check

import android.content.Intent
import android.graphics.drawable.PictureDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toDrawable
import com.caverock.androidsvg.SVG
import jp.co.yumemi.android.code_check.databinding.ActivitySplashBinding
import jp.co.yumemi.android.code_check.ui.TopActivity
import java.io.IOException

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set the background color to white
        binding.root.setBackgroundColor(ResourcesCompat.getColor(resources, android.R.color.white, theme))

        // Load the SVG into the ImageView using androidsvg
        try {
            val svg = SVG.getFromAsset(assets, "splash_screen.svg")
            val drawable = PictureDrawable(svg.renderToPicture())
            binding.imageViewSplash.setImageDrawable(drawable)
        } catch (e: IOException) {
            e.printStackTrace()
            // Handle the exception if the SVG file is not found or cannot be read
        }

        // Apply the zoom-in animation
        val zoomInAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_in)
        binding.imageViewSplash.startAnimation(zoomInAnimation)

        // Set a delay to transition to the main activity
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, TopActivity::class.java))
            finish()
        }, 4000)
    }
}