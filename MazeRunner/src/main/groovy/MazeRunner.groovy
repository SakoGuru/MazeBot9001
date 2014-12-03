import MazeLoader
class MazeRunner implements Runnable{
    final String[] args

    public void run(){
        try {
            def loader = new MazeLoader(args[0])
            loader.readFile()
            loader.prettyPrint()
        } catch (Exception e){
            println("There was a problem running the MazeBot: " + e)
        }
    }

    public static void main(String[] args){
        try{
            def loader = new MazeLoader(args[0])
            loader.readFile()
            loader.prettyPrint()
        } catch (Exception e){
            println("There was a problem running the MazeBot: " + e)
        }
    }


}
