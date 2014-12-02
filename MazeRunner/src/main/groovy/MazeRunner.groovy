import MazeLoader
class MazeRunner implements Runnable{
    def static loader = new MazeLoader("maze.txt")

    public void run(){
        loader.readFile()
        loader.prettyPrint()
    }

    public static void main(String[] args){
        loader.readFile()
        loader.prettyPrint()
    }


}
