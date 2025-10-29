# Critter Parade

A [libGDX](https://libgdx.com/) project generated with [gdx-liftoff](https://github.com/libgdx/gdx-liftoff).

Most of our code is in `core/src/main/java/com/roachstudios/critterparade`. The entrypoint and main class for handling state is `CritterParade.java`.

## Mini Games

### Simple Racer

Game Instructions:

For the Simple Racer Mini Game each player repeatidly presses a button to advance their Critter. The faster you press, the faster you win!

Player 1:
- Character: Bee
- Button: D

Player 2:
- Character: Lady Bug
- Button: H

Player 3:
- Character: Frog
- Button: L

Player 4:
- Character: Squirrel
- Button: Enter

Player 5:
- Character: Mouse
- Button: Right Arrow

Player 6:
- Character: Ant
- Button: Num pad 6

## Boards

### Picnic Pond

Picnic Pond is still under construction!

## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3; was called 'desktop' in older docs.

## Gradle

This project uses [Gradle](https://gradle.org/) to manage dependencies.
The Gradle wrapper was included, so you can run Gradle tasks using `gradlew.bat` or `./gradlew` commands.
Useful Gradle tasks and flags:

- `build`: builds sources and archives of every project.
- `lwjgl3:jar`: builds application's runnable jar, which can be found at `lwjgl3/build/libs`.
- `lwjgl3:run`: starts the application.
