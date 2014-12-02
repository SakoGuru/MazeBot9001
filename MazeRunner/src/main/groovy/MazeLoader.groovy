
import groovy.transform.Canonical

@Canonical
class MazeLoader {
    String fileName
    def maze = []

    def readFile(){
        def file = new File(System.getProperty("user.dir") + '/' + fileName)

        //Check if the file Exists
        if( !file.exists()){
            println "The file '$file' given does not seem to exists.  Please check your spelling and try again.\n"
        }
        else{
            //Loosely Based off of a chunk of code for parsing at work (I am a Software Developer I at Shelter)
            //which in turn from my googling seems to be based off of this stack overflow:
            //http://stackoverflow.com/questions/3360191/groovy-parsing-text-file
            maze = file.text.split('\n').inject([]) {list, line ->
                list << line.toCharArray().collect { key ->
                        def current
                        switch (key) {
                            case ' ':
                                current = CellType.OPEN
                                break
                            case 'S':
                                current = CellType.START
                                break
                            case 'E':
                                current = CellType.END
                                break
                            default:
                                current = CellType.WALL
                        }
                        [(key.charValue()) : current]
                    }

            }
        }
    }

    def prettyPrint() {
        maze.eachWithIndex{ current, index ->

            current.each{
                it.each{ k, v ->
                print "$k"
            }}
            print "\n"
        }
    }
}
