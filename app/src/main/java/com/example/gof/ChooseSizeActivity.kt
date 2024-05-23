package com.example.gof

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View


class ChooseSizeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_size)
    }

    fun highscore(view: View) {

            val intent = Intent(this, Score::class.java)
            intent.putExtra("SIZE", 1) // Assuming 1 represents "High Score"
            startActivity(intent)
            finish() // Finish this activity to return to the calling activity

    }

    fun chooseSize(view: View) {
        try {
            val selectedSize = when (view.id) {
//                R.id.button_highScore -> 1
                R.id.button_2x2 -> 2
                R.id.button_3x3 -> 3
                R.id.button_4x4 -> 4
                else -> 2 // Default to 2x2
            }

            // Determine which activity to start based on the selected size
            val targetActivity = when (selectedSize) {
//                1 -> Score::class.java
                2 -> MainActivity::class.java
                3 -> threeByThree::class.java
                4 -> fourByFour::class.java
                else -> MainActivity::class.java
            }

            // Create an intent to pass the selected size to the appropriate activity
            val intent = Intent(this, targetActivity)
            intent.putExtra("SIZE", selectedSize)

            // Start the selected activity
            startActivity(intent)

            // Finish this activity to return to the calling activity
            finish()
        } catch (e: Exception) {
            Log.e("ChooseSizeActivity", "Error starting activity", e)
        }
    }
}
