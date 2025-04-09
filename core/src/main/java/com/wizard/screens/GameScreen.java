package com.wizard.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.wizard.Main;
import com.wizard.entities.Player;
import com.wizard.game.WizardGame;

public class GameScreen extends ScreenAdapter {
    private Main game;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    // World is needed within the Box2D library
    private World world;
    private BitmapFont font;
    //objects
    private Player player;

    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer renderer;

    public GameScreen(Main game){
        this.game = game;
        this.batch = game.getBatch();

        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // tile map
        TmxMapLoader tmxMapLoader = new TmxMapLoader();
        tiledMap = new TmxMapLoader().load("maps/testMap.tmx");

        renderer = new OrthogonalTiledMapRenderer(tiledMap);
    }
    public void update(float delta){
//        util.update(delta, camera);
//        timer.update(delta);
//        world.step(1/30f, 1, 1);  //CHECK this shit before implementing not sure about the velocity but prolly gonna use 30 frames also dont know is the positioniterations are important
    }

    public void render(float delta){
        // Clear screen
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update camera
        camera.update();

        // Render map
        renderer.setView(camera);
        renderer.render();
    }

    public void resize(int width, int height){
        camera.setToOrtho(false, width/2, height/2); // Check if a better way to do this
        camera.update();
    }

    public void pause(){

    }

    public void resume(){

    }

    public void dispose(){
        tiledMap.dispose();
        renderer.dispose();
    }

}
