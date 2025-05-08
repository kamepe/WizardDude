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
        instance = new ShaderManager();
        return instance;
    }

    public ShaderProgram getVignetteShader() {
        return vignetteShader;
    }

    public void dispose() {
        instance = null;
    }
}
