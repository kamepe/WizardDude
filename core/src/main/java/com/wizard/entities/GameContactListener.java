package com.wizard.entities;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

public class GameContactListener implements ContactListener{
    private EntityManager entityManager;
    private Player player;

    public GameContactListener(EntityManager em, Player player){
        this.entityManager = em;
        this.player = player;
    }
    @Override
    public void  beginContact(Contact contact){
      Object ob1 =  contact.getFixtureA().getUserData();
      Object ob2 =  contact.getFixtureB().getUserData();
      
      if(ob1 instanceof Spells && ob2 instanceof Enemy){
        Spells s = (Spells) ob1;
        Enemy e = (Enemy) ob2;
        if (s.getOwner() != e){
            entityManager.onSpellHitEnemy((Spells) ob1, (Enemy) ob2);
        }
      }else if(ob2 instanceof  Spells && ob1 instanceof Enemy){
        Spells s = (Spells) ob2;
        Enemy e = (Enemy) ob1;
        if (s.getOwner() != e){
            entityManager.onSpellHitEnemy((Spells) ob2,(Enemy) ob1);
        }
      }
    }
    @Override public void endContact(Contact contact) { }
    @Override public void preSolve(Contact contact, Manifold oldManifold) { }
    @Override public void postSolve(Contact contact, ContactImpulse impulse) { }
}