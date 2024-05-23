package com.example.gof

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var mediaPlayer: MediaPlayer

    private lateinit var gridLayout: GridLayout
    private lateinit var timerTextView: TextView
    private lateinit var tiles: MutableList<Int>
    private var puzzleSize = 2 // Adjust puzzle size to 2x2 for testing (4 tiles)
    private var timer: CountDownTimer? = null
    private var elapsedTimeInSeconds: Long = 0

    private lateinit var sharedPreferences: SharedPreferences
    private var bestCompletionTime: String = "N/A"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize MediaPlayer with tapping sound
        mediaPlayer = MediaPlayer.create(this, R.raw.tap_sound)

        gridLayout = findViewById(R.id.gridLayout)
        timerTextView = findViewById(R.id.timerTextView)
        // Set background drawable for GridLayout boarder
        gridLayout.background = resources.getDrawable(R.drawable.grid_border, null)
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        initializePuzzle()
        addTilesToGrid()
        startTimer()
    }

    private fun initializePuzzle() {
        tiles = mutableListOf(1, 2, 3, 0)
        tiles.shuffle()
    }

    private fun moveTile(tileNumber: Int) {
        playTappingSound()

        val emptyPosition = tiles.indexOf(0)
        val tilePosition = tiles.indexOf(tileNumber)

        val puzzleDimension = puzzleSize

        val completionTime = timerTextView.text.toString()

        //val (minutes, seconds) = extractTimeComponents(completionTime)
       // Log.d("MainActivity", "Extracted Minutes: $minutes, Extracted Seconds: $seconds")

        val emptyRow = emptyPosition / puzzleDimension
        val emptyCol = emptyPosition % puzzleDimension
        val tileRow = tilePosition / puzzleDimension
        val tileCol = tilePosition % puzzleDimension

        if ((Math.abs(emptyRow - tileRow) == 1 && emptyCol == tileCol) ||
            (Math.abs(emptyCol - tileCol) == 1 && emptyRow == tileRow)) {

            tiles[emptyPosition] = tileNumber
            tiles[tilePosition] = 0

            addTilesToGrid()

            if (isWinning()) {
                timer?.cancel()
                val congratulationsTextView = findViewById<TextView>(R.id.congratulations)
                congratulationsTextView.visibility = View.VISIBLE
                congratulationsTextView.text = "Congratulations! 🎊 \n Your ${timerTextView.text}"

                // Show the New Game button
                val newGameButton = findViewById<Button>(R.id.newGameButton)
                newGameButton.visibility = View.VISIBLE


                //get the
                val oldCompletionTime = sharedPreferences.getString("completionTime", "N/A") ?: "N/A"
                var FinalNewBestTime = sharedPreferences.getString("finalBestTime", "N/A") ?: "N/A"
                Log.d("MainActivity", "Old Completion Time: $oldCompletionTime")
                Log.d("MainActivity", "new best Completion Time: $FinalNewBestTime")


                with(sharedPreferences.edit()) {
                    putString("completionTime", timerTextView.text.toString())
                    putInt("puzzleSize", puzzleSize)
                    apply()
                }


//                if(bestCompletionTime=="N/A"){
//                    FinalNewBestTime=completionTime
//                    with(sharedPreferences.edit()) {
//                        putString("bestCompletionTime", FinalNewBestTime)
//                        apply()
//                    }
//                    val bestCompletionTime = sharedPreferences.getString("bestCompletionTime", "N/A")
//                    Log.d("ScoreActivity", "Best  Timee: $bestCompletionTime")
//                }
                Log.d("MainActivity", "new best Completion Time: $FinalNewBestTime")
                var initialbestCompletionTime = sharedPreferences.getString("bestCompletionTime", "N/A")

                //intially add the first score as the best value
                if(initialbestCompletionTime=="N/A"){
                    initialbestCompletionTime=completionTime
                    with(sharedPreferences.edit()) {
                        putString("finalBestTime", initialbestCompletionTime)
                        apply()
                    }
                }
                Log.d("MainActivity", "intial 2 best Time: $initialbestCompletionTime")

                if (compareTimes(completionTime, FinalNewBestTime)) {
                    val newBestTime = timerTextView.text.toString()
                    with(sharedPreferences.edit()) {
                        putString("bestCompletionTime", newBestTime)
                        apply()
                    }
                    bestCompletionTime = newBestTime
                    Log.d("MainActivity", "New Best Completion Time: $bestCompletionTime")
                } else {
                    Log.d("MainActivity", "No new best completion time.")
                }
            }
        }
    }

    private fun playTappingSound() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.seekTo(0) // Rewind to start if already playing
        } else {
            mediaPlayer.start() // Start playing tapping sound
        }
    }

    private fun compareTimes(time1: String, time2: String): Boolean {
        val (minutes1, seconds1) = extractTimeComponents(time1) // new completion time
        val (minutes2, seconds2) = extractTimeComponents(time2) // old completion time

        Log.d("new sec", seconds1.toString())
        Log.d("old sec", seconds2.toString())



        return if (seconds1.toString() < seconds2.toString()) {
            true
        } else {
            false
        }
    }


    private fun extractTimeComponents(time: String): Pair<Int, Int> {
        val formattedTime = time.removePrefix("Timer: ")
        val components = formattedTime.split(":")
        val minutes = components[0].toIntOrNull() ?: 0
        val seconds = components.getOrNull(1)?.toIntOrNull() ?: 0

        return Pair(minutes, seconds)
    }

    fun startNewGame(view: View) {
        // Hide the congratulations message and New Game button
        val congratulationsTextView = findViewById<TextView>(R.id.congratulations)
        congratulationsTextView.visibility = View.GONE

        val newGameButton = findViewById<Button>(R.id.newGameButton)
        newGameButton.visibility = View.GONE

        // Reset the puzzle
        initializePuzzle()
        addTilesToGrid()
        elapsedTimeInSeconds = 0
        timerTextView.text = "Timer: 00:00"

        // Restart the timer
        startTimer()
    }





    fun resetPuzzle(view: android.view.View) {
        // Cancel the existing timer if it's running
        timer?.cancel()

        // Reset the puzzle
        initializePuzzle()
        addTilesToGrid() // Update the grid with the new puzzle

        // Reset timer-related variables
        elapsedTimeInSeconds = 0
        timerTextView.text = "Timer: 00:00" // Update the timer display

        // Start a new timer
        startTimer()
    }

    fun back(view: android.view.View) {
        val intent = Intent(this, ChooseSizeActivity::class.java)
        startActivity(intent)
    }


    private fun addTilesToGrid() {
        gridLayout.removeAllViews()

        val screenWidth = resources.displayMetrics.widthPixels
        //val tileWidth = screenWidth / puzzleSize
        val buttonMargin = 90 // Adjust the margin value as needed

        // Calculate tile width with margin subtracted
        val tileWidth = (screenWidth - (buttonMargin * (puzzleSize - 1))) / puzzleSize
        for (row in 0 until puzzleSize) {
            for (col in 0 until puzzleSize) {
                val tileIndex = row * puzzleSize + col
                val tileNumber = tiles[tileIndex]

                val button = Button(this)
                button.textSize = 24f
                button.width = tileWidth
                button.height = tileWidth

                if (tileNumber != 0) {
                    button.text = tileNumber.toString()
                } else {
                    button.text = ""
                    button.isEnabled = false
                }

                button.setOnClickListener {
                    moveTile(tileNumber)
                }

                val params = GridLayout.LayoutParams().apply {
                    rowSpec = GridLayout.spec(row)
                    columnSpec = GridLayout.spec(col)
                }
                gridLayout.addView(button, params)
            }
        }
    }

    private fun isWinning(): Boolean {
        return tiles == listOf(1, 2, 3, 0)
    }


    private fun startTimer() {
        timer = object : CountDownTimer(Long.MAX_VALUE, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                elapsedTimeInSeconds++
                val minutes = elapsedTimeInSeconds / 60
                val seconds = elapsedTimeInSeconds % 60
                timerTextView.text = String.format("Timer: %02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                // Handle timer finish (if needed)
            }
        }
        timer?.start()
    }

    override fun onDestroy() {
        timer?.cancel()
        super.onDestroy()
    }
}
