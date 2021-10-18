package com.indiehood.app;

import java.util.List;

public class User {
    private List<String> interestedShows;
    private List<String> favoritedBands;
    private String UID;
    private String artist;

    User() {

    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public List<String> getFavoritedBands() {
        return favoritedBands;
    }

    public boolean isArtistFavorited(String artist) {
        return favoritedBands.contains(artist);
    }

    public void setFavoritedBands(List<String> favoritedBands) {
        this.favoritedBands = favoritedBands;
    }

    public void addFavoritedBand(String artist) {
        favoritedBands.add(artist);
    }

    public void removeFavoritedBand(String artist) {
        if (isArtistFavorited(artist)) favoritedBands.remove(artist);
    }

    public List<String> getInterestedShows() {
        return interestedShows;
    }

    public void setInterestedShows(List<String> interestedShows) {
        this.interestedShows = interestedShows;
    }
}
