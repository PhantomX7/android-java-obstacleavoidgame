package com.phantomdeveloper.game.screen.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.phantomdeveloper.game.assets.AssetDescriptors;
import com.phantomdeveloper.game.assets.RegionNames;
import com.phantomdeveloper.game.config.GameConfig;
import com.phantomdeveloper.game.entity.Background;
import com.phantomdeveloper.game.entity.Obstacle;
import com.phantomdeveloper.game.entity.Player;
import com.phantomdeveloper.game.screen.menu.MenuScreenBase;
import com.phantomdeveloper.game.util.GdxUtils;
import com.phantomdeveloper.game.util.ViewportUtils;
import com.phantomdeveloper.game.util.debug.DebugCameraController;

/**
 * Created by Phantom on 2/8/2018.
 */

public class GameRenderer extends MenuScreenBase implements Disposable {

    //==attributes==
    private OrthographicCamera camera;
    private Viewport viewport;
    private ShapeRenderer renderer;

    private OrthographicCamera hudCamera;
    private Viewport hudViewport;

    private BitmapFont font;
    private final GlyphLayout layout = new GlyphLayout();
    private DebugCameraController debugCameraController;
    private final GameController controller;
    private final AssetManager assetManager;
    private final SpriteBatch batch;

    private TextureRegion playerTexture;
    private TextureRegion obstacleTexture;
    private TextureRegion obstacle2Texture;
    private TextureRegion backgroundTexture;

    //==constructor==
    public GameRenderer(SpriteBatch batch, AssetManager assetManager, GameController controller) {

        super(assetManager);
        this.batch = batch;
        this.assetManager = assetManager;
        this.controller = controller;
        init();
    }

    @Override
    public void show() {
        super.show();
    }

    //==init==
    private void init() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, camera);
        renderer = new ShapeRenderer();

        hudCamera = new OrthographicCamera();
        hudViewport = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT, hudCamera);
        font = assetManager.get(AssetDescriptors.FONT);

        //create debug camera controller
        debugCameraController = new DebugCameraController();
        debugCameraController.setStartPosition(GameConfig.WORLD_CENTER_X, GameConfig.WORLD_CENTER_Y);

        TextureAtlas gamePlayAtlas = assetManager.get(AssetDescriptors.GAME_PLAY);

        playerTexture = gamePlayAtlas.findRegion(RegionNames.PLAYER);
        obstacleTexture = gamePlayAtlas.findRegion(RegionNames.OBSTACLE);
        obstacle2Texture = gamePlayAtlas.findRegion(RegionNames.OBSTACLE2);
        backgroundTexture = gamePlayAtlas.findRegion(RegionNames.BACKGROUND);

        Stage stage = new Stage(hudViewport, batch);

        Gdx.input.setInputProcessor(stage);

        stage.addActor(createUi());
        super.stage = stage;
    }

    //==public method==
    public void render(float delta) {


//        return;
        batch.totalRenderCalls = 0;

        debugCameraController.handleDebugInput(delta);
        debugCameraController.applyTo(camera);

        if (!controller.isPaused()) {
            if (Gdx.input.isTouched() && !controller.isGameOver()) {
                Vector2 screenTouch = new Vector2(Gdx.input.getX(), Gdx.input.getY());
                Vector2 worldTouch = viewport.unproject(new Vector2(screenTouch));

                if (worldTouch.y < GameConfig.WORLD_WIDTH) {
                    Player player = controller.getPlayer();
                    worldTouch.x = MathUtils.clamp(worldTouch.x, player.getWidth() / 2, GameConfig.WORLD_WIDTH - player.getWidth() / 2);
                    player.setX(worldTouch.x - player.getWidth() / 2);
                }
            }
        }

        //clearScreen
        GdxUtils.clearScreen();

        renderGameplay();
        super.stage.act();
        super.stage.draw();

        //render ui/hud
        renderUi();


        //render debug graphics
//        renderDebug();

        //System.out.println("total render calls ="+batch.totalRenderCalls);
    }

    @Override
    protected Actor createUi() {

        Skin uiSkin = assetManager.get(AssetDescriptors.UI_SKIN);

        String pauseText = controller.isPaused() ? "RESUME" : "PAUSE";
        TextButton pauseButton = new TextButton(pauseText, uiSkin);
        pauseButton.setTransform(true);
        pauseButton.setScale(0.5f);

        pauseButton.setPosition(15, GameConfig.HUD_HEIGHT - pauseButton.getHeight() - 15);

        pauseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.setPaused(!controller.isPaused());
            }
        });

        return pauseButton;
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
        hudViewport.update(width, height, true);
        ViewportUtils.debugPixelPerUnit(viewport);
    }

    @Override
    public void dispose() {
        renderer.dispose();
    }

    //==private method==
    private void renderGameplay() {
        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        //draw background
        Background background = controller.getBackground();
        batch.draw(backgroundTexture,
                background.getX(), background.getY(),
                background.getWidth(), background.getHeight());

        //draw player
        Player player = controller.getPlayer();
        batch.draw(playerTexture,
                player.getX(), player.getY(),
                player.getWidth(), player.getHeight());

        //draw obstacles
        for (Obstacle obstacle : controller.getObstacles()) {
            batch.draw(obstacleTexture,
                    obstacle.getX(), obstacle.getY(),
                    obstacle.getWidth(), obstacle.getHeight() * 1.5f);
        }

        batch.end();
    }

    private void renderUi() {
        hudViewport.apply();
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();

        String livesText = "LIVES: " + controller.getLives();
        layout.setText(font, livesText);

        font.draw(batch, livesText,
                20,
                GameConfig.HUD_HEIGHT - layout.height);

        String scorText = "SCORE: " + controller.getDisplayScore();
        layout.setText(font, scorText);

        font.draw(batch, scorText,
                GameConfig.HUD_WIDTH - layout.width - 20,
                GameConfig.HUD_HEIGHT - layout.height);


//        layout.setText(font, pauseText);

//        font.draw(batch, pauseText,
//                20,
//                GameConfig.HUD_HEIGHT - 2 * layout.height - 10);

        batch.end();
    }

    private void renderDebug() {
        viewport.apply();
        renderer.setProjectionMatrix(camera.combined);
        renderer.begin(ShapeRenderer.ShapeType.Line);

        drawDebug();

        renderer.end();

        ViewportUtils.drawGrid(viewport, renderer);
    }

    private void drawDebug() {
        Player player = controller.getPlayer();
        player.drawDebug(renderer);

        Array<Obstacle> obstacles = controller.getObstacles();

        for (Obstacle obstacle : obstacles) {
            obstacle.drawDebug(renderer);
        }
    }
}
