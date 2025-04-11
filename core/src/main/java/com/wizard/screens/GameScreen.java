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
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.wizard.Main;
import com.wizard.entities.Player;
import com.wizard.game.WizardGame;

public class GameScreen extends ScreenAdapter {
    private Main game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private World world;
    private static final float PPM = 100; // neeeeds to go into the const class, for now using for testing

    private BitmapFont font;
    //objects
    private Player player;

    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer renderer;


    public GameScreen(Main game){
        this.game = game;
        this.world = new World(new Vector2(0, 0), true);
        this.batch = game.getBatch();
        // Tile map
        TmxMapLoader tmxMapLoader = new TmxMapLoader();
        tiledMap = new TmxMapLoader().load("maps/testMap.tmx");
        renderer = new OrthogonalTiledMapRenderer(tiledMap);

        this.camera = new OrthographicCamera(); // Still need to fix it so its not a bugged and dumb
//        this.camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        viewport = new FitViewport(Gdx.graphics.getWidth() * 1.3f,  Gdx.graphics.getHeight() * 1.3f, camera);
        viewport.apply();

        player = new Player(world,Gdx.graphics.getWidth() / 2 / PPM , Gdx.graphics.getHeight() / 2/ PPM);
        // Set initial camera position
        camera.position.set(
            player.getCenterX(),
            player.getCenterY(),
            0
        );
        camera.update();
    }

    public void update(float delta){
        world.step(1/60f, 1, 1);  //CHECK this shit before implementing not sure about the velocity but prolly gonna use 30 frames

        camera.update();
        player.update(delta);
    }

    private void updateCamera() {
        Vector3 position = camera.position;
        position.x = player.getCenterX() * PPM;
        position.y = player.getCenterY() * PPM;

        camera.position.set(position);
        camera.update();
    }

    public void render(float delta){
        update(delta);

        // Clear screen
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update camera
        camera.update();

        // Render map
        renderer.setView(camera);
        renderer.render();
        updateCamera();

        // Render player
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        player.render(batch);
        batch.end();

        }

    public void resize(int width, int height){
//        camera.setToOrtho(false, width, height);
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
    }

}
