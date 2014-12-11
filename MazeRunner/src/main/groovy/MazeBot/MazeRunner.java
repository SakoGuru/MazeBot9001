package MazeBot;


import groovy.transform.Canonical;

//Main for running through the maze.  Loads in all of the components and executes them in order.
//Provides runnables as both a groovy script and a jar/java class.
@Canonical
public class MazeRunner{

    //This is what will run if you use a compiled jar or run as java/groovyc
    public static void main(String[] args) {
        try {
            //Load in the maze with the given file name.
            MazeLoader loader = new MazeLoader();
            loader.init(args[0]);
            loader.readFile();

            //initialize the mazeBot by giving it the loader's maze
            MazeNavigator mazeBot = new MazeNavigator();
            mazeBot.init(loader.maze);
            //Find the shortest path with the provided maze
            mazeBot.pathfinder();

            //trace the path (with '.')
            mazeBot.getShorter();

            //Print the completed maze
            loader.prettyPrint();
        }catch(ArrayIndexOutOfBoundsException AIOOBE){
            System.out.println("Insufficient input variables. Please run as \"MazeRunner <FileName>\"\n");
        }catch (Exception e){
            //display the error message
            System.out.println("There was a problem running the MazeBot.MazeRunner could not continue since there was and error\n");
            System.out.println("navigating the maze which was serious enough to halt the traversal entirely.  Consider checking your\n");
            System.out.println("input to ensure it is valid.\n");
            e.printStackTrace();
        }
    }


}
