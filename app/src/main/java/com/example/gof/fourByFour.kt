package com.example.gof

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class fourByFour : AppCompatActivity() {
    private lateinit var mediaPlayer: MediaPlayer


    private lateinit var gridLayout: GridLayout
    private val puzzleSize = 4
    private lateinit var tiles: MutableList<Int>
    private var timer: CountDownTimer? = null
    private var elapsedTimeInSeconds: Long = 0
    private lateinit var timerTextView: TextView

    private lateinit var backButton: Button

    private lateinit var sharedPreferences: SharedPreferences
    private var bestCompletionTime: String = "N/A"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_four_by_four)

        backButton = findViewById(R.id.backButton)
        mediaPlayer = MediaPlayer.create(this, R.raw.tap_sound)

        gridLayout = findViewById(R.id.gridLayout)
        // Set background drawable for GridLayout boarder
        gridLayout.background = resources.getDrawable(R.drawable.grid_border, null)

        timerTextView = findViewById(R.id.timerTextView)

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Initialize the puzzle
        initializePuzzle()

        // Add tiles to the grid
        addTilesToGrid()

        // Start the timer
        startTimer()

        backButton.setOnClickListener {
            val intent = Intent(this, ChooseSizeActivity::class.java)
            startActivity(intent)
        }
    }


    private fun initializePuzzle() {
        // Generate a list of tiles (numbers 1 to 15)
        tiles = MutableList(puzzleSize * puzzleSize) { it + 1 }
        // Shuffle tiles (excluding the last tile which is the empty space)
        tiles.shuffle()
        // Ensure the shuffled puzzle is solvable
        while (!isSolvable(tiles)) {
            tiles.shuffle()
        }
    }

