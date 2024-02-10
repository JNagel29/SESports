package com.calculatorv5.fantasybasketball;

//Class to represent each game, will be put in list of games to display out on GamesActivity
public class Game {
    String homeName = "";
    String awayName = "";
    String homeScore = "";
    String awayScore = "";
    String gameStatus = "";

    //TODO: could add a logo path string to add an image view from drawable for each team
    String homeLogo = "";
    String awayLogo = "";

    //Constructor
    public Game(String homeName, String awayName, String homeScore, String awayScore, String gameStatus) {
        this.homeName = homeName;
        this.awayName = awayName;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.gameStatus = gameStatus;
    }
    //Getters
    public String getHomeName() {
        return homeName;
    }
    public String getAwayName() {
        return awayName;
    }
    public String getHomeScore() {
        return homeScore;
    }
    public String getAwayScore() {
        return awayScore;
    }
    public String getGameStatus() {
        return gameStatus;
    }
    //Setters
    public void setHomeName(String homeName) {
        this.homeName = homeName;
    }
    public void setAwayName(String awayName) {
        this.awayName = awayName;
    }
    public void setHomeScore(String homeScore) {
        this.homeScore = homeScore;
    }
    public void setAwayScore(String awayScore) {
        this.awayScore = awayScore;
    }
    public void setGameStatus(String gameStatus) {
        this.gameStatus = gameStatus;
    }
}
