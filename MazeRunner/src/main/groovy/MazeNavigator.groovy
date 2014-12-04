import groovy.transform.Canonical

@Canonical
class MazeNavigator {
    def maze = []
    def cleanMaze = []
    NodeList map //build the map from Start to finish, find the shortest path on the map.

    def shortestPath(Node start = findStart()) {
        //cleanMaze = maze.flatten()
        //grab the current position
        def x = start.attribute('x')
        def y = start.attribute('y')

        //look around
        //Check North
        if(y != 0){
            //Get the northern node
            def north = maze[y-1][x]
            if(north.name() == "open" && north.attribute('color') == Color.WHITE){
                north.attributes().put('depth', start.attribute('depth') + 1)
                north.attributes().put('color', Color.BLACK)
                start.append(north)
                shortestPath(north)
            }
        }
        //Check South
        if(y < maze.size()){
            //Get the northern node
            def south = maze[y+1][x]

            //first check if it's the end
            if(south.name() == "end")
                south.attributes().put('depth', start.attribute('depth') + 1)
                start.append(south)
            if(south.name() == "open" && south.attribute('color') == Color.WHITE){
                south.attributes().put('depth', start.attribute('depth') + 1)
                south.attributes().put('color', Color.BLACK)
                start.append(south)
                shortestPath(south)
            }
        }
        //Check West
        if(x != 0){
            //Get the northern node
            def west = maze[y][x-1]
            if(west.name() == "open" && west.attribute('color') == Color.WHITE){
                west.attributes().put('depth', start.attribute('depth') + 1)
                west.attributes().put('color', Color.BLACK)
                start.append(west)
                shortestPath(west)
            }
        }
        //Check East
        if(x < maze[y].size()){
            //Get the northern node
            def east = maze[y][x+1]
            if(east.name() == "open" && east.attribute('color') == Color.WHITE){
                east.attributes().put('depth', start.attribute('depth') + 1)
                east.attributes().put('color', Color.BLACK)
                start.append(east)
                shortestPath(east)
            }
        }

        //start.attributes().put('print','.')
        println(x + ' ' + y)
    }

    //Find the start node to get this party... started.
    Node findStart(){
        def start = maze.collect() {row -> row.find(){node -> node.name() == "start"}}
        start.find(){it != null}
    }
}
