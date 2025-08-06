package com.linari

import android.content.Context
import android.util.Log
import android.widget.Toast
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class CrashHandler(private val context: Context) : Thread.UncaughtExceptionHandler {

    private val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        // Log or save the crash info here
        Log.e("CrashHandler", "App crashed!", throwable)

        // Optionally save to a file or send to server
        saveCrashToFile(throwable)

        // Call the default handler (important to terminate app)
        defaultHandler?.uncaughtException(thread, throwable)
    }

    private fun saveCrashToFile(throwable: Throwable) {
        try {
            val crashInfo = Log.getStackTraceString(throwable)
            val time = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault()).format(Date())
            val fileName = "crash_log_$time.txt"

            // Choose directory
            val dir = File(context.getExternalFilesDir(null), "CrashLogs")
            if (!dir.exists()) dir.mkdirs()

            val file = File(dir, fileName)
            file.writeText(crashInfo)

            Log.d("CrashHandler", "Crash saved to ${file.absolutePath}")
            Toast.makeText(context,"Crash saved to ${file.absolutePath}",Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Log.e("CrashHandler", "Failed to write crash log", e)
            Toast.makeText(context,"Failed to write crash log",Toast.LENGTH_LONG).show()
        }
    }
}
