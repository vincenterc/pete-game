package com.vincenterc.pete

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Rectangle

class Acorn(
    private val texture: Texture,
    private val x: Float,
    private val y: Float
) {
    companion object {
        const val WIDTH = 16
        const val HEIGHT = 16
    }

    private var collision = Rectangle(x, y, WIDTH.toFloat(), HEIGHT.toFloat())

    fun getCollisionRectangle() = collision

    fun draw(batch: Batch) {
        batch.draw(texture, x, y)
    }
}