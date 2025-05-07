package com.wizard.entities;

import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.wizard.screens.GameScreen;


public class GameContactListener implements ContactListener {
  private EntityManager entityManager;
  private Player player;
  private TiledMap tiledMap;
  private MapObjects objects;
  private GameScreen gameScreen;

  public GameContactListener(EntityManager em, Player player) {
    this.entityManager = em;
    this.player = player;
    tiledMap = new TmxMapLoader().load("maps/mapo.tmx");
    objects = tiledMap.getLayers().get("collision").getObjects();
  }

  @Override
  public void beginContact(Contact contact) {
    Object ob1 = contact.getFixtureA().getUserData();
    Object ob2 = contact.getFixtureB().getUserData();

    if (ob1 instanceof Spells && ob2 instanceof Player) {
      Spells s = (Spells) ob1;
      Player p = (Player) ob2;
      if (!s.isVisualOnly() && s.getOwner() != p) {
        entityManager.onSpellHitPlayer(s, p);
      }
    } else if (ob2 instanceof Spells && ob1 instanceof Player) {
      Spells s = (Spells) ob2;
      Player p = (Player) ob1;
      if (!s.isVisualOnly() && s.getOwner() != p) {
        entityManager.onSpellHitPlayer(s, p);
      }
    }
    if (ob1 instanceof Spells && ob2 instanceof Enemy) {
      Spells s = (Spells) ob1;
      Enemy e = (Enemy) ob2;
      if (!s.isVisualOnly() && s.getOwner() != e && !(s.getOwner() instanceof Enemy)) {
        entityManager.onSpellHitEnemy(s, e);
      }
    } else if (ob2 instanceof Spells && ob1 instanceof Enemy) {
      Spells s = (Spells) ob2;
      Enemy e = (Enemy) ob1;
      if (!s.isVisualOnly() && s.getOwner() != e && !(s.getOwner() instanceof Enemy)) {
        entityManager.onSpellHitEnemy(s, e);
      }
    }
     if (ob1 instanceof Spells && "wall".equals(ob2)) {
      Spells s = (Spells) ob1;
        entityManager.onSpellHitWall(s);

    } else if (ob2 instanceof Spells && "wall".equals(ob1)) {
      Spells s = (Spells) ob2;
        entityManager.onSpellHitWall(s);
    }
  }

  @Override public void endContact(Contact contact) {}
  @Override public void preSolve(Contact contact, Manifold oldManifold) {}
  @Override public void postSolve(Contact contact, ContactImpulse impulse) {}
}
