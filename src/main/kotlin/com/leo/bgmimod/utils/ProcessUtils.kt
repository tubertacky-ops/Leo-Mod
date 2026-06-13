package com.leo.bgmimod.utils

import android.os.Debug
import android.util.Log
import java.io.File

class ProcessUtils {
    companion object {
        private const val TAG = "ProcessUtils"

        fun getProcessPid(processName: String): Int? {
            return try {
                val line = Runtime.getRuntime()
                    .exec(arrayOf("pidof", processName))
                    .inputStream.bufferedReader().use { it.readText().trim() }
                line.split(" ")[0].toIntOrNull().also {
                    Log.d(TAG, "Found PID for $processName: $it")
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to get PID for $processName", e)
                null
            }
        }

        fun getMemoryMaps(pid: Int): List<String> {
            return try {
                val mapsFile = File("/proc/$pid/maps")
                if (mapsFile.exists()) {
                    mapsFile.readLines()
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to read memory maps", e)
                emptyList()
            }
        }

        fun getLibraryBase(pid: Int, libName: String): Long {
            val maps = getMemoryMaps(pid)
            for (map in maps) {
                if (map.contains(libName)) {
                    val parts = map.split("-")
                    if (parts.isNotEmpty()) {
                        return parts[0].toLongOrNull(16) ?: 0L
                    }
                }
            }
            return 0L
        }

        fun isProcessRunning(pid: Int): Boolean {
            return try {
                val process = File("/proc/$pid")
                process.exists()
            } catch (e: Exception) {
                false
            }
        }

        fun getNativeHeapSize(): Long {
            return Debug.getNativeHeap().totalSize
        }
    }
}