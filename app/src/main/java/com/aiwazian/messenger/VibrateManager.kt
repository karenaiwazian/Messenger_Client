package com.aiwazian.messenger

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.VibratorManager

class VibrateManager {

    fun vibrate(context: Context, pattern: LongArray? = null) {
        
        val pattern = pattern ?: longArrayOf(0, 100, 50, 100)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            val vibrator = vibratorManager.defaultVibrator
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
        } else {
            //val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            //vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
        }
    }
}