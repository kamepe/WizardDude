package com.wizard.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class AudioManager {

    // Background music
    private static Music backgroundMusic;
    private static Music menuMusic;
    private static boolean isMuted = false;
    private static float savedBackgroundVolume = 0.2f;
    private static float savedMenuVolume = 0.5f;

    // Sound effects
    private static Sound playerSpellSound;
    private static Sound playerFireballSound;
    private static Sound playerDamageSound;
    private static Sound enemyShootSound;
    private static Sound enemyMeleeAttackSound;
    private static Sound enemyRangedAttackSound;
    private static Sound bossShootSound;
    private static Sound bossSpellCastSound;
    private static Sound enemyDeathSound;
    private static Sound walkingSound;
    private static Sound buttonClickSound;
    private static Sound speaksound;

    // Track walking sound state
    private static long walkingSoundId = -1;
    private static boolean isWalkingSoundPlaying = false;

    // Initialize the background music
    public static void initBackgroundMusic(String musicFile) {
        if (backgroundMusic != null) {
            backgroundMusic.dispose();
        }
        try {
            if (Gdx.files.internal(musicFile).exists()) {
                backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal(musicFile));
                backgroundMusic.setLooping(true);
                backgroundMusic.setVolume(0.2f);

                // Add completion listener to ensure music loops even if it ends unexpectedly
                backgroundMusic.setOnCompletionListener(music -> {
                    if (!music.isLooping()) {
                        music.play();
                    }
                });
            } else {
                System.out.println("Warning: " + musicFile + " not found");
            }
        } catch (Exception e) {
            System.out.println("Error loading background music: " + e.getMessage());
        }
    }

    // Initialize the menu music
    public static void initMenuMusic(String musicFile) {
        if (menuMusic != null) {
            menuMusic.dispose();
        }
        try {
            if (Gdx.files.internal(musicFile).exists()) {
                menuMusic = Gdx.audio.newMusic(Gdx.files.internal(musicFile));
                menuMusic.setLooping(true);
                menuMusic.setVolume(0.5f);

                // Add completion listener to ensure music loops even if it ends unexpectedly
                menuMusic.setOnCompletionListener(music -> {
                    if (!music.isLooping()) {
                        music.play();
                    }
                });
            } else {
                System.out.println("Warning: " + musicFile + " not found");
            }
        } catch (Exception e) {
            System.out.println("Error loading menu music: " + e.getMessage());
        }
    }

    // Initialize all sound effects
    public static void initSoundEffects() {
        // Initialize player sounds
        if (playerSpellSound == null) {
            playerSpellSound = Gdx.audio.newSound(Gdx.files.internal("audio/player_spell_cast2.mp3"));
        }

        if (playerFireballSound == null) {
            playerFireballSound = Gdx.audio.newSound(Gdx.files.internal("audio/player_fireball.mp3"));
        }

        if (playerDamageSound == null) {
            playerDamageSound = Gdx.audio.newSound(Gdx.files.internal("audio/player_damage.mp3"));
        }

        // Initialize enemy sounds

        if (enemyMeleeAttackSound == null) {
            enemyMeleeAttackSound = Gdx.audio.newSound(Gdx.files.internal("audio/enemy_mele_attack.mp3"));
        }

        if (enemyRangedAttackSound == null) {
            enemyRangedAttackSound = Gdx.audio.newSound(Gdx.files.internal("audio/enemy_ranged_attack.mp3"));
        }

        //speak storyline sound

        if (speaksound == null) {
            speaksound = Gdx.audio.newSound(Gdx.files.internal("audio/speaksound.mp3"));
        }


        if (bossSpellCastSound == null) {
            bossSpellCastSound = Gdx.audio.newSound(Gdx.files.internal("audio/boss_spell_cast.mp3"));
        }

        if (enemyDeathSound == null) {
            enemyDeathSound = Gdx.audio.newSound(Gdx.files.internal("audio/enemy_death.mp3"));
        }

        // Initialize UI and movement sounds
        if (walkingSound == null) {
            walkingSound = Gdx.audio.newSound(Gdx.files.internal("audio/walking.mp3"));
        }

        if (buttonClickSound == null) {
            buttonClickSound = Gdx.audio.newSound(Gdx.files.internal("audio/button_click.mp3"));
        }
    }

    // Play the background music
    public static void playBackgroundMusic() {
        if (backgroundMusic != null && !backgroundMusic.isPlaying()) {
            backgroundMusic.play();
        }
    }

    // Play the menu music
    public static void playMenuMusic() {
        if (menuMusic != null && !menuMusic.isPlaying()) {
            menuMusic.play();
        }
    }

    // Pause the background music
    public static void pauseBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isPlaying()) {
            backgroundMusic.pause();
        }
    }

    // Pause the menu music
    public static void pauseMenuMusic() {
        if (menuMusic != null && menuMusic.isPlaying()) {
            menuMusic.pause();
        }
    }

    // Stop the background music
    public static void stopBackgroundMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
        }
    }

    // Stop the menu music
    public static void stopMenuMusic() {
        if (menuMusic != null) {
            menuMusic.stop();
        }
    }

    //Speak sound storyline

    public static void playSpeakSound() {
        if (speaksound != null) {
            speaksound.play(0.5f);
        }
    }

    // Play sound effects
    public static void playPlayerSpellSound() {
        if (playerSpellSound != null) {
            playerSpellSound.play(0.2f);
        }
    }

    public static void playPlayerFireballSound() {
        if (playerFireballSound != null) {
            playerFireballSound.play(0.3f);
        }
    }

    public static void playPlayerDamageSound() {
        if (playerDamageSound != null) {
            playerDamageSound.play(0.5f);
        }
    }

    public static void playEnemyShootSound() {
        if (enemyShootSound != null) {
            enemyShootSound.play(0.5f);
        }
    }

    public static void playEnemyMeleeAttackSound() {
        if (enemyMeleeAttackSound != null) {
            enemyMeleeAttackSound.play(0.5f);
        }
    }

    public static void playEnemyRangedAttackSound() {
        if (enemyRangedAttackSound != null) {
            enemyRangedAttackSound.play(0.2f);
        }
    }

    public static void playBossShootSound() {
        if (bossShootSound != null) {
            bossShootSound.play(0.5f);
        }
    }

    public static void playBossSpellCastSound() {
        if (bossSpellCastSound != null) {
            bossSpellCastSound.play(0.5f);
        }
    }

    public static void playEnemyDeathSound() {
        if (enemyDeathSound != null) {
            enemyDeathSound.play(2.4f);
        }
    }

    public static void playWalkingSound() {
        if (walkingSound != null && !isWalkingSoundPlaying) {
            walkingSoundId = walkingSound.play(0.4f);
            walkingSound.setLooping(walkingSoundId, true); // Set the sound to loop
            isWalkingSoundPlaying = true;
        }
    }

    public static void stopWalkingSound() {
        if (walkingSound != null && isWalkingSoundPlaying) {
            walkingSound.stop(walkingSoundId);
            isWalkingSoundPlaying = false;
        }
    }

    public static void playButtonClickSound() {
        if (buttonClickSound != null) {
            buttonClickSound.play(0.5f);
        }
    }


    // Toggle mute state for all music
    public static void toggleMute() {
        if (isMuted) {
            unmuteMusic();
        } else {
            muteMusic();
        }
    }

    // Mute all music
    public static void muteMusic() {
        if (!isMuted) {
            if (backgroundMusic != null) {
                savedBackgroundVolume = backgroundMusic.getVolume();
                backgroundMusic.setVolume(0);
            }
            if (menuMusic != null) {
                savedMenuVolume = menuMusic.getVolume();
                menuMusic.setVolume(0);
            }
            isMuted = true;
        }
    }

    // Unmute all music
    public static void unmuteMusic() {
        if (isMuted) {
            if (backgroundMusic != null) {
                backgroundMusic.setVolume(savedBackgroundVolume);
            }
            if (menuMusic != null) {
                menuMusic.setVolume(savedMenuVolume);
            }
            isMuted = false;
        }
    }

    // Check if music is muted
    public static boolean isMuted() {
        return isMuted;
    }

    public static void dispose() {
        // Dispose music resources
        if (backgroundMusic != null) {
            backgroundMusic.dispose();
            backgroundMusic = null;
        }
        if (menuMusic != null) {
            menuMusic.dispose();
            menuMusic = null;
        }

        // Dispose sound effect resources
        if (playerSpellSound != null) {
            playerSpellSound.dispose();
            playerSpellSound = null;
        }
        if (playerFireballSound != null) {
            playerFireballSound.dispose();
            playerFireballSound = null;
        }
        if (playerDamageSound != null) {
            playerDamageSound.dispose();
            playerDamageSound = null;
        }
        if (enemyShootSound != null) {
            enemyShootSound.dispose();
            enemyShootSound = null;
        }
        if (enemyMeleeAttackSound != null) {
            enemyMeleeAttackSound.dispose();
            enemyMeleeAttackSound = null;
        }
        if (enemyRangedAttackSound != null) {
            enemyRangedAttackSound.dispose();
            enemyRangedAttackSound = null;
        }
        if (bossShootSound != null) {
            bossShootSound.dispose();
            bossShootSound = null;
        }
        if (bossSpellCastSound != null) {
            bossSpellCastSound.dispose();
            bossSpellCastSound = null;
        }
        if (enemyDeathSound != null) {
            enemyDeathSound.dispose();
            enemyDeathSound = null;
        }
        if (walkingSound != null) {
            if (isWalkingSoundPlaying) {
                walkingSound.stop(walkingSoundId);
                isWalkingSoundPlaying = false;
            }
            walkingSound.dispose();
            walkingSound = null;
        }
        if (buttonClickSound != null) {
            buttonClickSound.dispose();
            buttonClickSound = null;
        }

        if (speaksound != null) {
            speaksound.dispose();
            speaksound = null;
        }
    }
}
