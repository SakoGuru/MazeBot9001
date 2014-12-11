import groovy.transform.Canonical


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
            println("There was a problem running the MazeBot.MazeRunner could not continue since there was and error\n");
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
        }catch(ArrayIndexOutOfBoundsException AIOOBE){
            System.out.println("Insufficient input variables. Please run as \"MazeRunner <FileName>\"\n");
        }catch (Exception e){
            //display the error message
            println("There was a problem running the MazeBot.MazeRunner could not continue since there was and error\n");
            println("navigating the maze which was serious enough to halt the traversal entirely.  Consider checking your\n")
            println("input to ensure it is valid.")
        }
    }


}


//Canonical takes care of all the getters and setters and any combination of
//constructors using the variables and methods provided.
//MazeBot.MazeLoader is responsible for loading in the file specified and constructing the matrix
//representing the maze.  Additionally it provides a print function to reconstruct the maze
//as it was presented originally, with the discovered (or not) path from S to E.
@Canonical
class MazeLoader {
    String fileName
    public def maze = []

    //Make a constructor for the Java based MazeRunner
    public init(String fileName){
        this.fileName = fileName
    }

    //Map the coordinates for the node
    Map<Integer, Integer> coords
    def x = -1
    def y = -1

    //Read in the file and construct the maze from the file.
    def readFile(){
        //uses the current running directory to run the file out of.
        def file = new File(System.getProperty("user.dir") + '/' + fileName)

        //Check if the file Exists
        if( !file.exists()){
            println "The file '$file' given does not seem to exists.  Please check your spelling and try again.\n"
        }
        else{
            //node attributes.  Start with depth and add the others as we encounter them.
            def attribs = [depth:-1]

            //Loosely Based off of a chunk of code for parsing at work (I am a Software Developer I at Shelter)
            //which in turn from my googling seems to be based off of this stack overflow:
            //http://stackoverflow.com/questions/3360191/groovy-parsing-text-file
            try {
                //Split the file into rows based on the \n character.
                maze = file.text.split('\n').inject([]) { list, line ->
                    //Get the y coordinate for this line
                    coords = [y: ++y]
                    x = -1 //reset x

                    //create the array of nodes based on each character key found in the line
                    list << line.toCharArray().collect { key ->
                        //Node is an XML data structure provided by groovy.
                        //I store all the "Graph" as a XML tree(s) essentially.
                        Node current

                        //add in the x coordinate
                        coords += [x: ++x]

                        //put the coordinate map into the attributes of the node
                        attribs += coords

                        //pass in the key with the attributes mapped to the print value
                        //for pretty printing later
                        attribs += [print: key]

                        //Check what type this cell is.  Treat anything not open, start, or end as a wall
                        switch (key) {
                            case ' ':
                                current = new Node(null, "open", attribs)
                                break
                            case 'S':
                                current = new Node(null, "start", attribs)
                                break
                            case 'E':
                                current = new Node(null, "end", attribs)
                                break
                            default:
                                current = new Node(null, "wall", attribs)
                        }
                        //place current into its matrix position. Groovy has some functional
                        //tendencies FYI
                        current
                    }
                }

                //put the \n wall to the end of each row since we stripped it out earlier in our inject to split
                //the lines up into rows.
                maze.eachWithIndex { row, i ->
                    def last = row.size()
                    attribs += [print: '\n']
                    attribs += [y: i]
                    attribs += [x: last]
                    row.push(new Node(null, "wall", attribs))
                }
            } catch (Exception e){
                println("There was a problem loading in the maze matrix.")
                println("Please check the formatting of your maze file and try again.")
            }
        }
    }

    //Output the maze using the print attribute of each node.  Prints the matrix, not the graph
    def prettyPrint() {
        try {
            //Take each row, pop off the new line node, and print each print value for each node in each row.
            maze.eachWithIndex { current, index ->
                current.pop()//Clear off the new line on the end of the matrix rows to not mess up the print.
                current.each {
                    //Print each node's print value (IE ' ' for open, '.' for the path to the end.
                    print it.attribute('print')
                }
                print "\n"
            }
        } catch(Exception e){
            println("There was an error with the maze so it cannot be printed properly.  Consider checking your input file for errors.")
        }
    }
}


@Canonical
class MazeNavigator {
    def maze = []

    public init(Object maze){
        this.maze = maze
    }

