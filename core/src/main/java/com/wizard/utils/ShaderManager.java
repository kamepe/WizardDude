package com.wizard.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class ShaderManager {
    private static ShaderManager instance;
    private ShaderProgram vignetteShader;

    private ShaderManager() {
        // Load shaders
        String vertexShader = Gdx.files.internal("shaders/basic.vert").readString();
        String fragmentShader = Gdx.files.internal("shaders/vignette.frag").readString();

        ShaderProgram.pedantic = true;

        // Create shader program
        vignetteShader = new ShaderProgram(vertexShader, fragmentShader);
    }

    public static ShaderManager getInstance() {
        if (instance == null) {
            instance = new ShaderManager();
        }
        return instance;
    }

    public ShaderProgram getVignetteShader() {
        return vignetteShader;
    }

    public void updateVignetteShader(float playerX, float playerY, float screenWidth, float screenHeight) {
        vignetteShader.bind();
        vignetteShader.setUniformf("u_resolution", screenWidth, screenHeight);
        vignetteShader.setUniformf("u_playerPosition", playerX, playerY);
    }

    public void dispose() {
        vignetteShader.dispose();
        instance = null;
    }
}
