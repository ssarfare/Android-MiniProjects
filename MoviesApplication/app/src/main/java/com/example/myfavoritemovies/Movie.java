package com.example.myfavoritemovies;

import java.io.Serializable;

public class Movie implements Serializable {
    public String name;
    public String description;
    public String genre;
    public Integer rating;
    public Integer year;
    public String imdbLink;
    public boolean isValid;
    public int movieId;

    public Movie(String name, String description, String genre, Integer rating, Integer year, String imdbLink) {

        this.name = name;
        this.description = description;
        this.genre = genre;
        this.rating = rating;
        this.year = year;
        this.imdbLink = imdbLink;
    }

    public Movie() {
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getGenre() {
        return genre;
    }

    public Integer getRating() {
        return rating;
    }

    public Integer getYear() {
        return year;
    }

    public String getImdbLink() {
        return imdbLink;
    }

    public boolean isValid() {
        return isValid;
    }

    public int getMovieId() {
        return movieId;
    }
}
