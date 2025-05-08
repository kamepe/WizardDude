package com.wizard.entities;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.wizard.utils.AudioManager;
import com.wizard.utils.Constants;
import static com.wizard.utils.Constants.PPM;
import java.util.function.Predicate;
import com.badlogic.gdx.physics.box2d.RayCastCallback;


public class Enemy {
    // core
    private Body body;
    private World world;
    private Vector2 position;
    private EntityManager entityManager;
    private Player player;
    private EnemyType type; // Store reference to the EnemyType

    private boolean activated = false; // when an enemy sees a player activate them

    // animation
    private float width;
    private float height;
    private Constants.Direction current_dir = Constants.Direction.DOWN;
    private Animation<TextureRegion> down, up, left, right;
    private float stateTime = 0f;

    // stats
    private int health = 1;
    private float attackCooldown;
    private float detectionRange;
    private float moveSpeed;
    private Sprite attackSprite;
    private boolean isRanged;

    //added part
    protected void onDeath() {

    }

    // state
    private boolean dead = false;
    private float timeSinceLastAttack = 0f;
    private Vector2 lastPlayerPos = new Vector2();
    private Vector2 playerVelocity = new Vector2();
    private float predictionFactor = 0.82f; // I adjusted it until it became kind of accurate, while testing
                                            // keep in mind that the players hitbox is completely broken
    public Enemy(World world, float x, float y, EntityManager em, Player player, EnemyType type) {
        this.type = type; // Store the enemy type reference

        down = new Animation<>(0.1f, loadStrip(type.getDownSprite()));
        up = new Animation<>(0.1f, loadStrip(type.getUpSprite()));
        left = new Animation<>(0.1f, loadStrip(type.getLeftSprite()));
        right = new Animation<>(0.1f, loadStrip(type.getRightSprite()));

        this.world = world;
        this.entityManager = em;
        this.player = player;
        this.position = new Vector2();
        this.lastPlayerPos.set(player.getPosition());
        this.health = type.getHealth();
        this.attackCooldown = type.getAttackCooldown();
        this.detectionRange = type.getDetectionRange();
        this.moveSpeed = type.getMoveSpeed();
        this.attackSprite = new Sprite(type.getAttackSprite());
        this.isRanged = type.isRanged();

        TextureRegion sample = down.getKeyFrame(0f);
        width = sample.getRegionWidth() / PPM;
        height = sample.getRegionHeight() / PPM;

        createBody(x, y);
    }

    private TextureRegion[] loadStrip(String filename) {
        Texture sprite_part = new Texture(Gdx.files.internal("characters/enemy/" + filename));
        TextureRegion[][] tmp = TextureRegion.split(sprite_part, type.getSpriteSize(), type.getSpriteSize());
        TextureRegion[] frames = new TextureRegion[tmp[0].length];
        System.arraycopy(tmp[0], 0, frames, 0, frames.length);
        return frames;
    }

