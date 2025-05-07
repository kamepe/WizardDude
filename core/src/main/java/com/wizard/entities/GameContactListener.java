package com.wizard.entities;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

public class GameContactListener implements ContactListener {
  private EntityManager entityManager;
  private Player player;

  public GameContactListener(EntityManager em, Player player) {
    this.entityManager = em;
    this.player = player;
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
  }

  @Override public void endContact(Contact contact) {}
  @Override public void preSolve(Contact contact, Manifold oldManifold) {}
  @Override public void postSolve(Contact contact, ContactImpulse impulse) {}
}
