import java.util.Scanner;
import java.time.Duration;
import java.time.Instant;
import java.util.Random;


public class RoomAdventure {

    private static Room currentRoom;
    private static Item[] inventory = {null, null, null, null, null, null, null, null, null, null, null, null};
    private static String status;
    private static Instant startTime;
    private static int daysPassed;
    private static String timeOfDay;
    private static boolean hasBike = false;
    private static boolean paranoid = false;
    private static boolean candleLit = false;
    private static boolean hasRadio = false;    //not used in the current version of the game but will be eventually    
    private static boolean exhausted = false;   //not used in the current version of the game but will be eventually
    private static boolean badReception = false;    //not used in the current version of the game but will be eventually
    private static Scanner scanner = new Scanner(System.in);

    //rooms
    private static Room orchard;
    private static Room crossroads;
    private static Room forestEdge;
    private static Room deepForest;
    private static Room longRoad;
    private static Room graveyard;
    private static Room brewery;
    private static Room barn;
    private static Room farmhouse;

    private static boolean playing = true;

    final private static String DEFAULT_STATUS = "Sorry, I don't understand. Try something like [verb] [noun],\n or type 'help' if you need  more specifics.";
    final private static int MAX_INVENTORY_SIZE = 6;
    final private static long TIME_BLOCK_LENGTH = 4; //in minutes


    public static void main(String[] args){
        setupGame();

        while (playing == true){
            // Print info about game.
            System.out.println("\nWhat should I do now? ");

            // take input
            String input = scanner.nextLine(); // wait here for input

            // process input

            //single-word commands
            if (input.equals("help")){
                handleHelp();
            }

            //multi-word commands
            else{
                String[] words = input.split(" ");
    
                if (words.length != 2){
                    status = DEFAULT_STATUS;
                }
    
                else{
                    String verb = words[0];
                    String noun = words[1];
        
                    switch (verb){
        
                        case "go":
                            handleGo(noun);
                            break;
        
                        case "look":
                            handleLook(noun);
                            break;
        
                        case "take":
                            handleTake(noun);
                            break;
        
                        // case "drop":
                        //     handleDrop(noun);
                        //     break;
        
                        case "use":
                            handleUse(noun);
                            break;
        
                        case "talk":
                            handleTalk(noun);
                            break;
        
                        case "list":
                            handleListInventory();
                            break;
        
                        case "describe":
                            handleDescribeArea();
                            break;
    
                        case "listen":
                            handleListen();
                            break;
        
                        default: status = DEFAULT_STATUS;
                    }    
                }
            }

            if (paranoid == true){
                status += "I can't shake the feeling that someone is following me, but every time I check there's nothing there.";
            }

            checkTime();

            System.out.println(status);
        }
    }

    private static void handleGo(String noun){
        //enter a new room

        Exit[] exits = currentRoom.getExits();
        status = "I don't see that exit.";
        for (Exit exit : exits){
            if (noun.equals(exit.direction)){

                switch(exit.destination.name){
                    case("deep forest"):
                        switch(timeOfDay){

                            case "night":
                                status = "It's creepy enough out here, I don't think I want to go deeper into the woods at night.";
                                break;

                            case "dusk":
                                endGame("missing");
                                break;

                            case "midday":
                                endGame("wentHome");
                                break;

                            case "dawn":
                                Random random = new Random();
                                String[] probabilityDistribution = {"wentHome", "wentHome", "wentHome", "missing", "missing"};
                                endGame(probabilityDistribution[random.nextInt(probabilityDistribution.length)]);
                                break;
                        }

                        break;


                    case("long road"):

                        if(hasBike){
                            endGame("finishedDemo");
                        }

                        else{
                            status = "I've been walking for a long time and haven't seen anything of note.\nI don't think I'll be able to get anywhere on foot, so I'll have to turn\nback for now.";
                            changeTime();
                        }

                        break;


                    case("graveyard"):

                        if(paranoid){
                            status = "That's where the feeling of being watched started, so I don't really want to go back there yet.";
                        }

                        else{
                            currentRoom = exit.destination;
                            handleDescribeArea();
                        }

                        break;

                    default:
                        currentRoom = exit.destination;
                        handleDescribeArea();
                        break;
                }
            }
        }
    }

