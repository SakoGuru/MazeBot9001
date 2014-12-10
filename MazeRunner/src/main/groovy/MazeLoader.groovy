import groovy.transform.Canonical

//Canonical takes care of all the getters and setters and any combination of
//constructors using the variables and methods provided.
//MazeLoader is responsible for loading in the file specified and constructing the matrix
//representing the maze.  Additionally it provides a print function to reconstruct the maze
//as it was presented originally, with the discovered (or not) path from S to E.
@Canonical
class MazeLoader {
    String fileName
    def maze = []

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
