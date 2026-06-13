package com.leo.bgmimod.features

import android.content.Context
import android.util.Log
import com.leo.bgmimod.game.GameMemoryReader
kotlin.concurrent.thread
import kotlin.math.*

class Aimbot(private val context: Context) {
    private var isEnabled = false
    private var targetPlayer: String? = null
    private var aimSpeed = 0.5f // 0.0 - 1.0, lower = smoother
    private var maxDistance = 200f // Maximum distance to target in meters
    private var aimAssistOnly = true // If true, only assists aiming
    private val memoryReader = GameMemoryReader()
    private var currentCamera: GameMemoryReader.CameraData? = null
    private var allPlayers = mutableListOf<GameMemoryReader.PlayerData>()

    companion object {
        private const val TAG = "Aimbot"
    }

    fun enable() {
        if (isEnabled) return
        isEnabled = true
        Log.d(TAG, "Aimbot feature enabled")
        
        if (memoryReader.initialize()) {
            startAimbotLoop()
        } else {
            Log.e(TAG, "Failed to initialize memory reader")
        }
    }

    fun disable() {
        isEnabled = false
        Log.d(TAG, "Aimbot feature disabled")
    }

    fun setTargetPlayer(name: String) {
        targetPlayer = name
        Log.d(TAG, "Target set to: $name")
    }

    fun setAimSpeed(speed: Float) {
        aimSpeed = speed.coerceIn(0f, 1f)
        Log.d(TAG, "Aim speed set to: $aimSpeed")
    }

    fun setMaxDistance(distance: Float) {
        maxDistance = distance
        Log.d(TAG, "Max distance set to: $distance")
    }

    fun setAimAssistOnly(value: Boolean) {
        aimAssistOnly = value
        Log.d(TAG, "Aim assist only: $value")
    }

    private fun startAimbotLoop() {
        thread {
            memoryReader.startMonitoring()
            
            while (isEnabled) {
                try {
                    updateAim()
                    Thread.sleep(16) // ~60 FPS
                } catch (e: Exception) {
                    Log.e(TAG, "Aimbot loop error", e)
                }
            }
        }
    }

    private fun updateAim() {
        // Get current players
        allPlayers.clear()
        allPlayers.addAll(memoryReader.getPlayerPositions())
        
        // Get current camera position
        currentCamera = memoryReader.getPlayerCamera() ?: return
        
        // Get nearest enemy within range
        val nearestEnemy = getNearestEnemy() ?: return
        
        // Calculate angles to target
        val angles = calculateAngles(nearestEnemy)
        
        // Apply aim smoothing
        val smoothedAngles = applySmoothening(angles)
        
        // Update camera/aim position
        applyAim(smoothedAngles)
    }

    private fun getNearestEnemy(): GameMemoryReader.PlayerData? {
        if (allPlayers.isEmpty()) return null
        
        val camera = currentCamera ?: return null
        var nearest: GameMemoryReader.PlayerData? = null
        var nearestDistance = maxDistance

        for (player in allPlayers) {
            val dx = player.x - camera.x
            val dy = player.y - camera.y
            val dz = player.z - camera.z
            val distance = sqrt(dx * dx + dy * dy + dz * dz)

            if (distance < nearestDistance && distance > 0.1f) {
                nearestDistance = distance
                nearest = player
            }
        }

        return nearest
    }

    private fun calculateAngles(target: GameMemoryReader.PlayerData): AimAngles {
        val camera = currentCamera ?: return AimAngles(0f, 0f)
        
        val dx = target.x - camera.x
        val dy = target.y - camera.y
        val dz = target.z - camera.z

        val distance = sqrt(dx * dx + dy * dy + dz * dz)
        
        if (distance > maxDistance || distance < 0.1f) {
            return AimAngles(camera.yaw, camera.pitch)
        }

        val yaw = atan2(dx, dz).toDegrees()
        val pitch = atan2(-dy, sqrt(dx * dx + dz * dz)).toDegrees()

        return AimAngles(yaw, pitch)
    }

    private fun applySmoothening(angles: AimAngles): AimAngles {
        val camera = currentCamera ?: return angles
        
        // Lerp between current aim and target aim based on aimSpeed
        val smoothYaw = lerp(camera.yaw, angles.yaw, aimSpeed)
        val smoothPitch = lerp(camera.pitch, angles.pitch, aimSpeed)

        return AimAngles(smoothYaw, smoothPitch)
    }

    private fun applyAim(angles: AimAngles) {
        try {
            memoryReader.updateCamera(angles.yaw, angles.pitch)
            Log.d(TAG, "Applied aim: yaw=${angles.yaw}, pitch=${angles.pitch}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to apply aim", e)
        }
    }

    private fun lerp(a: Float, b: Float, t: Float): Float {
        return a + (b - a) * t
    }

    private fun Float.toDegrees(): Float = this * 180f / Math.PI.toFloat()

    data class AimAngles(
        val yaw: Float,
        val pitch: Float
    )
}