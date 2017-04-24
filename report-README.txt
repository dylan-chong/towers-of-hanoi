# Report #






IMPORTANT NOTES:
- Unzip the passworded a2 zip with the password '123' (without quotes)
  - This is needed because the submission system corrupts my jar. The
    password prevents this from happening.
  - You can run the program inside the zip with `java -jar submit/a3.jar`

- I used the dependency injection framework called Google Guice
- The java code can be found inside the full-project zip
    - The renderer java is under src/renderer/java
    - The test java is under src/test/java





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
 * [ ] (5) A report that describes what the code does/doesn't do, any bugs, and how it was tested.

Stage 2 out of 15 (up to 70):
 * [ ] (10) The renderer works correctly; No glitches or holes, the ambient light level is correctly used, and the light source is correctly used.
 * [x] (4) Code is clean and readable.
 * [ ] (1) Report is informative and clear.

Stage 3 out of 15 (up to 85):
 * [ ] (15) The user can navigate the render, i.e.\ change the viewing direction.
 	- Polygons are rotated based on the viewing direction.
 	- Polygons are translated and scaled to fit in the window.

Stage 4 out of 15 (up to 100, with 5 spare marks):
 * [ ] (10) Allows multiple light sources to be added (and possibly modified) dynamically.
 * [ ] (10) Gouraud shading is used to make smooth, curved surfaces. Alternatively, Phong shading is used for an over-the-top answer.

