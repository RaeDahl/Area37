class Exit {
    public String direction;
    public Room destination;
    public String preview;

    public Exit(String direction, Room destination){
        this.direction = direction;
        this.destination = destination;
    }

    public void setDirection(String direction){
        this.direction = direction;
    }

    public String getDirection(){
        return direction;
    }

    public void setPreview(String preview){
        this.preview = preview;
    }

    public String getPreview(){
        return preview;
    }

    public void setDestination(Room destination){
        this.destination = destination;
    }

    public Room getDestination(){
        return destination;
    }



    public String toString(){
        String result = "To the " + direction + ": ";
        result += preview;

        return result;
    }

}
