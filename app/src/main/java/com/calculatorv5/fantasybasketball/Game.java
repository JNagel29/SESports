package com.calculatorv5.fantasybasketball;

//Class to represent each game, will be put in list of games to display out on GamesActivity
public class Game {
    String homeName = "";
    String awayName = "";
    String homeScore = "";
    String awayScore = "";
    String gameStatus = "";
    int homeLogo = 0;
    int awayLogo = 0;

    //Constructor
    public Game(String homeName, String awayName, String homeScore, String awayScore, String gameStatus, int homeLogo, int awayLogo) {
        this.homeName = homeName;
        this.awayName = awayName;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.gameStatus = gameStatus;
        this.homeLogo = homeLogo;
        this.awayLogo = awayLogo;
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
    public int getHomeLogo() {
        return homeLogo;
    }
    public int getAwayLogo() {
        return awayLogo;
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
    public void setHomeLogo(int homeLogo) {
        this.homeLogo = homeLogo;
    }
    public void setAwayLogo(int awayLogo) {
        this.awayLogo = awayLogo;
    }
}
