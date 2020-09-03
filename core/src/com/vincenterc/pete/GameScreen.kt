package com.vincenterc.pete

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport

class GameScreen(private val peteGame: PeteGame) : ScreenAdapter() {
    companion object {
        private const val WORLD_WIDTH = 640f
        private const val WORLD_HEIGHT = 480f
    }

    private lateinit var shapeRenderer: ShapeRenderer
    private lateinit var viewport: Viewport
    private lateinit var camera: Camera
    private lateinit var batch: SpriteBatch

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun show() {
        camera = OrthographicCamera()
        camera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0f)
        camera.update()
        viewport = FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera)
        shapeRenderer = ShapeRenderer()
        batch = SpriteBatch()
    }

    override fun render(delta: Float) {
        update(delta)
        clearScreen()
        draw()
        drawDebug();
    }

    private fun update(delta: Float) {
    }

    private fun clearScreen() {
        Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
    }

    private fun draw() {
        batch.projectionMatrix = camera.projection
        batch.transformMatrix = camera.view
        batch.begin()
        batch.end()
    }

    private fun drawDebug() {
        shapeRenderer.projectionMatrix = camera.projection
        shapeRenderer.transformMatrix = camera.view
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.end()
    }
}
