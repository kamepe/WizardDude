package com.wizard.utils;

public class Constants {
    public static final float PPM = 100f;
    public static final float MAX_SPEED = 0.7f;
    public static final float PLAYER_DIMENSIONS = 32.0f;
    
    // Collision categories for physics filtering
    public static final short CATEGORY_PLAYER = 1;
    public static final short CATEGORY_ENEMY = 2;
    public static final short CATEGORY_PLAYER_SPELL = 3;
    public static final short CATEGORY_ENEMY_SPELL = 4;

    public enum Direction {
        LEFT, RIGHT, UP, DOWN
    }
}
