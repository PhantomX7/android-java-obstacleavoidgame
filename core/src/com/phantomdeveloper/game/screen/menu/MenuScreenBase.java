package com.phantomdeveloper.game.screen.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.phantomdeveloper.game.ObstacleGame;
import com.phantomdeveloper.game.config.GameConfig;
import com.phantomdeveloper.game.util.GdxUtils;

/**
 * Created by Phantom on 2/25/2018.
 */

public abstract class MenuScreenBase extends ScreenAdapter {
    protected final ObstacleGame game;
    protected final AssetManager assetManager;

    private Viewport viewport;
    protected Stage stage;

    public MenuScreenBase(AssetManager assetManager) {
        this.assetManager = assetManager;
        this.game=null;
    }

    public MenuScreenBase(ObstacleGame game) {
        this.game = game;
        assetManager = game.getAssetManager();
    }

    @Override
    public void show() {
        viewport = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT);
        stage = new Stage(viewport, game.getBatch());

        Gdx.input.setInputProcessor(stage);

        stage.addActor(createUi());
    }

    protected abstract Actor createUi();

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render(float delta) {
        GdxUtils.clearScreen();

        stage.act();
        stage.draw();
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
