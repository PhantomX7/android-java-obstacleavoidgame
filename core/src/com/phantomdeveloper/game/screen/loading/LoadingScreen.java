package com.phantomdeveloper.game.screen.loading;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.phantomdeveloper.game.ObstacleGame;
import com.phantomdeveloper.game.assets.AssetDescriptors;
import com.phantomdeveloper.game.config.GameConfig;
import com.phantomdeveloper.game.screen.menu.MenuScreen;
import com.phantomdeveloper.game.util.GdxUtils;

/**
 * Created by Phantom on 2/15/2018.
 */

public class LoadingScreen extends ScreenAdapter {


    //==constant==
    private static final float PROGRESS_BAR_WIDTH = GameConfig.HUD_WIDTH / 2f;//world unit
    private static final float PROGRESS_BAR_HEIGHT = 60;

    //==attributes==
    private OrthographicCamera camera;
    private Viewport viewport;
    private ShapeRenderer renderer;

    private float progress;
    private float waitTime = 0.74f;
    private boolean changeScreen;

    private final ObstacleGame game;
    private final AssetManager assetManager;

    //==constructor==
    public LoadingScreen(ObstacleGame game) {
        this.game = game;
        assetManager = game.getAssetManager();
    }

    //==public==

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT, camera);
        renderer = new ShapeRenderer();

        assetManager.load(AssetDescriptors.FONT);
        assetManager.load(AssetDescriptors.GAME_PLAY);
        assetManager.load(AssetDescriptors.UI_SKIN);
        assetManager.load(AssetDescriptors.HIT_SOUND);
        assetManager.load(AssetDescriptors.GAME_MUSIC);
    }

    @Override
    public void render(float delta) {
        update(delta);

        GdxUtils.clearScreen();
        viewport.apply();
        renderer.setProjectionMatrix(camera.combined);
        renderer.begin(ShapeRenderer.ShapeType.Filled);

        draw();

        renderer.end();

        if (changeScreen) {
            game.setScreen(new MenuScreen(game));
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void hide() {
        //NOTE: screen don't dispose automatically
        dispose();
    }

    @Override
    public void dispose() {
        renderer.dispose();
    }

    //==private method==
    private void update(float delta) {

        //progress is between 0 and 1
        progress = assetManager.getProgress();

        //update returns true when all asset are loaded
        if (assetManager.update()) {
            waitTime -= delta;

            if (waitTime <= 0) {
                changeScreen = true;
            }
        }
    }

    private void draw() {
        float progressBarX = (GameConfig.HUD_WIDTH - PROGRESS_BAR_WIDTH) / 2f;
        float progressBarY = (GameConfig.HUD_HEIGHT - PROGRESS_BAR_HEIGHT) / 2f;

        renderer.rect(progressBarX, progressBarY,
                progress * PROGRESS_BAR_WIDTH, PROGRESS_BAR_HEIGHT);
    }

    public static void waitMillis(long milis) {
        try {
            Thread.sleep(milis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