    private static void handleLook(String noun){
        //get detailed description of an item

        Item[] items = currentRoom.getItems();
        status = "I don't see that item.";
        for (int i = 0; i < items.length; i++){
            if (items[i] != null){
                if (noun.equals(items[i].name)){
                    //describe item to player
                    status = items[i].description;
    
                    //allow player the option to sleep if bed is selected
                    if(noun.equals("bed")){
                        System.out.println("Should I take a nap? Just type y or n, it will be faster.");
                        String response = scanner.nextLine();
                        if(response.equals("y") || response.equals("yes")){
                            sleep();
                        }
                    }
                }   
            }
        }
    }

    private static void handleTake(String noun){
        //remove an item from the room and add
        //it to inventory

        Item[] items = currentRoom.getItems();
        status = "I can't grab that.";
        for (int i = 0; i < items.length; i++){

            Item item = items[i];

            if(item != null){
                if (noun.equals(item.name) && item.grabbable == true){
                    status = "My bag is already full";
    
                    for (int j = 0; j < MAX_INVENTORY_SIZE; j++){
    
                        if (inventory[j] == null){
    
                            inventory[j] = item;
                            status = "I picked up the " + noun + ".";
                            currentRoom.items[i] = null;
                            break;
                        }
                    }
                }
            }
        }
    }

    // private static void handleDrop(String noun){
    //     //remove an item from inventory and add
    //     //it to the current room
    //     status = "I can't drop that silly.";

    //     Item[] currentRoomItems;

    //     for (Item item : inventory) {
    //         if(item.name == noun){
    //             currentRoom.items = 
    //         }
    //     }

    // }

    private static void handleUse(String noun){
        //use an item
        boolean hasItem = false;

        for (Item item : inventory) {
            if(item != null){
                if(item.name.equals(noun)){
                    hasItem = true;
                }
            } 
        }

        if(hasItem){        
            switch(noun){
                
                case "radio":
                    handleListen();
                    break;
                
                case "shovel":
                    if(currentRoom.name.equals("crossroads")){
                        status = "I can use the shovel to dig up that weird shiny thing.\nIt's a tin full of batteries";
                        for (Item item : currentRoom.items) {
                            if(item.name.equals("shiny")){
                                item.name = "batteries";
                                handleTake("batteries");
                                item = null;
                            }
                        }
                    }

                    break;

                case "batteries":
                    for (Item item : inventory) {
                        if(item.name.equals("radio")){
                            for (Item item1 : inventory) {
                                if(item1.name == "batteries"){
                                    status = "I put fresh batteries in the radio, hopefully it works now.";
                                    item1 = null;
                                    hasRadio = true;
                                }
                            }
                        }
                    }
                    
                    break;

                case "candle":

                    if(candleLit){
                        status = "I put the candle out.";
                    }
                    else{
                        status = "I lit the candle.";
                    }

                    break;

                case "lockpicks":
                    
                    if(currentRoom.name.equals("graveyard")){
                        for(Item item : inventory){
                            if(item.name.equals("crumpledPaper")){
                                status = "I got the bike unlocked!";
                                hasBike = true;
                            }
                            else{
                                status = "I don't know how to use these...";
                            }
                        }
                    }
                    

                default:
                    status = "Um... not really sure how I could use that here.";
        }
}


    }

    private static void handleHelp(){
        //list all accepted commands

        status = "Okay, here's the commands I can easily follow.\n";
        status += "'go', followed by a direction, to go to a new area\n";
        status += "'look', followed by an item, to take a closer look at it\n";
        status += "'take', followed by an item, to pick something up\n";
        status += "'drop', followed by an item, to remove an item from my bag\n";
        status += "'use', followed by an item, to use that object\n";
        status += "'talk', followed by a person, to talk to them\n";
        status += "'list inventory', to get a list of everything I have in my bag\n";
        status += "'describe area' or 'describe room', to hear my description of the area I'm in again\n";
        if(hasRadio){
            status += "'listen radio' to try to listen to the handheld radio\n";
        }
        status += "And of course 'help', in case you need this list again\n\n";
        status += "Don't forget that my phone sometimes does weird things if there's more than one space in a text,\n";
        status += "so just put one space between verbs and nouns, then camel case everything else.\n";

    }

