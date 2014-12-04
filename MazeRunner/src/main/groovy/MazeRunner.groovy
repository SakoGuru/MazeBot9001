import MazeLoader
class MazeRunner implements Runnable{
    final String[] args

    public void run(){
        try {
            //Load in the maze
            def loader = new MazeLoader(args[0])
            loader.readFile()

            //Pass the maze to the navigator
            //def mazeBot = new MazeNavigator(loader.maze)
            //mazeBot.shortestPath()

            loader.prettyPrint()
        } catch (Exception e){
            println("There was a problem running the MazeBot: " + e)
        }
    }

    public static void main(String[] args){
        try{
            //Load in the maze
            def loader = new MazeLoader(args[0])
            loader.readFile()

            //Pass the maze to the navigator
            def mazeBot = new MazeNavigator(loader.maze)
            mazeBot.shortestPath()

            loader.prettyPrint()
        } catch (Exception e){
            println("There was a problem running the MazeBot: " + e.printStackTrace())
        }
    }


}
