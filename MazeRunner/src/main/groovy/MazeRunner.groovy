import MazeLoader

//Main for running through the maze.  Loads in all of the components and executes them in order.
//Provides runnables as both a groovy script and a jar/java class.
class MazeRunner implements Runnable{
    final String[] args

    //This is what will run if you run it as a groovy script.
    public void run(){
        try{
            //Load in the maze with the given file name.
            def loader = new MazeLoader(args[0])
            loader.readFile()

            //initialize the mazeBot by giving it the loader's maze
            def mazeBot = new MazeNavigator(loader.maze)

            //Find the shortest path with the provided maze
            mazeBot.pathfinder()

            //trace the path (with '.')
            mazeBot.getShorter()

            //Print the completed maze
            loader.prettyPrint()
        } catch (Exception e){
            //display an error message
            println("There was a problem running the MazeRunner could not continue since there was and error\n");
            println("navigating the maze which was serious enough to halt the traversal entirely.  Consider checking your\n")
            println("input to ensure it is valid.")
        }
    }

    //This is what will run if you use a compiled jar or run as java/groovyc
    public static void main(String[] args){
        try{
            //Load in the maze with the given file name.
            def loader = new MazeLoader(args[0])
            loader.readFile()

            //initialize the mazeBot by giving it the loader's maze
            def mazeBot = new MazeNavigator(loader.maze)

            //Find the shortest path with the provided maze
            mazeBot.pathfinder()

            //trace the path (with '.')
            mazeBot.getShorter()

            //Print the completed maze
            loader.prettyPrint()
        } catch (Exception e){
            //display the error message
            println("There was a problem running the MazeRunner could not continue since there was and error\n");
            println("navigating the maze which was serious enough to halt the traversal entirely.  Consider checking your\n")
            println("input to ensure it is valid.")
        }
    }


}
