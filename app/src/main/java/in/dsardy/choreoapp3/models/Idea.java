package in.dsardy.choreoapp3.models;

/**
 * Created by dell on 8/16/2016.
 */
public class Idea {

    String title ;
    String description ;
    String uid ;
    String name ;
    String likes = "0";
    String time;

    public Idea(){

    }

    public Idea(String title,String description,String uid,String name,String time){

        this.name = name;
        this.title = title;
        this.description = description;
        this.uid = uid;
        this.time = time;

    }

    public String getTitle(){
        return title;
    }

    public String getDescription(){
        return description;
    }

    public String getName(){
        return name;
    }

    public String getLikes() {
        return likes;
    }

    public String getUid(){
        return uid;
    }

    public String getTime() {
        return time;
    }
}
