# Towers of Hanoi (actually SWEN221 assignments now)

Branch off this branch to start SWEN221 assignments

# Essential stuff for SWEN 221 #

## Installation ##

Download the repository and checkout this swen221-submittable-base branch. If
you would like to avoid the command line click the green `Clone or Download`
button on Github then `Download ZIP`, otherwise run this from the command line:

    git clone -b swen221-submittable-base https://github.com/dylan-chong/towers-of-hanoi

The folder will be called `towers-of-hanoi` because I just happen to put this
stuff in this 

### Download IntelliJ ###

Download the `Community Edition` from [this
link](https://www.jetbrains.com/idea/download/#section=mac)

### Setup Gradle project ###

- Open the project with IntelliJ
- Then make sure the Gradle plugin is on: 
    - Go to `Settings/Preferences -> Plugins`
    - Then make sure the Gradle plugin is ticked
- Open the `Gradle` toolbar thing:
    - Should be on an edge of the window
        - If you can't find it, Try pressing `Ctrl+Shift+A` or `Cmd+Shift+A`
          (Mac) then typing `Gradle`. You should see `Gradle`. Pick the one
          that has this [Gradle icon](https://lh6.googleusercontent.com/-fvt5jz8KJ9E/AAAAAAAAAAI/AAAAAAAAAAc/-dxpnszHExs/photo.jpg)
    - Click the blue refresh button to make IntelliJ load the Gradle stuff

## Structure ##

Put your java code in `src/main/java/swen221/assignment1/` (or rename the
package to `swen221.assignment2` or whatever).

### Importing Jar files ###

Put any `.jar` files that just have the `.class` files in the `./lib`.

Unzip `.jar` files that contain `.java` files and chuck those in
`src/main/java/swen221/assignment1` (you can rename the package as well).

## Running stuff ##

### Running JUnit tests ###

- Select the `JUnit Tests` run configuration
    - Open the `Run Configurations` menu (should be in the top right - it
      should look like a simpler version of
      [this](https://i.stack.imgur.com/UkljJ.png))
    - Click `JUnit Tests`
- Click the red debug button (or the run button) the top right

If you want to run stuff from the terminal, run `./gradlew test` (ignore the
`./` if you are on Windows)

## Submitting assignments ##

You can export the jar to the `./submit` directory for submitting jars

- If you like the command line:
    - Go into your project directory
    - Run `./gradlew clean submit` (ignore the `./` if you are on Windows)
- The slightly more tedious option is to run from IntelliJ
    - Open the Gradle toolbar window
    - Find `Tasks -> Other -> submit`
    - Double click `submit`

Then open the `./submit directory`
