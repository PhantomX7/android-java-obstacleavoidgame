package com.phantomdeveloper.game.screen.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Logger;
import com.phantomdeveloper.game.ObstacleGame;
import com.phantomdeveloper.game.assets.AssetDescriptors;
import com.phantomdeveloper.game.assets.RegionNames;
import com.phantomdeveloper.game.screen.game.GameScreen;

/**
 * Created by Phantom on 2/24/2018.
 */

public class MenuScreen extends MenuScreenBase {
    private static final Logger log = new Logger(MenuScreen.class.getName(), Logger.DEBUG);

    public MenuScreen(ObstacleGame game) {
        super(game);
    }

    @Override
    protected Actor createUi() {
        Table table = new Table();

        TextureAtlas gamePlayAtlas = assetManager.get(AssetDescriptors.GAME_PLAY);
        Skin uiSkin = assetManager.get(AssetDescriptors.UI_SKIN);
        TextureRegion backgroundRegion = gamePlayAtlas.findRegion(RegionNames.BACKGROUND);
        table.setBackground(new TextureRegionDrawable(backgroundRegion));

        //play button
        TextButton playButton = new TextButton("PLAY", uiSkin);
        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                play();
            }
        });

        //highscore button
        TextButton highscoreButton = new TextButton("HIGHSCORE", uiSkin);
        highscoreButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showHighScore();
            }
        });

        //options button
        TextButton optionsButton = new TextButton("OPTIONS", uiSkin);
        optionsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showOptions();
            }
        });

        //quit button
        TextButton quitButton = new TextButton("QUIT", uiSkin);
        quitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                quit();
            }
        });

        //setup table
        Table buttonTable = new Table(uiSkin);
        buttonTable.defaults().pad(20);
        buttonTable.setBackground(RegionNames.PANEL);

        buttonTable.add(playButton).row();
        buttonTable.add(highscoreButton).row();
        buttonTable.add(optionsButton).row();
        buttonTable.add(quitButton);

        buttonTable.center();

        table.add(buttonTable);
        table.center();
        table.setFillParent(true);
        table.pack();

        return table;
    }


    private void play() {
        game.setScreen(new GameScreen(game));
    }

    private void showHighScore() {
        game.setScreen(new HighScoreScreen(game));
    }

    private void showOptions() {
        game.setScreen(new OptionsScreen(game));
    }

    private void quit() {
        Gdx.app.exit();
    }
}
