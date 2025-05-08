package com.wizard.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.wizard.utils.Constants;
import com.wizard.utils.KeyManager;

/**
 * Represents a door in the game that can be opened and closed by the player.
 */
public class Door {
    private static final float INTERACTION_DISTANCE = 0.3f; // Distance in world units for interaction

    private final World world;
    private final TiledMap map;
    private final Rectangle bounds;
    private final String doorLayerName;
    private final String doorCollisionLayerName;
    private final MapLayer doorLayer;
    private final MapLayer doorCollisionLayer;
    private final RectangleMapObject doorObject;

    private Body collisionBody;
    private boolean isOpen;
    private boolean playerInRange;
    private boolean unlocked;
    private KeyManager keyManager;

    /**
     * Creates a new door with the specified parameters.
     *
     * @param world The Box2D world
     * @param map The tiled map
     * @param doorObject The rectangle map object representing the door
     * @param doorLayerName The name of the door's visual layer
     * @param doorCollisionLayerName The name of the door's collision layer
     */
    public Door(World world, TiledMap map, RectangleMapObject doorObject,
                String doorLayerName, String doorCollisionLayerName) {
        this(world, map, doorObject, doorLayerName, doorCollisionLayerName, null);
    }

    /**
     * Creates a new door with the specified parameters.
     *
     * @param world The Box2D world
     * @param map The tiled map
     * @param doorObject The rectangle map object representing the door
     * @param doorLayerName The name of the door's visual layer
     * @param doorCollisionLayerName The name of the door's collision layer
     * @param keyManager The key manager to check for keys and unlocked doors
     */
    public Door(World world, TiledMap map, RectangleMapObject doorObject,
                String doorLayerName, String doorCollisionLayerName, KeyManager keyManager) {
        this.world = world;
        this.map = map;
        this.doorObject = doorObject;
        this.doorLayerName = doorLayerName;
        this.doorCollisionLayerName = doorCollisionLayerName;
        this.keyManager = keyManager;

        this.doorLayer = map.getLayers().get(doorLayerName);
        this.doorCollisionLayer = map.getLayers().get(doorCollisionLayerName);

        // Get the door bounds
        this.bounds = doorObject.getRectangle();

        // Convert bounds to world units
        float unitScale = 1f / Constants.PPM;
        Rectangle worldBounds = new Rectangle(
            bounds.x * unitScale,
            bounds.y * unitScale,
            bounds.width * unitScale,
            bounds.height * unitScale
        );

        // Create collision body
        createCollisionBody(worldBounds);

        // Door starts closed and locked
        this.isOpen = false;
        this.playerInRange = false;
        this.unlocked = false;
    }

    /**
     * Creates the collision body for the door.
     */
    private void createCollisionBody(Rectangle worldBounds) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(
            worldBounds.x + worldBounds.width * 0.5f,
            worldBounds.y + worldBounds.height * 0.5f
        );

        collisionBody = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(
            worldBounds.width * 0.5f,
            worldBounds.height * 0.5f
        );

        Fixture doorFixture = collisionBody.createFixture(shape, 1.0f);
        doorFixture.setUserData("door");

        shape.dispose();
    }

    /**
     * Updates the door state based on player position and input.
     *
     * @param playerPosition The player's current position
     */
    public void update(Vector2 playerPosition) {
        // Check if player is in range
        float doorCenterX = (bounds.x + bounds.width * 0.5f) / Constants.PPM;
        float doorCenterY = (bounds.y + bounds.height * 0.5f) / Constants.PPM;

        float distance = Vector2.dst(
            playerPosition.x, playerPosition.y,
            doorCenterX, doorCenterY
        );

        playerInRange = distance <= INTERACTION_DISTANCE;

        // Handle door interaction
        if (playerInRange && Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            // If door is already unlocked, toggle it freely
            if (unlocked || keyManager.isDoorUnlocked(this)) {
                toggleDoorState();
            }
            // If player has a key, use it to unlock the door
            else if (keyManager.hasKey()) {
                if (keyManager.useKey(this)) {
                    unlocked = true;
                    toggleDoorState();
                }
            }
        }
    }

    /**
     * Toggles the door between open and closed states.
     */
    private void toggleDoorState() {
        isOpen = !isOpen;

        // Toggle door visibility
        doorLayer.setVisible(!isOpen);

        // Toggle collision
        if (isOpen) {
            // Disable collision
            collisionBody.setActive(false);
        } else {
            // Enable collision
            collisionBody.setActive(true);
        }
    }

    /**
     * Checks if the player is in range to interact with the door.
     *
     * @return true if player is in range, false otherwise
     */
    public boolean isPlayerInRange() {
        return playerInRange;
    }

    /**
     * Checks if the door is currently open.
     *
     * @return true if door is open, false if closed
     */
    public boolean isOpen() {
        return isOpen;
    }

    /**
     * Checks if the door is locked (requires a key to open).
     *
     * @return true if door is locked, false if unlocked
     */
    public boolean isLocked() {
        return !unlocked && (keyManager == null || !keyManager.isDoorUnlocked(this));
    }

    /**
     * Sets the key manager for this door.
     *
     * @param keyManager The key manager to use
     */
    public void setKeyManager(KeyManager keyManager) {
        this.keyManager = keyManager;
    }

    /**
     * Disposes of resources when no longer needed.
     */
    public void dispose() {
        // Remove body from world when door is no longer needed
        world.destroyBody(collisionBody);
    }
}
