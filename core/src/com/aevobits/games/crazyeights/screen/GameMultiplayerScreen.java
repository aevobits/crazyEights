package com.aevobits.games.crazyeights.screen;

import com.aevobits.games.crazyeights.BaseGame;
import com.aevobits.games.crazyeights.GameUtils;
import com.aevobits.games.crazyeights.actor.BaseActor;
import com.aevobits.games.crazyeights.entity.Card;
import com.aevobits.games.crazyeights.entity.SuitIcon;
import com.aevobits.games.crazyeights.manager.ComputerPlayerManager;
import com.aevobits.games.crazyeights.manager.GameManager;
import com.aevobits.games.crazyeights.manager.GameMultiplayerManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.StringBuilder;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;

/**
 * Created by vito on 12/11/16.
 */

public class GameMultiplayerScreen extends BaseScreen {

    private BaseActor background;
    public GameMultiplayerManager gameManager;
    public Label playerLabel;
    public Label computerLabel;
    public Label playerScoreLabel;
    public Label computerScoreLabel;
    public Label playerCardsLabel;
    public Label computerCardsLabel;
    private Label deckCountLabel;
    public Table chooseSuit;
    public Table computerLabelTable;
    public Table playerLabelTable;
    private boolean isShowedWinDialog;
    private int score;
    private String[] participantNames;

    // game world dimensions
    public final int mapWidth = 480;
    public final int mapHeight = 800;

    public GameMultiplayerScreen(BaseGame g, String message){
        super(g, message);

    }

    @Override
    public void create() {

        isShowedWinDialog = false;
        background = new BaseActor();
        background.setTexture( new Texture(Gdx.files.internal("greenTable.png")) );
        mainStage.addActor(background);

        chooseSuit = new Table();
        chooseSuit.setFillParent(true);

        Stack stacker = new Stack();
        stacker.setFillParent(true);
        uiStage.addActor(stacker);
        stacker.add(uiTable);
        stacker.add(chooseSuit);

        game.skin.add("white", new Texture( Gdx.files.internal("white4px.png")) );
        Drawable chooseSuitBackground = game.skin.newDrawable("white", Color.WHITE );

        BaseActor clubsSuit = new BaseActor();
        clubsSuit.setTexture( new Texture(Gdx.files.internal("clubs.png")) );
        clubsSuit.addListener(new InputListener(){
                    public boolean touchDown (InputEvent event, float x, float y, int pointer, int button)
                    {  return true;  }

                    public void touchUp (InputEvent event, float x, float y, int pointer, int button)
                    {
                        gameManager.setWildSuit(Card.Suit.CLUBS);
                        gameManager.darkInvalidMoves(gameManager.playerHandGroup);
                        SuitIcon suit = new SuitIcon();
                        suit.createGraphicIcon(Card.Suit.CLUBS, mapWidth / 2 + 120f, (mapHeight / 2) + 50f);
                        gameManager.suitGroup.addActor(suit);
                        mainStage.addActor(gameManager.suitGroup);
                        chooseSuit.setVisible(false);
                        gameManager.chooseSuitRunning = false;
                        String ct = gameManager.messageToSend.substring(gameManager.messageToSend.length() - 1);
                        if (ct.equals("@")){
                            gameManager.messageToSend.insert(gameManager.messageToSend.length() -1, "#" + Card.Suit.CLUBS.ordinal());
                        }else {
                            gameManager.messageToSend.append("#" + Card.Suit.CLUBS.ordinal());
                        }
                        game.playServices.sendReliableMessage(gameManager.messageToSend.toString());
                        if (gameManager.getTopOfDiscard().rank == Card.Rank.FOUR) {
                            gameManager.drawCardToComputer(gameManager.cardsToDrawRankFour);
                            gameManager.cardsToDrawRankFour = 0;
                        }
                    }
        });

        BaseActor heartsSuit = new BaseActor();
        heartsSuit.setTexture( new Texture(Gdx.files.internal("hearts.png")) );
        heartsSuit.addListener(new InputListener(){
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button)
            {  return true;  }

            public void touchUp (InputEvent event, float x, float y, int pointer, int button)
            {
                gameManager.setWildSuit(Card.Suit.HEARTS);
                gameManager.darkInvalidMoves(gameManager.playerHandGroup);
                SuitIcon suit = new SuitIcon();
                suit.createGraphicIcon(Card.Suit.HEARTS, mapWidth / 2 + 120f, (mapHeight / 2) + 50f);
                gameManager.suitGroup.addActor(suit);
                mainStage.addActor(gameManager.suitGroup);
                chooseSuit.setVisible(false);
                gameManager.chooseSuitRunning = false;
                String ct = gameManager.messageToSend.substring(gameManager.messageToSend.length() - 1);
                if (ct.equals("@")){
                    gameManager.messageToSend.insert(gameManager.messageToSend.length() -1, "#" + Card.Suit.HEARTS.ordinal());
                }else {
                    gameManager.messageToSend.append("#" + Card.Suit.HEARTS.ordinal());
                }
                game.playServices.sendReliableMessage(gameManager.messageToSend.toString());
                if (gameManager.getTopOfDiscard().rank == Card.Rank.FOUR) {
                    gameManager.drawCardToComputer(gameManager.cardsToDrawRankFour);
                    gameManager.cardsToDrawRankFour = 0;
                }
            }
        });

