package com.wizard.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.wizard.entities.Player;
import com.wizard.game.WizardGame;

public class GameScreen extends ScreenAdapter {
    private WizardGame game;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    // World is needed within the Box2D library
    private World world;
    //private Box2DDebugRenderer debugRenderer;
    private BitmapFont font;
    //objects
    private Player player;


    public GameScreen(WizardGame game){

        this.game = game;

        camera = new OrthographicCamera();
    }
    public void update(float delta){
//        util.update(delta, camera);
//        timer.update(delta);
//        world.step(1/30f, 1, 1);  //CHECK this shit before implementing not sure about the velocity but prolly gonna use 30 frames also dont know is the positioniterations are important
    }

    public void render(float delta){

    }

    public void resize(int width, int height){

    }

    public void pause(){

    }

    public void resume(){

    }

    public void dispose(){

    }

}
