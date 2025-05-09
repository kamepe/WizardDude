package com.wizard.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.wizard.Main;
import com.wizard.entities.Boss;
import com.wizard.entities.Door;
import com.wizard.entities.Enemy;
import com.wizard.entities.EnemyType;
import com.wizard.entities.EntityManager;
import com.wizard.entities.GameContactListener;
import com.wizard.entities.Player;
import com.wizard.utils.AudioManager;
import com.wizard.utils.Constants;
import com.wizard.utils.KeyManager;
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
    private final float ENEMY_SPAWN_INTERVAL = 3f; // in sec
    private final Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();

    private Sprite[] healthSprites;
    private ShapeRenderer shapes; // Declare ShapeRenderer instance
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
    private static final float VIRTUAL_WIDTH  = 400f;
    private static final float VIRTUAL_HEIGHT = 200f; 
    // Removed duplicate declaration of viewport

    private ShaderProgram vignetteShader;

    // List to store door objects
    private ArrayList<Door> doors = new ArrayList<>();

    // Font for UI text
    private BitmapFont font;

    // Key manager for door unlocking system
    private KeyManager keyManager;

    public GameScreen(Main game){
        this.game = game;
        this.world = new World(new Vector2(0, 0), true);
        this.batch = game.getBatch();

        // Initialize shader
        vignetteShader = ShaderManager.getInstance().getVignetteShader();
        // Initialize ShapeRenderer
        shapes = new ShapeRenderer();

        // Initialize font for UI text
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(1.5f);

        // Initialize key manager with 1 initial key
        keyManager = new KeyManager(1);

        // Set input processor to null to prevent menu inputs from affecting the game
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
            ShapeRenderer shapes = new ShapeRenderer();

            shape.dispose();
        }

        this.camera = new OrthographicCamera(); // Still need to fix it so its not a bugged and dumb
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);

        float PLAYER_SPAWN_X = 6.75f;
        float PLAYER_SPAWN_Y = 2.27f;
        player = new Player(world, PLAYER_SPAWN_X, PLAYER_SPAWN_Y, entityManager, camera, keyManager);

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
        player.update(delta);
        entityManager.updateAll(delta);

        // Update doors
        updateDoors();

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

        // Initialize doors
        initializeDoors(doorsSmallLayer, "doors_small", "doors_small_col");
        initializeDoors(doorsMedLayer, "doors_med", "doors_med_col");
        initializeDoors(doorsLargeLayer, "doors_large", "doors_large_col");

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
        // spawn 5 enemies in each locked room
        for (Rectangle area : spawnAreas) {
            for (int i = 0; i < 5; i++) {
                EnemyType type = (i % 2 == 0) ?
                    EnemyType.RANGED_WIZARD : EnemyType.MELEE_SKELETON;

                float x = area.x + (float)(Math.random() * area.width);
                float y = area.y + (float)(Math.random() * area.height);

                // a no enemy spawn in the 10 units are of the player
                float distToPlayer = Vector2.dst(x, y, player.getX(), player.getY());
                if (distToPlayer < 7.6f) {
                    continue;
                }
                // add enemy
                Enemy enemy = new Enemy(world, x, y, entityManager, entityManager.getPlayer(), type);
                entityManager.addEnemy(enemy);
            }
        }
    }

    /**
     * Initializes door objects from map layers
     * @param collisionLayer The layer containing door collision objects
     * @param visualLayerName The name of the layer containing door visuals
     * @param collisionLayerName The name of the collision layer
     */
    private void initializeDoors(MapLayer collisionLayer, String visualLayerName, String collisionLayerName) {
        if (collisionLayer == null) return;

        MapObjects doorObjects = collisionLayer.getObjects();
        for (MapObject obj : doorObjects) {
            if (obj instanceof RectangleMapObject) {
                RectangleMapObject doorObj = (RectangleMapObject) obj;
                Door door = new Door(world, tiledMap, doorObj, visualLayerName, collisionLayerName, keyManager);
                doors.add(door);
            }
        }
    }

    /**
     * Updates all doors based on player position
     */
    private void updateDoors() {
        Vector2 playerPosition = player.getPosition();
        for (Door door : doors) {
            door.update(playerPosition);
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

        float playerX = player.getX();
        float playerY = player.getY();

        // random type of enemy
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

        // calculate distance
        float distToPlayer = Vector2.dst(x, y, playerX, playerY);

        if (distToPlayer < 7f) {
            return;
        }

        Enemy enemy = new Enemy(world, x, y, entityManager, entityManager.getPlayer(), type);
        entityManager.addEnemy(enemy);

        // the further away a room is the more chance there is to spawn bigger groups of enemies
        float spawnChance = Math.min(0.7f, distToPlayer / 50f); // Cap at 70% chance

        int maxAdditional = (int)(distToPlayer / 8f);
        maxAdditional = Math.min(4, maxAdditional);

        for (int i = 0; i < maxAdditional; i++) {
            if (Math.random() < spawnChance) {
                float offsetX = (float)((Math.random() * 4) - 2);
                float offsetY = (float)((Math.random() * 4) - 2);

                Enemy companionEnemy = new Enemy(world, x + offsetX, y + offsetY,
                    entityManager, entityManager.getPlayer(), type);
                entityManager.addEnemy(companionEnemy);
            }
        }
    }

    public void render(float delta){
        // esc screen

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
        AudioManager.playButtonClickSound();
        ScreenManager.showPause();
        return;
    }
        int screenWidth = Gdx.graphics.getWidth();
        int screenHeight = Gdx.graphics.getHeight();
        update(delta);
        viewport.apply(true);
        updateCamera();
        // Clear screen
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        // calculate players position in screen coordinates
        Vector3 playerScreenPos = new Vector3(player.getX() * Constants.PPM, player.getY() * Constants.PPM, 0);
        camera.project(playerScreenPos);

        String osName = System.getProperty("os.name").toLowerCase();
        boolean Mac = osName.contains("mac");
        int macMultiplyer = 1;
        if (Mac){
            macMultiplyer = 2;
        }

        ShaderManager.getInstance().updateVignetteShader(
            playerScreenPos.x * macMultiplyer,
            playerScreenPos.y * macMultiplyer,
            screenWidth * macMultiplyer,
            screenHeight * macMultiplyer
        );

        vignetteShader = ShaderManager.getInstance().getVignetteShader();

        // Apply shader to the batch for world rendering
        batch.setShader(vignetteShader);

        // Apply shader to the tiled map renderer
        renderer.getBatch().setShader(vignetteShader);

        renderer.setView((OrthographicCamera)viewport.getCamera());
        renderer.render();

        // Render player
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        player.render(batch);
        entityManager.renderAll();
        batch.end();

        batch.setShader(null);
        debugRenderer.render(world, camera.combined.scl(Constants.PPM));
        // Render cooldown bars
        shapes.setProjectionMatrix(uiProj);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        // Fire bar
        shapes.setColor(Color.DARK_GRAY);
        shapes.rect(10, Gdx.graphics.getHeight() - 40, 100, 8);
        float fireFrac = 1 - (player.getCooldownFireTimer() / player.getCooldownFire());
        shapes.setColor(Color.ORANGE);
        shapes.rect(10, screenHeight-40, 100 * fireFrac, 8);

        // Lightning bar
        shapes.setColor(Color.DARK_GRAY);
        shapes.rect(10, screenHeight-60, 100, 8);
        float lightFrac = 1 - (player.getCooldownLightningTimer() / player.getCooldownLightning());
        shapes.setColor(Color.CYAN);
        shapes.rect(10, screenHeight-60, 100 * lightFrac, 8);

        shapes.end();
        // Render the health bar and adjust size
        batch.setProjectionMatrix(uiProj);
        batch.begin();
        int hp = MathUtils.clamp(player.getHealth(), 0, MAX_HEALTH);
        Sprite bar = healthSprites[hp];
        float desiredWidth = 150f;
        float desiredHeight = 20f;

        bar.setSize(desiredWidth, desiredHeight);
        bar.setPosition(10, Gdx.graphics.getHeight() - desiredHeight - 10);
        bar.draw(batch);

        // Draw key UI
        keyManager.render(batch, 10, Gdx.graphics.getHeight() - desiredHeight - 75, 30, 30);


        // Draw door interaction prompt if player is near a door
        boolean playerNearDoor = false;
        for (Door door : doors) {
            if (door.isPlayerInRange()) {
                playerNearDoor = true;
                break;
            }
        }

        if (playerNearDoor) {
            Door nearbyDoor = doors.stream().filter(Door::isPlayerInRange).findFirst().orElse(null);
            String promptText;

            if (nearbyDoor != null) {
                if (nearbyDoor.isOpen()) {
                    promptText = "Press E to Close Door";
                } else if (nearbyDoor.isLocked() && player.hasKey()) {
                    promptText = "Press E to Unlock Door (Uses 1 Key)";
                } else if (nearbyDoor.isLocked() && !player.hasKey()) {
                    promptText = "Door Locked - Need a Key";
                } else {
                    promptText = "Press E to Open Door";
                }
                font.draw(batch, promptText, screenWidth / 2 - 150, 50);
            }
        }




        batch.end();
    }

    public TiledMap getMap(){
        return tiledMap;
    }
    @Override
    public void resize(int width, int height){

        viewport.update(width, height, true);
        camera.update();

        // UI projection
        uiProj.setToOrtho2D(0, 0, width, height);
    }

    public void pause(){

    }

    public void resume(){

    }

    public void dispose(){
        debugRenderer.dispose();
        shapes.dispose(); // Dispose ShapeRenderer
        renderer.dispose();
        player.dispose();
        world.dispose();
        font.dispose(); // Dispose font
        ShaderManager.getInstance().dispose();

        // Dispose doors
        for (Door door : doors) {
            door.dispose();
        }

        for (Sprite s : healthSprites) {
            s.getTexture().dispose();
        }

        // Dispose key manager
        if (keyManager != null) {
            keyManager.dispose();
        }
    }
}
