package com.wizard.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.wizard.Main;
import com.wizard.entities.Boss;
import com.wizard.entities.Enemy;
import com.wizard.entities.EnemyType;
import com.wizard.entities.EntityManager;
import com.wizard.entities.GameContactListener;
import com.wizard.entities.Player;
import com.wizard.utils.Constants;
import com.wizard.utils.ShaderManager;

public class GameScreen extends ScreenAdapter {
    private Main game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private World world;

    private Player player;
    private EntityManager entityManager;

    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer renderer;

    // enemy spawning
    private float enemySpawnTimer = 0;
    private final float ENEMY_SPAWN_INTERVAL = 8f; // in sec
    private final Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();

    private Sprite[] healthSprites;
    private static final int MAX_HEALTH = 10;
    private Matrix4 uiProj = new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    private float unitScale = 1f / Constants.PPM;

    // Map dimensions in world units
    private float mapWidthInWorldUnits;
    private float mapHeightInWorldUnits;

    // spawning enemies variables
    private Rectangle bossSmallRoom;
    private Rectangle bossMedRoom;
    private Rectangle bossLargeRoom;
    private ArrayList<Rectangle> spawnAreas = new ArrayList<>();
    private boolean smallBossSpawned = false;
    private boolean mediumBossSpawned = false;
    private boolean largeBossSpawned = false;
    private Rectangle playerSpawnArea; // New field for player's spawn area

    private ShaderProgram vignetteShader;

    public GameScreen(Main game){
        this.game = game;
        this.world = new World(new Vector2(0, 0), true);
        this.batch = game.getBatch();

        // Initialize shader
        vignetteShader = ShaderManager.getInstance().getVignetteShader();

        // Set input processor to null to prevent menu inputs from affecting the game
        Gdx.input.setInputProcessor(null);
        // Tile map
        this.entityManager = new EntityManager(world, batch);
        tiledMap = new TmxMapLoader().load("maps/mapo.tmx");
        renderer = new OrthogonalTiledMapRenderer(tiledMap, 1);

        // Calculate map dimensions in world units
        int mapWidth = tiledMap.getProperties().get("width", Integer.class);
        int mapHeight = tiledMap.getProperties().get("height", Integer.class);
        int tileWidth = tiledMap.getProperties().get("tilewidth", Integer.class);
        int tileHeight = tiledMap.getProperties().get("tileheight", Integer.class);

        // Calculate map dimensions in world units
        mapWidthInWorldUnits = mapWidth * tileWidth * unitScale;
        mapHeightInWorldUnits = mapHeight * tileHeight * unitScale;

        // Collision of walls
        MapObjects objects = tiledMap.getLayers().get("collision").getObjects();

        for (MapObject obj : objects) {
            if (!(obj instanceof RectangleMapObject)) continue;
            Rectangle rect = ((RectangleMapObject)obj).getRectangle();

            BodyDef bdef = new BodyDef();
            bdef.type = BodyDef.BodyType.StaticBody;
            // world coords = pixels * unitScale
            bdef.position.set(
                (rect.x + rect.width  * 0.5f) * unitScale,
                (rect.y + rect.height * 0.5f) * unitScale
            );
            Body body = world.createBody(bdef);

            PolygonShape shape = new PolygonShape();
            shape.setAsBox(
                rect.width  * 0.5f * unitScale,
                rect.height * 0.5f * unitScale
            );
            Fixture wallFixture = body.createFixture(shape, 1.0f);
            wallFixture.setUserData("wall");

            shape.dispose();
        }

        this.camera = new OrthographicCamera(); // Still need to fix it so its not a bugged and dumb
        this.camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.camera.zoom = 0.5f;

        player = new Player(world, Gdx.graphics.getWidth() / 2f / Constants.PPM, Gdx.graphics.getHeight() / 2f / Constants.PPM, entityManager, camera);

        // Set up player spawn area - initialize it before initializeRooms
        playerSpawnArea = new Rectangle(
            player.getX() - 3,
            player.getY() - 3,
            6, 6); // 6x6 area around player starting position

        // Set the player in the entity manager
        entityManager.setPlayer(player);
        world.setContactListener(new GameContactListener(entityManager, player));
        healthSprites = new Sprite[MAX_HEALTH + 1];
        for (int i = 0; i <= MAX_HEALTH; i++) {
            Texture tex = new Texture(Gdx.files.internal("player_health/VIDA_" + i + ".png"));
            healthSprites[i] = new Sprite(tex);
        }

        // Set initial camera position
        camera.position.set(
            player.getX(),
            player.getY(),
            0
        );
        camera.update();

        initializeRooms();
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
        // Get the camera position
        Vector3 position = camera.position;

        // Set the camera to follow the player
        position.x = player.getX() * Constants.PPM;
        position.y = player.getY() * Constants.PPM;

        // Calculate the camera boundaries
        float viewportWidth = camera.viewportWidth * camera.zoom;
        float viewportHeight = camera.viewportHeight * camera.zoom;

        // Calculate the camera's half-width and half-height
        float cameraHalfWidth = viewportWidth / 2f;
        float cameraHalfHeight = viewportHeight / 2f;

        // Clamp the camera position to stay within map boundaries
        float mapWidthInPixels = mapWidthInWorldUnits * Constants.PPM;
        float mapHeightInPixels = mapHeightInWorldUnits * Constants.PPM;

        // Clamp X position
        position.x = MathUtils.clamp(position.x, cameraHalfWidth, mapWidthInPixels - cameraHalfWidth);

        // Clamp Y position
        position.y = MathUtils.clamp(position.y, cameraHalfHeight, mapHeightInPixels - cameraHalfHeight);

        // Update the camera position
        camera.position.set(position);
        camera.update();
    }

