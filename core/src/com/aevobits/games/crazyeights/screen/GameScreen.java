package com.aevobits.games.crazyeights.screen;

import com.aevobits.games.crazyeights.BaseGame;
import com.aevobits.games.crazyeights.actor.BaseActor;
import com.aevobits.games.crazyeights.entity.Card;
import com.aevobits.games.crazyeights.entity.SuitIcon;
import com.aevobits.games.crazyeights.manager.ComputerPlayerManager;
import com.aevobits.games.crazyeights.manager.GameManager;
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

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;

/**
 * Created by vito on 12/11/16.
 */

public class GameScreen extends BaseScreen {

    private BaseActor background;
    private GameManager gameManager;
    private ComputerPlayerManager computerPlayer;
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

    // game world dimensions
    public final int mapWidth = 480;
    public final int mapHeight = 800;

    private float hintTimer;

    public GameScreen(BaseGame g){
        super(g);
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
                        //gameManager.darkInvalidMoves(gameManager.playerHandGroup);
                        SuitIcon suit = new SuitIcon();
                        suit.createGraphicIcon(Card.Suit.CLUBS, mapWidth / 2 + 120f, (mapHeight / 2) + 50f);
                        gameManager.suitGroup.addActor(suit);
                        mainStage.addActor(gameManager.suitGroup);
                        chooseSuit.setVisible(false);
                        gameManager.chooseSuitRunning = false;
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
                //gameManager.darkInvalidMoves(gameManager.playerHandGroup);
                SuitIcon suit = new SuitIcon();
                suit.createGraphicIcon(Card.Suit.HEARTS, mapWidth / 2 + 120f, (mapHeight / 2) + 50f);
                gameManager.suitGroup.addActor(suit);
                mainStage.addActor(gameManager.suitGroup);
                chooseSuit.setVisible(false);
                gameManager.chooseSuitRunning = false;
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
                //gameManager.darkInvalidMoves(gameManager.playerHandGroup);
                SuitIcon suit = new SuitIcon();
                suit.createGraphicIcon(Card.Suit.SPADES, mapWidth / 2 + 120f, (mapHeight / 2) + 50f);
                gameManager.suitGroup.addActor(suit);
                mainStage.addActor(gameManager.suitGroup);
                chooseSuit.setVisible(false);
                gameManager.chooseSuitRunning = false;
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
                //gameManager.darkInvalidMoves(gameManager.playerHandGroup);
                SuitIcon suit = new SuitIcon();
                suit.createGraphicIcon(Card.Suit.DIAMONDS, mapWidth / 2 + 120f, (mapHeight / 2) + 50f);
                gameManager.suitGroup.addActor(suit);
                mainStage.addActor(gameManager.suitGroup);
                chooseSuit.setVisible(false);
                gameManager.chooseSuitRunning = false;
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

        gameManager = new GameManager(this);
        gameManager.startGame();
        gameManager.drawTable();
        computerPlayer = new ComputerPlayerManager(gameManager);


        playerLabel	= new Label("Player", game.defaultSkin, "exo15");
        computerLabel = new Label("Computer", game.defaultSkin, "exo15");

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


        playerScoreLabel = new Label(String.valueOf(gameManager.playerData.getPlayerScore()), game.defaultSkin, "exo15");
        playerScoreLabel.setAlignment(Align.center);
        playerCardsLabel = new Label(String.valueOf(gameManager.getPlayerHand().size()), game.defaultSkin, "exo15");
        playerCardsLabel.setAlignment(Align.center);
        computerScoreLabel = new Label(String.valueOf(gameManager.playerData.getOppScore()), game.defaultSkin, "exo15");
        computerScoreLabel.setAlignment(Align.center);
        computerCardsLabel = new Label(String.valueOf(gameManager.getOppHand().size()), game.defaultSkin, "exo15");
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


        deckCountLabel = new Label("", game.defaultSkin, "exo15");
        deckCountLabel.setFontScale(1.25f);
        deckCountLabel.setColor(Color.BLUE);
        deckCountLabel.setPosition(mapWidth / 2 + 130f, mapHeight / 2 - 30f);
        mainStage.addActor(deckCountLabel);

        startHand();

        hintTimer = 0;

        switch(Gdx.app.getType()) {
            case Android:
                //game.actionResolver.showToast("Ciao come stai?");
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
                } else if (!gameManager.isPlayerTurn()) {
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
        isShowedWinDialog = true;
        Label label;
        if (gameManager.playerData.isPlayerWinnerGame()) {
            label = new Label("You won \nScore hand is: " + score, game.skin);
        } else {
            label = new Label("Computer won \nScore hand is: " + score, game.skin);
        }
        label.setWrap(true);
        label.setAlignment(Align.center);

        Dialog dialog =
                new Dialog("Do you want continue?", game.skin) {
                    protected void result (Object object) {
                        System.out.println("Chosen: " + object);
                        if (object.equals(true)){
                            gameManager.playerData.resetGameData();
                            game.setScreen(new GameScreen(game));
                        }else {
                            Gdx.app.exit();
                        }
                    }
                };

        //dialog.padTop(50).padBottom(50);
        dialog.setMovable(false);
        dialog.getContentTable().add(label).width(250).row();
        dialog.getButtonTable().padTop(20);

        TextButton dbutton = new TextButton("Yes", game.skin);
        dialog.button(dbutton, true);

        dbutton = new TextButton("No", game.skin);
        dialog.button(dbutton, false);
        dialog.key(Input.Keys.ENTER, true).key(Input.Keys.ESCAPE, false);
        dialog.invalidateHierarchy();
        dialog.invalidate();
        dialog.layout();
        dialog.show(mainStage);
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
            labelScore = new Label(game.myBundle.get("computerWon") + " " + game.myBundle.get("hand") + "\n" + game.myBundle.get("score") + ": " + score, game.defaultSkin.get("exo35", Label.LabelStyle.class));
        }
        labelScore.setWrap(true);
        labelScore.setAlignment(Align.center);

        Dialog dialog =new Dialog("", game.defaultSkin, "dialog") {
                            protected void result (Object object) {
                                System.out.println("Chosen: " + object);
                                //game.setScreen(new GameScreen(game));
                            }
                        };
        dialog.setPosition((mapWidth / 2) - dialog.getWidth(), mapHeight / 3);
        dialog.setWidth(340f);
        dialog.setHeight(160f);
        dialog.getContentTable().add(labelScore).width(340).row();
        /*
        dialog.getButtonTable().padTop(20);

        TextButton dbutton = new TextButton("Yes", game.skin);
        dialog.button(dbutton, true);

        dbutton = new TextButton("No", game.skin);
        dialog.button(dbutton, false);

        dialog.key(Input.Keys.ENTER, true).key(Input.Keys.ESCAPE, false);
        dialog.invalidateHierarchy();
        dialog.invalidate();
        dialog.layout();
        */
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
                            game.setScreen(new GameScreen(game));
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
}
