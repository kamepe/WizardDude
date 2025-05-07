package com.wizard.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class AudioManager {
    // Background music
    private static Music backgroundMusic;
    private static Music menuMusic;

    // Sound effects
    private static Sound playerSpellSound;
    private static Sound playerDamageSound;
    private static Sound enemyShootSound;
    private static Sound bossShootSound;
    private static Sound enemyDeathSound;
    private static Sound walkingSound;
    private static Sound buttonClickSound;

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
                menuMusic.setVolume(0.5f); // Set volume to 50%

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
            try {
                if (Gdx.files.internal("audio/player_spell.mp3").exists()) {
                    playerSpellSound = Gdx.audio.newSound(Gdx.files.internal("audio/player_spell.mp3"));
                } else {
                    System.out.println("Warning: audio/player_spell.mp3 not found");
                }
            } catch (Exception e) {
                System.out.println("Error loading player_spell.mp3: " + e.getMessage());
            }
        }

        if (playerDamageSound == null) {
            try {
                if (Gdx.files.internal("audio/player_damage.mp3").exists()) {
                    playerDamageSound = Gdx.audio.newSound(Gdx.files.internal("audio/player_damage.mp3"));
                } else {
                    System.out.println("Warning: audio/player_damage.mp3 not found");
                }
            } catch (Exception e) {
                System.out.println("Error loading player_damage.mp3: " + e.getMessage());
            }
        }

        // Initialize enemy sounds
        if (enemyShootSound == null) {
            try {
                if (Gdx.files.internal("audio/enemy_shoot.wav").exists()) {
                    enemyShootSound = Gdx.audio.newSound(Gdx.files.internal("audio/enemy_shoot.wav"));
                } else {
                    System.out.println("Warning: audio/enemy_shoot.wav not found");
                }
            } catch (Exception e) {
                System.out.println("Error loading enemy_shoot.wav: " + e.getMessage());
            }
        }

        if (bossShootSound == null) {
            try {
                if (Gdx.files.internal("audio/boss_shoot.wav").exists()) {
                    bossShootSound = Gdx.audio.newSound(Gdx.files.internal("audio/boss_shoot.wav"));
                } else {
                    System.out.println("Warning: audio/boss_shoot.wav not found");
                }
            } catch (Exception e) {
                System.out.println("Error loading boss_shoot.wav: " + e.getMessage());
            }
        }

        if (enemyDeathSound == null) {
            try {
                if (Gdx.files.internal("audio/enemy_death.mp3").exists()) {
                    enemyDeathSound = Gdx.audio.newSound(Gdx.files.internal("audio/enemy_death.mp3"));
                } else {
                    System.out.println("Warning: audio/enemy_death.mp3 not found");
                }
            } catch (Exception e) {
                System.out.println("Error loading enemy_death.mp3: " + e.getMessage());
            }
        }

        // Initialize UI and movement sounds
        if (walkingSound == null) {
            try {
                if (Gdx.files.internal("audio/walking.mp3").exists()) {
                    walkingSound = Gdx.audio.newSound(Gdx.files.internal("audio/walking.mp3"));
                } else {
                    System.out.println("Warning: audio/walking.mp3 not found");
                }
            } catch (Exception e) {
                System.out.println("Error loading walking.mp3: " + e.getMessage());
            }
        }

        if (buttonClickSound == null) {
            try {
                if (Gdx.files.internal("audio/button_click.mp3").exists()) {
                    buttonClickSound = Gdx.audio.newSound(Gdx.files.internal("audio/button_click.mp3"));
                } else {
                    System.out.println("Warning: audio/button_click.mp3 not found");
                }
            } catch (Exception e) {
                System.out.println("Error loading button_click.mp3: " + e.getMessage());
            }
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

    // Play sound effects
    public static void playPlayerSpellSound() {
        if (playerSpellSound != null) {
            playerSpellSound.play(0.2f);
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

    public static void playBossShootSound() {
        if (bossShootSound != null) {
            bossShootSound.play(0.5f);
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

    // Dispose of resources when no longer needed
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
        if (playerDamageSound != null) {
            playerDamageSound.dispose();
            playerDamageSound = null;
        }
        if (enemyShootSound != null) {
            enemyShootSound.dispose();
            enemyShootSound = null;
        }
        if (bossShootSound != null) {
            bossShootSound.dispose();
            bossShootSound = null;
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
    }
}
