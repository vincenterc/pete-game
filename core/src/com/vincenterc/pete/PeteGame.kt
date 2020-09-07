package com.vincenterc.pete

import com.badlogic.gdx.Game
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader

class PeteGame : Game() {
    private val assetManager = AssetManager()

    override fun create() {
        assetManager.setLoader(TiledMap::class.java, TmxMapLoader(InternalFileHandleResolver()))
        setScreen(LoadingScreen(this))
    }

    fun getAssetManager(): AssetManager {
        return assetManager
    }
}