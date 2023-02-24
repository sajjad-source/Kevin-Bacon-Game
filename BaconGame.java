import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Kevin Bacon game with interactive interface
 * @author Sajjad
 */
public class BaconGame
{
    Graph<String, Set<String>> graph = new AdjacencyMapGraph<>(); //the graph with vertex as actors and edge as set of movies
    HashMap<String, String> actorIDMap = new HashMap<>(); //map for actorID -> actorName
    HashMap<String, String> movieIDMap = new HashMap<>(); //map for movieID -> movieName
    HashMap<String, Set<String>> movieToActorsMap = new HashMap<>(); //map for movieID -> set of actorIDs that appear in movie

    String centerOfUniverse = "Kevin Bacon"; //start with center of universe as Kevin Bacon


    /**
     * Method to build the actorIDMap by reading and parsing the file
     * @param actorFile - file that has each actor ID -> actor Names
     * @throws IOException
     */
    public void buildActorIDMap(String actorFile) throws IOException
    {
        BufferedReader input = new BufferedReader(new FileReader(actorFile)); //read the actor file
        String line; //line in file

        while ((line = input.readLine()) != null) //while there is a line to read
        {
            String[] l = line.split("\\|"); //parse the line -> array where l[0] is actorID && l[1] is actorName
            actorIDMap.put(l[0], l[1]); //add actorID as key -> actorName as value
        }

    }

    /**
     * Method to build the movieIDMap by reading and parsing the file
     * @param movieFile - the file path that contains movieID -> movieName
     * @throws IOException
     */
    public void buildMovieIDMap(String movieFile) throws IOException
    {

        BufferedReader input = new BufferedReader(new FileReader(movieFile)); //read the movie file
        String line; //line in file

        while ((line = input.readLine()) != null) //while there is a line to read
        {
            String[] l = line.split("\\|"); //parse the line -> array where l[0] is movieID && l[1] is movieName
            movieIDMap.put(l[0], l[1]); //add movieID as key -> movieName is value
        }
    }

    /**
     * Method to build the movieID -> set of actorIDs map by reading and parsing file
     * @param movieToActorFile - file path that contains movieID -> actorID
     * @throws IOException
     */
    public void buildMovieToActor(String movieToActorFile) throws IOException
    {

        BufferedReader input = new BufferedReader(new FileReader(movieToActorFile)); //read the file
        String line; //line in file

        while ((line = input.readLine()) != null) //while there is a line to read
        {
            Set<String> actorSet = new HashSet<>(); //create a set to store actorIDs

            String[] l = line.split("\\|"); //parse the line into a string arr of 2 l[0] movieID -> l[1] actorID
            if (!movieToActorsMap.containsKey(l[0])) //if the map doesn't contain the movieID
            {
                actorSet.add(l[1]); //add to the set the actorID
                movieToActorsMap.put(l[0], actorSet); //put the movieID as key -> set of actorID as value
            } else { //else if the movieID already in the map
                movieToActorsMap.get(l[0]).add(l[1]); //then get the movie ID set and just add on to it the actorID
            }

        }
    }

