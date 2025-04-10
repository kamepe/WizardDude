package com.wizard.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.wizard.Main;
import com.wizard.entities.Player;
import com.wizard.game.WizardGame;

public class GameScreen extends ScreenAdapter {
    private Main game;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Box2DDebugRenderer debugRenderer;
    private World world;
    private static final float PPM = 100; // neeeeds to go into the const class, for now using for testing

    private ShapeRenderer shapeRenderer = new ShapeRenderer();

    private BitmapFont font;
    //objects
    private Player player;

    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer renderer;


    public GameScreen(Main game){
        this.game = game;
        this.world = new World(new Vector2(0, 0), true);
        this.debugRenderer = new Box2DDebugRenderer();// remove when done
        this.batch = game.getBatch();

        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);

        // tile map
        TmxMapLoader tmxMapLoader = new TmxMapLoader();
        tiledMap = new TmxMapLoader().load("maps/testMap.tmx");

        renderer = new OrthogonalTiledMapRenderer(tiledMap);

        player = new Player(world,Gdx.graphics.getWidth() / 2 / PPM, Gdx.graphics.getHeight() / 2 / PPM);
    }

    public void update(float delta){
        player.update(delta);

        // Center camera on player
        camera.position.x = player.getCenterX();
        camera.position.y = player.getCenterY();

        camera.update();

        world.step(1/60f, 1, 1);  //CHECK this shit before implementing not sure about the velocity but prolly gonna use 30 frames

        updateCamera();
    }

    private void updateCamera() {
        // Center camera on player (converting from meters to pixels)
        camera.position.x = player.getCenterX();
        camera.position.y = player.getCenterY();

        // Update camera matrices
        camera.update();
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

        // Render player
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        player.render(batch);
        batch.end();

        debugRenderer.render(world, camera.combined.cpy().scl(PPM));// not working right now
    }

    public void resize(int width, int height){
        camera.setToOrtho(false, width/2, height/2); // Check if a better way to do this

//        camera.viewportWidth = VIEWPORT_WIDTH;
//        camera.viewportHeight = VIEWPORT_HEIGHT * height / width;
        camera.update();
    }

    public void pause(){

    }

    public void resume(){

    }

    public void dispose(){
        tiledMap.dispose();
        renderer.dispose();
        player.dispose();
        world.dispose();
        debugRenderer.dispose();
    }

}
