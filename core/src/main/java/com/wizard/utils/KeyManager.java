package com.wizard.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.wizard.entities.Door;

import java.util.HashSet;
import java.util.Set;

/**
 * Manages the player's keys and unlocked doors.
 */
public class KeyManager {
    private int availableKeys;
    private Set<Door> unlockedDoors;
    private Texture keyTexture;
    private Texture grayedKeyTexture;
    private Sprite keySprite;
    private Sprite grayedKeySprite;

    /**
     * Creates a new KeyManager with the specified number of initial keys.
     *
     * @param initialKeys The number of keys the player starts with
     */
    public KeyManager(int initialKeys) {
        this.availableKeys = initialKeys;
        this.unlockedDoors = new HashSet<>();

        // Load textures for keys (placeholders for now)
        keyTexture = new Texture(Gdx.files.internal("key_1.png")); // Placeholder
        grayedKeyTexture = new Texture(Gdx.files.internal("key_2.png")); // Placeholder

        keySprite = new Sprite(keyTexture);
        grayedKeySprite = new Sprite(grayedKeyTexture);
    }

    /**
     * Checks if the player has any available keys.
     *
     * @return true if the player has at least one key, false otherwise
     */
    public boolean hasKey() {
        return availableKeys > 0;
    }

    /**
     * Uses a key to unlock a door.
     *
     * @param door The door to unlock
     * @return true if the door was successfully unlocked, false otherwise
     */
    public boolean useKey(Door door) {
        if (availableKeys <= 0 || unlockedDoors.contains(door)) {
            return false;
        }

        availableKeys--;
        unlockedDoors.add(door);
        return true;
    }

    /**
     * Adds a key to the player's inventory.
     */
    public void addKey() {
        availableKeys++;
    }

    /**
     * Checks if a door has been unlocked.
     *
     * @param door The door to check
     * @return true if the door has been unlocked, false otherwise
     */
    public boolean isDoorUnlocked(Door door) {
        return unlockedDoors.contains(door);
    }

    /**
     * Gets the number of available keys.
     *
     * @return The number of available keys
     */
    public int getAvailableKeys() {
        return availableKeys;
    }

    /**
     * Renders the key UI.
     *
     * @param batch The SpriteBatch to use for rendering
     * @param x The x-coordinate to render at
     * @param y The y-coordinate to render at
     * @param width The width of the key sprite
     * @param height The height of the key sprite
     */
    public void render(SpriteBatch batch, float x, float y, float width, float height) {
        if (hasKey()) {
            keySprite.setSize(width, height);
            keySprite.setPosition(x, y);
            keySprite.draw(batch);
        } else {
            grayedKeySprite.setSize(width, height);
            grayedKeySprite.setPosition(x, y);
            grayedKeySprite.draw(batch);
        }
    }

    /**
     * Disposes of resources when no longer needed.
     */
    public void dispose() {
        keyTexture.dispose();
        grayedKeyTexture.dispose();
    }
}