    private void createBody(float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        bodyDef.fixedRotation = true;
        body = world.createBody(bodyDef);

        body.setUserData(this);

        PolygonShape shape = new PolygonShape();
        // Adjust hitbox size based on sprite size ratio
        float sizeRatio = type.getSpriteSize() / 32f;
        shape.setAsBox(width/4 * sizeRatio, height/4 * sizeRatio); // Scale hitbox for larger sprites

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f; // can be adjusted later


        Filter filter = new Filter();
        filter.categoryBits = Constants.CATEGORY_ENEMY;
        filter.maskBits = Constants.CATEGORY_PLAYER | Constants.CATEGORY_PLAYER_SPELL;
        fixtureDef.filter.categoryBits = filter.categoryBits;
        fixtureDef.filter.maskBits = filter.maskBits;

        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);
        shape.dispose();
    }

    public void update(float deltaTime) {
        if (dead) return;

        position = body.getPosition();
        stateTime += deltaTime;

        // enemies only go after the player if they see it
        if (!activated && hasLineOfSight()) {
            activated = true;
        }
        if (activated) {
            Vector2 playerPosition = player.getPosition();
            Vector2 direction;
            direction = new Vector2(playerPosition.x - position.x, playerPosition.y - position.y);
            float distance = direction.len();
            if (distance > 0) {
                direction.nor();
            }


            updateDirection(direction);
            timeSinceLastAttack += deltaTime;

            // attack
            if (distance < detectionRange && timeSinceLastAttack >= attackCooldown) {
                if (!isRanged || hasLineOfSight()) {
                    attackPlayer(direction);
                    timeSinceLastAttack = 0;
                }
            }

            // movement
            if (distance >= detectionRange) {
                body.setLinearVelocity(direction.x * moveSpeed, direction.y * moveSpeed);
            } else if (isRanged && !hasLineOfSight()) {
                tryFindPathAroundObstacle(playerPosition);
            } else if (isRanged) {
                float optimalDistance = detectionRange * 0.85f;
                if (distance < optimalDistance) {
                    body.setLinearVelocity(-direction.x * moveSpeed * 0.5f, -direction.y * moveSpeed * 0.5f);
                } else {
                    body.setLinearVelocity(0, 0);
                }
            } else {
                if (distance > 0.5f) {
                    if (!hasLineOfSight()) {
                        tryFindPathAroundObstacle(playerPosition);
                    } else {
                        body.setLinearVelocity(direction.x * moveSpeed, direction.y * moveSpeed);
                    }
                } else {
                    body.setLinearVelocity(0, 0);
                }
            }
        } else {
            body.setLinearVelocity(0, 0);
        }
    }


    private boolean hasLineOfSight() {
        // a ray from the enemy to the player
        World world = this.world;


        Vector2 start = this.position.cpy();
        Vector2 end = player.getPosition().cpy();
        Vector2 direction = end.cpy().sub(start);
        float distance = direction.len();

        final boolean[] sightBlocked = {false};
        RayCastCallback callback = new RayCastCallback() {
            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                if (fixture.getUserData() instanceof Player || fixture.getUserData() instanceof Enemy) {
                    return 1;
                }
                sightBlocked[0] = true;
                return 0;
            }
        };

        world.rayCast(callback, start, end);
        return !sightBlocked[0];
    }

    private void tryFindPathAroundObstacle(Vector2 targetPosition) {

        //check if the path is clear
        Predicate<Vector2> PathClear = (dir) -> {
            final boolean[] clear = {true};
            Vector2 end = new Vector2(position).add(dir.cpy().scl(1.0f));

            world.rayCast((fixture, point, normal, fraction) -> {
                if (!(fixture.getUserData() instanceof Player ||
                      fixture.getUserData() instanceof Enemy)) {
                    clear[0] = false;
                    return 0;
                }
                return 1;
            }, position, end);

            return clear[0];
        };

        // angles
        float[] path_angles = new float[32];
        for (int i = 0; i < 32; i++) {
            path_angles[i] = (360f/32) * i;
        }

        Vector2 baseDir = new Vector2(targetPosition).sub(position).nor();
        // try angles until there is a path towards the player
        for (float angle : path_angles) {
            Vector2 testDir = new Vector2(baseDir).rotate(angle).nor();
            if (PathClear.test(testDir)) {
                body.setLinearVelocity(testDir.x * moveSpeed, testDir.y * moveSpeed);
                return;
            }
        }
    }

    protected void attackPlayer(Vector2 direction) {
        if (isRanged && !hasLineOfSight()) {
            return;
        }

        if (isRanged) {
            // copy of the direction vector
            entityManager.addToActiveSpells(new Spells(
                world,
                position.x, position.y,
                new Vector2(direction),
                0.5f, 0.5f,
                1.5f,
                new Sprite(attackSprite),
                this
            ));
            // play ranged sound
            AudioManager.playEnemyRangedAttackSound();
        } else {

            if (Vector2.dst(position.x, position.y, player.getX(), player.getY()) < 0.5f) {
                player.takeDamage();
            }
            // put the melee attack sprite the on the player's position
            Vector2 playerPos = player.getPosition();
            Spells melee = new Spells(
                world,
                playerPos.x, playerPos.y,
                new Vector2(0, 0),
                0.4f, 0.4f,
                0.2f,
                new Sprite(attackSprite),
                this
            );
            // play melee sound
            AudioManager.playEnemyMeleeAttackSound();

            // the visual indicator of the attack shouldnt disappear in 0.1 sec
            melee.setVisualOnly(true);
            melee.getBody().getFixtureList().get(0).setSensor(true);
            melee.setMaxTime(0.6f);
            entityManager.addToActiveSpells(melee);
        }
    }

    public void takeDamage() {
        health--;
        System.out.println("enemy damaged / health: " + health);
        if (health <= 0) {
            die();
            onDeath();
        }
    }

    private void updateDirection(Vector2 direction) {
        if (Math.abs(direction.x) > Math.abs(direction.y)) {
            if (direction.x > 0) {
                current_dir = Constants.Direction.RIGHT;
            } else {
                current_dir = Constants.Direction.LEFT;
            }
        } else {
            if (direction.y > 0) {
                current_dir = Constants.Direction.UP;
            } else {
                current_dir = Constants.Direction.DOWN;
            }
        }
    }

    public void render(SpriteBatch batch) {
        if (dead) return;

        TextureRegion frame = null;

        switch (current_dir) {
            case UP:    frame = up.getKeyFrame(stateTime, true); break;
            case DOWN:  frame = down.getKeyFrame(stateTime, true); break;
            case LEFT:  frame = left.getKeyFrame(stateTime, true); break;
            case RIGHT: frame = right.getKeyFrame(stateTime, true); break;
            default:
                System.out.println("direction doesnt exist, defaulting to down.");
                frame = down.getKeyFrame(stateTime, true);
        }

        batch.draw(
            frame,
            (position.x - width/2) * PPM,
            (position.y - height/2) * PPM,
            width * PPM,
            height * PPM
        );
    }
    private void die() {
        dead = true;
        // Play enemy death sound when an enemy dies
        AudioManager.playEnemyDeathSound();
    }
    public boolean shouldRemove() {
        return dead;
    }
    public Body getBody() {
        return body;
    }
    public void dispose() {
        attackSprite.getTexture().dispose();
    }
    public void forceActivate() {
        this.activated = true;
    }
    protected EntityManager getEntityManager() {
        return entityManager;
    }
    protected World getWorld() {
        return world;
    }
}
