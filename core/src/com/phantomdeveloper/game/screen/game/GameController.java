package com.phantomdeveloper.game.screen.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.phantomdeveloper.game.ObstacleGame;
import com.phantomdeveloper.game.assets.AssetDescriptors;
import com.phantomdeveloper.game.common.GameManager;
import com.phantomdeveloper.game.config.DifficultyLevel;
import com.phantomdeveloper.game.config.GameConfig;
import com.phantomdeveloper.game.entity.Background;
import com.phantomdeveloper.game.entity.Obstacle;
import com.phantomdeveloper.game.entity.Player;

/**
 * Created by Phantom on 2/8/2018.
 */

public class GameController {

    //==constant==
    private static final Logger log = new Logger(GameController.class.getName(), Logger.DEBUG);

    //==attribute==
    private Player player;
    private Array<Obstacle> obstacles = new Array<Obstacle>();
    private Background background;
    private float obstacleTimer;
    private float scoreTimer;
    private int lives = GameConfig.LIVES_START;
    private int score;
    private int displayScore;
    private Pool<Obstacle> obstaclePool;

    private Sound hit;
    private Music music;

    private final ObstacleGame game;
    private final AssetManager assetManager;

    private final float startPlayerX = (GameConfig.WORLD_WIDTH - GameConfig.PLAYER_SIZE) / 2;
    private final float startPlayerY = 1 - GameConfig.PLAYER_SIZE / 2;


    //==constructor==
    public GameController(ObstacleGame game) {
        this.game = game;
        assetManager = game.getAssetManager();
        init();
    }

    //==init==
    private void init() {
        //create player
        player = new Player();

        //calculate position
        float startPlayerX = (GameConfig.WORLD_WIDTH - GameConfig.PLAYER_SIZE) / 2;
        float startPlayerY = 1 - GameConfig.PLAYER_SIZE / 2;

        //position player
        player.setPosition(startPlayerX, startPlayerY);

        //create obstacle pool
        obstaclePool = Pools.get(Obstacle.class, 40);

        //create background and position it
        background = new Background();
        background.setPosition(0, 0);
        background.setSize(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT);

        //load sound
        hit = assetManager.get(AssetDescriptors.HIT_SOUND);
        music = assetManager.get(AssetDescriptors.GAME_MUSIC);
        music.setLooping(true);
        music.play();
    }

    //==public method==
    public void update(float delta) {
        if (isGameOver()) {
            return;
        }
        updatePlayer();
        updateObstacles(delta);
        updateScore(delta);
        updateDisplayScore(delta);

        if (isPlayerCollidingWithObstacle()) {
            log.debug("Collision");
            lives--;

            if (isGameOver()) {
                log.debug("Game Over");
                GameManager.INSTANCE.updateHighScore(score);
                music.stop();

            } else {
                restart();
            }
        }
    }

    public Player getPlayer() {
        return player;
    }


    public int getLives() {
        return lives;
    }

    public Array<Obstacle> getObstacles() {
        return obstacles;
    }

    public Background getBackground() {
        return background;
    }

    public int getDisplayScore() {
        return displayScore;
    }

    public boolean isGameOver() {
        return lives <= 0;
    }

    //==private method==

    private void restart() {
        obstaclePool.freeAll(obstacles);
        obstacles.clear();
        player.setPosition(startPlayerX, startPlayerY);
    }

    private boolean isPlayerCollidingWithObstacle() {
        for (Obstacle obstacle : obstacles) {
            if (obstacle.isNotHit() && obstacle.isPlayerColliding(player)) {
                hit.play();
                return true;
            }
        }

        return false;
    }

    private void updatePlayer() {
        float xSpeed = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            xSpeed = GameConfig.MAX_PLAYER_X_SPEED;
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            xSpeed = -GameConfig.MAX_PLAYER_X_SPEED;
        }

        player.setX(player.getX() + xSpeed);

        blockPlayerFromLeavingTheWorld();
    }

    private void blockPlayerFromLeavingTheWorld() {
        float playerX = MathUtils.clamp(player.getX(), //value
                0, //min
                GameConfig.WORLD_WIDTH - player.getWidth()); //max

        player.setPosition(playerX, player.getY());
    }

    private void updateObstacles(float delta) {
        for (Obstacle obstacle : obstacles) {
            obstacle.update();
        }

        createNewObstacle(delta);
        removePassedObstacle();
    }

    private void createNewObstacle(float delta) {
        obstacleTimer += delta;

        if (obstacleTimer >= GameConfig.OBSTACLE_SPAWN_TIME) {
            float min = 0;
            float max = GameConfig.WORLD_WIDTH - GameConfig.OBSTACLE_SIZE;
            float obstacleX = MathUtils.random(min, max);
            float obstacleY = GameConfig.WORLD_HEIGHT;

            Obstacle obstacle = obstaclePool.obtain();
            DifficultyLevel difficultyLevel = GameManager.INSTANCE.getDifficultyLevel();
            obstacle.setYSpeed(difficultyLevel.getObstacleSpeed());
            obstacle.setPosition(obstacleX, obstacleY);

            obstacles.add(obstacle);
            obstacleTimer = 0f;
        }
    }

    private void removePassedObstacle() {
        if (obstacles.size > 0) {
            Obstacle first = obstacles.first();

            float minObstacleY = -GameConfig.OBSTACLE_SIZE;
            if (first.getY() < minObstacleY) {
                obstacles.removeValue(first, true);
                obstaclePool.free(first);
            }
        }
    }

    private void updateScore(float delta) {
        scoreTimer += delta;

        if (scoreTimer >= GameConfig.SCORE_MAX_TIME) {

            score += 1;
            scoreTimer = 0.0f;
        }
    }

    private void updateDisplayScore(float delta) {
        if (displayScore < score) {
            displayScore = Math.min(score, displayScore + (int) (60 * delta));
        }
    }

}
