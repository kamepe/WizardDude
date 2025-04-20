package com.wizard.entities;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import static com.wizard.utils.Constants.PPM;

public class Spells {
    // comment
    private Body body;
    private World world;
    private Vector2 velocity;
    private Sprite  sprite;
    private float aliveTime = 0.f;
    private boolean destroyed = false;
    private static float maxTime = 3f;

    public Spells(World world, float startX, float startY, Vector2 rawDir , float width, float height, float speed, Sprite spellSprite){
    //  World
        this.world = world;
        velocity = new Vector2(rawDir);
        velocity.nor();
        velocity.scl(speed);
        //  Init a body
        
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(startX, startY);
        bodyDef.fixedRotation = true;
        body = world.createBody(bodyDef);
        // Shapes
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width, height);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;

        body.createFixture(fixtureDef);
        body.setLinearVelocity(velocity);

        this.sprite = spellSprite;
        this.sprite.setSize(width, height);
         shape.dispose();
    }
    public void update(float delta){
        Vector2 pos = body.getPosition();
        aliveTime += delta;

         sprite.setPosition(
            pos.x * PPM - sprite.getWidth()  / 2f,
            pos.y * PPM - sprite.getHeight() / 2f
            );
        if(aliveTime >= maxTime){
            destroyed = true;
        }
    }
    public void render(SpriteBatch batch){
        Vector2 position = body.getPosition();
        batch.draw(sprite,
            (position.x - sprite.getWidth()/2) * PPM,
            (position.y - sprite.getHeight()/2) * PPM,
            sprite.getWidth() * PPM, sprite.getHeight() * PPM);
    }

    public boolean shouldDestroy(){
        return destroyed;
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

}
