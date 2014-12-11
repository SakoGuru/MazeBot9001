package MazeBot

import groovy.transform.Canonical

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
