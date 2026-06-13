package com.leo.bgmimod.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.leo.bgmimod.features.ESP
import com.leo.bgmimod.features.Aimbot

class ModLoaderService : Service() {
    private var esp: ESP? = null
    private var aimbot: Aimbot? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("ModLoaderService", "Service started")
        initializeMods()
        return START_STICKY
    }

    private fun initializeMods() {
        try {
            esp = ESP(this)
            aimbot = Aimbot(this)
            Log.d("ModLoaderService", "ESP and Aimbot initialized")
        } catch (e: Exception) {
            Log.e("ModLoaderService", "Failed to initialize mods", e)
        }
    }

    fun enableESP() {
        esp?.enable()
        Log.d("ModLoaderService", "ESP enabled")
    }

    fun disableESP() {
        esp?.disable()
        Log.d("ModLoaderService", "ESP disabled")
    }

    fun enableAimbot() {
        aimbot?.enable()
        Log.d("ModLoaderService", "Aimbot enabled")
    }

    fun disableAimbot() {
        aimbot?.disable()
        Log.d("ModLoaderService", "Aimbot disabled")
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        esp?.disable()
        aimbot?.disable()
        Log.d("ModLoaderService", "Service destroyed")
    }
}