    //Accounts for trixies where there are multiple starts.
    //Note only really supports 1 S per open region (See crazyMaze.txt)
    def pathfinder(Node[] nodes = findStart()){
        //Create Grandad node to parent duplicate starts (prevents NPE's where there are multiple starts)
        def grandNode = new Node(null, 'grandNode')
        grandNode.attribute('name')
        //find shortest path from start node
        nodes.each {node ->
            grandNode.append(node)
            shortestPath(node)}
    }

    //Will look N S E and W to traverse the maze until it finds the shortest path to the end node.
    //it is given only the start node and has no knowledge of the maze layout or location of the end node to start.
    def shortestPath(Node start) {
        //grab the current position
        def x = start.attribute('x')
        def y = start.attribute('y')

        //look around... No teleporting so BFS is out, no foreknowledge of maze either so can't calc
        //distance to the exit (Matrix to eliminate visiting some nodes).
        //This is similar to a DFS with some extra logic to deal with large open areas (not alleyways)
        //and provide for the cases where there are multiple end/starts (as you all got onto me for at the
        //code review on Thursday).
        //
        //Note: It only supports one S per open region, and multiple E's can't
        //have overlapping paths or I would have to duplicate the matrix to allow those to be solved which would
        //be a huge resource hog and the instructions say only 1 S and 1 E anyway.

        //Check North
        if(y != 0 && maze[y-1].size() > x){
            //Get the northern node
            Node north = maze[y-1][x]

            hierarchy(start, north)
        }
        //Check West
        if(x != 0){
            //Get the western node
            Node west = maze[y][x-1]

            hierarchy(start, west)
        }
        //Check South
        if(y < maze.size()-1 && maze[y+1].size() > x){
            //Get the southern node
            Node south = maze[y+1][x]

            hierarchy(start, south)
        }
        //Check East
        if(x < maze[y].size()){
            //Get the eastern node
            Node east = maze[y][x+1]

            hierarchy(start, east)
        }
    }

    //Given the current node and the next node determine the hierarchy of the next node
    //IE: Is 'node' going to be a child of the start node and is it the end node?
    //Also checks if an already visited node (now checked via depth) is a shorter path as well.
    def hierarchy(Node start, Node node){
        //Is it the end node?
        if(node.name() == "end") {
            //Make sure there isn't a shorter path already or hasn't been visited
            if(node.attribute('depth') < 0 || node.attribute('depth') > (start.attribute('depth') + 1)) {
                node.attributes().put('depth', start.attribute('depth') + 1)
                start.append(node)
            }
        }
        //Is the node open or a wall?
        else if(node.name() == "open"){
            //Is it an already visited, but potentially shorter path?
            if(node.attribute('depth') > 0){
                if(node.attribute('depth') > (start.attribute('depth') + 1)){
                    start.append(node)
                    node.attributes().put('depth', start.attribute('depth')+ 1)
                    shortestPath(node)
                }
            }
            //Is it a not yet visited node?
            else {
                node.attributes().put('depth', start.attribute('depth') + 1)
                start.append(node)
                shortestPath(node)
            }
        }
    }

    //Find the start node to get this party... started.
    //Searches the maze matrix to find the start location(s)
    Node[] findStart(){
        def start = maze.collect() {row -> row.find(){node -> node.name() == "start"}}
        start = start.findAll(){it != null}
        start.each{ node -> node.attributes().put('depth', 0)}
        //start
    }

    //This function searches the matrix (not the graph) to find the end node(s)
    //It is only used for tracing the path found by the shortestPath method in order to print the path
    Node[] findEnd(){
        Node[] end = maze.collect() {row -> row.find(){node -> node.name() == "end"}} as Node[]
        end.findAll(){it != null}
    }

    //Find the end nodes and follow the graph back to start by calling parent of each node and placeing
    //a '.' (for the printed path) on the 'print' attibute of each node in between E and S
    def getShorter(Node[] end = findEnd()) {
        try {
            end.each { endNode ->
                try {
                    def paths = endNode.parent()
                    while (paths.parent() != null && paths.parent().name() != 'grandNode') {
                        paths.attributes().put('print', '.')
                        paths = paths.parent()
                    }
                } catch (Exception e){
                    println("One of the end nodes could not be found from the given the starting position.")
                }
            }
        }catch (Exception e){
            println("The end node could not be found given the starting position.")
        }
    }
}