    private static void handleTalk(String noun){
        //talk to a character

        status = "How am I supposed to talk to someone who isn't there?";

        //if character name is unknown speak to first unknown character in room
        if(noun.equals("???")){
            for(Character character : currentRoom.characters){
                if(character.known == false){
                    status = character.speak();
                }
            }
        }

        else{
            for(Character character : currentRoom.characters){
                if(noun.equals(character.name)){
                    status = character.speak();
                }
            }
        }
    }

    private static void handleListInventory(){
        //list everything in inventory
        status = "Alright, I'll list out everything in my bag";
        status += "\nphone\ncompass\nrope\npocketknife\nwater bottle\n";
        for(Item item : inventory){
            if(item != null){
                status += item.toString() + "\n";
            }
        }  
    }

    private static void handleDescribeArea(){
        //print a description of the current area
        status = currentRoom.toString();
    }

    private static void handleListen(){
        //listen to radio
        String radioLine0 = "*Static*";
        String radioLine1 = "12 18 14\t12 16 7\t19 18 6 1\t21 16 19\t13 2 2 3\n18 1 10 15 1 2 7 7 26 3 15 8 *static*";//7%
        String radioLine2 = "The weather forecast for tomorrow is *static* with moderate winds and temperatures of 55-67 degrees *static*"; //12%
        String radioLine3 = "*static* ple *static* please *static* he l p u s *click*"; //2.4%
        String radioLine4 = "*static* Wake up."; //4.7%
        String radioLine5 = "Huh. I don't think it's working.";//7%
        String radioLine6 = "do do do doo, *static* do do do do do doo, do do do do do doo-ooo-oo. *static*"; //12%
        String radioLine7 = "Never gonna give you up, never let you down, never gonna run around and desert you\nNever gonna make you cry, never gonna say goodbye, never gonna tell a lie, and hurt you."; //12%
        String radioLine8 = "Public Safety Announcements: Do not go to the graveyard at night or into the forest at dusk.\nIf the payphone in town rings, do not answer it. Do not attempt to explore the radio tower alone,\nand stay away from the abandoned cereal factory.";//12%
        String radioLine9 = "6 10 21 16 5 2 7\t18 3\t2 22 10 2 1 26 4 2 3 5\t7 5 16 5 6 7 *static* *crackle";//4.7%
        String radioLine10 = "So tell us a bit more about your secret to growing those bea*static*utiful sunflowers. Well, it takes a lo*static*t of work Mr. Oddwheel*crackle*. Keeping them watered, making sure they get enou*static*gh sun and don't have bu*crackle*gs, bl*static*ood sacri*crackle*fices.*crackle*";//9.5%
        String radioLine11 = "do do dooo! Tired of interesting breakfasts? *crackle* mix things up with the\nworld's most generic, boring, and totally not sentient and evil cereal brand, Cheeri Tori!";//7%

        String[] radioOptions = {radioLine3, radioLine4, radioLine4, radioLine9, radioLine9, radioLine11, radioLine11, radioLine1, radioLine1, radioLine1, radioLine5, radioLine5, radioLine5, radioLine10, radioLine10, radioLine10, radioLine10, radioLine0, radioLine0, radioLine0, radioLine0, radioLine0, radioLine2, radioLine2, radioLine2, radioLine2, radioLine2, radioLine6, radioLine6, radioLine6, radioLine6, radioLine6, radioLine7, radioLine7, radioLine7, radioLine7, radioLine7, radioLine8, radioLine8, radioLine8, radioLine8, radioLine8};
        Random random = new Random();
        String output = radioOptions[random.nextInt(radioOptions.length)];

        status = output;
    }

