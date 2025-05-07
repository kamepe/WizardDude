package com.wizard.entities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;

public class EntityManager {
    private List<Spells> spells = new ArrayList<>();
    private List<Enemy> enemies = new ArrayList<>(); // New list for enemies
    private SpriteBatch batch;
    private Sprite sprite;
    private World world;
    private Player player; // Reference to player

    public EntityManager(World world, SpriteBatch batch){
        this.world = world;
        this.batch = batch;
    }
    
    // Method to set player after creation
    public void setPlayer(Player player) {
        this.player = player;
    }
    
    // Method to add enemies
    public void addEnemy(Enemy enemy) {
        enemies.add(enemy);
    }
    
    public void addToActiveSpells(Spells spell){
        spells.add(spell);
    }
    // Function to iterate over all active spells and update them all 
    public void updateAll(float delta){
        Iterator<Spells> iterator = spells.iterator();
        while(iterator.hasNext()){
            Spells spell = iterator.next();
            spell.update(delta);

            if(spell.shouldDestroy()){
                 world.destroyBody(spell.getBody());
                 iterator.remove();
            }
        }
        
        // update enemies
        Iterator<Enemy> enemyIterator = enemies.iterator();
        while(enemyIterator.hasNext()){
            Enemy enemy = enemyIterator.next();
            enemy.update(delta);
            
            if(enemy.shouldRemove()){
                world.destroyBody(enemy.getBody());
                enemyIterator.remove();
            }
        }
    }
    public void onSpellHitEnemy(Spells s, Enemy e){
        e.takeDamage();
        s.destroy(); 
    }
    public void onSpellHitPlayer(Spells s, Player p){
        p.takeDamage();
        s.destroy();
    }
    public void renderAll() {
        for (Spells spell : spells) {
            spell.render(batch);
        }
        
        for (Enemy enemy : enemies) {
            enemy.render(batch);
        }
    }

    public int getEnemyCount() {
        return enemies.size();
    }
    
    public Player getPlayer() {
        return player;
    }
}