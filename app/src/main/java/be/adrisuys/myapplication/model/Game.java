package be.adrisuys.myapplication.model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Game implements Serializable {

    private String name;
    private int yearPublished;
    private int minPlayers;
    private int maxPlayers;
    private int minPlaytime;
    private int maxPlaytime;
    private String description;
    private int numUserRatings;
    private double averageUserRatings;
    private String imgUrl;
    private int minAge;
    private List<Categorie> categories;

    public Game(String name, int yearPublished, int minPlayers, int maxPlayers, int minPlaytime, int maxPlaytime, String description, int numUserRatings, double averageUserRatings, String imgUrl, int minAge) {
        this.name = name;
        this.yearPublished = yearPublished;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.minPlaytime = minPlaytime;
        this.maxPlaytime = maxPlaytime;
        this.description = (description == "" || description.isEmpty()) ? "No description available" : description;
        this.numUserRatings = numUserRatings;
        this.averageUserRatings = averageUserRatings;
        this.imgUrl = imgUrl;
        this.minAge = minAge;
        categories = new ArrayList<>();
    }

    public void addCategorie(Categorie categorie) {
        this.categories.add(categorie);
    }

    public String getName() {
        return name;
    }

    public String getNumbersOfPlayers() {
        if (minPlayers == maxPlayers){
            return maxPlayers + "";
        }
        return minPlayers + "-" + maxPlayers;
    }

    public String getPlayTime() {
        if (minPlaytime == maxPlaytime){
            return maxPlaytime + "''";
        }
        return minPlaytime + "-" + maxPlaytime + "''";
    }

    public String getRatings() {
        double rounded = Math.round(averageUserRatings * 10) / 10.0;
        return rounded + "/5 (" + numUserRatings + ")";
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getDescription() {
        return description;
    }

    public float getRatingAsFloat() {
        return (float) averageUserRatings;
    }

    public String getNumberOfRatings() {
        return "(" + numUserRatings + ")";
    }

    public String getNameAndYear() {
        if (yearPublished == 0){
            return name;
        }
        return name + " (" + yearPublished + ")";
    }

    public String getMinAge() {
        if (minAge <= 0){
            return "?";
        } else {
            return minAge + "+";
        }
    }

    @NonNull
    @Override
    public String toString() {
        return getNameAndYear() + " " + getNumbersOfPlayers() + " " + getPlayTime() + " " + getRatings() + " " + getDescription();
    }

    public String getCategories(){
        String s = "Categories : ";
        for (int i = 0; i < categories.size(); i++){
            Categorie c = categories.get(i);
            if (i != categories.size() - 1){
                s += c.getName() + ", ";
            } else {
                s += c.getName();
            }
        }
        return s;
    }
}