    public static void setupGame(){
        //set up the game

        //set up time tracking
        startTime = Instant.now();
        daysPassed = 0;
        timeOfDay = "dawn";

        //create rooms
        orchard = new Room("Orchard");
        graveyard = new Room("Graveyard");
        crossroads = new Room("Crossroads");
        forestEdge = new Room("Forest Edge");
        deepForest = new Room("Deep Forest");
        barn = new Room("Barn");
        farmhouse = new Room("Farmhouse");
        longRoad = new Room("Long Road");
        brewery = new Room("Brewery");

        /* To be implemented later, once bare minimum is done 
         * Room ciderStand = new Room("Cider Stand");
         * Room mainTown = new Room("Main Town");
         * Room relayTower = new Room("Microwave Relay Tower")
        */


        // Setup orchard
        orchard.setDescription("The orchard is full of ancient apple trees filled with ripe fruit.\nIt's so quiet and peaceful here. It reminds me a lot of the apple\ntrees we had at my house growing up.");
        
        //create items
        Item swing = new Item("swing");
        swing.setDescription("There's an old tire swing hanging from one of the trees.\nIt looks like it might fall apart if a squirrel climbs on it.");

        Item apple = new Item("apple");
        apple.setDescription("These apples look really good. I think they're macintoshes.");
        apple.setGrabbable(true);

        Item mushroom = new Item("mushroom");
        mushroom.setDescription("I found some cool looking mushrooms near the roots of the\ntrees. They look like the ones my family used to pick when I was little.");

        Item crumpledPaper = new Item("crumpledPaper");
        crumpledPaper.setDescription("There's a crumpled piece of paper wedged between two branches.\nIt's labeled 'Lockpicking for Dummies'.");
        crumpledPaper.setGrabbable(true);

        Item[] orchardItems = {swing, apple, mushroom, crumpledPaper};

        orchard.setItems(orchardItems);

        //create exits
        Exit toBrewery = new Exit("north", brewery);
        toBrewery.setPreview("There's some kind of building ahead.");

        Exit toFarmhouse = new Exit("east", farmhouse);
        toFarmhouse.setPreview("I think I see a house that way.");

        Exit toCrossroads = new Exit("south", crossroads);
        toCrossroads.setPreview("That would take me back to the crossroads.");

        Exit toBarn = new Exit("west", barn);
        toBarn.setPreview("There's a building through the trees.");

        Exit [] orchardExits = {toCrossroads, toBarn, toFarmhouse, toBrewery};
        orchard.setExits(orchardExits);


        // Setup crossroads
        crossroads.setDescription("The roads extend in all directions as far as I can see.\nIt looks completely empty though, and I can't see any cars.");
        
        //create items
        Item stopSign = new Item("stopSign");
        stopSign.setDescription("There's a slightly bent stop sign in the corner.");

        Item flowers = new Item("flowers");
        flowers.setDescription("There's a bunch of really pretty light orange coneflowers growing by the edge of the road.");
        flowers.setGrabbable(true);

        Item shiny = new Item("shiny");
        shiny.setDescription("There's something glittery half-buried in the ground, but I can't\nget it out. It feels cold and metallic.");


        Item[] crossroadsItems = {stopSign, flowers, shiny};

        crossroads.setItems(crossroadsItems);

        //create exits
        Exit toOrchard = new Exit("north", orchard);
        toOrchard.setPreview("There's a sign that says 'Honeycomb Orchard and Brewery'.");

        Exit toLongRoad = new Exit("east", longRoad);
        toLongRoad.setPreview("Just more road, stretching off into the distance.");

        Exit toGraveyard = new Exit("south", graveyard);
        toGraveyard.setPreview("It leads to an arched gate in an ornate iron fence.");

        Exit toForestEdge = new Exit("west", forestEdge);
        toForestEdge.setPreview("It leads back to the woods.");

        Exit[] crossroadsExits = {toOrchard, toLongRoad, toGraveyard, toForestEdge};
        crossroads.setExits(crossroadsExits);


        // Setup forest edge
        forestEdge.setDescription("This place is... eerie. I can't quite place it but something\nfeels really off, like I shouldn't be here.");
        
        //create items
        Item well = new Item("well");
        well.setDescription("There's an ancient well, overgrown with vines. It's creepy.");

        Item lockpicks = new Item("lockpicks");
        lockpicks.setDescription("That's weird. There's a cracked plastic pencil case full of\nlockpicking tools on the ground. There's a label on it, but\nit's too scratched up to read.");
        lockpicks.setGrabbable(true);

        Item marbles = new Item("marbles");
        marbles.setDescription("Are those ... marbles scattered on the ground?");

        Item[] forestEdgeItems = {well, lockpicks, marbles};

        forestEdge.setItems(forestEdgeItems);

        //create exits
        Exit forestToCrossroads = new Exit("east", crossroads);
        forestToCrossroads.setPreview("Back to the crossroads.");

        Exit toDeepForest = new Exit("west", deepForest);
        toDeepForest.setPreview("The road goes deeper into the woods for a while, then disappears.");

        Exit [] forestEdgeExits = {forestToCrossroads, toDeepForest};
        forestEdge.setExits(forestEdgeExits);

        //add characters (animals are counted as characters)
        Character crow = new Character("crow");
        crow.setIntro("Crow: caw!");
        String crowLine1 = "Crow: caw!"; //38% chance
        String crowLine2 = "It's staring at me in disapproving silence."; //17% chance
        String crowLine3 = "Crow: hELlO HuMAn\nDid...did that bird just talk? You heard that, right?"; //11% chance
        String crowLine4 = "*flap flap*"; //17% chance
        String crowLine5 = "Crow: Wake up.\nUmm...well that's not creepy at all"; //6% chance
        String crowLine6 = "Crow: oOh ShInY!\nDid...did that bird just talk? You heard that, right?"; //11% chance

        //some elements in list are duplicates so they have a greater chance of being randomly selected
        String[] crowDialogue = {crowLine1, crowLine1, crowLine1, crowLine1, crowLine1, crowLine1, crowLine1, crowLine2, crowLine2, crowLine2, crowLine3, crowLine3, crowLine4, crowLine4, crowLine4, crowLine5, crowLine6, crowLine6};

        crow.setDialogue(crowDialogue);
        crow.setKnown(true);

        Character[] forestEdgeCharacters = {crow};
        forestEdge.setCharacters(forestEdgeCharacters);


        // Setup deep forest


        // Setup graveyard
        graveyard.setDescription("inruibdgujergergnrengugun9orgojovve rg erng[uiergu  er urbe rth ]\n Sorry, dropped my phone. The graveyard is surprisingly un-creepy compared to the forest.\nIt looks old, but well maintained. Almost all the graves have real flowers planted\non them and I have not spotted any ghosts yet.");

        //create items
        Item bikeRack = new Item("bikeRack");
        bikeRack.setDescription("There's a rusty bike rack that looks like it's one strong wind\naway from crumbling completely.");

        Item bike = new Item("bike");
        bike.setDescription("A light green bike is resting on the rack. Some of its paint is\npeeling, but other than that it seems to be in pretty good shape.\nI'd need a way to get the lock off if I wanted to use it though.");

        Item oldTree = new Item("oldTree");
        oldTree.setDescription("A massive, ancient tree sits in the center of the graveyard.");

        Item shovel = new Item("shovel");
        shovel.setDescription("It's a small shovel. In a graveyard. I really hope whoever left\nit here was just planting flowers with it.");
        shovel.setGrabbable(true);

        Item paperScrap = new Item("paperScrap");
        paperScrap.setDescription("A torn, water-stained piece of paper is lying on the ground by a\nheadstone. Written on it in shaky handwriting is 'When thoughts   \r\n" + //
                        "Of the last bitter hour come like a blight   \r\n" +
                        "Over thy spirit, and sad images   \r\n" +
                        "Of the stern agony, and shroud, and pall,   \r\n" +
                        "And breathless darkness, and the narrow house,   \r\n" +
                        "Make thee to shudder, and grow sick at heart;—   \r\n" +
                        "Go forth, under the open sky, and list   \r\n" +
                        "To Nature’s teachings'");
        paperScrap.setGrabbable(true);

        Item[] graveyardItems = {bikeRack, bike, oldTree, paperScrap, shovel};

        graveyard.setItems(graveyardItems);

        //create exits
        Exit graveyardToCrossroads = new Exit("north", crossroads);
        graveyardToCrossroads.setPreview("That would take me back to the crossroads.");

        Exit [] graveyardExits = {graveyardToCrossroads};
        graveyard.setExits(graveyardExits);


        // Setup long road

        //create exits
        Exit longRoadToCrossroads = new Exit("east", crossroads);
        toCrossroads.setPreview("That would take me back to the crossroads.");

        // Exit toTown = new Exit("west", mainTown);
        // toBarn.setPreview("There's a building through the trees.");

        Exit [] longRoadExits = {longRoadToCrossroads};
        longRoad.setExits(longRoadExits);


        // Setup barn
        barn.setDescription("I don't think this barn has actually been used to hold any animals in a while.\nThere is a cat wandering around, but other than that I think it's just storage.");
        
        //create items
        Item bed = new Item("bed");
        bed.setDescription("There's a small cot set up in the old hay loft. It's full of blankets and actually looks really comfy.");

        Item photos = new Item("photo");
        photos.setDescription("Hey, I found a stack of old polaroid pictures on one of the\nshelves! A lot of them are blank, but this one shows a group of teenagers\nsmiling next to what looks like ... an abandoned radio tower?");
        photos.setGrabbable(true);

        Item candle = new Item("candle");
        candle.setDescription("There's a tall candle in a holder on the stairs to the loft.\nIt smells like beeswax and vanilla. There's a box of matches with it too.");
        candle.setGrabbable(true);

        Item beehives = new Item("beehives");
        beehives.setDescription("There's a bunch of weird boxes lined up in rows just outside\nthe barn. I think they're probably beehives, but I'm not 100% sure.");

        Item jarOfHoney = new Item("jarOfHoney");
        jarOfHoney.setDescription("There's also a few jars of what I assume is honey from the beehives.");

        Item[] barnItems = {bed, photos, candle, beehives, jarOfHoney};

        barn.setItems(barnItems);

        //create exits
        Exit barnToOrchard = new Exit("east", orchard);
        barnToOrchard.setPreview("Back to the orchard again.");

        Exit [] barnExits = {barnToOrchard};
        barn.setExits(barnExits);

        //add characters (animals are counted as characters)
        Character cat = new Character("cat");
        cat.setIntro("Cat: Mrow?");
        String catLine1 = "Cat: *purr*"; //38 purr-cent chance
        String catLine2 = "It's staring at me in disapproving silence."; //17% chance
        String catLine3 = "Cat: *hiss*"; //11% chance
        String catLine4 = "Cat: Mrow?"; //17% chance
        String catLine5 = "Cat: Wake up.\nW-what just - I must be losing it. Cats don't talk."; //6% chance
        String catLine6 = "Cat: *growls*"; //11% chance

        //some elements in list are duplicates so they have a greater chance of being randomly selected
        String[] catDialogue = {catLine1, catLine1, catLine1, catLine1, catLine1, catLine1, catLine1, catLine2, catLine2, catLine2, catLine3, catLine3, catLine4, catLine4, catLine4, catLine5, catLine6, catLine6};

        cat.setDialogue(catDialogue);
        cat.setKnown(true);

        Character[] barnCharacters = {cat};
        barn.setCharacters(barnCharacters);



        // Setup brewery
        brewery.setDescription("A brewery? I wonder if the orchard sells hard cider or something. Oh hey! there's a person here.\nHe's wearing something that looks like a hazmat suit... or maybe a beekeeper suit?");
        
        //create items
        Item ciderPress = new Item("ciderPress");
        ciderPress.setDescription("I can see a strange contraption made of wood and metal.\nIt has a big funnel on top and a crank on the side. I think it might be a cider press?");

        Item bottles = new Item("bottles");
        bottles.setDescription("There are a bunch of bottles and gallon jugs lining the shelves.\nPresumably they're full of cider.");

        Item barrels = new Item("barrels");
        barrels.setDescription("One wall is lined with big barrels. I bet they're for fermenting hard cider.");
       
        Item honeyJars = new Item("honeyJars");
        honeyJars.setDescription("There are more jars of honey here too.");
       
        Item binsOfApples = new Item("binsOfApples");
        binsOfApples.setDescription("A bunch of big wooden bins full of fresh apples.");

        Item donuts = new Item("donuts");
        donuts.setDescription("Ooh, donuts! Do you think anyone would mind if I took some?");
        donuts.setGrabbable(true);

        Item[] breweryItems = {ciderPress, bottles, barrels, honeyJars, binsOfApples, donuts};

        brewery.setItems(breweryItems);

        //create exits

        Exit breweryToOrchard = new Exit("south", orchard);
        breweryToOrchard.setPreview("Back to the orchard again.");

        Exit [] breweryExits = {breweryToOrchard};
        brewery.setExits(breweryExits);

        //characters
        Character clive = new Character("clive");
        clive.setIntro("???: Hi. Um... My name is Clive.");
        String cliveLine1 = "Clive: Hi.";
        String cliveLine2 = "Clive: We're known for our apple cider, but we also make mead from our bees' honey.";
        String cliveLine3 = "Clive: Marge handles most of the business stuff, I'm just the beekeper.\nIf you need help with anything, you're probably better off talking to her.";
        String cliveLine4 = "Clive: Umm...lovely weather we're having?";
        String cliveLine5 = "Clive: Sorry, I'm not super social.";
        String cliveLine6 = "He doesn't seem to notice me.";

        String[] cliveDialogue = {cliveLine1, cliveLine2, cliveLine3, cliveLine4, cliveLine5, cliveLine6};

        clive.setDialogue(cliveDialogue);

        Character[] breweryCharacters = {clive};
        brewery.setCharacters(breweryCharacters);


        // Setup farmhouse
        farmhouse.setDescription("A big house with faded white wood siding and light blue shutters on the windows.\nIt has a big porch with rows of alternating sunflowers and large green bushes in front.\nThere's someone sitting on the front porch knitting.");

        //create items
        Item porchSwing = new Item("porchSwing");
        porchSwing.setDescription("Ooh, a porch swing. It's got a bunch of really comfy looking pillows on it too.");

        Item sunflowers = new Item("sunflowers");
        sunflowers.setDescription("There's a vase of beautiful sunflowers on a little table.");
        sunflowers.setGrabbable(true);

        Item hummingbirdFeeder = new Item("hummingbirdFeeder");
        hummingbirdFeeder.setDescription("A hummingbird feeder is hanging from the roof. It's full\nof sugar water, but it's probably too late in the year for any birds to\nactually come to it.");

        Item dollhouse = new Item("dollhouse");
        dollhouse.setDescription("A big wooden dollhouse with tiny furniture inside. It looks handmade.");

        Item hammock = new Item("hammock");
        hammock.setDescription("There's a hammock hanging in the corner made of faded green fabric \npatterned with multicolored flowers and butterflies.");

        Item handheldRadio = new Item("handheldRadio");
        handheldRadio.setDescription("A small, handheld radio. It looks pretty old, I wonder if it still works?");
        handheldRadio.setGrabbable(true);

        Item[] farmhouseItems = {porchSwing, sunflowers, hummingbirdFeeder, dollhouse, handheldRadio, hammock};

        farmhouse.setItems(farmhouseItems);

        //create exits

        Exit farmhouseToOrchard = new Exit("west", orchard);
        farmhouseToOrchard.setPreview("That way leads back to the orchard.");

        Exit [] farmhouseExits = {farmhouseToOrchard};
        farmhouse.setExits(farmhouseExits);

        //add characters

        Character marge = new Character("marge");
        marge.setIntro("???: Hello there! Who are you talking to?\nOh, my friend is on the phone with me. What's your name?\n???:I'm Marge, nice to meet you. What are you doing all the way out here?\nExploring.\nMarge: Well, if you ever need a place to rest, you can stay in the barn west of the orchard.");
        String margeLine1 = "Marge: How's the exploring going?";
        String margeLine2 = "Marge: If you come back next week, we'll be giving tours of the orchard and cider mill during the cider festival.";
        String margeLine3 = "Marge: Oh, you want to go into town? It's a pretty long walk.\nMarge: Actually, I left my old bike in the graveyard a while ago, but I lost the key.\nMarge: If you can get it unlocked, you can take it and use it to travel long distances.";
        String margeLine4 = "Marge: Do you like the azaleas? They're pretty, but they're actually quite toxic.\nMarge: We have to be careful to label the honey out bees make\nMarge: while they're blooming so we don't accidentally sell anything poisonous.";
        String margeLine5 = "Marge: My oldest daughter, Oleander, is a toxicologist.\nI've always thought her interest in poisons was a bit odd, but she seems to really enjoy her research.";
        String margeLine6 = "Marge: Don't forget that you can use the bed in the hay loft of the old barn.\nMarge: A good rest can help a lot if you're feeling tired or stressed.";
        String margeLine7 = "Marge: Do you like to fix things? That old radio's been acting weird for years.\nMarge: You can take it if you want to try your hand at getting to work again\nMarge: It probably needs new batteries though.";
        String margeLine8 = "Marge: My parents helped my daughters make that dollhouse themselves.\nMarge: They even carved all the furniture and made tiny quilts out of scrap fabric.";
        String margeLine9 = "Marge: Have you had any luck getting that bike lock off?";
        String margeLine10 = "Marge is fast asleep in the hammock";

        String[] margeDialogue = {margeLine1, margeLine2, margeLine3, margeLine4, margeLine5, margeLine6, margeLine7, margeLine8, margeLine9, margeLine10};

        marge.setDialogue(margeDialogue);

        Character[] farmhouseCharacters = {marge};
        farmhouse.setCharacters(farmhouseCharacters);


        String intro = "*static* Can you hear me? I think we lost connection for a little bit.\n";
        intro += "anyways, I found something weird. Our map said the forest went on for nearly 10 miles\n";
        intro += "in every direction, but I've only been walking for an hour and I already found a road.\n";
        intro += "It looked like it was falling apart at first, but after I followed it for a bit\n";
        intro += "it seemed pretty well maintained, and the trees started thinning out too.\n";
        intro += "I'm out of the forest completely now. I wish you could have come exploring with me,\n";
        intro += "but the phone call will have to do. I'll try my best to describe what I see to you,\n";
        intro += "and you can text me instructions on what to do.\n";

        System.out.println(intro);

        currentRoom = crossroads;

        System.out.println(currentRoom.toString());

    }


