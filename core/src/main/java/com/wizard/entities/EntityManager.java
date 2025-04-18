package com.wizard.entities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;

public class EntityManager {
    private List<Spells> spells = new ArrayList<>();
    private SpriteBatch batch;
    private Sprite sprite;
    private World world;

    public EntityManager(World world, SpriteBatch batch){
        this.world = world;
        this.batch = batch;
    }
    public void addToActiveSpells(Spells spell ){
        spells.add(spell);
    }
    // Function to iterate over all active spells and update them all 
    public void updateAll(float delta){
        batch.begin();
        Iterator<Spells> iterator = spells.iterator();
        while(iterator.hasNext()){
            Spells spell = iterator.next();
            spell.update(delta);

            if(spell.shouldDestroy()){
                 world.destroyBody(spell.getBody());
                 iterator.remove();
            }
            spell.render(batch);
        }
        batch.end();
    }

}