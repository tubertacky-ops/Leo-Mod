package com.leo.bgmimod.features

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import com.leo.bgmimod.game.GameMemoryReader
kotlin.concurrent.thread

class ESP(private val context: Context) {
    private var isEnabled = false
    private val paint = Paint().apply {
        color = 0xFF00FF00.toInt()
        style = Paint.Style.STROKE
        strokeWidth = 2f
        isAntiAlias = true
    }
    private val textPaint = Paint().apply {
        color = 0xFF00FF00.toInt()
        textSize = 12f
        isAntiAlias = true
    }
    private var espOverlay: ESPOverlayView? = null
    private val memoryReader = GameMemoryReader()
    private var detectedPlayers = mutableListOf<PlayerInfo>()

    companion object {
        private const val TAG = "ESP"
    }

    fun enable() {
        if (isEnabled) return
        isEnabled = true
        Log.d(TAG, "ESP feature enabled")
        
        if (memoryReader.initialize()) {
            startESPOverlay()
        } else {
            Log.e(TAG, "Failed to initialize memory reader")
        }
    }

    fun disable() {
        isEnabled = false
        Log.d(TAG, "ESP feature disabled")
        stopESPOverlay()
    }

    private fun startESPOverlay() {
        try {
            memoryReader.startMonitoring()
            
            thread {
                while (isEnabled) {
                    try {
                        updatePlayerPositions()
                        Thread.sleep(16) // ~60 FPS
                    } catch (e: Exception) {
                        Log.e(TAG, "ESP update error", e)
                    }
                }
            }
            
            Log.d(TAG, "ESP overlay started")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start ESP overlay", e)
        }
    }

    private fun stopESPOverlay() {
        memoryReader.stopMonitoring()
        detectedPlayers.clear()
    }

    private fun updatePlayerPositions() {
        try {
            val players = memoryReader.getPlayerPositions()
            detectedPlayers.clear()
            
            for (player in players) {
                val distance = calculateDistance(player)
                detectedPlayers.add(
                    PlayerInfo(
                        name = "Player_${player.index}",
                        x = player.x,
                        y = player.y,
                        z = player.z,
                        distance = distance,
                        health = player.health
                    )
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update player positions", e)
        }
    }

    private fun calculateDistance(player: GameMemoryReader.PlayerData): Float {
        return Math.sqrt(
            (player.x * player.x + player.y * player.y + player.z * player.z).toDouble()
        ).toFloat()
    }

    fun getDetectedPlayers(): List<PlayerInfo> = detectedPlayers.toList()

    data class PlayerInfo(
        val name: String,
        val x: Float,
        val y: Float,
        val z: Float,
        val distance: Float,
        val health: Int
    )

    inner class ESPOverlayView(context: Context) : View(context) {
        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            
            for (player in detectedPlayers) {
                // Draw bounding box
                val rect = Rect(
                    player.x.toInt() - 20,
                    player.y.toInt() - 30,
                    player.x.toInt() + 20,
                    player.y.toInt() + 30
                )
                canvas.drawRect(rect, paint)
                
                // Draw player info
                canvas.drawText(
                    "${player.name} - ${player.health}HP",
                    player.x,
                    player.y - 35f,
                    textPaint
                )
            }
        }
    }
}