//    private fun initializePuzzle() {
//        // Generate a list of tiles (numbers 1 to 15)
//        tiles = MutableList(puzzleSize * puzzleSize) { it + 1 }
//
//        // Create a solvable arrangement by shuffling strategically
//        do {
//            // Start with a sorted arrangement
//            tiles = MutableList(puzzleSize * puzzleSize) { it + 1 }
//
//            // Shuffle tiles while maintaining solvability
//            repeat(100) { _ ->
//                val emptyIndex = tiles.indexOf(puzzleSize * puzzleSize) // Find the empty space (last tile)
//                val neighbors = mutableListOf<Int>()
//
//                // Collect valid neighbors of the empty space
//                if (emptyIndex % puzzleSize > 0) { // Left neighbor
//                    neighbors.add(emptyIndex - 1)
//                }
//                if (emptyIndex % puzzleSize < puzzleSize - 1) { // Right neighbor
//                    neighbors.add(emptyIndex + 1)
//                }
//                if (emptyIndex >= puzzleSize) { // Top neighbor
//                    neighbors.add(emptyIndex - puzzleSize)
//                }
//                if (emptyIndex < puzzleSize * (puzzleSize - 1)) { // Bottom neighbor
//                    neighbors.add(emptyIndex + puzzleSize)
//                }
//
//                // Randomly swap the empty space with one of its valid neighbors
//                val randomNeighbor = neighbors.random()
//                tiles[emptyIndex] = tiles[randomNeighbor]
//                tiles[randomNeighbor] = puzzleSize * puzzleSize // Place the empty space
//
//                // Ensure the shuffled puzzle is solvable
//                if (isSolvable(tiles)) {
//                    return
//                }
//            }
//        } while (true)
//    }


    private fun moveTile(position: Int) {
        playTappingSound()

        val emptyPosition = tiles.indexOf(puzzleSize * puzzleSize) // Find index of empty space (represented by puzzleSize * puzzleSize)

        val completionTime = timerTextView.text.toString() ?: "N/A"


        // Check if the clicked tile is adjacent to the empty space (up, down, left, or right)
        if (position - 1 == emptyPosition && position % puzzleSize != 0) {
            // Move tile left (swap positions in tiles list)
            swapTiles(position, emptyPosition)
        } else if (position + 1 == emptyPosition && emptyPosition % puzzleSize != 0) {
            // Move tile right (swap positions in tiles list)
            swapTiles(position, emptyPosition)
        } else if (position - puzzleSize == emptyPosition) {
            // Move tile up (swap positions in tiles list)
            swapTiles(position, emptyPosition)
        } else if (position + puzzleSize == emptyPosition) {
            // Move tile down (swap positions in tiles list)
            swapTiles(position, emptyPosition)
        }

        // Refresh the grid with updated tile positions
        addTilesToGrid()

        // Check if the player has won
        if (isWinning()) {
            Log.d("MainActivity", "User Guess: , Result:")
            timer?.cancel()
            val congratulationsTextView = findViewById<TextView>(R.id.congratulations)
            congratulationsTextView.visibility = View.VISIBLE
            congratulationsTextView.text = "Congratulations! 🎊 \n Your ${timerTextView.text}"

            // Show the New Game button
            val newGameButton = findViewById<Button>(R.id.newGameButton)
            newGameButton.visibility = View.VISIBLE

            var initialbestCompletionTime = sharedPreferences.getString("bestCompletionTime4", "N/A")
            Log.d("MainActivity", "intial best Time: $initialbestCompletionTime")


            val oldCompletionTime = sharedPreferences.getString("completionTime", "N/A") ?: "N/A"
            val FinalNewBestTime = sharedPreferences.getString("finalBestTime4", "N/A") ?: "N/A"
            Log.d("MainActivity", "Old Completion Time: $oldCompletionTime")


            with(sharedPreferences.edit()) {
                putString("completionTime4", timerTextView.text.toString())
                putInt("puzzleSize", puzzleSize)
                apply()
            }

            //intially add the first score as the best value
            if(initialbestCompletionTime=="N/A"){
                initialbestCompletionTime=completionTime
                with(sharedPreferences.edit()) {
                    putString("finalBestTime4", initialbestCompletionTime)
                    apply()
                }
            }
            Log.d("MainActivity", "intial 2 best Time: $initialbestCompletionTime")


            if (compareTimes(completionTime, FinalNewBestTime)) {
                val newBestTime = timerTextView.text.toString()
                Log.d("fourByFour", "New Best Completion Timeee: $newBestTime")

                with(sharedPreferences.edit()) {
                    putString("bestCompletionTime4", newBestTime)
                    apply()
                }
                bestCompletionTime = newBestTime
                Log.d("MainActivity", "New Best Completion Time: $bestCompletionTime")
            } else {
                //If want to add a new best time
//                val newBestTime = timerTextView.text.toString()
//                Log.d("fourByFour", "New Best Completion Timeee: $newBestTime")
//
//                with(sharedPreferences.edit()) {
//                    putString("bestCompletionTime4", newBestTime)
//                    apply()
//                }
                Log.d("MainActivity", "No new best completion time. ")
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



        return if (minutes1.toString() == minutes2.toString() && seconds1.toString() < seconds2.toString() || minutes1.toString() < minutes2.toString() && seconds1.toString() < seconds2.toString() ) {
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




    private fun swapTiles(position1: Int, position2: Int) {
        // Swap tile positions in the tiles list
        val temp = tiles[position1]
        tiles[position1] = tiles[position2]
        tiles[position2] = temp
    }

    private fun addTilesToGrid() {
        // Clear existing views from GridLayout
        gridLayout.removeAllViews()

        // Calculate tile width dynamically based on screen size
        val screenWidth = resources.displayMetrics.widthPixels
        //val tileWidth = screenWidth / puzzleSize
        val buttonMargin = 29 // Adjust the margin value as needed

        // Calculate tile width with margin subtracted
        val tileWidth = (screenWidth - (buttonMargin * (puzzleSize - 1))) / puzzleSize


        // Add buttons dynamically to the grid
        val totalTiles = puzzleSize * puzzleSize
        for (i in 0 until totalTiles) {
            val button = Button(this)

            if (tiles[i] != puzzleSize * puzzleSize) {
                // Set button text for tiles 1 to 15
                button.text = tiles[i].toString()
            } else {
                // Set button text for empty space (last tile)
                button.text = ""
                button.isEnabled = false // Disable empty space button
            }

            button.textSize = 24f // Set text size for button
            button.width = tileWidth
            button.height = tileWidth

            button.setOnClickListener {
                // Handle tile click (move tile logic)
                moveTile(i)
            }

            gridLayout.addView(button)

        }
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
        timer?.cancel() // Cancel the timer when the activity is destroyed
        super.onDestroy()
    }

    private fun isWinning(): Boolean {
        val winningConfiguration = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9,10,11,12,13,14,15,16) // Winning configuration: [1, 2, 3, ..., 8, 0]
        val currentTiles = tiles.toList()
        Log.d("MainActivity", "Current Tiles: $currentTiles")
        // Check if the current tiles match the winning configuration
        val isWinning = currentTiles == winningConfiguration

        return isWinning
    }

    private fun isSolvable(tiles: List<Int>): Boolean {
        // Implement solvability check here (if needed)
        return true
    }
}
