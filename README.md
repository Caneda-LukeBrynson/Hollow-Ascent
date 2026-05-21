# Hollow-Ascent

A 2D puzzle-platformer set in a grid-based underworld where every player action is recorded and replayed by delayed shadow entities. Solve logic-driven levels by coordinating with the player's past movements, triggering buttons, opening paths, and navigating through tightly designed spatial challenges.

## Overview

This project is a 2D puzzle-platformer built around action replay and temporal mechanics. Set in a bleak underworld, the player ascends through grid-based levels where every move is recorded and later replayed by shadow entities.

## Core Idea

The main mechanic centers on recording actions as a sequence of `PlayerAction` objects. After a delay, a `Shadow` replays those exact actions, allowing the player to cooperate with their past self. Solving puzzles requires careful planning, timing, and positioning across multiple iterations.

## Game Mechanics

The gameplay is built on a deterministic, tick-based system where every action is discrete and reproducible. Each player input—movement, interaction, or waiting—is stored in a structured action list. This creates a full “run history” that can be replayed precisely by Shadow entities.

Shadows function as delayed echoes of the player. They do not think independently but instead execute the recorded action sequence frame-by-frame. This allows players to treat their past self as a puzzle element—using previous movements to hold buttons, trigger switches, or block hazards while the current player advances elsewhere.

The environment is designed around these interactions. Grid-based levels contain tiles, obstacles, and interactive objects such as buttons and doors. Buttons can be activated by either the player or shadows, often requiring synchronization between multiple timelines. A single action in one moment may unlock or restrict possibilities in a later iteration.

Because movement and interactions are fully deterministic, experimentation becomes a core part of problem-solving. Players are encouraged to fail, refine their action sequence, and replay levels until an optimal timeline is constructed. Success is achieved not through reflexes, but through understanding cause-and-effect across time, effectively “solving” the level as a sequence of interdependent actions.

---

## Screenshots

<p align="center">
  <img width="1920" height="1080" alt="Screenshot 2026-05-21 224338" src="https://github.com/user-attachments/assets/55b5ae93-a6a3-44f0-9685-1b96e37ff58b" />
</p>

<p align="center">
  <img width="1920" height="1080" alt="Screenshot 2026-05-21 224452" src="https://github.com/user-attachments/assets/de75a08b-ab07-4b8d-b17c-c85fb0302fd8" />
</p>

<p align="center">
  <img width="1920" height="1080" alt="Screenshot 2026-05-21 233748" src="https://github.com/user-attachments/assets/625d6fed-8b5f-4ef5-98ea-18e073cbe2c2" />
</p>

---
## Use Case Diagram

<p align="center">
  <img src="https://github.com/user-attachments/assets/b118aad4-c507-4af8-ad8f-2e1ecb9874c3" width="700"/>
</p>

## UML Class Diagram

<p align="center">
  <img src="https://github.com/user-attachments/assets/95fe7230-b4eb-47a3-afc4-6dda821a2e32" width="950"/>
</p>
