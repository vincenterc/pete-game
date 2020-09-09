package com.vincenterc.pete

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport

class LoadingScreen(private val peteGame: PeteGame) : ScreenAdapter() {
    companion object {
        private const val WORLD_WIDTH = 640f
        private const val WORLD_HEIGHT = 480f
        private const val PROGRESS_BAR_WIDTH = 100f
        private const val PROGRESS_BAR_HEIGHT = 25f
    }

    private lateinit var shapeRenderer: ShapeRenderer
    private lateinit var viewport: Viewport
    private lateinit var camera: Camera
    private var progress = 0f

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun show() {
        camera = OrthographicCamera()
        camera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0f)
        camera.update()

        viewport = FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera)
        shapeRenderer = ShapeRenderer()

        peteGame.getAssetManager().load("pete.png", Texture::class.java)
        peteGame.getAssetManager().load("acorn.png", Texture::class.java)
        peteGame.getAssetManager().load("pete.tmx", TiledMap::class.java)
    }

    override fun render(delta: Float) {
        update()
        clearScreen()
        draw()
    }

    override fun dispose() {
        shapeRenderer.dispose()
    }

    private fun update() {
        if (peteGame.getAssetManager().update()) {
            peteGame.screen = GameScreen(peteGame)
        } else {
            progress = peteGame.getAssetManager().progress
        }
    }

    private fun clearScreen() {
        Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
    }

    private fun draw() {
        shapeRenderer.projectionMatrix = camera.projection
        shapeRenderer.transformMatrix = camera.view
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color.WHITE
        shapeRenderer.rect(
            WORLD_WIDTH / 2f - PROGRESS_BAR_WIDTH / 2f,
            WORLD_HEIGHT / 2f - PROGRESS_BAR_HEIGHT / 2f,
            progress * PROGRESS_BAR_WIDTH,
            PROGRESS_BAR_HEIGHT
        )
        shapeRenderer.end()
    }
}