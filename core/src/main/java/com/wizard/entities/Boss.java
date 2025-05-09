package com.wizard.entities;
import com.badlogic.gdx.utils.Timer;
import com.wizard.screens.ScreenManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.wizard.utils.AudioManager;
import com.wizard.utils.KeyManager;

public class Boss extends Enemy {

    private static int bossKillCount = 0;
    private static final int TOTAL_BOSSES = 3;

    private final Sprite fireballSprite;
    private final Sprite lightningSprite;
    private final Sprite icegSprite;
    private World world;

    public Boss(World world, float x, float y,
                EntityManager em, Player player,
                EnemyType type) {
        // must call super first
        super(world, x, y, em, player, type);

        Texture fireballTex = new Texture(
            Gdx.files.internal("spells/firebullet.png")
        );
        Texture lightningTex = new Texture(
            Gdx.files.internal("spells/ball.png")
        );
        Texture iceTex = new Texture(
            Gdx.files.internal("spells/ice.png")
        );
        fireballSprite = new Sprite(fireballTex);
        lightningSprite = new Sprite(lightningTex);
        icegSprite = new Sprite(iceTex);
        // size in world units:
        fireballSprite.setSize(0.5f, 0.5f);
        fireballSprite.setOriginCenter();
    }

    @Override
    protected void attackPlayer(Vector2 direction) {
        int choice = MathUtils.random(0, 2);
        switch (choice) {
            case 0: attackSpray(direction);  break;
            case 1: attackCircle(direction); break;
            default:attackRapid(direction);  break;
        }
        AudioManager.playBossSpellCastSound();
    }


    private void attackSpray(Vector2 dir) {
        float base = dir.angleDeg();
        for (int i = -2; i <= 2; i++) {
            Vector2 d = new Vector2(1, 0).setAngleDeg(base + i * 10);
            spawnSpellFire(d);
        }
    }


    private void attackCircle(Vector2 dir) {
        for (int i = 0; i < 360; i += 3) {
            Vector2 d = new Vector2(1, 0).setAngleDeg(i * 10);
            spawnSpellIce(d);
        }
    }


    private void attackRapid(Vector2 dir) {
        float interval = 0.1f;
        for (int i = 0; i < 10; i++) {
           final Vector2 d = new Vector2(dir);
           Timer.schedule(new Timer.Task(){
                @Override
                public void run() {
                    spawnSpellLightning(d);
                }
            }, i * interval);
        }
    }

    private void spawnSpellFire(Vector2 dir) {
         getEntityManager().addToActiveSpells(new Spells(
            getWorld(),
            getBody().getPosition().x,
            getBody().getPosition().y,
            dir,
            0.5f,
            0.5f,
            0.8f,
            "spells/firebullet.png",
            this,
             true
        ));
    }

    private void spawnSpellLightning(Vector2 dir) {
         getEntityManager().addToActiveSpells(new Spells(
            getWorld(),
            getBody().getPosition().x,
            getBody().getPosition().y,
            dir,
            0.2f,
            0.2f,
            1.0f,
            "spells/ball.png",
            this
        ));
    }

    private void spawnSpellIce(Vector2 dir) {
         getEntityManager().addToActiveSpells(new Spells(
            getWorld(),
            getBody().getPosition().x,
            getBody().getPosition().y,
            dir,
            0.6f,
            0.6f,
            0.3f,
            new Sprite(icegSprite),
            this
        ));
    }


    @Override
    public void onDeath() {
        super.onDeath();


        Player player = getEntityManager().getPlayer();
        if (player != null) {
            player.addKey();


            bossKillCount++;



            if (bossKillCount >= TOTAL_BOSSES) {


                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        ScreenManager.showGameOver();
                    }
                }, 1.5f);
            }
        }
    }

    public static void resetBossKillCount() {
        bossKillCount = 0;
    }
}
