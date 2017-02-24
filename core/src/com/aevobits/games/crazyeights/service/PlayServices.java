package com.aevobits.games.crazyeights.service;

import com.aevobits.games.crazyeights.BaseGame;

/**
 * Created by vito on 13/12/16.
 */

public interface PlayServices {
    void signIn();
    void signOut();
    void rateGame();
    void unlockAchievement();
    void submitScore(int highScore);
    void showAchievement();
    void showScore();
    boolean isSignedIn();
    void quickStartGame(BaseGame game);
    void recordVideo();
    void sendReliableMessage(String messageToSend);
    void showOrLoadInterstital();
}
