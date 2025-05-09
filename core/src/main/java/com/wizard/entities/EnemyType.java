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
    private int spriteSize = 32; // Default sprite size
    private String attackSpritePath;

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
        this.attackSpritePath = attackSpritePath;
    }

    // Overloaded constructor with sprite size
    public EnemyType(String upSprite, String downSprite, String leftSprite, String rightSprite, int health, float attackCooldown, float detectionRange, float moveSpeed,
                    String attackSpritePath, boolean isRanged, int spriteSize) {this(upSprite, downSprite, leftSprite, rightSprite, health, attackCooldown,
            detectionRange, moveSpeed, attackSpritePath, isRanged);
            this.spriteSize = spriteSize;
    }

    // here is how different enemies are created: (I can add more variables for customization later)
    //------------------------------------------------------------------------------------------------------------------------------------------------------
    public static EnemyType RANGED_WIZARD = new EnemyType(
        "e_up.png", "e_down.png", "e_left.png", "e_right.png",
        2, 1.2f, 2.0f, 0.5f,
        "spells/firebullet.png", true
    );

    public static EnemyType MELEE_SKELETON = new EnemyType(
        "skeleton_up.png", "skeleton_down.png", "skeleton_left.png", "skeleton_right.png",
        3, 1.2f, 0.2f, 0.45f,
        "weapons/spiky.png", false
    );
    //------------------------------------------------------------------------------------------------------------------------------------------------------
    // the 3 bosses for the 3 rooms
    public static EnemyType SMALL_BOSS = new EnemyType(
        "SkeletonKingUpWalk.png", "SkeletonKingDownWalk.png", "SkeletonKingLeftWalk.png", "SkeletonKingRightWalk.png",
        6, 3.0f, 3.0f, 0.6f,
        "spells/fireball.png", true,
        48 // 48x48 sprite size
    );

    public static EnemyType MEDIUM_BOSS = new EnemyType(
        "SkeletonKingUpWalk.png", "SkeletonKingDownWalk.png", "SkeletonKingLeftWalk.png", "SkeletonKingRightWalk.png",
        8, 2.0f, 1f, 0.5f,
        "spells/fireball.png", true,
        48 // 48x48 sprite size
    );

    public static EnemyType LARGE_BOSS = new EnemyType(
        "SkeletonKingUpWalk.png", "SkeletonKingDownWalk.png", "SkeletonKingLeftWalk.png", "SkeletonKingRightWalk.png",
        12, 0.6f, 3.5f, 0.45f,
        "spells/fireball.png", true,
        48 // 48x48 sprite size
    );

    // getters
    public String getUpSprite() {
        return upSprite;
    }
    public String getDownSprite() {
        return downSprite;
    }
    public String getLeftSprite() { return leftSprite; }
    public String getRightSprite() { return rightSprite; }

    public int getHealth() { return health; }
    public float getAttackCooldown() { return attackCooldown; }
    public float getDetectionRange() {
        return detectionRange;
    }
    public float getMoveSpeed() { return moveSpeed; }
    public Texture getAttackSprite() {
        return attackSprite;
    }
    public String getAttackPath(){return attackSpritePath;}
    public boolean isRanged() { return isRanged; }
    public int getSpriteSize() {
        return spriteSize;
    }

    public void dispose() {
        if (attackSprite != null) {
            attackSprite.dispose();
        }
    }
}
