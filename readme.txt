Using Groovy-all 2.3.8
To run simply go into the MazeRunner/src/main/groovy/ folder
in from the command line and type
'groovy MazeRunner.groovy'

Or from IntelliJ import as a gradle project then right click on the
runner and select run after compiling.

Alternatively if you do not have groovy installed simply type
'java -jar MazeRunner.jar'

This build doesn't use Args it assumes a file named 'maze.txt' will
exist in the same directory it is being run from. To proove it
simply place or replace a different maze named 'maze.txt' in the
directory the jar is running out of.