    private void initializeRooms() {
        // get the boss room locations
        MapLayer bossSmallLayer = tiledMap.getLayers().get("boss_small");
        MapLayer bossMedLayer = tiledMap.getLayers().get("boss_med");
        MapLayer bossLargeLayer = tiledMap.getLayers().get("boss_large");
        MapLayer spawnLayer = tiledMap.getLayers().get("spawn");

        // get the door layers
        MapLayer doorsSmallLayer = tiledMap.getLayers().get("doors_small_col");
        MapLayer doorsMedLayer = tiledMap.getLayers().get("doors_med_col");
        MapLayer doorsLargeLayer = tiledMap.getLayers().get("doors_large_col");

        // get and set boss rooms
        RectangleMapObject bossMedObj = (RectangleMapObject) bossMedLayer.getObjects().get(0);
        RectangleMapObject bossLargeObj = (RectangleMapObject) bossLargeLayer.getObjects().get(0);
        RectangleMapObject bossSmallObj = (RectangleMapObject) bossSmallLayer.getObjects().get(0);
        bossSmallRoom = bossSmallObj.getRectangle();
        bossLargeRoom = bossLargeObj.getRectangle();
        bossMedRoom = bossMedObj.getRectangle();

        // areas where normal enemies can be spawned
        if (spawnLayer != null) {
            for (MapObject obj : spawnLayer.getObjects()) {
                if (obj instanceof RectangleMapObject) {
                    Rectangle rect = ((RectangleMapObject) obj).getRectangle();
                    spawnAreas.add(new Rectangle(rect));
                }
            }
        }

        // rectangles -> world units
        convertToWorldUnits(bossSmallRoom);
        convertToWorldUnits(bossMedRoom);
        convertToWorldUnits(bossLargeRoom);

        for (Rectangle rect : spawnAreas) {
            convertToWorldUnits(rect);
        }

        // spawn all enemies
        spawnBosses();
        prespawnEnemies();
    }

    // convert rectangle -> world units
    private void convertToWorldUnits(Rectangle rect) {
        rect.x *= unitScale;
        rect.y *= unitScale;
        rect.width *= unitScale;
        rect.height *= unitScale;
    }

