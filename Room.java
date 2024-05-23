class Room{

    public String name;
    public Exit[] exits; // north, south, east, west
    public Item[] items;
    public String description;
    public Character[] characters;


    public Room(String name){
        this.name = name;
    }

    public void setExits(Exit[] exits){
        this.exits = exits;
    }

    public Exit[] getExits(){
        return exits;
    }

    public void setItems(Item[] items){
        this.items = items;
    }

    public Item[] getItems(){
        return items;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getDescription(){
        return description;
    }

    public void setCharacters(Character[] characters){
        this.characters = characters;
    }

    public Character[] getCharacters(){
        return characters;
    }



    public String toString(){
        String result = "\n";
        result += "I'm at the " + name + ".\n\n";

        //describe area
        result += description + "\n";

        // add items to the output
        result += "\nI can see:\n";
        for (int i = 0; i < items.length; i++){
            if(items[i] != null){
                result += items[i].name + "\n";
            }
        }

        // add exits to the output
        result += "\nExits:\n";
        for (Exit exit : exits){
            result += exit.toString() + "\n";
        }

        //add characters
        if (characters != null) {
            result += "\nPeople and Animals\n";
            for (Character character : characters){
                if(character.known){
                result += character.name + "\n";
                }
                else{
                result += "???\n";
                }
            }
        }

        return result;
    }

}
