package com.leo.bgmimod.utils

import android.util.Log
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder

class MemoryUtils {
    companion object {
        private const val TAG = "MemoryUtils"

        fun readMemory(pid: Int, address: Long, size: Int): ByteArray? {
            return try {
                val memFile = File("/proc/$pid/mem")
                if (!memFile.exists()) {
                    Log.e(TAG, "Memory file not found for PID: $pid")
                    return null
                }

                memFile.inputStream().use { stream ->
                    stream.skip(address)
                    val buffer = ByteArray(size)
                    stream.read(buffer)
                    buffer
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to read memory", e)
                null
            }
        }

        fun writeMemory(pid: Int, address: Long, data: ByteArray): Boolean {
            return try {
                val memFile = File("/proc/$pid/mem")
                if (!memFile.exists()) {
                    Log.e(TAG, "Memory file not found for PID: $pid")
                    return false
                }

                memFile.outputStream().use { stream ->
                    stream.write(data)
                    stream.flush()
                }
                true
            } catch (e: Exception) {
                Log.e(TAG, "Failed to write memory", e)
                false
            }
        }

        fun readFloat(data: ByteArray, offset: Int): Float {
            return ByteBuffer.wrap(data, offset, 4)
                .order(ByteOrder.LITTLE_ENDIAN)
                .float
        }

        fun readInt(data: ByteArray, offset: Int): Int {
            return ByteBuffer.wrap(data, offset, 4)
                .order(ByteOrder.LITTLE_ENDIAN)
                .int
        }

        fun floatToBytes(value: Float): ByteArray {
            val buffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
            buffer.putFloat(value)
            return buffer.array()
        }

        fun intToBytes(value: Int): ByteArray {
            val buffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
            buffer.putInt(value)
            return buffer.array()
        }
    }
}