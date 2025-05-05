package com.wizard.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.wizard.Main;
import com.wizard.entities.Enemy;
import com.wizard.entities.EnemyType;
import com.wizard.entities.EntityManager;
import com.wizard.entities.GameContactListener;
import com.wizard.entities.Player;
import com.wizard.utils.Constants;
import com.wizard.utils.VisibilityRadiusEffect;

public class GameScreen extends ScreenAdapter {
    private Main game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private World world;

    private BitmapFont font;
    //objects
    private Player player;
    private EntityManager entityManager;

    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer renderer;

    // enemy spawning
    private float enemySpawnTimer = 0;
    private final float ENEMY_SPAWN_INTERVAL = 5f; // Spawn enemy every 5 seconds
    private final Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();

    private Sprite[] healthSprites;
    private static final int MAX_HEALTH = 10;
    private Matrix4 uiProj = new Matrix4().setToOrtho2D(0, 0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

    // Visibility radius effect
    private VisibilityRadiusEffect visibilityEffect;
    private boolean useVisibilityEffect = true; // Toggle for enabling/disabling effect

    public GameScreen(Main game){
        this.game = game;
        this.world = new World(new Vector2(0, 0), true);
        this.batch = game.getBatch();
        // Tile map
        this.entityManager = new EntityManager(world, batch);
        tiledMap = new TmxMapLoader().load("maps/testMap.tmx");
        renderer = new OrthogonalTiledMapRenderer(tiledMap, 1);

        this.camera = new OrthographicCamera(); // Still need to fix it so its not a bugged and dumb
        this.camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        player = new Player(world,Gdx.graphics.getWidth() / 2f / Constants.PPM, Gdx.graphics.getHeight() / 2f / Constants.PPM, entityManager, camera );

        // Set the player in the entity manager
        entityManager.setPlayer(player);
        world.setContactListener(new GameContactListener(entityManager, player));
        // Spawn initial enemy
        spawnEnemy();

        healthSprites = new Sprite[MAX_HEALTH + 1];
        for (int i = 0; i <= MAX_HEALTH; i++) {
            Texture tex = new Texture(Gdx.files.internal("player_health/VIDA_" + i + ".png"));
            healthSprites[i] = new Sprite(tex);
        }

        // Create visibility radius effect
        // Parameters: SpriteBatch, Player, radius (pixels), soft edge width (pixels)
        visibilityEffect = new VisibilityRadiusEffect(batch, player, 250f, 70f);

        // Set initial camera position
        camera.position.set(
            player.getX(),
            player.getY(),
            0
        );
        camera.update();
    }

    public void update(float delta){
        world.step(1/60f, 1, 1);  //CHECK this shit before implementing not sure about the velocity but prolly gonna use 30 frames

        camera.update(); // these need to be after the world step
        player.update(delta);
        entityManager.updateAll(delta);

        // Enemy spawning logic
        enemySpawnTimer += delta;
        if (enemySpawnTimer >= ENEMY_SPAWN_INTERVAL) {
            spawnEnemy();
            enemySpawnTimer = 0;
        }
    }

    private void updateCamera() {
        Vector3 position = camera.position;
        position.x = player.getX() * Constants.PPM;
        position.y = player.getY() * Constants.PPM;

        camera.position.set(position);
        camera.update();
    }

    // Enemy spawning method
    private void spawnEnemy() {
        // Randomly select enemy type
        EnemyType type = (Math.random() < 0.5) ?
            EnemyType.RANGED_WIZARD : EnemyType.MELEE_SKELETON;

        // Random position at the edge of the screen
        float x, y;
        if (Math.random() < 0.5) {
            // Spawn on left or right edge
            x = (Math.random() < 0.5) ? 0 : Gdx.graphics.getWidth() / Constants.PPM;
            y = (float) (Math.random() * Gdx.graphics.getHeight() / Constants.PPM);
        } else {
            // Spawn on top or bottom edge
            x = (float) (Math.random() * Gdx.graphics.getWidth() / Constants.PPM);
            y = (Math.random() < 0.5) ? 0 : Gdx.graphics.getHeight() / Constants.PPM;
        }

        // Create and add the enemy
        Enemy enemy = new Enemy(world, x, y, entityManager,
            entityManager.getPlayer(), type);
        entityManager.addEnemy(enemy);
    }

    public void render(float delta){
        update(delta);

        // Clear screen
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update camera
        updateCamera();

        if (useVisibilityEffect) {
            // Begin visibility effect
            visibilityEffect.begin();
        }

        // Render map
        renderer.setView(camera);
        renderer.render();

        // Render player and entities
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        player.render(batch);
        entityManager.renderAll();
        batch.end();

        // Optional: render debug
        // debugRenderer.render(world, camera.combined.scl(Constants.PPM));

        if (useVisibilityEffect) {
            // End visibility effect
            visibilityEffect.end();
        }

        // Render the health bar - this is outside the visibility effect
        batch.setProjectionMatrix(uiProj);
        batch.begin();
        int hp = MathUtils.clamp(player.getHealth(), 0, MAX_HEALTH);
        Sprite bar = healthSprites[hp];
        float desiredWidth  = 150f;
        float desiredHeight = 20f;

        bar.setSize(desiredWidth, desiredHeight);
        bar.setPosition(10, Gdx.graphics.getHeight() - desiredHeight - 10);
        bar.draw(batch);
        batch.end();
    }

    public void resize(int width, int height){
        camera.update();
        visibilityEffect.resize(width, height);
    }

    public void pause(){

    }

    public void resume(){

    }

    public void dispose(){
        debugRenderer.dispose();
        tiledMap.dispose();
        renderer.dispose();
        player.dispose();
        world.dispose();
        visibilityEffect.dispose();
        for (Sprite s : healthSprites) {
            s.getTexture().dispose();
        }
    }

    // Toggle visibility effect on/off
    public void toggleVisibilityEffect() {
        useVisibilityEffect = !useVisibilityEffect;
    }
}
