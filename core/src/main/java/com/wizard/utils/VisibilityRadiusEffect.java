package com.wizard.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.wizard.entities.Player;

public class VisibilityRadiusEffect {
    private ShaderProgram shader;
    private FrameBuffer frameBuffer;
    private SpriteBatch batch;
    private Player player;

    // Visibility radius parameters
    private float radius;
    private float softEdge;

    public VisibilityRadiusEffect(SpriteBatch batch, Player player, float radius, float softEdge) {
        this.batch = batch;
        this.player = player;
        this.radius = radius;
        this.softEdge = softEdge;

        ShaderProgram.pedantic = false;

        // Create our shader
        shader = new ShaderProgram(
            Gdx.files.internal("shaders/VisibilityRadius.vert"),
            Gdx.files.internal("shaders/VisibilityRadius.frag")
        );

        // Create our frame buffer for rendering
        frameBuffer = new FrameBuffer(Format.RGBA8888,
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight(),
            false
        );
    }

    public void begin() {
        // Start capturing to frame buffer
        frameBuffer.begin();
//        Gdx.gl.glClearColor(0, 0, 0, 1);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    public void end() {
        // End capture
        frameBuffer.end();

        // Get the texture from the frame buffer
        Texture frameTexture = frameBuffer.getColorBufferTexture();

        frameTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        // Player position in screen coordinates
        Vector2 playerScreenPos = new Vector2(
            player.getX() * Constants.PPM,
            Gdx.graphics.getHeight() - (player.getY() * Constants.PPM)
        );

        // Store the old shader
        ShaderProgram oldShader = batch.getShader();

        // Use our visibility shader
        batch.setShader(shader);

        // Set shader uniforms
        shader.setUniformf("u_resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shader.setUniformf("u_playerPos", playerScreenPos);
        shader.setUniformf("u_radius", radius);
        shader.setUniformf("u_softEdge", softEdge);

        // Draw the frame texture with the shader
        batch.begin();
        batch.draw(frameTexture,
            0, 0,
            Gdx.graphics.getWidth() * 1, Gdx.graphics.getHeight() ,
            0, 0,
            Gdx.graphics.getWidth() * 1, Gdx.graphics.getHeight(),
            false, true);
        batch.end();

        // Restore the old shader
        batch.setShader(oldShader);
    }

    public void resize(int width, int height) {
        frameBuffer = new FrameBuffer(Format.RGBA8888, width, height, false);
    }

    public void dispose() {
        shader.dispose();
        frameBuffer.dispose();
    }
}
