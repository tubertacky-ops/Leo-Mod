package com.leo.bgmimod.features

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import com.leo.bgmimod.game.GameMemoryReader
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class ESPOverlay(context: Context) : View(context) {
    private val enemyPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 2f
        isAntiAlias = true
    }

    private val antennaPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 1.5f
        isAntiAlias = true
    }

    private val itemPaint = Paint().apply {
        color = Color.YELLOW
        style = Paint.Style.STROKE
        strokeWidth = 2f
        isAntiAlias = true
    }

    private val textPaint = Paint().apply {
        color = Color.GREEN
        textSize = 12f
        isAntiAlias = true
    }

    private val distancePaint = Paint().apply {
        color = Color.WHITE
        textSize = 10f
        isAntiAlias = true
    }

    private val healthPaint = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private var enemies = mutableListOf<EnemyData>()
    private var items = mutableListOf<ItemData>()
    private val handler = Handler(Looper.getMainLooper())

    companion object {
        private const val TAG = "ESPOverlay"
        private const val SCREEN_WIDTH = 1080f
        private const val SCREEN_HEIGHT = 1920f
        private const val MAX_DRAW_DISTANCE = 500f
    }

    fun updateEnemies(enemyList: List<GameMemoryReader.PlayerData>) {
        enemies.clear()
        for (enemy in enemyList) {
            val distance = sqrt(enemy.x * enemy.x + enemy.y * enemy.y + enemy.z * enemy.z)
            if (distance < MAX_DRAW_DISTANCE && distance > 1) {
                enemies.add(
                    EnemyData(
                        x = enemy.x,
                        y = enemy.y,
                        z = enemy.z,
                        distance = distance,
                        health = enemy.health,
                        id = enemy.index
                    )
                )
            }
        }
        invalidate()
    }

    fun updateItems(itemList: List<ItemData>) {
        items.clear()
        items.addAll(itemList)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw enemies
        for (enemy in enemies) {
            val screenPos = worldToScreen(enemy.x, enemy.y)
            drawEnemy(canvas, screenPos.first, screenPos.second, enemy)
        }

        // Draw items
        for (item in items) {
            val screenPos = worldToScreen(item.x, item.y)
            drawItem(canvas, screenPos.first, screenPos.second, item)
        }
    }

    private fun drawEnemy(canvas: Canvas, x: Float, y: Float, enemy: EnemyData) {
        // Draw bounding box
        val boxSize = 30f
        canvas.drawRect(x - boxSize, y - boxSize, x + boxSize, y + boxSize, enemyPaint)

        // Draw antenna
        drawAntenna(canvas, x, y, 50f)

        // Draw health bar
        drawHealthBar(canvas, x, y - boxSize - 15f, enemy.health)

        // Draw enemy info
        canvas.drawText(
            "Enemy #${enemy.id}",
            x - 25f,
            y - boxSize - 40f,
            textPaint
        )

        // Draw distance
        val distanceText = String.format("%.1f m", enemy.distance)
        canvas.drawText(
            distanceText,
            x - 15f,
            y + boxSize + 20f,
            distancePaint
        )
    }

    private fun drawAntenna(canvas: Canvas, x: Float, y: Float, height: Float) {
        // Draw vertical antenna pole
        canvas.drawLine(x, y - 30f, x, y - 30f - height, antennaPaint)

        // Draw antenna waves
        val waveCount = 3
        val waveRadius = 15f
        for (i in 1..waveCount) {
            val radius = waveRadius * i
            canvas.drawCircle(x, y - 30f - height, radius, antennaPaint)
        }

        // Draw antenna tip
        canvas.drawCircle(x, y - 30f - height, 4f, antennaPaint)
    }

    private fun drawHealthBar(canvas: Canvas, x: Float, y: Float, health: Int) {
        val maxHealth = 100
        val barWidth = 50f
        val barHeight = 4f
        val healthPercent = (health.coerceIn(0, maxHealth).toFloat() / maxHealth)

        // Background (red)
        canvas.drawRect(
            x - barWidth / 2,
            y,
            x + barWidth / 2,
            y + barHeight,
            Paint().apply { color = Color.RED }
        )

        // Health (green)
        canvas.drawRect(
            x - barWidth / 2,
            y,
            x - barWidth / 2 + (barWidth * healthPercent),
            y + barHeight,
            healthPaint
        )
    }

    private fun drawItem(canvas: Canvas, x: Float, y: Float, item: ItemData) {
        when (item.type) {
            ItemType.AMMO -> {
                // Draw diamond shape for ammo
                val path = Path()
                path.moveTo(x, y - 15f)
                path.lineTo(x + 15f, y)
                path.lineTo(x, y + 15f)
                path.lineTo(x - 15f, y)
                path.close()
                canvas.drawPath(path, itemPaint)

                canvas.drawText(
                    "Ammo",
                    x - 15f,
                    y + 25f,
                    distancePaint
                )
            }
            ItemType.HEALTH -> {
                // Draw cross for health
                canvas.drawLine(x, y - 15f, x, y + 15f, itemPaint)
                canvas.drawLine(x - 15f, y, x + 15f, y, itemPaint)

                canvas.drawText(
                    "Health",
                    x - 18f,
                    y + 25f,
                    distancePaint
                )
            }
            ItemType.LOOT -> {
                // Draw square for loot boxes
                canvas.drawRect(
                    x - 15f,
                    y - 15f,
                    x + 15f,
                    y + 15f,
                    itemPaint
                )

                canvas.drawText(
                    "Loot",
                    x - 12f,
                    y + 25f,
                    distancePaint
                )
            }
            ItemType.WEAPON -> {
                // Draw triangle for weapons
                val path = Path()
                path.moveTo(x, y - 15f)
                path.lineTo(x + 15f, y + 15f)
                path.lineTo(x - 15f, y + 15f)
                path.close()
                canvas.drawPath(path, itemPaint)

                canvas.drawText(
                    "Weapon",
                    x - 18f,
                    y + 25f,
                    distancePaint
                )
            }
        }
    }

    private fun worldToScreen(worldX: Float, worldY: Float): Pair<Float, Float> {
        // Convert world coordinates to screen coordinates
        // This is a simplified conversion - adjust based on actual game coordinates
        val screenX = (SCREEN_WIDTH / 2) + (worldX / MAX_DRAW_DISTANCE) * (SCREEN_WIDTH / 2)
        val screenY = (SCREEN_HEIGHT / 2) + (worldY / MAX_DRAW_DISTANCE) * (SCREEN_HEIGHT / 2)

        return Pair(
            screenX.coerceIn(0f, SCREEN_WIDTH),
            screenY.coerceIn(0f, SCREEN_HEIGHT)
        )
    }

    data class EnemyData(
        val x: Float,
        val y: Float,
        val z: Float,
        val distance: Float,
        val health: Int,
        val id: Int
    )

    data class ItemData(
        val x: Float,
        val y: Float,
        val z: Float,
        val type: ItemType,
        val distance: Float
    )

    enum class ItemType {
        AMMO, HEALTH, LOOT, WEAPON
    }
}