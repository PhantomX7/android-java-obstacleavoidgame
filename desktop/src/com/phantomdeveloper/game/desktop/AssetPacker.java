package com.phantomdeveloper.game.desktop;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

/**
 * Created by Phantom on 2/14/2018.
 */

public class AssetPacker {
    private static final boolean DRAW_DEBUG_OUTLINE = false;

    private static final String RAW_ASSET_PASTH = "desktop/assets-raw";
    private static final String ASSETS_PATH = "android/assets";

    public static void main(String[] args) {
        TexturePacker.Settings settings = new TexturePacker.Settings();
        settings.debug = DRAW_DEBUG_OUTLINE;

        TexturePacker.process(settings,
                RAW_ASSET_PASTH + "/gameplay",
                ASSETS_PATH + "/gameplay",
                "gameplay");

        TexturePacker.process(settings,
                RAW_ASSET_PASTH + "/skin",
                ASSETS_PATH + "/ui",
                "uiskin");
    }
}