    private void prespawnEnemies() {
        // spawn 3 enemies in each locked room
        for (Rectangle area : spawnAreas) {
            for (int i = 0; i < 3; i++) {
                EnemyType type = (i % 2 == 0) ?
                    EnemyType.RANGED_WIZARD : EnemyType.MELEE_SKELETON;

                float x = area.x + (float)(Math.random() * area.width);
                float y = area.y + (float)(Math.random() * area.height);

                // range of safe area on 4 as of now
                float distToPlayer = Vector2.dst(x, y, player.getX(), player.getY());
                if (distToPlayer < 4f) {
                    continue;
                }
                // add enemy
                Enemy enemy = new Enemy(world, x, y, entityManager, entityManager.getPlayer(), type);
                entityManager.addEnemy(enemy);
            }
        }
    }

    private void spawnBosses() {
        // spawn the bosses
        if (!smallBossSpawned) {
            float x = bossSmallRoom.x + bossSmallRoom.width / 2;
            float y = bossSmallRoom.y + bossSmallRoom.height / 2;
            Boss smallBoss = new Boss(world, x, y, entityManager, entityManager.getPlayer(), EnemyType.SMALL_BOSS);
            entityManager.addEnemy(smallBoss);
            smallBossSpawned = true;
        }
        if (!mediumBossSpawned) {
            float x = bossMedRoom.x + bossMedRoom.width / 2;
            float y = bossMedRoom.y + bossMedRoom.height / 2;
            Boss mediumBoss = new Boss(world, x, y, entityManager, entityManager.getPlayer(), EnemyType.MEDIUM_BOSS);
            entityManager.addEnemy(mediumBoss);
            mediumBossSpawned = true;
        }
        if (!largeBossSpawned) {
            float x = bossLargeRoom.x + bossLargeRoom.width / 2;
            float y = bossLargeRoom.y + bossLargeRoom.height / 2;
            Boss largeBoss = new Boss(world, x, y, entityManager, entityManager.getPlayer(), EnemyType.LARGE_BOSS);
            entityManager.addEnemy(largeBoss);
            largeBossSpawned = true;
        }
    }

    // Enemy spawning method
    private void spawnEnemy() {
        // Randomly select an enemy type
        EnemyType type;
        if (Math.random() < 0.5) {
            type = EnemyType.RANGED_WIZARD;
        } else {
            type = EnemyType.MELEE_SKELETON;
        }

        // randomly select a spawn area
        Rectangle spawnArea = spawnAreas.get((int)(Math.random() * spawnAreas.size()));
        float x = spawnArea.x + (float)(Math.random() * spawnArea.width);
        float y = spawnArea.y + (float)(Math.random() * spawnArea.height);

        if (playerSpawnArea != null &&
            (playerSpawnArea.contains(x, y) ||
                playerSpawnArea.overlaps(new Rectangle(x - 0.5f, y - 0.5f, 1f, 1f)))) {
            return;
        }

        // Create and add the enemy
        Enemy enemy = new Enemy(world, x, y, entityManager, entityManager.getPlayer(), type);
        entityManager.addEnemy(enemy);
    }

    public void render(float delta){
        update(delta);

        // Clear screen
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update camera
        updateCamera();

        // Set shader
        vignetteShader.bind();
        vignetteShader.setUniformf("u_resolution", Gdx.graphics.getWidth()*2, Gdx.graphics.getHeight()*2);

        // Apply shader to the batch for world rendering
        batch.setShader(vignetteShader);

        // Apply shader to the tiled map renderer
        renderer.getBatch().setShader(vignetteShader);

        renderer.setView(camera);
        renderer.render();

        // Render player
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        player.render(batch);
        entityManager.renderAll();
        batch.end();

        batch.setShader(null);
        debugRenderer.render(world, camera.combined.scl(Constants.PPM));

        // Render the helth bar and adjust size
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

        batch.setShader(vignetteShader);
    }

    public TiledMap getMap(){
        return tiledMap;
    }

    public void resize(int width, int height){
        camera.update();
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
        vignetteShader.dispose();
        for (Sprite s : healthSprites) {
            s.getTexture().dispose();
        }
    }
}
