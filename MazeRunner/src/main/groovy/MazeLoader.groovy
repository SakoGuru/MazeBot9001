import groovy.transform.Canonical

@Canonical
class MazeLoader {
    String fileName
    def maze = []

    //Map the coordinates for the node
    Map<Integer, Integer> coords
    def x = -1
    def y = -1

    def readFile(){
        def file = new File(System.getProperty("user.dir") + '/' + fileName)

        //Check if the file Exists
        if( !file.exists()){
            println "The file '$file' given does not seem to exists.  Please check your spelling and try again.\n"
        }
        else{
            //node attributes
            def attribs = [depth:0]
            attribs += [color:Color.WHITE]

            //Loosely Based off of a chunk of code for parsing at work (I am a Software Developer I at Shelter)
            //which in turn from my googling seems to be based off of this stack overflow:
            //http://stackoverflow.com/questions/3360191/groovy-parsing-text-file
            maze = file.text.split('\n').inject([]) {list, line ->

                //Get the y coordinate for this line
                coords = [y:++y]
                x = -1
                list << line.toCharArray().collect { key ->
                        Node current

                        //add in the x coordinate
                        coords += [x:++x]
                        attribs += coords

                        //pass in the key with the attributes
                        attribs += [print:key]

                        //Check what type this cell is.
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
                        current
                    }

            }
        }
    }

    def prettyPrint() {
        maze.eachWithIndex{ current, index ->

            current.each{
                //print it[0] //Print first value of each array pair aka the map
                print it.attribute('print')
            }
            print "\n"
        }
    }
}