    private static void checkTime(){
        //check the amount of time that has
        //passed since the start of the current
        //time block and change it if necessary

        Instant now = Instant.now();
        Duration timeElapsed = Duration.between(startTime, now);
        long timeElapsedMinutes = timeElapsed.toMinutes();
        if (timeElapsedMinutes >= TIME_BLOCK_LENGTH){
            changeTime();
        }

        if (daysPassed == 3){
            endGame("outOfTime");
        }

    }

    private static void changeTime(){
        //adjust current time block as appropriate
        switch(timeOfDay){

            case "dawn":
                timeOfDay = "midday";
                status += "\nI think the sun is fully up now.";
                break;

            case "midday":
                timeOfDay = "dusk";
                status += "\nIt's starting to get dark.";
                break;

            case "dusk":
                timeOfDay = "night";
                status += "It's definitely nighttime now";
                break;

            case "night":
                timeOfDay = "dawn";
                status += "\nThe sun is finally starting to rise.";
                daysPassed ++;
                break;
        }

        startTime = Instant.now();
    }


    private static void sleep(){
        //clears all negative status effects and skips a time block

        //clear status effects
        paranoid = false;
        badReception = false;
        exhausted = false;

        status = "That was a good nap.";

        changeTime();
    }

    private static void endGame(String ending){
        //ends the game and outputs a message based on what happened
        String result;

        switch(ending){
            case "wentHome":
                result = "Honestly, it wasn't the same without you here. Maybe we can come back someday together.\n\nLazy Ending";
                break;
            
            case "missing":
                result = "It's getting hard to see with so many branches in the way. I don't remember\nit being like this befo- What- what is that?? *static* *call ends*\nYou try to call back, but your friend never answers.\n\nMissing Ending";
                break;

            case "outOfTime":
                result = "Is the weekend really already over? *sigh* I guess I need to head home now\nAre you ready for the calculus test Tuesday?\n\nOut of Time Ending";
                break;

            case "insanity":
                result = "Not Implemented";
                break;

            case "finishedDemo":
                result = "I rode the bike into town. Unfortunately, the developer of this game was on\na time crunch and couldn't add any more gameplay before the due date,\nso I can't tell you any more about it.\n\nNonexistent Ending";
                break;

            default:
                result = "Not Implemented";
        }

        System.out.println(result);
        scanner.close();
        playing = false;
    }
}






