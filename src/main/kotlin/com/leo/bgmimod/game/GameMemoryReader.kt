package com.leo.bgmimod.game

import android.util.Log
import com.leo.bgmimod.utils.MemoryUtils
import com.leo.bgmimod.utils.ProcessUtils
kotlin.concurrent.thread

class GameMemoryReader {
    private var bgmiPid: Int? = null
    private var isRunning = false
    private var playerBaseAddress: Long = 0
    private var cameraBaseAddress: Long = 0

    companion object {
        private const val TAG = "GameMemoryReader"
        private const val BGMI_PACKAGE = "com.tencent.tmgp.bgmi"
    }

    fun initialize(): Boolean {
        return try {
            bgmiPid = ProcessUtils.getProcessPid(BGMI_PACKAGE)
            if (bgmiPid == null) {
                Log.e(TAG, "BGMI process not found")
                return false
            }

            Log.d(TAG, "BGMI PID: $bgmiPid")
            findGameAddresses()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Initialization failed", e)
            false
        }
    }

    private fun findGameAddresses() {
        try {
            val pid = bgmiPid ?: return
            
            // Try to find player data structure
            playerBaseAddress = ProcessUtils.getLibraryBase(pid, "libil2cpp.so")
            Log.d(TAG, "Player base address: 0x${playerBaseAddress.toString(16)}")
            
            // Try to find camera data structure
            cameraBaseAddress = ProcessUtils.getLibraryBase(pid, "libunity.so")
            Log.d(TAG, "Camera base address: 0x${cameraBaseAddress.toString(16)}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to find game addresses", e)
        }
    }

    fun getPlayerPositions(): List<PlayerData> {
        return try {
            val pid = bgmiPid ?: return emptyList()
            val players = mutableListOf<PlayerData>()

            // Scan memory for player objects
            // This is a simplified example - actual implementation would require
            // deep knowledge of BGMI's memory layout
            
            for (i in 0 until 100) { // Assume max 100 players
                val offset = playerBaseAddress + (i * 0x100) // 0x100 bytes per player struct
                val data = MemoryUtils.readMemory(pid, offset, 0x40) ?: continue
                
                if (data.size >= 0x40) {
                    val x = MemoryUtils.readFloat(data, 0x00)
                    val y = MemoryUtils.readFloat(data, 0x04)
                    val z = MemoryUtils.readFloat(data, 0x08)
                    val health = MemoryUtils.readInt(data, 0x20)
                    val isAlive = health > 0

                    if (isAlive) {
                        players.add(
                            PlayerData(
                                x = x,
                                y = y,
                                z = z,
                                health = health,
                                index = i
                            )
                        )
                    }
                }
            }

            players
        } catch (e: Exception) {
            Log.e(TAG, "Failed to read player positions", e)
            emptyList()
        }
    }

    fun getPlayerCamera(): CameraData? {
        return try {
            val pid = bgmiPid ?: return null
            val data = MemoryUtils.readMemory(pid, cameraBaseAddress, 0x30) ?: return null

            CameraData(
                yaw = MemoryUtils.readFloat(data, 0x00),
                pitch = MemoryUtils.readFloat(data, 0x04),
                roll = MemoryUtils.readFloat(data, 0x08),
                x = MemoryUtils.readFloat(data, 0x10),
                y = MemoryUtils.readFloat(data, 0x14),
                z = MemoryUtils.readFloat(data, 0x18)
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to read camera data", e)
            null
        }
    }

    fun updateCamera(yaw: Float, pitch: Float): Boolean {
        return try {
            val pid = bgmiPid ?: return false
            var success = true

            success = success && MemoryUtils.writeMemory(
                pid,
                cameraBaseAddress,
                MemoryUtils.floatToBytes(yaw)
            )
            success = success && MemoryUtils.writeMemory(
                pid,
                cameraBaseAddress + 4,
                MemoryUtils.floatToBytes(pitch)
            )

            success
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update camera", e)
            false
        }
    }

    fun startMonitoring() {
        if (isRunning) return
        isRunning = true

        thread {
            while (isRunning) {
                try {
                    val pid = bgmiPid
                    if (pid == null || !ProcessUtils.isProcessRunning(pid)) {
                        Log.w(TAG, "BGMI process stopped")
                        isRunning = false
                        break
                    }
                    Thread.sleep(100)
                } catch (e: Exception) {
                    Log.e(TAG, "Monitoring error", e)
                }
            }
        }
    }

    fun stopMonitoring() {
        isRunning = false
    }

    data class PlayerData(
        val x: Float,
        val y: Float,
        val z: Float,
        val health: Int,
        val index: Int
    )

    data class CameraData(
        val yaw: Float,
        val pitch: Float,
        val roll: Float,
        val x: Float,
        val y: Float,
        val z: Float
    )
}