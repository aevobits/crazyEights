package com.aevobits.games.crazyeights.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * Created by vito on 02/12/16.
 */

public class PlayerDataManager {
    private Preferences prefs;
    private static PlayerDataManager instance;
    private static final String PREFERENCE_NAME = "com.aevobits.games.crazyeights.prefs";

    public int playerScore;
    public int oppScore;
    public int round;
    public boolean playerWinnerGame;
    public boolean oppWinnerGame;
    public boolean playerWinnerHand;
    public boolean playerStartTurn;


    private PlayerDataManager(){
        prefs = Gdx.app.getPreferences(PREFERENCE_NAME);
        this.playerScore = 0;
        this.oppScore = 0;
        this.round = 1;
        this.playerWinnerGame = false;
        this.oppWinnerGame = false;
        this.playerWinnerHand = false;
    }

    public static PlayerDataManager getInstance(){
        if (instance == null){
            instance = new PlayerDataManager();
        }
        return instance;
    }

    public void resetGameData(){
        this.playerScore = 0;
        this.oppScore = 0;
        this.round = 1;
        this.playerWinnerGame = false;
        this.oppWinnerGame = false;
        this.playerWinnerHand = false;
    }

    public boolean isPlayerWinnerGame(){
        return this.playerWinnerGame;
    }

    public void setPlayerWinnerGame(boolean gameWon){
        this.playerWinnerGame = gameWon;
    }

    public boolean isOppWinnerGame() {
        return oppWinnerGame;
    }

    public void setOppWinnerGame(boolean oppWinnerGame) {
        this.oppWinnerGame = oppWinnerGame;
    }

    public boolean isPlayerWinnerHand(){
        return this.playerWinnerHand;
    }

    public void setPlayerWinnerHand(boolean handWon){
        this.playerWinnerHand = handWon;
    }

    public int getPlayerScore() {
        return playerScore;
    }

    public void setPlayerScore(int playerScore) {
        this.playerScore = playerScore;
    }

    public int getOppScore() {
        return oppScore;
    }

    public void setOppScore(int oppScore) {
        this.oppScore = oppScore;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public boolean isPlayerStartTurn() {
        return playerStartTurn;
    }

    public void setPlayerStartTurn(boolean playerStartTurn) {
        this.playerStartTurn = playerStartTurn;
    }

    public boolean isSound() {
        return prefs.getBoolean("Sound", true);
    }

    public void setSound(boolean sound){
        prefs.putBoolean("Sound", sound);
        prefs.flush();
    }

    public int getThresholdScore(){
        return 300;//prefs.getInteger("ThresholdScore", 20);
    }

    public void setThresholdScore(int thresholdScore){
        prefs.getInteger("ThresholdScore", thresholdScore);
        prefs.flush();
    }

    public boolean isDarkInvalidMoves() {
        return prefs.getBoolean("DarkInvalidMoves", true);
    }

    public void setDarkInvalidMoves(boolean darkInvalidMoves) {
        prefs.putBoolean("DarkInvalidMoves", darkInvalidMoves);
        prefs.flush();
    }
}
