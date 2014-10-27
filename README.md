#Overview

This is one of the first toy games that I wrote when I was playing around with JavaFX, the GUI library that is gradually replacing Swing as the Java default. The game is nothing particularly groundbreaking (it's just pong), but it's pleasant to look at, and I am rather proud of the AI, which I programmed to play optimally within the constraints of a maximum speed that the game user can set.

#Compiling and Running the Code

Assuming that you have Java 8 and the Gradle build tool installed, just run `gradle build` from the command line inside the main project directory! You'll get a runnable jar inside build/distributions. You can also run `gradle jfxDeploy` if you'd like to build native packages to run on a machine without Java installed. They'll be built inside build/distributions/bundles.