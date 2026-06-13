package com.leo.bgmimod.services

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Switch
import com.leo.bgmimod.R

class FloatingWindowService : Service() {
    private var windowManager: WindowManager? = null
    private var floatingView: View? = null
    private var params: WindowManager.LayoutParams? = null
    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private var aimbotEnabled = false
    private var espEnabled = false

    companion object {
        private const val TAG = "FloatingWindowService"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service started")
        createFloatingWindow()
        return START_STICKY
    }

    private fun createFloatingWindow() {
        try {
            windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

            // Inflate the floating window layout
            floatingView = LayoutInflater.from(this).inflate(R.layout.floating_window, null)

            // Set up window manager layout params
            params = WindowManager.LayoutParams().apply {
                type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                format = PixelFormat.TRANSLUCENT
                flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                width = 250
                height = WindowManager.LayoutParams.WRAP_CONTENT
                gravity = Gravity.TOP or Gravity.START
                x = 0
                y = 100
            }

            // Add the view to window manager
            windowManager?.addView(floatingView, params)

            // Setup switches
            val switchAimbot = floatingView?.findViewById<Switch>(R.id.switch_aimbot)
            val switchESP = floatingView?.findViewById<Switch>(R.id.switch_esp)

            switchAimbot?.setOnCheckedChangeListener { _, isChecked ->
                aimbotEnabled = isChecked
                Log.d(TAG, "Aimbot: $isChecked")
            }

            switchESP?.setOnCheckedChangeListener { _, isChecked ->
                espEnabled = isChecked
                Log.d(TAG, "ESP: $isChecked")
            }

            // Make the window draggable
            floatingView?.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = params!!.x
                        initialY = params!!.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val deltaX = (event.rawX - initialTouchX).toInt()
                        val deltaY = (event.rawY - initialTouchY).toInt()
                        params!!.x = initialX + deltaX
                        params!!.y = initialY + deltaY
                        windowManager?.updateViewLayout(floatingView, params)
                    }
                }
                false
            }

            Log.d(TAG, "Floating window created successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error creating floating window", e)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        if (floatingView != null) {
            windowManager?.removeView(floatingView)
            floatingView = null
        }
        Log.d(TAG, "Service destroyed")
    }
}