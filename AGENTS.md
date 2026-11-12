# AGENTS.md — Master Blueprint: Window Droplet Game

## 1. Project Context & Identity
- **Project Name:** Window Droplet
- **Project Type:** 2D Raindrop Touch-Merging Physics Game (Jetpack Compose Canvas)
- **Package Name:** `com.kmkcodelab.window_droplet`
- **Primary Objective:** Build a high-performance, jank-free (30/120 FPS), memory-safe mobile game following Clean Architecture principles.

---

## 2. Mandatory Skills & Rules Index (Required for Agents)
Before writing or modifying any code, the agent MUST inspect and load the following modular rules and skills located in the global repository (`~/ecc`):

### A. Game Physics, Performance & Interaction Feel
1. **Performance & Canvas Optimization:**
   - Path: `~/ecc/rules/common/performance.md`
   - *Purpose:* Zero-allocation draw loops, avoiding recomposition lag, and maintaining smooth 30/120 FPS.
2. **Touch & Interaction Polish:**
   - Path: `~/ecc/skills/make-interfaces-feel-better/`
   - *Purpose:* Precise droplet touch hit areas, gesture feedback, bounce/stretch dynamics, and tactile polish.
3. **Low-Latency & Realtime Systems:**
   - Path: `~/ecc/skills/latency-critical-systems/`
   - *Purpose:* Hot-path profiling, minimizing garbage collection pauses during merge calculations, and monitoring p95/p99 frame rendering latency.

### B. Kotlin Coding, Concurrency & Clean Architecture
4. **Kotlin Coding Style & Immutability:**
   - Path: `~/ecc/rules/kotlin/coding-style.md`
   - *Purpose:* Immutable state models (`val` over `var`), clean file structures, and strict null safety.
5. **Concurrency & Game Loop:**
   - Path: `~/ecc/skills/kotlin-coroutines-flows/`
   - *Purpose:* Structured concurrency, `withFrameNanos` physics ticker, and lifecycle-aware `StateFlow`.
6. **Idiomatic Kotlin Patterns:**
   - Path: `~/ecc/rules/kotlin/patterns.md` (and `~/ecc/skills/kotlin-patterns/`)
   - *Purpose:* Sealed hierarchies for physics events, expressive domain DSLs, and allocation-free math/vector utilities.
7. **Clean Architecture & Separation of Boundaries:**
   - Path: `~/ecc/skills/hexagonal-architecture/`
   - *Purpose:* Strict decoupling of pure physics engine logic (`engine/`) from Compose Canvas UI (`ui/`).
8. **Security & Data Protection:**
   - Path: `~/ecc/rules/kotlin/security.md`
   - *Purpose:* Input sanitization, encrypted local storage for high scores/settings, and zero hardcoded secrets.

### C. Communication, Discussion & Intent Alignment
9. **Intent-Driven Development & Requirement Clarification:**
   - Path: `~/ecc/skills/intent-driven-development/`
   - *Purpose:* Exposing genuine ambiguities, asking the right questions, and clarifying game design mechanics before implementation.
10. **Collaborative Dev Team Simulation:**
    - Path: `~/ecc/skills/dev-team/`
    - *Purpose:* Simulating multiple role perspectives (Game Designer, Physics Architect, Android Dev, QA) for rich discussions and brainstorming.
11. **Domain Context & Decision Governance:**
    - Path: `~/ecc/skills/context-and-decisions/`
    - *Purpose:* Maintaining consistent terminology (droplet mass, surface tension, merge radius) and documenting architectural choices.
12. **Visual Planning & Workflows:**
    - Path: `~/ecc/skills/plan-canvas/`
    - *Purpose:* Structuring feature execution plans clearly so game ideas and physics loops can be reviewed and aligned before execution.

### D. Testing, Verification & Code Review
13. **Kotlin TDD & Unit Testing:**
    - Path: `~/ecc/rules/kotlin/testing.md` (and `~/ecc/skills/kotlin-testing/`)
    - *Purpose:* Test-Driven Development for physics formulas, droplet collisions, and merge mathematics without UI dependencies.
14. **Systematic Verification Loop:**
    - Path: `~/ecc/skills/verification-loop/`
    - *Purpose:* Rigorous testing of edge cases (screen boundaries, multi-touch merges, high-speed collisions) before marking tasks complete.
15. **Code Review & Quality Standards:**
    - Path: `~/ecc/rules/common/code-review.md`
    - *Purpose:* Rigorous review checklist for draw loop allocations, memory leaks, and adherence to performance standards.

---

## 3. Core Architectural Boundaries
- **Layer 1 (Engine):** Pure Kotlin physics calculation, collision detection, and merging logic (`engine/`).
- **Layer 2 (Model):** Immutable data classes representing droplets, particles, and game states (`model/`).
- **Layer 3 (ViewModel):** Coroutine-driven state management and game ticker (`viewmodel/`).
- **Layer 4 (UI/Canvas):** Jetpack Compose Canvas rendering and touch gesture handling (`ui/`).

---

## 4. Code Efficiency & Token Optimization Rules
- **Concise Code:** Write clean, minimal, and efficient code. Avoid unnecessary boilerplate, redundant wrapper classes, or overly verbose explanations.
- **Incremental Updates:** When modifying existing files or writing updates, output only the modified blocks, functions, or changes rather than rewriting the entire file from scratch, unless a full structural rewrite is explicitly requested.
- **Code Reuse:** Always prioritize referencing and reusing existing components, utility functions, or patterns previously established in the project instead of duplicating logic.

---

## 5. Strict Agent Operating Rules
1. **NO AUTO-BUILD / NO BLIND CODING:** Do not generate random code or build APKs unless explicitly requested and approved by the user.
2. **MODULAR AWARENESS:** Always check the modular files in `~/ecc` referenced above before implementing complex physics or UI loops.
3. **ERROR PREVENTION:** Honor cooperative cancellation in coroutines and never use the `!!` (double-bang) null operator.
