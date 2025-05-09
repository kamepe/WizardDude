# WizardDude – System Architecture

## 1. Overview  
_WizardDude_ is a 2D top-down action game built with LibGDX.  Its core responsibilities are:
- **Rendering** a Tiled map + animated sprites  
- **Physics** via Box2D (movement, collisions)  
- **Entity management** (player, enemies, spells)  
- **Input handling** (keyboard + mouse)  
- **Game logic** (spawning, health, cooldowns, AI)

---


## 2. High-Level Module Structure
```bash
root
├── core
│ ├── src
│ │ ├── com.wizard.screens # ScreenAdapter implementations
│ │ ├── com.wizard.entities # Player, Enemy, Spells, EntityManager, Contacts
│ │ └── com.wizard.utils # Constants, Animator, AudioManager, ShaderManager
├── desktop # LWJGL3 launcher
├── assets # .tmx, textures, fonts, sounds
└── docs
└── ARCHITECTURE.md # <-- you are here

```
---
## 3. Main Components

### 3.1 GameScreen  
- **Game loop**: calls `world.step()`, updates camera, entities, renders map & sprites.  
- **Entity spawning**: spawns various types of enemies, including bosses  
- **UI**: shows health bar and fireball and lightning spell cooldown bars, it is updated each `world.step()`

### 3.2 EntityManager  
- Holds active `Spells` & `Enemy` instances in a list and checks on each update call in the GameScreen if they should still exist
- `updateAll()` steps each and removes when `shouldRemove()`, this is always called in the GameScreen.  
- Is related to GameContactListener, in order to destroy entities on contacts.

### 3.3 Player / Enemy  
- **Box2D body** + **Sprite/Animator**.  
- Movement sets body velocity, meanwhile the update function reads Box2D position for drawing sprites and animations.  
- Spell-casting checks where mouse position is currently and creates a vector towards it, if the player is firing and  creates a vector towards the player if the enemy is firing.

### 3.4 Spells    
- Self-destruct after a lifetime or on collision.
- Both the player and Enemies attack using spells and each spell instance is created upon firing.

### 3.5 Collision Handling  
- `GameContactListener` implements `ContactListener`.  
- Each entity has flags to check if they are spell owners, so they do not detect colision on themselves.

---

## 4. Data & Control Flow

1. **Input** is managed by  `Player.handleMovement` & `handleSpellCast`.  
2. On cast: new `Spells`  are added to `EntityManager` and are removed based on the set conditions.  
3. **World.step** advances the entire game and all the update calls.  
4. **GameContactListener** detects collisions   
5. **GameScreen.render** draws map, player, entities, UI.

---

## 5. Build & Run  
- **Gradle** based, with `core` and `desktop` subprojects.  
- Launch via `./gradlew desktop:run`.  
- Assets live in `/assets`, automatically copied.

---