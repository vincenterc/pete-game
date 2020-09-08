package com.vincenterc.pete

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle

class Pete(texture: Texture) {
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

    private var animationTimer = 0f
    private var walking: Animation<TextureRegion>
    private var standing: TextureRegion
    private var jumpUP: TextureRegion
    private var jumpDown: TextureRegion

    init {
        var regions = TextureRegion.split(texture, WIDTH, HEIGHT)[0]

        walking = Animation(0.25f, regions[0], regions[1])
        walking.playMode = Animation.PlayMode.LOOP

        standing = regions[0]
        jumpUP = regions[2]
        jumpDown = regions[3]
    }

    fun update(delta: Float) {
        animationTimer += delta

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

    fun draw(batch: Batch) {
        var toDraw = standing
        if (xSpeed != 0f) {
            toDraw = walking.getKeyFrame(animationTimer)
        }
        if (ySpeed > 0) {
            toDraw = jumpUP
        } else if (ySpeed < 0) {
            toDraw = jumpDown
        }

        if (xSpeed < 0) {
            if (!toDraw.isFlipX) toDraw.flip(true, false)
        } else if (xSpeed > 0) {
            if (toDraw.isFlipX) toDraw.flip(true, false)
        }

        batch.draw(toDraw, x, y)
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

    fun getCollisionRectangle(): Rectangle {
        return collisionRectangle
    }

    private fun updateCollisionRectangle() {
        collisionRectangle.setPosition(x, y)
    }
}