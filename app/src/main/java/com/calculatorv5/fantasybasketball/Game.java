package com.calculatorv5.fantasybasketball;

//Class to represent each game, will be put in list of games to display out on GamesActivity
public class Game {
    private String homeName, awayName, homeScore, awayScore, gameStatus, gameTime;
    private int homeLogo, awayLogo;


    //Constructor
    public Game(String homeName, String awayName, String homeScore, String awayScore, String gameStatus, String gameTime,
                int homeLogo, int awayLogo) {
        this.homeName = homeName;
        this.awayName = awayName;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.gameStatus = gameStatus;
        this.gameTime = gameTime;
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
    public String getGameTime() {
        return gameTime;
    }
    public int getHomeLogo() {
        return homeLogo;
    }
    public int getAwayLogo() {
        return awayLogo;
    }

}
