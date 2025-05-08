package com.wizard.entities;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.wizard.utils.Animator;
import static com.wizard.utils.Constants.PPM;

public class Spells {
    private Body body;
    private World world;
    private Vector2 velocity;
    private Sprite sprite;
    private float aliveTime = 0.f;
    private boolean destroyed = false;
    private static float maxTime = 3f;
    private final Object owner;
    private boolean isVisualOnly = false;

    private Animator animator;
    private String animationPath;
    private boolean useAnimation = false;

    public Spells(World world, float startX, float startY, Vector2 rawDir, float width, float height, float speed, Sprite spellSprite, Object own) {
        // World
        this.world = world;
        velocity = new Vector2(rawDir);
        velocity.nor();
        velocity.scl(speed);
        this.createBody(startX, startY, width, height);
        this.sprite = spellSprite;
        this.sprite.setSize(width, height);
        this.owner = own;
    }

    // New constructor that accepts an animation path instead of a static sprite
    public Spells(World world, float startX, float startY, Vector2 rawDir, float width, float height, float speed, String animationPath, Object own) {
        this.world = world;
        velocity = new Vector2(rawDir);
        velocity.nor();
        velocity.scl(speed);

        this.createBody(startX, startY, width, height);
        this.owner = own;
        this.useAnimation = true;
        this.animationPath = animationPath;

        // Create animator with the specified animation path
        this.animator = new Animator(null, body, animationPath, false);
    }

    private void createBody(float x, float y, float width, float height) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        bodyDef.fixedRotation = true;
        body = world.createBody(bodyDef);

        // Shapes
        PolygonShape shape = new PolygonShape();
        // Reduce hitbox by 80% (multiply width and height by 0.2)
        shape.setAsBox(width * 0.15f, height * 0.15f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;

        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);
        body.setLinearVelocity(velocity);
        shape.dispose();
    }

    public void update(float delta){
        Vector2 pos = body.getPosition();
        aliveTime += delta;

        if (useAnimation) {
            animator.update(delta);
        } else {
            // Only update sprite position if not using animator
            sprite.setPosition(
                pos.x * PPM - sprite.getWidth() / 2f,
                pos.y * PPM - sprite.getHeight() / 2f
            );
        }
        if(aliveTime >= maxTime){
            destroy();
        }
    }

    public void render(SpriteBatch batch) {
        if (useAnimation) {
            // Render the animated sprite
            animator.render(batch);
        } else {
            // Render the static sprite
            Vector2 position = body.getPosition();
            batch.draw(sprite,
                (position.x - sprite.getWidth()/2) * PPM,
                (position.y - sprite.getHeight()/2) * PPM,
                sprite.getWidth() * PPM, sprite.getHeight() * PPM);
        }
    }

    public void destroy() {
        destroyed = true;
    }
    public boolean shouldDestroy(){
        return destroyed;
    }
    public Object getOwner(){
        return owner;
    }
    public Sprite getSprite(){
        return sprite;
    }
    public Vector2 getVelocity(){
        return velocity;
    }
    public Body getBody(){
        return body;
    }
    public void setMaxTime(float time) {
        maxTime = time;
    }
    public void setVisualOnly(boolean visualOnly) {
        this.isVisualOnly = visualOnly;
    }
    public boolean isVisualOnly() {
        return isVisualOnly;
    }
}
