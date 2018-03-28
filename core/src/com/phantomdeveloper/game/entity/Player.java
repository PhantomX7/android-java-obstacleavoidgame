package com.phantomdeveloper.game.entity;

import com.phantomdeveloper.game.config.GameConfig;

/**
 * Created by Phantom on 2/1/2018.
 */

public class Player extends GameObjectBase {


    public Player() {
        super(GameConfig.PLAYER_BOUNDS_RADIUS);
        setSize(GameConfig.PLAYER_SIZE, GameConfig.PLAYER_SIZE);
    }

}
