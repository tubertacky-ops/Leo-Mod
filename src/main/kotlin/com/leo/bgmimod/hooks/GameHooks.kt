package com.leo.bgmimod.hooks

import android.util.Log
import com.leo.bgmimod.utils.ReflectionUtils
import java.lang.reflect.Method

class GameHooks {
    companion object {
        private const val TAG = "GameHooks"
        private var originalMethods = mutableMapOf<String, Method>()

        fun hookRenderingPipeline(): Boolean {
            return try {
                // Try to hook common BGMI rendering classes
                val renderClasses = listOf(
                    "com.unity3d.player.UnityPlayer",
                    "com.tencent.tmgp.bgmi.rendering.Renderer",
                    "com.tencent.bgmi.game.render.GameRender"
                )

                for (className in renderClasses) {
                    val clazz = ReflectionUtils.findClass(className) ?: continue
                    val renderMethod = ReflectionUtils.findMethod(clazz, "onDrawFrame") ?: continue
                    
                    Log.d(TAG, "Found render method in $className")
                    originalMethods[className] = renderMethod
                }

                originalMethods.isNotEmpty()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to hook rendering pipeline", e)
                false
            }
        }

        fun hookCameraSystem(): Boolean {
            return try {
                val cameraClasses = listOf(
                    "com.unity3d.player.UnityPlayer",
                    "com.tencent.tmgp.bgmi.camera.CameraController",
                    "com.tencent.bgmi.game.camera.GameCamera"
                )

                for (className in cameraClasses) {
                    val clazz = ReflectionUtils.findClass(className) ?: continue
                    val updateMethod = ReflectionUtils.findMethod(clazz, "updateRotation") ?: continue
                    
                    Log.d(TAG, "Found camera method in $className")
                    originalMethods["camera_$className"] = updateMethod
                }

                true
            } catch (e: Exception) {
                Log.e(TAG, "Failed to hook camera system", e)
                false
            }
        }

        fun hookInputSystem(): Boolean {
            return try {
                val inputClasses = listOf(
                    "com.unity3d.player.UnityPlayer",
                    "com.tencent.tmgp.bgmi.input.InputHandler",
                    "com.tencent.bgmi.game.input.GameInput"
                )

                for (className in inputClasses) {
                    val clazz = ReflectionUtils.findClass(className) ?: continue
                    val touchMethod = ReflectionUtils.findMethod(clazz, "onTouchEvent") ?: continue
                    
                    Log.d(TAG, "Found input method in $className")
                    originalMethods["input_$className"] = touchMethod
                }

                true
            } catch (e: Exception) {
                Log.e(TAG, "Failed to hook input system", e)
                false
            }
        }

        fun hookPlayerDataAccess(): Boolean {
            return try {
                val playerClasses = listOf(
                    "com.tencent.tmgp.bgmi.player.Player",
                    "com.tencent.bgmi.game.entity.Player",
                    "com.tencent.bgmi.game.player.PlayerInfo"
                )

                for (className in playerClasses) {
                    val clazz = ReflectionUtils.findClass(className) ?: continue
                    Log.d(TAG, "Found player class: $className")
                    originalMethods[className] = clazz.declaredMethods.firstOrNull() ?: continue
                }

                true
            } catch (e: Exception) {
                Log.e(TAG, "Failed to hook player data access", e)
                false
            }
        }
    }
}