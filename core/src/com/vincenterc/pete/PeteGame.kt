package com.vincenterc.pete

import com.badlogic.gdx.Game
import com.badlogic.gdx.assets.AssetManager

class PeteGame : Game() {
    private val assetManager = AssetManager()

    override fun create() {
        setScreen(LoadingScreen(this))
    }

    fun getAssetManager(): AssetManager {
        return assetManager
    }
}