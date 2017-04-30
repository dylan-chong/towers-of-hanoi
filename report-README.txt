# Report #






IMPORTANT NOTES:

- Run the a3.jar file to run the program
- The full java code can be found inside the full-project zip
    - The renderer java is under src/main/java/
    - The test java is under src/test/java/





## What was done ##

Note: a '[x]' represents a completed task it is the markdown symbol for a
ticked tickbox

Stage 1 out of 55:
 * [x] (5) Reads the light source direction and all polygons from file.
 * [x] (10) Marks all the polygons that are facing away from the viewer.
 * [x] (10) Computes normal and reflected light intesity of every non-hidden polygon.
 * [x] (10) Finds the edge lists of polygons.
 * [x] (10) Renders the image to an array of colours using a Z-buffer.
 * [x] (5) Displays the array of colours.
 * [x] (5) A report that describes what the code does/doesn't do, any bugs, and how it was tested.

Stage 2 out of 15 (up to 70):
 * [x] (10) The renderer works correctly; No glitches or holes, the ambient light level is correctly used, and the light source is correctly used.
 * [x] (4) Code is clean and readable.
 * [x] (1) Report is informative and clear.

Stage 3 out of 15 (up to 85):
 * [x] (15) The user can navigate the render, i.e. change the viewing direction.
 	- Polygons are rotated based on the viewing direction.
 	- Polygons are translated and scaled to fit in the window.

Stage 4 out of 15 (up to 100, with 5 spare marks):
 * [ ] (10) Allows multiple light sources to be added (and possibly modified) dynamically.
 * [x] (10) Gouraud shading is used to make smooth, curved surfaces. Alternatively, Phong shading is used for an over-the-top answer.

## How I tested ##

I got all of the stuff up to stage 4 working by doing the tasks incrementally,
and making sure each test passed. (I made sure to do the easy tasks first to
allow for time to learn about the project and to understand 3D graphics, so
that the hard tasks would be easier). After the rendering code was completed, I
resorted to visual testing. Then, I made my own tests for scaling and
implemented that. Then for the Gouraud shading I just tested it visually,
barely made any unit tests for it, and just implemented it manually. I also
didn't bother to update the tests with the Gourand shading.

## Bugs ##

There is a slight rounding error with interpolation causing there to be some
minor gaps in between shapes (eg in the ball.txt) but I emailed Yi about it and
he said we won't be marked down on it.
