package com.example.artspaceapp

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var horizontalScrollView: HorizontalScrollView
    private lateinit var previousButton: Button
    private lateinit var nextButton: Button
    private lateinit var imageContainer: LinearLayout

    private var currentIndex = 0
    private val scrollHandler = Handler(Looper.getMainLooper())
    private var isUserScrolling = false
    private var sectionWidth = 0

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        horizontalScrollView = findViewById(R.id.horizontalScrollView)
        previousButton = findViewById(R.id.button)
        nextButton = findViewById(R.id.button2)
        imageContainer = findViewById(R.id.imageContainer)

        // Set up buttons to navigate images
        previousButton.setOnClickListener {
            if (currentIndex > 0) {
                currentIndex--
                scrollToCurrentIndex()
            }
        }

        nextButton.setOnClickListener {
            if (currentIndex < imageContainer.childCount - 1) {
                currentIndex++
                scrollToCurrentIndex()
            }
        }

        // Calculate the section width after layout is inflated
        horizontalScrollView.viewTreeObserver.addOnGlobalLayoutListener {
            if (sectionWidth == 0) {
                sectionWidth = calculateSectionWidth()
            }
        }

        // Detect scroll changes
        horizontalScrollView.setOnScrollChangeListener { _, _, _, _, _ ->
            isUserScrolling = true
            scrollHandler.removeCallbacks(snapRunnable)
            scrollHandler.postDelayed(snapRunnable, 200) // 200ms delay after user stops scrolling
        }
    }

    private val snapRunnable = Runnable {
        if (isUserScrolling) {
            isUserScrolling = false

            // Determine the scroll position and snap to the nearest section
            val scrollX = horizontalScrollView.scrollX
            val targetIndex = (scrollX + sectionWidth / 2) / sectionWidth
            currentIndex = targetIndex.coerceIn(0, imageContainer.childCount - 1)
            scrollToCurrentIndex()
        }
    }

    private fun scrollToCurrentIndex() {
        val scrollX = currentIndex * sectionWidth
        horizontalScrollView.smoothScrollTo(scrollX, 0)
    }

    // Dynamically calculate section width based on the first child in the image container
    private fun calculateSectionWidth(): Int {
        if (imageContainer.childCount > 0) {
            // Here we assume that each child of imageContainer is a section
            val firstSection = imageContainer.getChildAt(0) as LinearLayout
            return firstSection.width
        }
        return 0
    }
}