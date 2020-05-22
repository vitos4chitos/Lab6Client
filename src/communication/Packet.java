package communication;

import Data.Person;

import java.io.Serializable;
import java.util.*;

public class Packet implements Serializable {

    private boolean mode;
    private String command;
    private Person person;
    private ArrayList<String> answer = new ArrayList<>();
    private String info;


    public boolean getMode(){
        return mode;
    }
    public String getCommand(){
        return command;
    }
    public Person getPersons(){
        return person;
    }
    public String getInfo(){
        return info;
    }

    public Packet(boolean mode, String command, String info, Person person){
        this.command = command;
        this.mode = mode;
        this.info = info;
        this.person = person;
    }

    public void cleanAnswer(){
        answer.clear();
    }
    public void addAnswer(String s){
        answer.add(s);
    }
    public void giveAnswer(){
        for(String s: answer){
            System.out.println(s);
        }
    }
}