        BaseActor spadesSuit = new BaseActor();
        spadesSuit.setTexture( new Texture(Gdx.files.internal("spades.png")) );
        spadesSuit.addListener(new InputListener(){
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button)
            {  return true;  }

            public void touchUp (InputEvent event, float x, float y, int pointer, int button)
            {
                gameManager.setWildSuit(Card.Suit.SPADES);
                gameManager.darkInvalidMoves(gameManager.playerHandGroup);
                SuitIcon suit = new SuitIcon();
                suit.createGraphicIcon(Card.Suit.SPADES, mapWidth / 2 + 120f, (mapHeight / 2) + 50f);
                gameManager.suitGroup.addActor(suit);
                mainStage.addActor(gameManager.suitGroup);
                chooseSuit.setVisible(false);
                gameManager.chooseSuitRunning = false;
                String ct = gameManager.messageToSend.substring(gameManager.messageToSend.length() - 1);
                if (ct.equals("@")){
                    gameManager.messageToSend.insert(gameManager.messageToSend.length() -1, "#" + Card.Suit.SPADES.ordinal());
                }else {
                    gameManager.messageToSend.append("#" + Card.Suit.SPADES.ordinal());
                }
                game.playServices.sendReliableMessage(gameManager.messageToSend.toString());
                if (gameManager.getTopOfDiscard().rank == Card.Rank.FOUR) {
                    gameManager.drawCardToComputer(gameManager.cardsToDrawRankFour);
                    gameManager.cardsToDrawRankFour = 0;
                }
            }
        });

        final BaseActor diamondsSuit = new BaseActor();
        diamondsSuit.setTexture( new Texture(Gdx.files.internal("diamonds.png")) );
        diamondsSuit.addListener(new InputListener(){
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button)
            {  return true;  }

            public void touchUp (InputEvent event, float x, float y, int pointer, int button)
            {
                gameManager.setWildSuit(Card.Suit.DIAMONDS);
                gameManager.darkInvalidMoves(gameManager.playerHandGroup);
                SuitIcon suit = new SuitIcon();
                suit.createGraphicIcon(Card.Suit.DIAMONDS, mapWidth / 2 + 120f, (mapHeight / 2) + 50f);
                gameManager.suitGroup.addActor(suit);
                mainStage.addActor(gameManager.suitGroup);
                chooseSuit.setVisible(false);
                gameManager.chooseSuitRunning = false;
                String ct = gameManager.messageToSend.substring(gameManager.messageToSend.length() - 1);
                if (ct.equals("@")){
                    gameManager.messageToSend.insert(gameManager.messageToSend.length() -1, "#" + Card.Suit.DIAMONDS.ordinal());
                }else {
                    gameManager.messageToSend.append("#" + Card.Suit.DIAMONDS.ordinal());
                }
                game.playServices.sendReliableMessage(gameManager.messageToSend.toString());
                if (gameManager.getTopOfDiscard().rank == Card.Rank.FOUR) {
                    gameManager.drawCardToComputer(gameManager.cardsToDrawRankFour);
                    gameManager.cardsToDrawRankFour = 0;
                }
            }
        });

        //chooseSuit.setBackground( chooseSuitBackground );
        chooseSuit.add().expand();
        chooseSuit.row();

        Table suitTable = new Table();
        suitTable.setBackground(chooseSuitBackground);
        Label chooseSuitLabel = new Label(game.myBundle.get("chooseSuit"), game.defaultSkin, "exo35");
        suitTable.add(chooseSuitLabel).colspan(4).expandX().center();
        suitTable.row();
        suitTable.add(clubsSuit).center().pad(10f);
        suitTable.add(heartsSuit).center().pad(10f);
        suitTable.add(spadesSuit).center().pad(10f);
        suitTable.add(diamondsSuit).center().pad(10f);
        //suitTable.setDebug(true);

        chooseSuit.add(suitTable);
        chooseSuit.row();
        chooseSuit.add().height(mapHeight / 3f);
        chooseSuit.setVisible(false);
        //chooseSuit.setDebug(true);

        gameManager = new GameMultiplayerManager(this);
        gameManager.startGame();
        gameManager.drawTable();

        participantNames = game.playServices.getParticipantNames();

        if (participantNames == null){
            participantNames[0] = "Computer0";
        }

        playerLabel	= new Label("You", game.defaultSkin, "collegia15");
        computerLabel = new Label(participantNames[0], game.defaultSkin, "collegia15");

        BaseActor playerCards = new BaseActor();
        playerCards.setTexture( new Texture(Gdx.files.internal("cards.png")) );
        BaseActor computerCards = playerCards.clone();
        BaseActor playerNCards = new BaseActor();
        playerNCards.setTexture( new Texture(Gdx.files.internal("nCards.png")) );
        BaseActor computerNCards = playerNCards.clone();

        BaseActor playerPoints = new BaseActor();
        playerPoints.setTexture( new Texture(Gdx.files.internal("points.png")) );
        BaseActor computerPoints = playerPoints.clone();
        BaseActor playerNPoints = new BaseActor();
        playerNPoints.setTexture( new Texture(Gdx.files.internal("nCards.png")) );
        BaseActor computerNPoints = playerNPoints.clone();

        BaseActor facePlayer = new BaseActor();
        int faceNumber = (int)GameUtils.randomFloatInRange(1, 3);
        facePlayer.setTexture(new Texture(Gdx.files.internal("faces/" + faceNumber + ".png")) );
        int newFaceNumber = faceNumber;
        while (newFaceNumber == faceNumber){
            newFaceNumber = (int)GameUtils.randomFloatInRange(1, 3);
        }
        BaseActor faceComputer = new BaseActor();
        faceComputer.setTexture(new Texture(Gdx.files.internal("faces/" + newFaceNumber + ".png")) );


        playerScoreLabel = new Label(String.valueOf(gameManager.playerData.getPlayerScore()), game.defaultSkin, "collegia15");
        playerScoreLabel.setAlignment(Align.center);
        playerCardsLabel = new Label(String.valueOf(gameManager.getPlayerHand().size()), game.defaultSkin, "collegia15");
        playerCardsLabel.setAlignment(Align.center);
        computerScoreLabel = new Label(String.valueOf(gameManager.playerData.getOppScore()), game.defaultSkin, "collegia15");
        computerScoreLabel.setAlignment(Align.center);
        computerCardsLabel = new Label(String.valueOf(gameManager.getOppHand().size()), game.defaultSkin, "collegia15");
        computerCardsLabel.setAlignment(Align.center);

        uiTable.add().colspan(5).height(120f);

        uiTable.row();
        uiTable.add(computerPoints).padLeft(20f);
        Stack stack = new Stack();
        stack.add(computerNPoints);
        stack.add(computerScoreLabel);
        uiTable.add(stack).left().padLeft(10f);
        computerLabelTable = new Table();
        computerLabelTable.setBackground(chooseSuitBackground);
        computerLabelTable.add(faceComputer).pad(5f);
        computerLabelTable.add(computerLabel).pad(5f);
        computerLabelTable.setVisible(false);
        uiTable.add(computerLabelTable).expandX();
        uiTable.add(computerCards).padRight(10f);
        stack = new Stack();
        stack.add(computerNCards);
        stack.add(computerCardsLabel);
        uiTable.add(stack).center().padRight(20f);

        uiTable.row();
        uiTable.add().colspan(5).expand();

        uiTable.row();
        uiTable.add(playerPoints).padLeft(20f);
        stack = new Stack();
        stack.add(playerNPoints);
        stack.add(playerScoreLabel);
        uiTable.add(stack).left().padLeft(10f);
        playerLabelTable = new Table();
        playerLabelTable.setBackground(chooseSuitBackground);
        playerLabelTable.add(facePlayer).pad(5f);
        playerLabelTable.add(playerLabel).pad(5f);
        playerLabelTable.setVisible(false);
        uiTable.add(playerLabelTable).expandX();
        uiTable.add(playerCards).padRight(10f);
        stack = new Stack();
        stack.add(playerNCards);
        stack.add(playerCardsLabel);
        uiTable.add(stack).center().padRight(20f);

        uiTable.row();
        uiTable.add().colspan(5).height(250f);
        //uiTable.setDebug(true);


        deckCountLabel = new Label("", game.defaultSkin, "collegia15");
        deckCountLabel.setFontScale(1.25f);
        deckCountLabel.setColor(Color.BLUE);
        deckCountLabel.setPosition(mapWidth / 2 + 130f, mapHeight / 2 - 30f);
        mainStage.addActor(deckCountLabel);

        startHand();

        switch(Gdx.app.getType()) {
            case Android:
                if(messageReceived.isEmpty()) {
                    game.playServices.sendReliableMessage(gameManager.createMessage());
                }else {
                    messageReceived.remove();
                }
                //game.actionResolver.showToast("Ciao come stai?");
                Gdx.app.log("GameScreen", "isSignedIn: " + game.playServices.isSignedIn() + ".");
                break;
            case Desktop:
                // desktop specific code
            case WebGL:
                /// HTML5 specific code
        }
    }

    @Override
    public void update(float dt) {
        if ((BaseActor.stackActions<=0) && (!gameManager.chooseSuitRunning)){
            if (!gameManager.isGameOver()) {
                changePlayLabel();
                if (gameManager.isPlayerTurn() && (!gameManager.hasValidMove(gameManager.getPlayerHand()))) {
                    gameManager.drawCardToPlayer(1);
                } else if ((!gameManager.isPlayerTurn()) && (!messageReceived.isEmpty())) {
                    gameManager.runComputerTurn();
                }
                deckCountLabel.setText(String.valueOf(gameManager.deck.size()));
            }else if (!isShowedWinDialog){
                score = gameManager.updateScores();
                if (gameManager.playerData.isPlayerWinnerGame() || gameManager.playerData.isOppWinnerGame()) {
                    showWinnerGame();
                }else {
                    showWinnerHand();
                }
            }
        }
    }

    public void changePlayLabel(){
        if (!gameManager.isGameOver()) {
            if (BaseActor.stackActions <= 0) {
                playerCardsLabel.setText(String.valueOf(gameManager.getPlayerHand().size()));
                computerCardsLabel.setText(String.valueOf(gameManager.getOppHand().size()));
                if (gameManager.isPlayerTurn()) {
                    playerLabelTable.setVisible(true);
                    computerLabelTable.setVisible(false);
                } else {
                    computerLabelTable.setVisible(true);
                    playerLabelTable.setVisible(false);
                }
            }
        }
    }

    private void showWinnerGame(){
        //game.playServices.onQuickMatchClicked();
        isShowedWinDialog = true;
        Label label;
        if (gameManager.playerData.isPlayerWinnerGame()) {
            label = new Label(game.myBundle.get("youWon") + " " + game.myBundle.get("game") + "\n" + game.myBundle.get("score") + ": " + score, game.defaultSkin.get("exo35", Label.LabelStyle.class));
        } else {
            label = new Label(participantNames[0] + "\n" + game.myBundle.get("won") + " " + game.myBundle.get("game") + "\n" + game.myBundle.get("score") + ": " + score, game.defaultSkin.get("exo35", Label.LabelStyle.class));
        }
        label.setWrap(true);
        label.setAlignment(Align.center);

        Dialog dialog = new Dialog("Do you want continue?", game.defaultSkin, "dialogWinnerGame") {
                    protected void result (Object object) {
                        System.out.println("Chosen: " + object);
                        if (object.equals(true)){
                            switch(Gdx.app.getType()) {
                                case Android:
                                    if (gameManager.playerData.isPlayerWinnerGame()) {
                                        gameManager.playerData.resetGameData();
                                        game.setScreen(new GameMultiplayerScreen(game, null));
                                    }
                                    game.playServices.showOrLoadInterstitial();
                                    break;
                                case Desktop:
                                    gameManager.playerData.resetGameData();
                                    game.setScreen(new GameScreen(game));
                                    break;
                                case WebGL:
                                    /// HTML5 specific code
                            }
                        }else {
                            Gdx.app.exit();
                        }
                    }
                };

        dialog.padTop(50);
        dialog.setMovable(false);
        dialog.setWidth(340f);
        dialog.setHeight(156f);
        dialog.getContentTable().add(label).width(320);
        dialog.getButtonTable().padTop(100f);

        TextButton dbutton = new TextButton("Yes", game.defaultSkin, "uiTextButtonStyle");
        //dbutton.setWidth(52f);
        //dbutton.setHeight(18f);
        dialog.button(dbutton, true);
        dbutton = new TextButton("No", game.defaultSkin, "uiTextButtonStyle");
        //dbutton.setWidth(52f);
        //dbutton.setHeight(18f);
        dialog.button(dbutton, false);

        dialog.key(Input.Keys.ENTER, true).key(Input.Keys.ESCAPE, false);
        //dialog.invalidateHierarchy();
        //dialog.invalidate();
        //dialog.layout();
        dialog.show(uiStage);
    }

    private void showWinnerHand(){
        isShowedWinDialog = true;

        Drawable background = game.skin.newDrawable("white", new Color(0,0,0,0.7f));
        Table backgroundTable = new Table();
        backgroundTable.setFillParent(true);
        backgroundTable.background(background);
        uiStage.addActor(backgroundTable);

        Label gameText = new Label(game.myBundle.get("end"), game.defaultSkin.get("komikax70", Label.LabelStyle.class));
        gameText.setPosition(-180f, (mapHeight / 2) + 150f);

        gameText.addAction(Actions.sequence(
                Actions.moveTo((mapWidth - gameText.getWidth()) / 2, (mapHeight / 2) + 150f, 0.5f),
                Actions.delay(6f),
                Actions.moveTo(mapWidth + 180f, (mapHeight / 2) + 150f, 0.5f)
                //Actions.delay(0.5f)
        ));
        uiStage.addActor(gameText);

        Label overText = new Label(game.myBundle.get("hand"), game.defaultSkin.get("komikax70", Label.LabelStyle.class));
        overText.setPosition(mapWidth + 180f, (mapHeight / 2) + 50f);
        overText.addAction(Actions.sequence(
                Actions.moveTo((mapWidth - overText.getWidth()) / 2, (mapHeight / 2) + 50f, 0.5f),
                Actions.delay(6f),
                Actions.moveTo(-280f, (mapHeight / 2) + 50f, 0.5f)
                //Actions.delay(0.5f)
        ));
        uiStage.addActor(overText);

        Label labelScore;
        if (gameManager.playerData.isPlayerWinnerHand()) {
            labelScore = new Label(game.myBundle.get("youWon") + " " + game.myBundle.get("hand") + "\n" + game.myBundle.get("score") + ": " + score, game.defaultSkin.get("exo35", Label.LabelStyle.class));
        } else {
            labelScore = new Label(participantNames[0] + "\n" + game.myBundle.get("won") + " " + game.myBundle.get("hand") + "\n" + game.myBundle.get("score") + ": " + score, game.defaultSkin.get("exo35", Label.LabelStyle.class));
        }
        labelScore.setWrap(true);
        labelScore.setAlignment(Align.center);

        Dialog dialog =new Dialog("", game.defaultSkin, "dialogWinnerHand") {
                            protected void result (Object object) {
                                System.out.println("Chosen: " + object);
                            }
                        };
        dialog.setPosition((mapWidth / 2) - dialog.getWidth(), mapHeight / 3);
        dialog.setWidth(340f);
        dialog.setHeight(156f);
        dialog.getContentTable().add(labelScore).width(320).row();

        dialog.addAction(Actions.sequence(
                //Actions.moveTo((mapWidth - labelScore.getWidth()) / 2, mapHeight / 3, 1f),
                Actions.fadeIn(0.5f),
                Actions.delay(6f),
                Actions.fadeOut(0.5f),
                //Actions.moveTo(mapWidth + 150f, mapHeight / 3, 1f),
                //Actions.delay(0.5f),
                run(new Runnable(){
                        @Override
                        public void run() {
                            switch(Gdx.app.getType()) {
                                case Android:
                                    if (gameManager.playerData.isPlayerWinnerHand()) {
                                        //game.setScreen(new GameMultiplayerScreen(game, null));
                                        game.playServices.startMultiplayerGame(null);
                                    }
                                    game.playServices.showOrLoadInterstitial();
                                    break;
                                case Desktop:
                                    break;
                                case WebGL:
                                    /// HTML5 specific code
                            }
                        }
                    }
                )
        ));
        uiStage.addActor(dialog);

    }

    private void startHand(){
        Label gameText = new Label(game.myBundle.get("start"), game.defaultSkin.get("komikax70", Label.LabelStyle.class));
        gameText.setPosition(-180f, (mapHeight / 2) + 150f);

        gameText.addAction(Actions.sequence(
                Actions.moveTo((mapWidth - gameText.getWidth()) / 2, (mapHeight / 2) + 150f, 0.5f),
                Actions.delay(2f),
                Actions.moveTo(mapWidth + 180f, (mapHeight / 2) + 150f, 0.5f)
        ));
        uiStage.addActor(gameText);

        Label overText = new Label(game.myBundle.get("hand") + " " + String.valueOf(gameManager.playerData.getRound()), game.defaultSkin.get("komikax70", Label.LabelStyle.class));
        overText.setPosition(mapWidth + 180f, (mapHeight / 2) + 50f);
        overText.addAction(Actions.sequence(
                Actions.moveTo((mapWidth - overText.getWidth()) / 2, (mapHeight / 2) + 50f, 0.5f),
                Actions.delay(2f),
                Actions.moveTo(-350f, (mapHeight / 2) + 50f, 0.5f)
        ));
        uiStage.addActor(overText);
    }

    public void updateMessage(String message){
        this.messageReceived.add(message);
    }
}
