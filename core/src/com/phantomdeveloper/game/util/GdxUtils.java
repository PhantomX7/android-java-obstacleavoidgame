package com.phantomdeveloper.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;

/**
 * Created by Phantom on 1/28/2018.
 */

public class GdxUtils {

    private GdxUtils() {
    }

    public static void clearScreen() {
        //clear screen
        clearScreen(Color.BLACK);
    }

    public static void clearScreen(Color color) {
        //clear screen
        Gdx.gl.glClearColor(color.r, color.g, color.b, color.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }
}
