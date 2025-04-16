//package com.wizard.utils;
//
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.graphics.Texture;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.graphics.g2d.TextureRegion;
//import com.badlogic.gdx.physics.box2d.Body;
//
//public class Animator {
//    private TextureRegion[] frames;
//    private Body body;
//    private float duration;
//    private float delay;
//    private int current;
//    private float width;
//    private float height;
//    private boolean playOnce; // true if it's something like a death sprite and the last frame has to be looped
//
//    public Animator(Body body, String path){
//        Texture texture = new Texture(Gdx.files.internal("characters/idleSoldier.png"));
//        frames = TextureRegion.split(texture, 100, 100)[0];
//        delay = 0.2f;
//    }
//
//    public void update(float delta) {
//        duration += delta;
//        while(duration >= delay){
//            step();
//        }
//    }
//
//    // Rendering the animation
//    public void render(SpriteBatch batch) {
//        batch.begin();
//        // With will become negative if facing left
//        batch.draw(frames[current], body.getPosition().x - (width / Constants.PPM) / 2, b2body.getPosition().y - (height / Constants.PPM) / 2,width / Constants.PPM, height / Constants.PPM);
//        batch.end();
//    }
//
//    public void step() {
//        duration -= delay;
//        current++;
//    }
//}
