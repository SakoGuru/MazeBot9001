import groovy.transform.Canonical

@Canonical
class MazeNavigator {
    def maze = []

    //Accounts for trixies where there are multiple starts.
    def pathfinder(Node[] nodes = findStart()){
        //Create Grandad node to parent duplicate starts:
        def grandNode = new Node(null, 'grandNode')
        grandNode.attribute('name')
        nodes.each {node ->
            grandNode.append(node)
            shortestPath(node)}
    }

    def shortestPath(Node start) {
        //grab the current position
        def x = start.attribute('x')
        def y = start.attribute('y')

        //look around... No teleporting so BFS is out, no foreknowledge of maze either so can't calc
        //distance to the exit (Matrix to eliminate visiting some nodes)...

        //TODO Need to check the nodes adjacent to cut distance. IE: did we go S-E-N instead of just E
        //TODO Find a way to leave inner nodes WHITE

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

        //Check if we hit a wall
        if(start.children().size() < 1){
            //Hit a wall
            start.attributes().put('color', Color.BLACK)
        }
    }

    def hierarchy(Node start, Node node){
        if(node.name() == "end") {
            node.attributes().put('depth', start.attribute('depth') + 1)
            start.append(node)
            start.attributes().put('color', Color.GREEN)
        } else if(node.name() == "open"){
            if(node.attribute('depth') > 0){
                if(node.attribute('depth') < start.parent().attribute('depth')){
                    node.append(start)

                }
            }else {
                node.attributes().put('depth', start.attribute('depth') + 1)
                node.attributes().put('color', Color.GREY)
                start.append(node)
                shortestPath(node)
                start.attributes().put('color', node.attribute('color'))
            }
        }
    }

    //Find the start node to get this party... started.
    Node[] findStart(){
        def start = maze.collect() {row -> row.find(){node -> node.name() == "start"}}
        start = start.findAll(){it != null}
        start.each{ node -> node.attributes().put('depth', 0)}
        //start
    }

    Node findEnd(){
        def end = maze.collect() {row -> row.find(){node -> node.name() == "end"}}
        end.find(){it != null}
    }

    //Look at the depth of each paths leaf node to choose
    //TODO This doesn't do anything of value yet, just sketched out...
    //TODO allow for multiple end nodes.
    def getShorter(Node end = findEnd()) {
        try {
            def paths = end.parent()
            while (paths.parent() != null && paths.parent().name() != 'grandNode') {
                paths.attributes().put('print', '.')
                paths = paths.parent()
            }
        }catch (Exception e){
            println("The end node could not be found given the starting position.")
        }
    }
}
