
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Game {

    //
    // Public
    //

    // Globals
    public static final boolean DEBUGGING  = false; // Debugging flag.
    public static final int MAX_LOCALES = 10;    // Total number of rooms/locations we have in the game.
    public static int currentLocale = 0;        // Player starts in locale 0.
    public static String command;               // What the player types as he or she plays the game.
    public static boolean stillPlaying = true; // Controls the game loop.
    public static Locale[] locations;           // An uninitialized array of type Locale. See init() for initialization.
    public static int[][]  nav;                 // An uninitialized array of type int int.
    public static int moves = 0;                // Counter of the player's moves.
    public static int score = 0;                // Tracker of the player's score.
    public static ItemMan[] inventory;
    public static boolean tadiliHasBeenKilled = false;
    public static boolean hasDisk = false;
   
    
    
    public static void main(String[] args) {
        if (DEBUGGING) {
            // Display the command line args.
            System.out.println("Starting with args:");
            for (int i = 0; i < args.length; i++) {
                System.out.println(i + ":" + args[i]);
            }
        }

        // Set starting locale, if it was provided as a command line parameter.
        if (args.length > 0) {
            try {
                int startLocation = Integer.parseInt(args[0]);
                // Check that the passed-in value for startLocation is within the range of actual locations.
                if ( startLocation >= 0 && startLocation <= MAX_LOCALES) {
                    currentLocale = startLocation;
                } else {
                    System.out.println("WARNING: passed-in starting location (" + args[0] + ") is out of range.");
                }
            } catch(NumberFormatException ex) {
                System.out.println("WARNING: Invalid command line arg: " + args[0]);
                if (DEBUGGING) {
                    System.out.println(ex.toString());
                }
            }
        }
        
     
        
        // Get the game started.
        init();
        updateDisplay();

        // Game Loop
        while (stillPlaying) {
            getCommand();
            navigate();
            updateDisplay();
        }

        // We're done. Thank the player and exit.
        System.out.println("Thank you for playing.");
    }

    //
    // Private
    //
    
 
    
    private static void init() {
        // Initialize any uninitialized globals.
        command = new String();
        stillPlaying = true;   // TODO: Do we need this?
        System.out.println("Welcome to the Entrapment of the Enigma. Type help for assistance.. you're going to need it..");
        
      
        // Set up the location instances of the Locale class.
        Locale loc0 = new Locale(0);
        loc0.setName("Peripheral Sphere of Existence");
        loc0.setDesc("You are trapped within a sphere that radiates with energy.. You need to return back to your home... You can float down to see the lowest tier of the universe or up towards the surface of reality..... Would you like a map? Take one!");

        Space loc1 = new Space(1);
        loc1.setName("Lower Universe");
        loc1.setDesc("You are in the lowest tier of the Universe. The only place you can float is up.... Unless you have the the Disk of Returning, only then you can return to your true home.");
        loc1.setNearestPlanet("Home");

        Locale loc2 = new Locale(2);
        loc2.setName("Fabric of Reality");
        loc2.setDesc("You find yourself in a white space. There seems to be something more to this place, but you cannot put your finger on it.... You can float up or down, but theres nothing of use here.");
        
        Locale loc3 = new Locale(3);
        loc3.setName("Home");
        loc3.setDesc("The current plane looks unfamiliar and foreign, but it feels like home. You begin to feel the force of gravity. You can walk east, west or south from here...");
        
        Locale loc4 = new Locale(4);
        loc4.setName("Tadili's Magic Shop");
        loc4.setDesc("Welcome to Tadili's Magic Wares and Equipment! Type shop to browse my store! \n Or you can leave by going east.");
        
        
        Locale loc5 = new Locale(5);
        loc5.setName("Novice Pass");
        loc5.setDesc("A dirt path lined with tall reeds stretches as far as the eye can see. It seems that the only direction to proceed is east, but you can turn back and go west.");

        Locale loc6 = new Locale(6);
        loc6.setName("Cross Roads");
        loc6.setDesc("You step into a clearing. You see a signpost. It reads: NORTH: Death Plateau, WEST: Novice Pass, EAST: Thick Woods ");
        
        Locale loc7 = new Locale(7);
        loc7.setName("Death Plateau");
        loc7.setDesc("You have reached the Death Plateau. It smells like death, and it seems there's no way down. Turn back by going south.. ");
        
        Locale loc8 = new Locale(8);
        loc8.setName("Thick Woods");
        loc8.setDesc("The brush is so thick you can barely see the ahead. Unless you can get through, you must turn back by going west.. ");
        
        Locale loc9 = new Locale(9);
        loc9.setName("Deep Crater");
        loc9.setDesc("You find a large crater made in the ground. Perhaps this was an impact from a meteor. You can continue by going north, or turn back by going south..  ");
        
        Locale loc10 = new Locale(10);
        loc10.setName("Earth");
        loc10.setDesc("You find yourself back on Planet Earth! Congratultions you have escaped the Enigma!  ");
        
        Locale loc11 = new Locale(11);
        loc11.setName("Hero's Pass");
        loc11.setDesc("Only the most badass heros are allowed here. Since you have killed Tadili, you must be badass! ");
        
        

        // Set up the location array.
        locations = new Locale[12];
        locations[11] = loc11; // "Heros Pass **hiddenzone"; 
        locations[10] = loc10; // "Earth" **hiddenzone; 
        locations[9] = loc9; // "Deep Crater"; 
        locations[8] = loc8; // "Thick Woods"; 
        locations[7] = loc7; // "Death Plateau";   
        locations[6] = loc6; // "Crossroads";  
        locations[5] = loc5; // "Novice Pass";   
        locations[4] = loc4; // "Tadili";    
        locations[3] = loc3; // "Home"; 	 
        locations[2] = loc2; // "FabricOfReality";   
        locations[0] = loc0; // "SphereOfExistence";  
        locations[1] = loc1; // "LowerUniverse";  

        if (DEBUGGING) {
            System.out.println("All game locations:");
            for (int i = 0; i < locations.length; ++i) {
                System.out.println(i + ":" + locations[i].toString());
            }
        }

        
        
        // Set up the navigation matrix.
        nav = new int[][] {
                                 /* N   S   E   W */								
                                 /* 0   1   2   3 */
         /* nav[0] for loc 0 */  {  2,  1, -1, -1 },
         /* nav[1] for loc 1 */  {  0, -1, -1, -1 },
         /* nav[2] for loc 2 */  {  3,  0, -1, -1 },
         /* nav[3] for loc 3 */  { -1,  2,  5,  4 },
         /* nav[4] for loc 4 */  { -1, -1,  3, -1 },
         /* nav[5] for loc 5 */  { -1, -1,  6,  3 },
         /* nav[6] for loc 6 */  {  7, -1,  8,  5 },
         /* nav[7] for loc 7 */  {  9,  6, -1, -1 },
         /* nav[8] for loc 8 */  { -1, -1, -1,  6 },
         /* nav[9] for loc 9 */  { -1,  7, -1, -1 },
         /* nav[10] for loc 10 */{ -1, -1, -1, -1 },
         /* nav[11] for loc 11 */{ -1, -1, -1,  8 }
         
        };
        
        
        ItemMan item0 = new ItemMan(0);
        item0.setName("Map");
        item0.setDesc("This is the map of the Universe.");

        ItemMan item1 = new ItemMan(1);
        item1.setName("Retractable Shovel");
        item1.setDesc("Wow! A portable shovel!");

        ItemMan item2 = new ItemMan(2);
        item2.setName("Tadili's Machete");
        item2.setDesc("If you found this razor sharp machete, you must be either professor Labouseur, David, or a 1337h4x0r.");

        ItemMan item3 = new ItemMan(3);
        item3.setName("Novice Pass Brochure");
        item3.setDesc("You are a novice explorer on your way to ADVENTURE!");

        ItemMan item4 = new ItemMan(4);
        item4.setName("Climbing Boots");
        item4.setDesc("Boots fitted with climbing studs. This must make scaling easy...");

        ItemMan item5 = new ItemMan(5);
        item5.setName("Disk of Returning");
        item5.setDesc("You have found the disk of returning. Go to the lower universe and type home");


        inventory  = new ItemMan[6];
        inventory[0] = item0;
        inventory[1] = item1;
        inventory[2] = item2;
        inventory[3] = item3;
        inventory[4] = item4;
        inventory[5] = item5;
    }

    
    private static void getInvy(){

        String LeatherBag="Your inventory contains: \n";
        if (inventory[0].itemFound()){
            LeatherBag= LeatherBag+inventory[0].toString()+ "\n";
        }
        if(inventory[1].itemFound()){
            LeatherBag =LeatherBag+inventory[1].toString()+ "\n";
        }
        if(inventory[2].itemFound()){
            LeatherBag =LeatherBag+inventory[2].toString()+ "\n";
        }
        if(inventory[3].itemFound()){
            LeatherBag =LeatherBag+inventory[3].toString()+ "\n";
        }
        if(inventory[4].itemFound()){
            LeatherBag =LeatherBag+inventory[4].toString()+ "\n";
        }
        if(inventory[5].itemFound()){
            LeatherBag =LeatherBag+inventory[4].toString()+ "\n";
        }
        System.out.println(LeatherBag);

    }
    
    
    private static void updateDisplay() {
        System.out.println(locations[currentLocale].getText());
    }

    private static void getCommand() {
        System.out.print("[" + moves + " moves, score " + score + "] ");
        Scanner inputReader = new Scanner(System.in);
        command = inputReader.nextLine();  // command is global.
    }

    private static void navigate() {
        final int INVALID = -1;
        int dir = INVALID;  // This will get set to a value > 0 if a direction command was entered.

        if (        command.equalsIgnoreCase("north") || command.equalsIgnoreCase("n") ) {
            dir = 0;
        } else if ( command.equalsIgnoreCase("south") || command.equalsIgnoreCase("s") ) {
            dir = 1;
        } else if ( command.equalsIgnoreCase("east")  || command.equalsIgnoreCase("e") ) {
            dir = 2;
        } else if ( command.equalsIgnoreCase("west")  || command.equalsIgnoreCase("w") ) {
            dir = 3;
        } else if ( command.equalsIgnoreCase("Kill Tadili")) {
        	killTadili();
        } else if ( command.equalsIgnoreCase("Chop Brush")) {
        	enterHero();
        } else if ( command.equalsIgnoreCase("quit")  || command.equalsIgnoreCase("q")) {
            quit();
        } else if ( command.equalsIgnoreCase("help")  || command.equalsIgnoreCase("h")) {
            help();
        } else if ( command.equalsIgnoreCase("map")  || command.equalsIgnoreCase("m")) {
            map();
        } else if ( command.equalsIgnoreCase("take")  || command.equalsIgnoreCase("t")) {
            take();   
        } else if ( command.equalsIgnoreCase("home")) {
            home();   
        } else if ( command.equalsIgnoreCase("shop")  && currentLocale == 4) {
        	createMagicItems();
        } else if ( command.equalsIgnoreCase("inventory") || command.equalsIgnoreCase("i")) {
        	getInvy();
        };
       
        
        if (dir > -1) {   // This means a dir was set.
            int newLocation = nav[currentLocale][dir];
            if (newLocation == INVALID) {
                System.out.println("You cannot go that way.");
            } else {
                currentLocale = newLocation;
                moves = moves + 1;
                score = score + 5;
                locations[currentLocale].hasVisited = true;
            }
        }
    }
    private static void enterHero() {
		// TODO Auto-generated method stub
    	if (locations[currentLocale]== locations[8] && tadiliHasBeenKilled == true) { 
    		 currentLocale = 11;
    		
    	} else {
    		System.out.println("You either haven't killed Tadili or you aren't at the Thick Woods");
    	}
    		
	}

	private static void home() {
		
    	if (hasDisk == true) {
    		currentLocale = 10;
    		System.out.println("Congratulations on returning to your home, Planet Earth!");
    	}
    	else {
    		System.out.println("You never got the disk of returning, how do you expect to return????");
    	}
		
	}

	private static void killTadili() {
		// TODO Auto-generated method stub
    	if (locations[currentLocale]== locations[4]){
    		inventory[2].setFound(true);
    		System.out.println("You have killed Tadili and picked up his Machete. You can return back here and Tadili will magically reappear as if nothing happened!");
    		tadiliHasBeenKilled = true;
    	}
		
	}

	private static void take(){

        if (locations[currentLocale] == locations[0]) {
            inventory[0].setFound(true);
            System.out.println("You find " + inventory[0].getName() + ", it was placed in your inventory.");
        }
        if (locations[currentLocale] == locations[3]) {
            inventory[1].setFound(true);
            System.out.println("You find " + inventory[1].getName() + ", it was placed in your inventory.");
         
        }
        if (locations[currentLocale] == locations[5]) {
            inventory[3].setFound(true);
            System.out.println("You find " + inventory[3].getName() + ", it was placed in your inventory.");
         
        }
        if (locations[currentLocale] == locations[6]) {
            inventory[4].setFound(true);
            System.out.println("You find " + inventory[4].getName() + ", it was placed in your inventory.");
         
        }
        if (locations[currentLocale] == locations[9]) {
            inventory[5].setFound(true);
            System.out.println("You find " + inventory[5].getName() + ", it was placed in your inventory.");
            hasDisk = true;
  
        }

        else if (locations[currentLocale] == locations[1] || locations[currentLocale] == locations[2] || locations[currentLocale]== locations[4] || locations[currentLocale] == locations[7] || locations[currentLocale] == locations[8]) {
            System.out.println("There's nothing to take!");
        }
    }
	//map is intentionally messed up took 30 minutes to set up so that it would display properly in console >.<
	private static void map() {
		  if (inventory[0].itemFound()){
			  if (locations[currentLocale] == locations[0]) {
			 	System.out.println("                                                                                             ___________________                                                                                                                                                                                                                                                                   ");																		
		    	System.out.println("	                                       		                                    (    Deep Crater    )                                                                                                                                      															                                                        ");
				System.out.println("                                                                                            (_______|   |_______)                                                                                                                                                                                                                                                                 ");																		
		    	System.out.println("	                                       		                            		    |   |				                                                                                                                                      															                                                        ");
				System.out.println("                                                                                            ________|   |________                                                                                                                                                                                                                                                                  ");																		
		    	System.out.println("	                                       		                                   (    Death Plateau    )     					                                                                                                                      															                                                        ");
				System.out.println("                                                                                           (________|   |________)                                                                                                                                                                                                                                                                 ");																		
		    	System.out.println("	                                       		                            	            |   |		                                                                                                         															                                                        ");
				System.out.println("                                       _____________________________________________________________|___|_______________________________                                                                                                                                                                                                                       ");																		
		    	System.out.println("	                              (    Tadili            Home	       Novice Pass           Cross Roads           Thick Woods  )                                                                                                 															                                                        ");
				System.out.println("                                      (_____________________|	 |______________________________________________________________________)                                                                                                                                                                                                                     ");																		
		    	System.out.println("	                                            ________|    |________                                                                                                             															                                                        ");
				System.out.println("                                                   (	    Reality       )                                                                                                                                                                                                                                        ");																		
		    	System.out.println("	                                           (________|    |________)                                                                                                                     															                                                        ");
				System.out.println("                                                    ________|    |________                                                                                                                                                                                                                                         ");																		
		    	System.out.println("	                                           (	  *Existence*     )                                                                                                                     															                                                        ");
				System.out.println("                                                   (________|    |________)                                                                                                                                                                                                                                        ");																		
		    	System.out.println("	                                            ________|    |________                                                                                                                                              															                                                        ");
				System.out.println("                                                   (	    LowerU        )                                                                                                                                                                                                                                       ");																		
		    	System.out.println("	                                           (______________________)                                                                                        															                                                        ");
				
		  } else if (locations[currentLocale] == locations[1]) {
			 	System.out.println("                                                                                             ___________________                                                                                                                                                                                                                                                                   ");																		
		    	System.out.println("	                                       		                                    (    Deep Crater    )                                                                                                                                      															                                                        ");
				System.out.println("                                                                                            (_______|   |_______)                                                                                                                                                                                                                                                                 ");																		
		    	System.out.println("	                                       		                            		    |   |				                                                                                                                                      															                                                        ");
				System.out.println("                                                                                            ________|   |________                                                                                                                                                                                                                                                                  ");																		
		    	System.out.println("	                                       		                                   (    Death Plateau    )     					                                                                                                                      															                                                        ");
				System.out.println("                                                                                           (________|   |________)                                                                                                                                                                                                                                                                 ");																		
		    	System.out.println("	                                       		                            	            |   |		                                                                                                         															                                                        ");
				System.out.println("                                       _____________________________________________________________|___|_______________________________                                                                                                                                                                                                                       ");																		
		    	System.out.println("	                              (    Tadili            Home	       Novice Pass           Cross Roads           Thick Woods  )                                                                                                 															                                                        ");
				System.out.println("                                      (_____________________|	 |______________________________________________________________________)                                                                                                                                                                                                                     ");																		
		    	System.out.println("	                                            ________|    |________                                                                                                             															                                                        ");
				System.out.println("                                                   (	    Reality       )                                                                                                                                                                                                                                        ");																		
		    	System.out.println("	                                           (________|    |________)                                                                                                                     															                                                        ");
				System.out.println("                                                    ________|    |________                                                                                                                                                                                                                                         ");																		
		    	System.out.println("	                                           (	   Existence      )                                                                                                                     															                                                        ");
				System.out.println("                                                   (________|    |________)                                                                                                                                                                                                                                        ");																		
		    	System.out.println("	                                            ________|    |________                                                                                                                                              															                                                        ");
				System.out.println("                                                   (	   *LowerU*       )                                                                                                                                                                                                                                       ");																		
		    	System.out.println("	                                           (______________________)                                                                                        															                                                        ");
				
		  }  else if (locations[currentLocale] == locations[2]) {
			 	System.out.println("                                                                                             ___________________                                                                                                                                                                                                                                                                   ");																		
		    	System.out.println("	                                       		                                    (    Deep Crater    )                                                                                                                                      															                                                        ");
				System.out.println("                                                                                            (_______|   |_______)                                                                                                                                                                                                                                                                 ");																		
		    	System.out.println("	                                       		                            		    |   |				                                                                                                                                      															                                                        ");
				System.out.println("                                                                                            ________|   |________                                                                                                                                                                                                                                                                  ");																		
		    	System.out.println("	                                       		                                   (    Death Plateau    )     					                                                                                                                      															                                                        ");
				System.out.println("                                                                                           (________|   |________)                                                                                                                                                                                                                                                                 ");																		
		    	System.out.println("	                                       		                            	            |   |		                                                                                                         															                                                        ");
				System.out.println("                                       _____________________________________________________________|___|_______________________________                                                                                                                                                                                                                       ");																		
		    	System.out.println("	                              (    Tadili            Home	       Novice Pass           Cross Roads           Thick Woods  )                                                                                                 															                                                        ");
				System.out.println("                                      (_____________________|	 |______________________________________________________________________)                                                                                                                                                                                                                     ");																		
		    	System.out.println("	                                            ________|    |________                                                                                                             															                                                        ");
				System.out.println("                                                   (	   *Reality*      )                                                                                                                                                                                                                                        ");																		
		    	System.out.println("	                                           (________|    |________)                                                                                                                     															                                                        ");
				System.out.println("                                                    ________|    |________                                                                                                                                                                                                                                         ");																		
		    	System.out.println("	                                           (	   Existence      )                                                                                                                     															                                                        ");
				System.out.println("                                                   (________|    |________)                                                                                                                                                                                                                                        ");																		
		    	System.out.println("	                                            ________|    |________                                                                                                                                              															                                                        ");
				System.out.println("                                                   (	    LowerU        )                                                                                                                                                                                                                                       ");																		
		    	System.out.println("	                                           (______________________)                                                                                        															                                                        ");
				
		  }  else if (locations[currentLocale] == locations[3]) {
			 	System.out.println("                                                                                             ___________________                                                                                                                                                                                                                                                                   ");																		
		    	System.out.println("	                                       		                                    (    Deep Crater    )                                                                                                                                      															                                                        ");
				System.out.println("                                                                                            (_______|   |_______)                                                                                                                                                                                                                                                                 ");																		
		    	System.out.println("	                                       		                            		    |   |				                                                                                                                                      															                                                        ");
				System.out.println("                                                                                            ________|   |________                                                                                                                                                                                                                                                                  ");																		
		    	System.out.println("	                                       		                                   (    Death Plateau    )     					                                                                                                                      															                                                        ");
				System.out.println("                                                                                           (________|   |________)                                                                                                                                                                                                                                                                 ");																		
		    	System.out.println("	                                       		                            	            |   |		                                                                                                         															                                                        ");
				System.out.println("                                       _____________________________________________________________|___|_______________________________                                                                                                                                                                                                                       ");																		
		    	System.out.println("	                              (    Tadili           *Home*       Novice Pass            Cross Roads                Thick Woods  )                                                                                                 															                                                        ");
				System.out.println("                                      (_____________________|	 |______________________________________________________________________)                                                                                                                                                                                                                     ");																		
		    	System.out.println("	                                            ________|    |________                                                                                                             															                                                        ");
				System.out.println("                                                   (	    Reality       )                                                                                                                                                                                                                                        ");																		
		    	System.out.println("	                                           (________|    |________)                                                                                                                     															                                                        ");
				System.out.println("                                                    ________|    |________                                                                                                                                                                                                                                         ");																		
		    	System.out.println("	                                           (	   Existence      )                                                                                                                     															                                                        ");
				System.out.println("                                                   (________|    |________)                                                                                                                                                                                                                                        ");																		
		    	System.out.println("	                                            ________|    |________                                                                                                                                              															                                                        ");
				System.out.println("                                                   (	    LowerU        )                                                                                                                                                                                                                                       ");																		
		    	System.out.println("	                                           (______________________)                                                                                        															                                                        ");
				
		  } else if (locations[currentLocale] == locations[4]) {
			 	System.out.println("                                                                                             ___________________                                                                                                                                                                                                                                                                   ");																		
		    	System.out.println("	                                       		                                    (    Deep Crater    )                                                                                                                                      															                                                        ");
				System.out.println("                                                                                            (_______|   |_______)                                                                                                                                                                                                                                                                 ");																		
		    	System.out.println("	                                       		                            		    |   |				                                                                                                                                      															                                                        ");
				System.out.println("                                                                                            ________|   |________                                                                                                                                                                                                                                                                  ");																		
		    	System.out.println("	                                       		                                   (    Death Plateau    )     					                                                                                                                      															                                                        ");
				System.out.println("                                                                                           (________|   |________)                                                                                                                                                                                                                                                                 ");																		
		    	System.out.println("	                                       		                            	            |   |		                                                                                                         															                                                        ");
				System.out.println("                                       _____________________________________________________________|___|___________________________                                                                                                                                                                                                                      ");																		
		    	System.out.println("	                              (   *Tadili*           Home	       Novice Pass           Cross Roads       Thick Woods  )                                                                                                 															                                                        ");
				System.out.println("                                      (_____________________|	 |__________________________________________________________________)                                                                                                                                                                                                                     ");																		
		    	System.out.println("	                                            ________|    |________                                                                                                             															                                                        ");
				System.out.println("                                                   (	    Reality       )                                                                                                                                                                                                                                        ");																		
		    	System.out.println("	                                           (________|    |________)                                                                                                                     															                                                        ");
				System.out.println("                                                    ________|    |________                                                                                                                                                                                                                                         ");																		
		    	System.out.println("	                                           (	   Existence      )                                                                                                                     															                                                        ");
				System.out.println("                                                   (________|    |________)                                                                                                                                                                                                                                        ");																		
		    	System.out.println("	                                            ________|    |________                                                                                                                                              															                                                        ");
				System.out.println("                                                   (	    LowerU        )                                                                                                                                                                                                                                       ");																		
		    	System.out.println("	                                           (______________________)                                                                                        															                                                        ");
				
		  } else if (locations[currentLocale] == locations[5]) {
			 	System.out.println("                                                                                             ___________________                                                                                                                                                                                                                                                                   ");																		
		    	System.out.println("	                                       		                                    (    Deep Crater    )                                                                                                                                      															                                                        ");
				System.out.println("                                                                                            (_______|   |_______)                                                                                                                                                                                                                                                                 ");																		
		    	System.out.println("	                                       		                            		    |   |				                                                                                                                                      															                                                        ");
				System.out.println("                                                                                            ________|   |________                                                                                                                                                                                                                                                                  ");																		
		    	System.out.println("	                                       		                                   (    Death Plateau    )     					                                                                                                                      															                                                        ");
				System.out.println("                                                                                           (________|   |________)                                                                                                                                                                                                                                                                 ");																		
		    	System.out.println("	                                       		                            	            |   |		                                                                                                         															                                                        ");
				System.out.println("                                       _____________________________________________________________|___|_______________________________                                                                                                                                                                                                                       ");																		
		    	System.out.println("	                              (    Tadili            Home	      *Novice Pass*          Cross Roads           Thick Woods  )                                                                                                 															                                                        ");
				System.out.println("                                      (_____________________|	 |______________________________________________________________________)                                                                                                                                                                                                                     ");																		
		    	System.out.println("	                                            ________|    |________                                                                                                             															                                                        ");
				System.out.println("                                                   (	    Reality       )                                                                                                                                                                                                                                        ");																		
		    	System.out.println("	                                           (________|    |________)                                                                                                                     															                                                        ");
				System.out.println("                                                    ________|    |________                                                                                                                                                                                                                                         ");																		
		    	System.out.println("	                                           (	   Existence      )                                                                                                                     															                                                        ");
				System.out.println("                                                   (________|    |________)                                                                                                                                                                                                                                        ");																		
		    	System.out.println("	                                            ________|    |________                                                                                                                                              															                                                        ");
				System.out.println("                                                   (	    LowerU        )                                                                                                                                                                                                                                       ");																		
		    	System.out.println("	                                           (______________________)                                                                                        															                                                        ");
				
		  } else if (locations[currentLocale] == locations[6]) {
			 	System.out.println("                                                                                             ___________________                                                                                                                                                                                                                                                                   ");																		
		    	System.out.println("	                                       		                                    (    Deep Crater    )                                                                                                                                      															                                                        ");
				System.out.println("                                                                                            (_______|   |_______)                                                                                                                                                                                                                                                                 ");																		
		    	System.out.println("	                                       		                            		    |   |				                                                                                                                                      															                                                        ");
				System.out.println("                                                                                            ________|   |________                                                                                                                                                                                                                                                                  ");																		
		    	System.out.println("	                                       		                                   (    Death Plateau    )     					                                                                                                                      															                                                        ");
				System.out.println("                                                                                           (________|   |________)                                                                                                                                                                                                                                                                 ");																		
		    	System.out.println("	                                       		                            	            |   |		                                                                                                         															                                                        ");
				System.out.println("                                       _____________________________________________________________|___|_______________________________                                                                                                                                                                                                                       ");																		
		    	System.out.println("	                              (    Tadili            Home	       Novice Pass          *Cross Roads*          Thick Woods  )                                                                                                 															                                                        ");
				System.out.println("                                      (_____________________|	 |______________________________________________________________________)                                                                                                                                                                                                                     ");																		
		    	System.out.println("	                                            ________|    |________                                                                                                             															                                                        ");
				System.out.println("                                                   (	    Reality       )                                                                                                                                                                                                                                        ");																		
		    	System.out.println("	                                           (________|    |________)                                                                                                                     															                                                        ");
				System.out.println("                                                    ________|    |________                                                                                                                                                                                                                                         ");																		
		    	System.out.println("	                                           (	   Existence      )                                                                                                                     															                                                        ");
				System.out.println("                                                   (________|    |________)                                                                                                                                                                                                                                        ");																		
		    	System.out.println("	                                            ________|    |________                                                                                                                                              															                                                        ");
				System.out.println("                                                   (	    LowerU        )                                                                                                                                                                                                                                       ");																		
		    	System.out.println("	                                           (______________________)                                                                                        															                                                        ");
				
		  } else if (locations[currentLocale] == locations[7]) {
			 	System.out.println("                                                                                             ___________________                                                                                                                                                                                                                                                                   ");																		
		    	System.out.println("	                                       		                                    (    Deep Crater    )                                                                                                                                      															                                                        ");
				System.out.println("                                                                                            (_______|   |_______)                                                                                                                                                                                                                                                                 ");																		
		    	System.out.println("	                                       		                            		    |   |				                                                                                                                                      															                                                        ");
				System.out.println("                                                                                            ________|   |________                                                                                                                                                                                                                                                                  ");																		
		    	System.out.println("	                                       		                                   (   *Death Plateau*   )     					                                                                                                                      															                                                        ");
				System.out.println("                                                                                           (________|   |________)                                                                                                                                                                                                                                                                 ");																		
		    	System.out.println("	                                       		                            	            |   |		                                                                                                         															                                                        ");
				System.out.println("                                       _____________________________________________________________|___|_______________________________                                                                                                                                                                                                                       ");																		
		    	System.out.println("	                              (    Tadili            Home	       Novice Pass           Cross Roads           Thick Woods  )                                                                                                 															                                                        ");
				System.out.println("                                      (_____________________|	 |______________________________________________________________________)                                                                                                                                                                                                                     ");																		
		    	System.out.println("	                                            ________|    |________                                                                                                             															                                                        ");
				System.out.println("                                                   (	    Reality       )                                                                                                                                                                                                                                        ");																		
		    	System.out.println("	                                           (________|    |________)                                                                                                                     															                                                        ");
				System.out.println("                                                    ________|    |________                                                                                                                                                                                                                                         ");																		
		    	System.out.println("	                                           (	   Existence      )                                                                                                                     															                                                        ");
				System.out.println("                                                   (________|    |________)                                                                                                                                                                                                                                        ");																		
		    	System.out.println("	                                            ________|    |________                                                                                                                                              															                                                        ");
				System.out.println("                                                   (	    LowerU        )                                                                                                                                                                                                                                       ");																		
		    	System.out.println("	                                           (______________________)                                                                                        															                                                        ");
				
		  }  else if (locations[currentLocale] == locations[8]) {
			 	System.out.println("                                                                                             ___________________                                                                                                                                                                                                                                                                   ");																		
		    	System.out.println("	                                       		                                    (    Deep Crater    )                                                                                                                                      															                                                        ");
				System.out.println("                                                                                            (_______|   |_______)                                                                                                                                                                                                                                                                 ");																		
		    	System.out.println("	                                       		                            		    |   |				                                                                                                                                      															                                                        ");
				System.out.println("                                                                                            ________|   |________                                                                                                                                                                                                                                                                  ");																		
		    	System.out.println("	                                       		                                   (    Death Plateau    )     					                                                                                                                      															                                                        ");
				System.out.println("                                                                                           (________|   |________)                                                                                                                                                                                                                                                                 ");																		
		    	System.out.println("	                                       		                            	            |   |		                                                                                                         															                                                        ");
				System.out.println("                                       _____________________________________________________________|___|_______________________________                                                                                                                                                                                                                       ");																		
		    	System.out.println("	                              (    Tadili            Home	       Novice Pass           Cross Roads          *Thick Woods* )                                                                                                 															                                                        ");
				System.out.println("                                      (_____________________|	 |______________________________________________________________________)                                                                                                                                                                                                                     ");																		
		    	System.out.println("	                                            ________|    |________                                                                                                             															                                                        ");
				System.out.println("                                                   (	    Reality       )                                                                                                                                                                                                                                        ");																		
		    	System.out.println("	                                           (________|    |________)                                                                                                                     															                                                        ");
				System.out.println("                                                    ________|    |________                                                                                                                                                                                                                                         ");																		
		    	System.out.println("	                                           (	   Existence      )                                                                                                                     															                                                        ");
				System.out.println("                                                   (________|    |________)                                                                                                                                                                                                                                        ");																		
		    	System.out.println("	                                            ________|    |________                                                                                                                                              															                                                        ");
				System.out.println("                                                   (	    LowerU        )                                                                                                                                                                                                                                       ");																		
		    	System.out.println("	                                           (______________________)                                                                                        															                                                        ");
				
		  }  else if (locations[currentLocale] == locations[9]) {
			 	System.out.println("                                                                                             ___________________                                                                                                                                                                                                                                                                   ");																		
		    	System.out.println("	                                       		                                    (   *Deep Crater*   )                                                                                                                                      															                                                        ");
				System.out.println("                                                                                            (_______|   |_______)                                                                                                                                                                                                                                                                 ");																		
		    	System.out.println("	                                       		                            		    |   |				                                                                                                                                      															                                                        ");
				System.out.println("                                                                                            ________|   |________                                                                                                                                                                                                                                                                  ");																		
		    	System.out.println("	                                       		                                   (    Death Plateau    )     					                                                                                                                      															                                                        ");
				System.out.println("                                                                                           (________|   |________)                                                                                                                                                                                                                                                                 ");																		
		    	System.out.println("	                                       		                            	            |   |		                                                                                                         															                                                        ");
				System.out.println("                                       _____________________________________________________________|___|_______________________________                                                                                                                                                                                                                       ");																		
		    	System.out.println("	                              (    Tadili            Home	       Novice Pass           Cross Roads           Thick Woods  )                                                                                                 															                                                        ");
				System.out.println("                                      (_____________________|	 |______________________________________________________________________)                                                                                                                                                                                                                     ");																		
		    	System.out.println("	                                            ________|    |________                                                                                                             															                                                        ");
				System.out.println("                                                   (	    Reality       )                                                                                                                                                                                                                                        ");																		
		    	System.out.println("	                                           (________|    |________)                                                                                                                     															                                                        ");
				System.out.println("                                                    ________|    |________                                                                                                                                                                                                                                         ");																		
		    	System.out.println("	                                           (	   Existence      )                                                                                                                     															                                                        ");
				System.out.println("                                                   (________|    |________)                                                                                                                                                                                                                                        ");																		
		    	System.out.println("	                                            ________|    |________                                                                                                                                              															                                                        ");
				System.out.println("                                                   (	    LowerU        )                                                                                                                                                                                                                                       ");																		
		    	System.out.println("	                                           (______________________)                                                                                        															                                                        ");
				
		  } else if (locations[currentLocale] == locations[10]) {
			  System.out.println("This map doesn't apply to planet earth, you have your own maps here.");
		  } else if (locations[currentLocale] == locations[11]) {
			  System.out.println("You think the hero's pass would be marked on the map? Get outta here!");
		  }
		  else {
			  System.out.println("You never picked up the map, fool.");
		  }
    }
	}
    private static void help() {
        System.out.println("The commands are as follows:");
        System.out.println("   i/inventory");
        System.out.println("   n/north");
        System.out.println("   s/south");
        System.out.println("   q/quit");
        System.out.println("   t/take");
        System.out.println("   m/map");
        System.out.println("   home");
        
       
    }

    private static void quit() {
        stillPlaying = false;
    }
    
        public static void createMagicItems() {
            // Make the list manager.
            ListMan lm1 = new ListMan();
            lm1.setName("Tadili's Magic Items");
            lm1.setDesc("Take a look at what I have to offer!");

            // Make some list items.
            ListItem item1 = new ListItem();
            item1.setName("Tadili's Staff of Vigour");
            item1.setDesc("Glowing with energy");
            item1.setCost(42.2112);
            item1.setNext(null);  // Redundant, but safe.

            ListItem item2 = new ListItem();
            item2.setName("Embroidered Vest of Swiftness");
            item2.setDesc("I'll probably move a lot faster wearing this!");
            item2.setCost(666);
            item2.setNext(null); // Still redundant. Still safe.

            ListItem item3 = new ListItem();
            item3.setName("Dull Butter Knife");
            item3.setDesc("I look useless, but I have uses!");
            item3.setCost(12);
            item3.setNext(null); // Still redundant. Still safe.

            // Put items in the list.
            lm1.add(item1);
            lm1.add(item2);
            lm1.add(item3);
            
            final String fileName = "magic.txt";

            readMagicItemsFromFileToList(fileName, lm1);
            // Display the list of items.
            System.out.println(lm1.toString());


            // Ask player for an item.
            Scanner inputReader = new Scanner(System.in);
            System.out.print("What item would you like? ");
            String targetItem = new String();
            targetItem = inputReader.nextLine();
            System.out.println();

            ListItem li = new ListItem();
            li = sequentialSearch(lm1, targetItem);
            if (li != null) {
                System.out.println(li.toString());
            }
        }
        //
        // Private
        //
        private static ListItem sequentialSearch(ListMan lm,
                                                 String target) {
            ListItem retVal = null;
            System.out.println("Searching for " + target + ".");
            int counter = 0;
            ListItem currentItem = new ListItem();
            currentItem = lm.getHead();
            boolean isFound = false;
            while ( (!isFound) && (currentItem != null) ) {
                counter = counter +1;
                if (currentItem.getName().equalsIgnoreCase(target)) {
                    // We found it!
                    isFound = true;
                    retVal = currentItem;
                } else {
                    // Keep looking.
                    currentItem = currentItem.getNext();
                }
            }
            if (isFound) {
                System.out.println("I have your " + target + " after looking through " + counter + " other items.");
                return  currentItem;
            } else {
                System.out.println("I could not find the" + target + " after looking through " + counter + " other items.");
            }

            return retVal;
        }


        private static void readMagicItemsFromFileToList(String fileName,
                                                         ListMan lm) {
            File myFile = new File(fileName);
            try {
                Scanner input = new Scanner(myFile);
                while (input.hasNext()) {
                    // Read a line from the file.
                    String itemName = input.nextLine();

                    // Construct a new list item and set its attributes.
                    ListItem fileItem = new ListItem();
                    fileItem.setName(itemName);
                    fileItem.setCost(Math.random() * 100);
                    fileItem.setNext(null); // Still redundant. Still safe.

                    // Add the newly constructed item to the list.
                    lm.add(fileItem);
                }
                // Close the file.
                input.close();
            } catch (FileNotFoundException ex) {
                System.out.println("File not found. " + ex.toString());
            }

        }

       
}

          

    


