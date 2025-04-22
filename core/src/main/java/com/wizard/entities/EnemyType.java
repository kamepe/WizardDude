package com.wizard.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class EnemyType {
    // sprites
    private String leftSprite;
    private String rightSprite;
    private String upSprite;
    private String downSprite;

    // stats
    private int health;
    private Texture attackSprite;
    private boolean isRanged;
    private float moveSpeed;
    private float attackCooldown;
    private float detectionRange;
    
    // constructor
    public EnemyType(String upSprite, String downSprite, String leftSprite, String rightSprite,
                    int health, float attackCooldown, float detectionRange, float moveSpeed,
                    String attackSpritePath, boolean isRanged) {
        this.upSprite = upSprite;
        this.rightSprite = rightSprite;
        this.downSprite = downSprite;
        this.leftSprite = leftSprite;
        this.detectionRange = detectionRange;
        this.moveSpeed = moveSpeed;
        this.health = health;
        this.attackCooldown = attackCooldown;
        this.attackSprite = new Texture(Gdx.files.internal(attackSpritePath));
        this.isRanged = isRanged;
    }
    

    // here is how different enemies are created: (I can add more variables for customization later)
    //------------------------------------------------------------------------------------------------------------------------------------------------------
    public static EnemyType RANGED_WIZARD = new EnemyType(
        "e_up.png", "e_down.png", "e_left.png", "e_right.png",
        2, 1.2f, 2.2f, 0.5f,
        "spells/fireball.png", true
    );
    
    public static EnemyType MELEE_SKELETON = new EnemyType(
        "skeleton_up.png", "skeleton_down.png", "skeleton_left.png", "skeleton_right.png",
        3, 1.2f, 0.2f, 0.45f,
        "weapons/spiky.png", false
    );
    //------------------------------------------------------------------------------------------------------------------------------------------------------

    
    // getters
    public String getUpSprite() { return upSprite; }
    public String getDownSprite() { return downSprite; }
    public String getLeftSprite() { return leftSprite; }
    public String getRightSprite() { return rightSprite; }
    
    public int getHealth() { return health; }
    public float getAttackCooldown() { return attackCooldown; }
    public float getDetectionRange() { return detectionRange; }
    public float getMoveSpeed() { return moveSpeed; }
    public Texture getAttackSprite() { return attackSprite; }
    public boolean isRanged() { return isRanged; }
    
    public void dispose() {
        if (attackSprite != null) {
            attackSprite.dispose();
        }
    }
}
