package com.example.gof

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Score : AppCompatActivity() {

    private lateinit var lastTimeTextView2: TextView
    private lateinit var lowestTimeTextView2: TextView

    private lateinit var lastTimeTextView3: TextView
    private lateinit var lowestTimeTextView3: TextView

    private lateinit var lastTimeTextView4: TextView
    private lateinit var lowestTimeTextView4: TextView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)

        // Initialize views
        lastTimeTextView2 = findViewById(R.id.lastTimeTextView2)
        lowestTimeTextView2 = findViewById(R.id.lowestTimeTextView2)

        lastTimeTextView3 = findViewById(R.id.lastTimeTextView3)
        lowestTimeTextView3 = findViewById(R.id.lowestTimeTextView3)

        lastTimeTextView4 = findViewById(R.id.lastTimeTextView4)
        lowestTimeTextView4 = findViewById(R.id.lowestTimeTextView4)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        // Retrieve completion time and puzzle size from SharedPreferences
        val completionTime2 = sharedPreferences.getString("completionTime", "N/A") ?: "N/A"
        val puzzleSize2 = sharedPreferences.getInt("puzzleSize", 2)
        //val bestTime = sharedPreferences.getInt("bestCompletionTime", 3)

        // Retrieve completion time and puzzle size from SharedPreferences
        val completionTime3 = sharedPreferences.getString("completionTime3", "N/A") ?: "N/A"
        val puzzleSize3 = sharedPreferences.getInt("puzzleSize", 3)

        // Retrieve completion time and puzzle size from SharedPreferences
        val completionTime4 = sharedPreferences.getString("completionTime4", "N/A") ?: "N/A"
        val puzzleSize4 = sharedPreferences.getInt("puzzleSize", 4)

        Log.d("ScoreActivity", "Completion Time: $completionTime2, Puzzle Size: $puzzleSize2")


        // Display last completion time and lowest completion time
        displayLastCompletionTime2(completionTime2)
        displayLowestCompletionTime2(puzzleSize2)

        displayLastCompletionTime3(completionTime3)
        displayLowestCompletionTime3(puzzleSize3)

           displayLastCompletionTime4(completionTime4)
          displayLowestCompletionTime4(puzzleSize4)


    }

    private fun displayLastCompletionTime2(completionTime: String) {
        lastTimeTextView2.text = "Your Last $completionTime"
            }
    private fun displayLowestCompletionTime2(puzzleSize: Int) {
        val bestCompletionTime = sharedPreferences.getString("bestCompletionTime", "N/A")
        Log.d("ScoreActivity", "Best Completion Timee: $bestCompletionTime")

        lowestTimeTextView2.text = "Best  $bestCompletionTime"

        with(sharedPreferences.edit()) {
            putString("finalBestTime", bestCompletionTime)
            apply()
        }
    }



    private fun displayLastCompletionTime3(completionTime: String) {
        lastTimeTextView3.text = "Your Last $completionTime"
    }
    private fun displayLowestCompletionTime3(puzzleSize: Int) {
        val bestCompletionTime = sharedPreferences.getString("bestCompletionTime3", "N/A")
        Log.d("ScoreActivity", "Best Completion 3 Timee: $bestCompletionTime")

        lowestTimeTextView3.text = "Best  $bestCompletionTime"

        with(sharedPreferences.edit()) {
            putString("finalBestTime3", bestCompletionTime)
            apply()
        }


    }

    fun back(view: android.view.View) {
        val intent = Intent(this, ChooseSizeActivity::class.java)
        startActivity(intent)
    }
    private fun displayLastCompletionTime4(completionTime: String) {
        lastTimeTextView4.text = "Your Last $completionTime"
    }
    private fun displayLowestCompletionTime4(puzzleSize: Int) {
        val bestCompletionTime = sharedPreferences.getString("bestCompletionTime4", "N/A")
        Log.d("ScoreActivity", "Best Completion Time: $bestCompletionTime")

        lowestTimeTextView4.text = "Best  $bestCompletionTime"

        with(sharedPreferences.edit()) {
            putString("finalBestTime4", bestCompletionTime)
            apply()
        }

    }

}
