package com.leo.bgmimod.features

import android.content.Context
import android.graphics.PixelFormat
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import com.leo.bgmimod.game.GameMemoryReader
import kotlin.concurrent.thread

class ESP(private val context: Context) {
    private var isEnabled = false
    private val memoryReader = GameMemoryReader()
    private var espOverlayView: ESPOverlay? = null
    private var windowManager: WindowManager? = null
    private var layoutParams: WindowManager.LayoutParams? = null
    private val handler = Handler(Looper.getMainLooper())
    private var updateThread: Thread? = null
    private var isRunning = false

    companion object {
        private const val TAG = "ESP"
    }

    fun enable() {
        if (isEnabled) return
        isEnabled = true
        Log.d(TAG, "ESP feature enabled")

        if (memoryReader.initialize()) {
            startESPOverlay()
            startUpdateThread()
        } else {
            Log.e(TAG, "Failed to initialize memory reader")
            isEnabled = false
        }
    }

    fun disable() {
        isEnabled = false
        Log.d(TAG, "ESP feature disabled")
        stopESPOverlay()
        stopUpdateThread()
    }

    private fun startESPOverlay() {
        try {
            windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            espOverlayView = ESPOverlay(context)

            layoutParams = WindowManager.LayoutParams().apply {
                type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                format = PixelFormat.TRANSLUCENT
                flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                width = WindowManager.LayoutParams.MATCH_PARENT
                height = WindowManager.LayoutParams.MATCH_PARENT
                gravity = Gravity.TOP or Gravity.START
            }

            windowManager?.addView(espOverlayView, layoutParams)
            Log.d(TAG, "ESP overlay started")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start ESP overlay", e)
            isEnabled = false
        }
    }

    private fun stopESPOverlay() {
        try {
            if (espOverlayView != null && windowManager != null) {
                windowManager?.removeView(espOverlayView)
                espOverlayView = null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping ESP overlay", e)
        }
    }

    private fun startUpdateThread() {
        isRunning = true
        updateThread = thread {
            while (isRunning && isEnabled) {
                try {
                    // Get player positions from memory
                    val players = memoryReader.getPlayerPositions()
                    val items = generateMockItems(players)

                    // Update overlay on main thread
                    handler.post {
                        espOverlayView?.updateEnemies(players)
                        espOverlayView?.updateItems(items)
                    }

                    Thread.sleep(100) // Update 10 times per second
                } catch (e: Exception) {
                    Log.e(TAG, "Update thread error", e)
                }
            }
        }
    }

    private fun stopUpdateThread() {
        isRunning = false
        updateThread?.join()
    }

    private fun generateMockItems(players: List<GameMemoryReader.PlayerData>): List<ESPOverlay.ItemData> {
        val items = mutableListOf<ESPOverlay.ItemData>()

        // Generate random items around players for demo
        for ((index, player) in players.withIndex()) {
            // Add ammo crate
            items.add(
                ESPOverlay.ItemData(
                    x = player.x + 20,
                    y = player.y - 30,
                    z = player.z,
                    type = ESPOverlay.ItemType.AMMO,
                    distance = 50f
                )
            )

            // Add health item
            if (index % 2 == 0) {
                items.add(
                    ESPOverlay.ItemData(
                        x = player.x - 20,
                        y = player.y + 30,
                        z = player.z,
                        type = ESPOverlay.ItemType.HEALTH,
                        distance = 45f
                    )
                )
            }

            // Add weapon
            if (index % 3 == 0) {
                items.add(
                    ESPOverlay.ItemData(
                        x = player.x + 40,
                        y = player.y,
                        z = player.z,
                        type = ESPOverlay.ItemType.WEAPON,
                        distance = 55f
                    )
                )
            }

            // Add loot box
            if (index % 4 == 0) {
                items.add(
                    ESPOverlay.ItemData(
                        x = player.x - 40,
                        y = player.y - 20,
                        z = player.z,
                        type = ESPOverlay.ItemType.LOOT,
                        distance = 60f
                    )
                )
            }
        }

        return items
    }
}