    /**
     * Method to build the graph using the maps
     */
    public void buildGraph()
    {

        for (Map.Entry<String, String> entry : actorIDMap.entrySet()) //for every item in the map of actorID -> actorNames
        {
            graph.insertVertex(entry.getValue()); //create a vertex in the graph of the actorNames
        }

        for (Map.Entry<String, Set<String>> entry : movieToActorsMap.entrySet()) //for every item in the map of movieID -> set of actorIDs
        {
            String movieId = entry.getKey(); //get the movieID
            Set<String> actors = entry.getValue(); //get the set of actorIDs
            for (String actor1 : actors) //get actor1 from the set
            {
                for (String actor2 : actors)  //get actor2 from the set
                {
                    if (!actor1.equals(actor2)) //if actor1 is not the same as actor2
                    {
                        String movieName = movieIDMap.get(movieId); //get the movieName from the movieID
                        String actor1Name = actorIDMap.get(actor1); //get name of actor1
                        String actor2Name = actorIDMap.get(actor2); //get name of actor2

                        if (!graph.hasEdge(actor1Name, actor2Name)) //if there isn't an edge between actor1 and actor2
                        {
                            Set<String> movieSet = new HashSet<>(); //create the movieSet for the edge labels
                            movieSet.add(movieName); //add the movie that both actors appeared in to the movie set
                            graph.insertUndirected(actor1Name, actor2Name, movieSet); //insert an undirected edge between the two actors with the movie set

                        } else { //else if there is already an undirected edge between the two actors
                            Set<String> movieSet = graph.getLabel(actor1Name, actor2Name); //get the edge label movie set
                            movieSet.add(movieName); //add the movie to the movie set
                            graph.insertUndirected(actor1Name, actor2Name, movieSet); //reinsert back the movieSet
                        }
                    }
                }
            }
        }
    }

    /**
     * method to find the shortest path from <name> to current center of the universe
     * @param baconGame - the instantiated baconGame object
     */
    public void findPath(BaconGame baconGame)
    {
        try
        {
            Scanner inp = new Scanner(System.in); //instantiate user input
            System.out.print("Please enter name of an actor: ");
            String actor = inp.nextLine(); //read the user input of actorName

            //do BFS on the graph with the center of the universe
            Graph<String, Set<String>> shortPath = GraphLib.bfs(baconGame.graph, baconGame.centerOfUniverse);
            List<String> path = GraphLib.getPath(shortPath, actor); //get the shortest path from the actor(user input) back to center of universe

            System.out.println(actor + "'s number is " + (path.size() - 1)); //print out the actors kevin bacon number

            for (int i = 0; i < path.size() - 1; i++) //for each index in the shortest path list
            {
                String res = "";
                //print the chain of movies the actor appeared in back to the center of the universe
                res += res + path.get(i) + " appeared in " + baconGame.graph.getLabel(path.get(i), path.get(i + 1)) + " with " + path.get(i + 1);
                System.out.println(res);
            }
        }catch (Exception e) //catch exception if not valid actor name / not possible bfs
        {
            System.out.println("Please enter a valid name of an actor!");
        }

    }

    /**
     * Method to make a new actor the center of the universe
     * @param baconGame
     */
    public void makeCenterOfUniverse(BaconGame baconGame)
    {
        try
        {
            Scanner inp = new Scanner(System.in); //instantiate the user input
            System.out.print("Please enter the center of the universe: ");

            String newCenterOfUniverse = inp.nextLine(); //get the name of actor from user input
            baconGame.centerOfUniverse = newCenterOfUniverse; //update the center of the universe to the new actor

            //get the shortest path using BFS with the new center of the universe
            Graph<String, Set<String>> shortPath = GraphLib.bfs(baconGame.graph, baconGame.centerOfUniverse);

            //get the average separation of the new center of the universe
            double avgSep = GraphLib.averageSeparation(shortPath, centerOfUniverse);

            String res = centerOfUniverse + " is now the center of the acting universe, connected to ";
            res += (shortPath.numVertices() - 1) + "/" + baconGame.graph.numVertices() + " with average separation " + avgSep;

            //print out the new center of universe with connected numbers of actors and average of separation
            System.out.println(res);
        } catch (Exception e){ //catch exception if trying to make non-existent actor center of universe
            System.out.println("Please enter a valid actor name!");
        }

    }

    /**
     * Method to find those of infinite separation to the current center of universe
     * @param baconGame
     */
    public void infiniteSeparation(BaconGame baconGame)
    {
        //get the shortest path using BFS with the center of universe
        Graph<String, Set<String>> shortPath = GraphLib.bfs(baconGame.graph, baconGame.centerOfUniverse);

        System.out.print("Actors with infinite separation from the current center " + baconGame.centerOfUniverse + " are: ");

        //get the list of missingVertices. Those that are missing -> meaning infinite separation
        System.out.println(GraphLib.missingVertices(baconGame.graph, shortPath));
    }

