package com.khamse.pomodoro_clock_android

import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.media.RingtoneManager
import android.net.Uri
import android.os.CountDownTimer
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog


class ButtonClickListeners(private val context: Context) : View.OnClickListener {
    private var countDownTimer: CountDownTimer? = null
    private var remainingTime: Long = MainActivity.workSessionTime


    override fun onClick(v: View?) {
        when(v?.id){
            R.id.startStopButton -> startStopFun()
            R.id.endActivityButton -> endAndStoreActivity()
            R.id.resetClockButton -> resetClock()
        }
    }

    private fun resetClock(){
        updateStatus("Not Working")
        Toast.makeText(context,"Resting Clock", Toast.LENGTH_LONG).show()
        MainActivity.binding.timerTextView.text = String.format("00:%02d:00", MainActivity.workSessionTime)
        MainActivity.binding.resetClockButton.visibility = View.INVISIBLE
        remainingTime = MainActivity.workSessionTime
//        MainActivity.binding.pauseClockButton.visibility = View.INVISIBLE
    }

    private fun endAndStoreActivity() {
        Toast.makeText(context, "Ending Activity", Toast.LENGTH_SHORT).show()
        TODO("Not yet implemented")
    }

    private fun startStopFun(fromFinish: Boolean = false) {

        if (!fromFinish) {
            if (MainActivity.binding.startStopButton.isChecked) {
                updateStatus("Working")
                MainActivity.binding.startStopButton.text = "Stop"
                MainActivity.binding.resetClockButton.visibility = View.INVISIBLE


                val duration = if (remainingTime != MainActivity.workSessionTime) {
                    remainingTime
                } else {
                    MainActivity.workSessionTime * 60 * 1000
                }

                countDownTimer = object : CountDownTimer(duration, 1000) {

                    override fun onTick(millisUntilFinished: Long) {
                        remainingTime = millisUntilFinished
                        val hours = remainingTime / (1000 * 60 * 60)
                        val minutes = (remainingTime / (1000 * 60)) % 60
                        val seconds = (remainingTime / 1000) % 60

                        MainActivity.binding.timerTextView.text =
                            String.format("%02d:%02d:%02d", hours, minutes, seconds)
                    }

                    override fun onFinish() {
                        MainActivity.binding.timerTextView.text = "00:00:00"
                        val alarmSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                        val ringtone = RingtoneManager.getRingtone(context, alarmSound)
                        ringtone?.isLooping = true
                        ringtone.play()
                        val alertDialog = AlertDialog.Builder(context)

                        alertDialog.apply {
                            setIcon(R.drawable.baseline_celebration_24)
                            setTitle("Break Time")
                            setMessage("Great work time to reward yourself with short break!")
                            setPositiveButton("Start Break") { _: DialogInterface?, _: Int ->
                                Toast.makeText(context, "Brake Started", Toast.LENGTH_SHORT).show()
                                remainingTime = MainActivity.breakSessionTime * 60 * 1000
                                ringtone.stop()
                                startStopFun(fromFinish = true)
                            }
                            setNegativeButton("Skip Break") { _, _ ->
                                Toast.makeText(
                                    context,
                                    "You are skipping the break",
                                    Toast.LENGTH_SHORT
                                ).show()
                                remainingTime = MainActivity.workSessionTime * 60 * 1000
                                ringtone.stop()
                                startStopFun(fromFinish = false)
                            }
                        }.create().show()

                    }
                }.start()
            } else {
                updateStatus("Clock Paused")
                MainActivity.binding.startStopButton.text = "Start"
                MainActivity.binding.resetClockButton.visibility = View.VISIBLE
                countDownTimer?.cancel()
            }
        }else{ //Break timer
            updateStatus("It is Break time")
            if (MainActivity.binding.startStopButton.isChecked) {
                MainActivity.binding.startStopButton.text = "Can't Stop Break"
                MainActivity.binding.startStopButton.isClickable = false

                val duration = MainActivity.breakSessionTime * 60 * 1000

                countDownTimer = object : CountDownTimer(duration, 1000) {

                    override fun onTick(millisUntilFinished: Long) {
                        remainingTime = millisUntilFinished
                        val hours = remainingTime / (1000 * 60 * 60)
                        val minutes = (remainingTime / (1000 * 60)) % 60
                        val seconds = (remainingTime / 1000) % 60

                        MainActivity.binding.timerTextView.text =
                            String.format("%02d:%02d:%02d", hours, minutes, seconds)
                    }

                    override fun onFinish() {
                        MainActivity.binding.timerTextView.text = "00:00:00"
                        MainActivity.binding.startStopButton.isClickable = true
                        val alarmSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                        val ringtone = RingtoneManager.getRingtone(context, alarmSound)
                        ringtone?.isLooping = true
                        ringtone.play()
                        val alertDialog = AlertDialog.Builder(context)

                        alertDialog.apply {
                            setIcon(R.drawable.baseline_celebration_24)
                            setTitle("Work Time")
                            setMessage("Lets get back on work!")
                            setPositiveButton("Start Work") { _: DialogInterface?, _: Int ->
                                Toast.makeText(context, "You can do it!", Toast.LENGTH_SHORT).show()
                                remainingTime = MainActivity.workSessionTime
                                ringtone.stop()
                                startStopFun(fromFinish = false)
                            }
                            setNegativeButton("Done for Today") { _, _ ->
                                Toast.makeText(
                                    context,
                                    "Great work! Next time let achieve more",
                                    Toast.LENGTH_SHORT
                                ).show()
                                remainingTime = MainActivity.workSessionTime * 60 * 1000
                                ringtone.stop()
                                endAndStoreActivity()
                            }
                        }.create().show()

                    }
                }.start()
            } else {
                MainActivity.binding.startStopButton.text = "Start"
                countDownTimer?.cancel()
            }

        }

    }


    private fun updateStatus(userStatus: String){
        val builder = SpannableStringBuilder()
        builder.append("Status: ")
        builder.setSpan(StyleSpan(Typeface.BOLD),0, builder.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder.append(userStatus)

        MainActivity.binding.statusTextView.setText(builder)
    }
}