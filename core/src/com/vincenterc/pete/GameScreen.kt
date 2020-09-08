package com.vincenterc.pete

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport

class GameScreen(private val peteGame: PeteGame) : ScreenAdapter() {
    companion object {
        private const val CELL_SIZE = 16

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
        pete.setPosition(0f, WORLD_HEIGHT / 2f)
    }

    private fun whichCellsDoesPeteCover(): Array<CollisionCell> {
        var x = pete.getX()
        var y = pete.getY()

        var cellsCovered: MutableList<CollisionCell> = mutableListOf()


        var cellX = x / CELL_SIZE
        var cellY = y / CELL_SIZE

        var bottomLeftCellX = MathUtils.floor(cellX)
        var bottomLeftCellY = MathUtils.floor(cellY)

        var tiledMapTileLayer: TiledMapTileLayer = tiledMap.layers.get(0) as TiledMapTileLayer

        cellsCovered.add(
            CollisionCell(
                tiledMapTileLayer.getCell(bottomLeftCellX, bottomLeftCellY),
                bottomLeftCellX,
                bottomLeftCellY
            )
        )

        if (cellX % 1 != 0f && cellY % 1 != 0f) {
            var topRightCellX = bottomLeftCellX + 1
            var topRightCellY = bottomLeftCellY + 1
            cellsCovered.add(
                CollisionCell(
                    tiledMapTileLayer.getCell(topRightCellX, topRightCellY),
                    topRightCellX,
                    topRightCellY
                )
            )
        }

        if (cellX % 1 != 0f) {
            var bottomRightCellX = bottomLeftCellX + 1
            var bottomRightCellY = bottomLeftCellY
            cellsCovered.add(
                CollisionCell(
                    tiledMapTileLayer.getCell(bottomRightCellX, bottomRightCellY),
                    bottomRightCellX,
                    bottomRightCellY
                )
            )
        }

        if (cellY % 1 != 0f) {
            var topLeftCellX = bottomLeftCellX
            var topLeftCellY = bottomLeftCellY + 1
            cellsCovered.add(
                CollisionCell(
                    tiledMapTileLayer.getCell(topLeftCellX, topLeftCellY),
                    topLeftCellX,
                    topLeftCellY
                )
            )
        }

        return cellsCovered.toTypedArray()
    }

    private fun filterOutNonTiledCells(cells: Array<CollisionCell>): Array<CollisionCell> {
        var cellList = cells.toList()
        var cellListFiltered = cellList.filter { !it.isEmpty() }

        return cellListFiltered.toTypedArray()
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
        handlePeteCollision()
    }

    private fun handlePeteCollision() {
        var peteCells = whichCellsDoesPeteCover()
        peteCells = filterOutNonTiledCells(peteCells)

        for (cell in peteCells) {
            var cellLevelX = cell.cellX * CELL_SIZE
            var cellLevelY = cell.cellY * CELL_SIZE

            var intersection = Rectangle()
            Intersector.intersectRectangles(
                pete.getCollisionRectangle(),
                Rectangle(cellLevelX.toFloat(), cellLevelY.toFloat(), CELL_SIZE.toFloat(), CELL_SIZE.toFloat()),
                intersection
            )

            if (intersection.getHeight() < intersection.getWidth()) {
                pete.setPosition(pete.getX(), intersection.getY() + intersection.getHeight())
                pete.landed()
            } else if (intersection.getWidth() < intersection.getHeight()) {
                if (intersection.getX() == pete.getX()) {
                    pete.setPosition(intersection.getX() + intersection.getWidth(), pete.getY())
                }
                if (intersection.getX() > pete.getX()) {
                    pete.setPosition(intersection.getX() - Pete.WIDTH, pete.getY())
                }
            }
        }
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

    private class CollisionCell(
        private val cell: TiledMapTileLayer.Cell?,
        val cellX: Int,
        val cellY: Int
    ) {
        fun isEmpty(): Boolean {
            return cell == null
        }
    }
}