    /**
     * Method to sort actors by bacon number to current center of universe from low -> high
     * @param baconGame
     */
    public void sortByBaconNumber(BaconGame baconGame)
    {
        TreeMap<Integer, Set<String>> map = new TreeMap<>(); //tree map to store kevin bacon number -> set of actors with that number

        ArrayList<String> sortActorByKB = new ArrayList<>(); //list to sort actors by kevin bacon number

        //get the shortest path tree by bfs
        Graph<String, Set<String>> shortPath = GraphLib.bfs(baconGame.graph, baconGame.centerOfUniverse);

        for (String actor : shortPath.vertices()) //for every actor in the shortest path tree
        {

            List<String> getPath = GraphLib.getPath(shortPath, actor); //get a path between that actor to the center of universe
            Set<String> setActors = new HashSet<>(); //create a set to store possible multiple actors with same KB number

            int KBNumber = getPath.size() - 1; //the kevin bacon number is the size of path - 1

            if (!map.containsKey(KBNumber)) //if the map doesn't contain the kevin bacon number
            {
                setActors.add(actor); //add the actor of that kevin bacon number to the set
                map.put(KBNumber, setActors); //put KBNumber as key -> value is set of actors with that KBNumber

            }else{ //else if the map already contains the KBNumber
                Set<String> set = map.get(KBNumber); //get the set from the map
                set.add(actor); //add to the set the actor
                map.put(KBNumber, set); //reinsert the set back into the map
            }
        }

        for (Integer key : map.keySet()) //for each key in the map (map is ordered lowest to highest because TreeMap)
        {
            sortActorByKB.addAll(map.get(key)); //add everything in the set to the list
        }

        //print out the list of actors sorted by KB number
        System.out.println("List of actors sorted by Kevin Bacon number low to high: " + sortActorByKB);
    }

    /**
     * Method to show the top k number of actors sorted by average separation (high->low)
     * @param baconGame
     * @param k - top k number of actors
     */
    public void topByAverageSeparation(BaconGame baconGame, int k)
    {
        //map to store average separation as key and the set of actors with that separation number
        TreeMap<Double, Set<String>> map = new TreeMap<>(Comparator.reverseOrder());  //reverseOrder so it is high->low
        ArrayList<String> sortedAverageSeparation = new ArrayList<>(); //final list of sorted actors by avg separation

        try
        {
            for (String actor : baconGame.graph.vertices()) //for every actor in the graph
            {
                Graph<String, Set<String>> shortPath = GraphLib.bfs(baconGame.graph, actor); //get the shortest path to that actor
                double avgSep = GraphLib.averageSeparation(shortPath, actor); //calculate the average separation
                Set<String> setActors = new HashSet<>(); //set to store possible multiple actors for average separation

                if (!map.containsKey(avgSep)) //if the map doesn't contain the average separation number
                {
                    setActors.add(actor); //add the actor to the set
                    map.put(avgSep, setActors); //put the avg separation number as the key -> set of actors as value

                } else { //else if the map already contains the average separation number
                    Set<String> set = map.get(avgSep); //get the set of that average separation number
                    set.add(actor); //add to the set the actor
                    map.put(avgSep, set); //put the set back into the map
                }
            }

            for (Double key : map.keySet()) //for each avgSep key in the map (sorted high->low)
            {
                sortedAverageSeparation.addAll(map.get(key)); //add all the actors in the set to the list
            }

            System.out.println("Top " + k + " centers of the universe sorted by average separation: \t");
            for (int i = 0; i < k; i++) //loop through every index in the sorted list of avg separation
            {
                System.out.println("\t" + (i + 1) + ": " + sortedAverageSeparation.get(i)); //print it out
            }
        } catch (Exception e){ //catch exception if more k > actors in graph
            System.out.println("There aren't " + k + " actors in the graph");
        }
    }

