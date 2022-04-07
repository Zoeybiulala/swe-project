package com.example.explorejournal;

import androidx.annotation.NonNull;

import java.util.List;

public class Recipe {
    public String id;
    public String url;
    public String description;
    public String name;
    public List<String> tags;
    public List<String> users;

    public Recipe(String id, String url, String description, String name, List<String> tags, List<String> users){
        this.id = id;
        this.url = url;
        this.description = description;
        this.name = name;
        this.tags = tags;
        this.users = users;
    }

    @NonNull
    @Override
    public String toString(){
        String returnString = "ID: " + id + "/URL: " + url + "/Name: " + name + "\n";
        returnString += "Tags: ";
        for(int i=0; i<tags.size(); i++){
            returnString += tags.get(i) + ", ";
        }
        returnString += "\n";
        returnString += "Users: ";
        for(int i=0; i<users.size(); i++){
            returnString += users.get(i) + ", ";
        }
        returnString += "\n";
        returnString += "Description: " + description;
        return returnString;
    }
}
