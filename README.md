# Critter Parade

A [libGDX](https://libgdx.com/) project generated with [gdx-liftoff](https://github.com/libgdx/gdx-liftoff).

Most of our code is in `core/src/main/java/com/roachstudios/critterparade`. The entrypoint and main class for handling state is `CritterParade.java`.

## Mini Games

### Simple Racer

Race to the finish line! Repeatedly tap your RIGHT input to move forward. First player to cross the finish line wins!

### Dodgeball

Don't let the balls touch you! Use your DIRECTIONAL inputs to move around. Survive the longest to win!

### Catching Stars

Catch YOUR colored stars to score! Use your DIRECTIONAL inputs to move. Only YOUR stars count - watch the colors!

## Boards

### Picnic Pond

A Mario Party-like experience set in a peaceful pond area. Players navigate around a picnic blanket near a pond, collecting crumbs and triggering minigames. The board features multiple interconnected paths and junctions for strategic movement.

### Kitchen Havoc

A Mario Party-like experience set in a bustling kitchen. Players navigate around countertops, appliances, and cooking stations. The board features an outer counter loop and an inner kitchen island, connected by cross paths for strategic movement options.

### Ant Farmageddon

A Mario Party-like experience set in underground ant tunnels. Players navigate through winding tunnels and chambers beneath the surface. The board features multiple branching paths and underground chambers connected by narrow tunnel passages.

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
