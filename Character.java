import java.util.Random;

class Character {
    public String name;
    public boolean known;
    public String intro;
    public String[] dialogue;

    public Character(String name){
        this.name = name;
        known = false;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setKnown(boolean known){
        this.known = known;
    }

    public boolean getKnown(){
        return known;
    }

    public void setIntro(String intro){
        this.intro = intro;
    }

    public String getIntro(){
        return intro;
    }

    public void setDialogue(String[] dialogue){
        this.dialogue = dialogue;
    }

    public String[] getDialogue(){
        return dialogue;
    }

    public String speak(){
        //If character has not been met before, returns 
        //their introduction dialogue. Otherwise, returns
        //a random dialogue

        if(known == false){
            known = true;
            return intro;
        }

        else{
            Random random = new Random();
            String words = dialogue[random.nextInt(dialogue.length)];
            return words;
        }
    }

}
