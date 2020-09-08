package com.vincenterc.pete

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport

class GameScreen(private val peteGame: PeteGame) : ScreenAdapter() {
    companion object {
        private const val WORLD_WIDTH = 640f
        private const val WORLD_HEIGHT = 480f
    }

    private lateinit var shapeRenderer: ShapeRenderer
    private lateinit var viewport: Viewport
    private lateinit var camera: OrthographicCamera

    private lateinit var batch: SpriteBatch

    private lateinit var tiledMap: TiledMap
    private lateinit var orthogonalTiledMapRenderer: OrthogonalTiledMapRenderer

    private lateinit var pete: Pete

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun show() {
        camera = OrthographicCamera()
        viewport = FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera)
        viewport.apply(true)

        shapeRenderer = ShapeRenderer()
        batch = SpriteBatch()

        tiledMap = peteGame.getAssetManager().get("pete.tmx")
        orthogonalTiledMapRenderer = OrthogonalTiledMapRenderer(tiledMap, batch)
        orthogonalTiledMapRenderer.setView(camera)

        pete = Pete(peteGame.getAssetManager().get("pete.png"))
    }

    override fun render(delta: Float) {
        update(delta)
        clearScreen()
        draw()
        drawDebug()
    }

    private fun update(delta: Float) {
        pete.update(delta)
        stopPeteLeavingTheScreen()
    }

    fun stopPeteLeavingTheScreen() {
        if (pete.getY() < 0f) {
            pete.setPosition(pete.getX(), 0f)
            pete.landed()
        }
        if (pete.getX() < 0f) {
            pete.setPosition(0f, pete.getY())
        }
        if (pete.getX() + Pete.WIDTH > WORLD_WIDTH) {
            pete.setPosition(WORLD_WIDTH - Pete.WIDTH, pete.getY())
        }
    }

    private fun clearScreen() {
        Gdx.gl.glClearColor(Color.TEAL.r, Color.TEAL.g, Color.TEAL.b, Color.TEAL.a)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
    }

    private fun draw() {
        batch.projectionMatrix = camera.projection
        batch.transformMatrix = camera.view
        orthogonalTiledMapRenderer.render()
        batch.begin()
        pete.draw(batch)
        batch.end()
    }

    private fun drawDebug() {
        shapeRenderer.projectionMatrix = camera.projection
        shapeRenderer.transformMatrix = camera.view
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        pete.drawDebug(shapeRenderer)
        shapeRenderer.end()
    }
}
