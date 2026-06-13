package com.leo.bgmimod

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.leo.bgmimod.services.FloatingWindowService

class MainActivity : AppCompatActivity() {
    private lateinit var btnLaunchBGMI: Button
    private lateinit var switchFloatingWindow: Switch
    private lateinit var tvStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupListeners()
    }

    private fun initializeViews() {
        btnLaunchBGMI = findViewById(R.id.btn_launch_bgmi)
        switchFloatingWindow = findViewById(R.id.switch_floating_window)
        tvStatus = findViewById(R.id.tv_status)
    }

    private fun setupListeners() {
        // Launch BGMI button
        btnLaunchBGMI.setOnClickListener {
            launchBGMI()
        }

        // Floating window toggle
        switchFloatingWindow.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                startFloatingWindow()
            } else {
                stopFloatingWindow()
            }
        }
    }

    private fun launchBGMI() {
        try {
            // Try to launch BGMI
            val launchIntent = packageManager.getLaunchIntentForPackage("com.tencent.tmgp.bgmi")
            if (launchIntent != null) {
                startActivity(launchIntent)
                tvStatus.text = "BGMI Launched ✓"
                Toast.makeText(this, "BGMI Launched", Toast.LENGTH_SHORT).show()
            } else {
                // If BGMI not installed, try to open play store
                tvStatus.text = "BGMI Not Installed"
                Toast.makeText(this, "BGMI not installed. Opening Play Store...", Toast.LENGTH_SHORT).show()
                val uri = Uri.parse("https://play.google.com/store/apps/details?id=com.tencent.tmgp.bgmi")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
        } catch (e: Exception) {
            tvStatus.text = "Error launching BGMI"
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startFloatingWindow() {
        try {
            val serviceIntent = Intent(this, FloatingWindowService::class.java)
            startService(serviceIntent)
            tvStatus.text = "Floating Window Enabled ✓"
            Toast.makeText(this, "Floating Window Started", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            tvStatus.text = "Error starting floating window"
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopFloatingWindow() {
        try {
            val serviceIntent = Intent(this, FloatingWindowService::class.java)
            stopService(serviceIntent)
            tvStatus.text = "Floating Window Disabled"
            Toast.makeText(this, "Floating Window Stopped", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (switchFloatingWindow.isChecked) {
            stopFloatingWindow()
        }
    }
}