package com.vincenterc.pete

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle

class Pete {
    companion object {
        const val WIDTH = 16
        const val HEIGHT = 15

        private const val MAX_X_SPEED = 2f
        private const val MAX_Y_SPEED = 2f
        private const val MAX_JUMP_DISTANCE = 3 * HEIGHT
    }

    private val collisionRectangle = Rectangle(0f, 0f, WIDTH.toFloat(), HEIGHT.toFloat())

    private var x = 0f
    private var y = 0f
    private var xSpeed = 0f
    private var ySpeed = 0f

    private var blockJump = false
    private var jumpYDistance = 0f

    fun update() {
        var input = Gdx.input

        xSpeed = if (input.isKeyPressed(Input.Keys.RIGHT)) MAX_X_SPEED
        else if (input.isKeyPressed(Input.Keys.LEFT)) -MAX_X_SPEED
        else 0f

        if (input.isKeyPressed(Input.Keys.UP) && !blockJump) {
            ySpeed = MAX_Y_SPEED
            jumpYDistance += ySpeed
            blockJump = jumpYDistance > MAX_JUMP_DISTANCE
        } else {
            ySpeed = -MAX_Y_SPEED
            blockJump = jumpYDistance > 0
        }

        x += xSpeed
        y += ySpeed

        updateCollisionRectangle()
    }

    fun drawDebug(shapeRenderer: ShapeRenderer) {
        shapeRenderer.rect(
            collisionRectangle.x,
            collisionRectangle.y,
            collisionRectangle.width,
            collisionRectangle.height
        )
    }

    fun landed() {
        blockJump = false
        jumpYDistance = 0f
        ySpeed = 0f
    }

    fun setPosition(x: Float, y: Float) {
        this.x = x
        this.y = y
        updateCollisionRectangle()
    }

    fun getX(): Float {
        return x
    }

    fun getY(): Float {
        return y
    }

    private fun updateCollisionRectangle() {
        collisionRectangle.setPosition(x, y)
    }
}