//TODO dynamically created use function

class Item {
    public String name;
    public String description;
    public boolean grabbable;
    public boolean usable;

    public Item(String name){
        this.name = name;
        grabbable = false;
        usable = false;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getDescription(){
        return description;
    }

    public void setGrabbable(boolean grabbable){
        this.grabbable = grabbable;
    }

    public boolean getGrabbable(){
        return grabbable;
    }

    public void setUsable(boolean usable){
        this.usable = usable;
    }

    public boolean getUsable(){
        return usable;
    }

    public String toString(){
        return name + ": " + description;
    }

}