package com.wizard.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.wizard.entities.Door;

import java.util.HashSet;
import java.util.Set;

public class KeyManager {
    private int availableKeys;
    private Set<Door> unlockedDoors;
    private Texture keyTexture;
    private Texture grayedKeyTexture;
    private Sprite keySprite;
    private Sprite grayedKeySprite;

    public KeyManager(int initialKeys) {
        this.availableKeys = initialKeys;
        this.unlockedDoors = new HashSet<>();

        keyTexture = new Texture(Gdx.files.internal("key_1.png"));
        grayedKeyTexture = new Texture(Gdx.files.internal("key_2.png"));

        keySprite = new Sprite(keyTexture);
        grayedKeySprite = new Sprite(grayedKeyTexture);
    }

    public boolean hasKey() {
        return availableKeys > 0;
    }

    public boolean useKey(Door door) {
        if (availableKeys <= 0 || unlockedDoors.contains(door)) {
            return false;
        }

        availableKeys--;
        unlockedDoors.add(door);
        return true;
    }

    public void addKey() {
        availableKeys++;
    }

    public boolean isDoorUnlocked(Door door) {
        return unlockedDoors.contains(door);
    }

    public int getAvailableKeys() {
        return availableKeys;
    }

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

    public void dispose() {
        keyTexture.dispose();
        grayedKeyTexture.dispose();
    }
}
