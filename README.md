# MinecraftMods

Yeah some mods woo

## Testing Locally
`./gradlew runClient`

## Installation FAQ
https://mcforge.readthedocs.io/en/latest/gettingstarted/

### Gradle Build Failure in `gradlew setupDecompWorkspace`
> > What went wrong:
  A problem occurred evaluating root project 'MinecraftMods'.
  > Failed to apply plugin [id 'net.minecraftforge.gradle']
  > Found java version 12.0.1. Minimum required is 1.8.0_101. Versions 11.0.0 and newer are not supported yet.

Lots of stuff to do here.. Basically Gradle doesn't like versions above 1.8_101 (the minimum part is a lie). not fun.

 1. Install a version in JDK \[1.8.0 - 1.8.101\] : https://www.oracle.com/technetwork/java/javase/downloads/java-archive-javase8-2177648.html
 2. Run Gradle sync via IntellJ.