    /**
     * Method to show the top k number of actors sorted by in degree (high->low)
     * @param baconGame
     * @param k
     */
    public void topByInDegree(BaconGame baconGame, int k)
    {
        try
        {
            //get the list of vertices sorted by in degree in decreasing order
            List<String> sortedByInDegree = GraphLib.verticesByInDegree(baconGame.graph);

            System.out.println("Top " + k + " centers of the universe sorted by degree (number of costars): \t");

            for (int i = 0; i < k; i++) //for each item in that list
            {
                System.out.println("\t" + (i + 1) + ": " + sortedByInDegree.get(i)); //print out the actor
            }
        } catch (Exception e) { //Catch exception if k > num of actors in graph
            System.out.println("There aren't " + k + " actors in the graph");
        }
    }

    public static void main(String[] args) throws IOException
    {
        BaconGame baconGame = new BaconGame(); //instantiate a new bacon game
        baconGame.buildActorIDMap("/Users/sajjadck/IdeaProjects/PS4/src/actors.txt");
        baconGame.buildMovieIDMap("/Users/sajjadck/IdeaProjects/PS4/src/movies.txt");
        baconGame.buildMovieToActor("/Users/sajjadck/IdeaProjects/PS4/src/movie-actors.txt");
        baconGame.buildGraph(); //build the graph

        Scanner userInput = new Scanner(System.in); //instantiate scanner for user input
        String character = ""; //character to hold user input

        //print out possible options of commands
        System.out.println("Commands:\n" +
                "c <#>: list top (positive number) centers of the universe, sorted by average separation\n" +
                "b <low> <high>: list actors sorted by Kevin Bacon number low to high\n" +
                "i: list actors with infinite separation from the current center\n" +
                "p <name>: find path from <name> to current center of the universe\n" +
                "d <#>: list top (positive number) <#> centers of the universe, sorted by degree (number of costars)\n" +
                "u <name>: make <name> the center of the universe\n" +
                "q: quit game\n");

        System.out.println(baconGame.centerOfUniverse + " is now the center of the acting universe");

        while (!character.equals("q")) //while the character input isn't q (quit game)
        {
            System.out.print("Choose a command: "); //prompt to choose a command
            character = userInput.nextLine(); //get the user input

            if (character.equals("p")) //if command p
            {
                baconGame.findPath(baconGame); //find path from <name> to current center of the universe
            }
            else if (character.equals("u")) //if command u
            {
               baconGame.makeCenterOfUniverse(baconGame); //make <name> the center of the universe
            }
            else if (character.equals("i")) //if command i
            {
                baconGame.infiniteSeparation(baconGame); //list actors with infinite separation from the current center
            } 
            else if (character.equals("b")) //if command b
            {
                baconGame.sortByBaconNumber(baconGame); //list actors sorted by Kevin Bacon number low to high
            }
            else if (character.equals("c")) //if command c
            {
                System.out.print("Choose the number of top actors by their average separation: "); //prompt user to enter number
                Scanner numInput = new Scanner(System.in);
                int num = numInput.nextInt(); //get the integer from user

                //list top (positive number) centers of the universe, sorted by average separation
                baconGame.topByAverageSeparation(baconGame, num);
            }
            else if (character.equals("d")) //if command d
            {
                System.out.print("Choose the number of top actors by in degree: "); //prompt user to enter number
                Scanner numInput = new Scanner(System.in);
                int num = numInput.nextInt(); //get the number from user

                //list top (positive number) <#> centers of the universe, sorted by degree (number of costars)
                baconGame.topByInDegree(baconGame, num);
            }else { //else if any other command that doesn't exist
                if (!character.equals("q")) //make sure it isn't the quit command
                    System.out.println("Please choose a valid command!"); //prompt user to choose a valid command
            }
        }

        System.out.println("Game is over"); //game has finished
        userInput.close(); //close user input
    }
}

