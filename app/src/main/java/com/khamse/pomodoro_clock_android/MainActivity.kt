package com.khamse.pomodoro_clock_android

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.khamse.pomodoro_clock_android.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object{
        lateinit var binding:ActivityMainBinding
        var workSessionTime: Long = 1
        var breakSessionTime: Long = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val buttonClickListeners = ButtonClickListeners(this)

        binding.startStopButton.setOnClickListener(buttonClickListeners)
        binding.endActivityButton.setOnClickListener(buttonClickListeners)
        binding.resetClockButton.setOnClickListener(buttonClickListeners)

    }
}