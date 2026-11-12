# 💧 Window Droplet — 2D Raindrop Physics Game

[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://android.com)
[![Language](https://img.shields.io/badge/Language-Kotlin-purple.svg)](https://kotlinlang.org)
[![UI](https://img.shields.io/badge/UI-Jetpack%20Compose%20Canvas-blue.svg)](https://developer.android.com/jetpack/compose)
[![Architecture](https://img.shields.io/badge/Architecture-Clean%20%2F%20Hexagonal-orange.svg)](#architecture)

**Window Droplet** is a high-performance, 2D physics-based Android game built entirely with **Jetpack Compose Canvas**. It recreates the nostalgic and soothing experience of raindrops falling and merging on a windowpane on a rainy day.

---

## 🎮 Gameplay & Core Concept

* **Player Droplet:** The player controls a raindrop sliding downward on glass under gravity.
* **Liquid Merging:** Absorb smaller water droplets on the pane to increase mass, size, and downward momentum.
* **Mass-Velocity Dynamics:** As mass grows, terminal velocity increases and steering inertia intensifies, demanding higher player agility.
* **Obstacles & Hazards:**
  * ⚡ **Glass Cracks (Fatal):** Deep fractures that split the droplet on direct impact, causing immediate Game Over.
  * 💨 **Dust Patches (Depleting):** Absorb moisture and shrink droplet volume. If water volume reaches 0%, the droplet evaporates.
  * 🌀 **Grease Spots (Slipping):** Causes sudden loss of surface grip and unpredictable lateral momentum drift.
* **Dynamic Endless Scenery:** Every 200–250 meters, the backdrop and physics smoothly shift between different rainy environments.

---

## 🏞️ Environments & Seasons

1. **🏙️ Monsoon City (শহুরে বৃষ্টি):** Multi-story building window overlooking blurred city traffic lights and bokeh reflections.
2. **🚗 Vehicle Windshield (গাড়ির উইন্ডশিল্ড):** Highway speed with continuous lateral wind forces pushing raindrops sideways.
3. **❄️ Winter Frost (শীতের কুয়াশা):** Slippery frost and condensation on freezing glass.
4. **🏡 Village Rain (গ্রামের স্নিগ্ধ বৃষ্টি):** Gentle rainfall with natural green ambient tones on a rustic wooden-framed window.

---

## 🕹️ Controls & Ergonomics

* **Bottom Touch Steering Pad (Bottom 65% of screen):** Touch anywhere in the lower portion of the screen to guide the droplet.
* **Zero Finger Occlusion:** The player droplet is anchored at the top 28% of the viewport, so your finger never blocks the view of the droplet or incoming obstacles.
* **Direct Finger Tracking & 2.8x Agility:** The droplet swiftly tracks your finger’s horizontal position with amplified swipe sensitivity for fatigue-free gameplay.

---

## 🏗️ Architecture & Technical Highlights

Built strictly following Clean Architecture and global mobile game performance guidelines:

* **Engine Layer (`engine/`):** Pure Kotlin physics formulas, collision detection, and procedural gap spawning without UI dependencies.
* **Model Layer (`model/`):** 100% immutable data classes (`Droplet`, `Obstacle`, `CollectibleDrop`, `GameUiState`).
* **ViewModel Layer (`viewmodel/`):** Coroutine-driven unidirectional state flow (`StateFlow<GameUiState>`).
* **UI Layer (`ui/`):** Declarative Jetpack Compose Canvas with **zero-allocation draw loops** (`withFrameNanos` frame ticker matching 60Hz/120Hz refresh rates).
* **Audio Subsystem (`audio/`):** Low-latency procedural audio playback via Android `SoundPool`.

---

## 📁 Repository Structure

```
.
├── AGENTS.md                                                  # Engineering rules & master blueprint
├── PROJECT_CONTEXT_AND_CONVERSATION_HISTORY.md                 # Complete development history & specifications
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── res/
│       │   ├── values/strings.xml & themes.xml
│       │   └── raw/water_merge.wav & obstacle_hit.wav         # Sound assets
│       └── java/com/pocketdev/window_droplet/
│           ├── MainActivity.kt
│           ├── audio/SoundManager.kt
│           ├── engine/DropletPhysicsEngine.kt
│           ├── model/                                         # Immutable game models
│           ├── viewmodel/DropletGameViewModel.kt
│           └── ui/                                            # Compose Canvas & Screens
└── build.gradle.kts
```

---

## 📄 Master Blueprint & Chat History

For the complete background story, decision log, physics formulas, and conversation transcript from inception to completion, please read:
👉 **[PROJECT_CONTEXT_AND_CONVERSATION_HISTORY.md](PROJECT_CONTEXT_AND_CONVERSATION_HISTORY.md)**

---

## 👤 Author
Developed by **[kmkcodelab](https://github.com/kmkcodelab)**
