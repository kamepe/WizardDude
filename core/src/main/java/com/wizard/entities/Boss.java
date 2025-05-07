package com.wizard.entities;
import com.badlogic.gdx.physics.box2d.World;

public class Boss extends Enemy {
    private World world;
    private Player player;
    private EnemyType type;
    private int moveIndex = 0;
    private static final int MOVE_COUNT = 3;
    
    public Boss(World world, float x, float y,
                   EntityManager em, Player player,
                   EnemyType type) {
    super(world,x,y,em,player,type);
  }
}
