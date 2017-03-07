package com.aevobits.games.crazyeights.manager;

import com.aevobits.games.crazyeights.GameUtils;
import com.aevobits.games.crazyeights.actor.BaseActor;
import com.aevobits.games.crazyeights.entity.Card;
import com.aevobits.games.crazyeights.entity.Card.Rank;
import com.aevobits.games.crazyeights.entity.Card.Suit;
import com.aevobits.games.crazyeights.entity.CardBack;
import com.aevobits.games.crazyeights.entity.CardBackAnimated;
import com.aevobits.games.crazyeights.entity.SuitIcon;
import com.aevobits.games.crazyeights.screen.GameMultiplayerScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.StringBuilder;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;

/**
 * Created by vito on 15/11/16.
 */

public class GameMultiplayerManager {
    public List<Card> deck;
    public List<Card> playerHand;
    public List<Card> oppHand;
    public List<Card> discardPile;

    public Group playerHandGroup;
    private Group oppHandGroup;
    private Group discadPileGroup;
    public Group suitGroup;

    final CardBack cardBack = new CardBack();
    private Suit wildSuit = null;

    private GameMultiplayerScreen gameMultiplayerScreen;
    private ComputerMultiplayerManager computerPlayer;
    public PlayerDataManager playerData;
    private InputListener cardListner;

    private boolean playerTurn;
    public boolean chooseSuitRunning;
    public boolean darkInvalidMoves;
    private int perHand;

    private final float topDiscardX;
    private final float topDiscardY;
    private float initCard;
    private float distanceCardX;
    private float cardWidth = 80;
    private float cardHeight = 115;
    private float cardPlayerWidth = 110;
    private float cardPlayerHeight = 160;

    public int cardsToDrawRankTwo;
    public int cardsToDrawRankFour;
    private String action="";
    private boolean isChangedTurn;
    public StringBuilder messageToSend;

    public GameMultiplayerManager(GameMultiplayerScreen gameMultiplayerScreen) {
        this.gameMultiplayerScreen = gameMultiplayerScreen;
        if(this.gameMultiplayerScreen.messageReceived.isEmpty()) {
            deck = CardManager.getShuffledDeck();
        }else{
            deck = new LinkedList<Card>();
        }

        playerHand = new LinkedList<Card>();
        oppHand = new LinkedList<Card>();
        discardPile = new LinkedList<Card>();
        playerHandGroup = new Group();
        oppHandGroup = new Group();
        discadPileGroup = new Group();
        suitGroup = new Group();
        computerPlayer = new ComputerMultiplayerManager();
        playerData = PlayerDataManager.getInstance();
        perHand = 8;
        cardsToDrawRankTwo = 0;
        cardsToDrawRankFour = 0;
        chooseSuitRunning = false;
        darkInvalidMoves = playerData.isDarkInvalidMoves();
        topDiscardX = (gameMultiplayerScreen.mapWidth / 2) - (cardWidth * 3 / 2);
        topDiscardY = gameMultiplayerScreen.mapHeight / 2;
        initCard = gameMultiplayerScreen.mapWidth * 0.1f;
        distanceCardX = (gameMultiplayerScreen.mapWidth * 0.8f) / perHand;
    }

    /**
     * Start the game by dealing 7 cards to both players, and
     * starting the discard pile with the next card
     * Also sets the playerTurn boolean to a random value
     */
    public void startGame(){
        if(this.gameMultiplayerScreen.messageReceived.isEmpty()) {
            CardManager.reshuffle(deck, playerHand, oppHand, discardPile);
            CardManager.deal(deck, perHand, playerHand, oppHand);
            //CardManager.dealDebug(deck, playerHand, oppHand);

            CardManager.draw(deck, discardPile);
            //Make sure we don't start with an 8
            while ((getTopOfDiscard().rank == Rank.EIGHT) || (getTopOfDiscard().rank == Rank.FOUR) || getTopOfDiscard().rank == Rank.TWO) {
                CardManager.draw(deck, discardPile);
            }
            if (playerData.getRound() == 1) {
                playerData.setPlayerStartTurn(new Random().nextBoolean());
            } else {
                playerData.setPlayerStartTurn(!playerData.isPlayerStartTurn());
            }
            //for debug purpose
            //playerTurn = true;
            playerTurn = playerData.isPlayerStartTurn();
            Gdx.app.log("Start playing:", (isPlayerTurn() ? "Player " : " Computer "));
        }else{
            String mr = this.gameMultiplayerScreen.messageReceived.element();
            String[] token = mr.split("#");

            playerTurn = !token[0].equals("*P");

            String[] oppToken = token[1].split(";");
            String[] playerToken = token[2].split(";");
            String[] deckToken = token[3].split(";");
            String[] discardToken = token[4].split(";");

            for (String card:oppToken){
                String[] cardsToken = card.split(",");
                oppHand.add(new Card(Suit.values()[Integer.valueOf(cardsToken[0])], Rank.values()[Integer.valueOf(cardsToken[1])]));
            }

            for (String card:playerToken){
                String[] cardsToken = card.split(",");
                playerHand.add(new Card(Suit.values()[Integer.valueOf(cardsToken[0])], Rank.values()[Integer.valueOf(cardsToken[1])]));
            }

            for (String card:deckToken){
                String[] cardsToken = card.split(",");
                deck.add(new Card(Suit.values()[Integer.valueOf(cardsToken[0])], Rank.values()[Integer.valueOf(cardsToken[1])]));
            }

            for (String card:discardToken){
                String[] cardsToken = card.split(",");
                discardPile.add(new Card(Suit.values()[Integer.valueOf(cardsToken[0])], Rank.values()[Integer.valueOf(cardsToken[1])]));
            }

        }
    }

    public void drawTable(){
        Card discardCard = getTopOfDiscard();
        discardCard.createGraphicCard(cardWidth, cardHeight, gameMultiplayerScreen.mapWidth / 2, gameMultiplayerScreen.mapHeight / 2, "front");
        discardCard.addActionBase(Actions.sequence(
                Actions.delay((float) perHand + 3f),
                Actions.moveTo(topDiscardX, topDiscardY, 0.5f, Interpolation.exp10),
                run(new Runnable(){
                        @Override
                        public void run() {
                            if (darkInvalidMoves) {
                                darkInvalidMoves(playerHandGroup);
                            }
                        }
                    }
                )
        ));
        discadPileGroup.addActor(discardCard);
        this.gameMultiplayerScreen.mainStage.addActor(discadPileGroup);

        cardBack.createGraphicCard(cardWidth, cardHeight, gameMultiplayerScreen.mapWidth / 2, gameMultiplayerScreen.mapHeight / 2);
        cardBack.setZIndex(1000);
        this.gameMultiplayerScreen.mainStage.addActor(cardBack);

        float initCardX = initCard + (distanceCardX / 2) - (cardWidth / 2);
        float initCardPlayerX = initCard + (distanceCardX / 2) - (cardPlayerWidth / 2);
        float delay = 2.5f;
        CardBackAnimated cardBackAnimated = new CardBackAnimated();
        for (int i = 0; i < perHand; i++) {
            //cardBackAnimated.createGraphicCard(cardWidth, cardHeight, gameMultiplayerScreen.mapWidth / 2, gameMultiplayerScreen.mapHeight / 2);
            //cardBackAnimated.setZIndex(1001);
            //cardBackAnimated.animateCard();
            //cardBackAnimated.setActiveAnimation("flip");
            //cardBackAnimated.addActionBase(Actions.delay(delay+=0.4f));

            final Card card = playerHand.get(i);
            card.createGraphicCard(cardWidth, cardHeight, gameMultiplayerScreen.mapWidth / 2, gameMultiplayerScreen.mapHeight / 2, "back");
            card.addActionBase(Actions.sequence(
                            Actions.delay(delay+=0.5f),
                            Actions.parallel(
                                    Actions.moveTo(initCardPlayerX, 60f, 0.5f, Interpolation.exp10),
                                    Actions.sizeTo(cardPlayerWidth, cardPlayerHeight, 0.5f, Interpolation.exp10)
                            ),
                            run(new Runnable(){
                                    @Override
                                    public void run() {
                                        card.createGraphicCard(card.getWidth(), card.getHeight(), card.getX(), card.getY(), "front");
                                    }
                                }
                            )
                        )
            );


            cardListner = new InputListener(){
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
                    if (isPlayerTurn() && (BaseActor.stackActions<=0)) {
                        Card card = (Card) event.getTarget();
                        if (isValidMove(card, playerHand)) {
                            card.offsetX = x;
                            card.offsetY = y;
                            card.originalX = event.getStageX();
                            card.originalY = event.getStageY();
                            card.moveBy(0, 15f);
                            return true; // T = call other methods
                        }
                    }
                    return false;
                }

                public void touchDragged(InputEvent event, float x, float y, int pointer){
                    Card card = (Card) event.getTarget();
                    card.moveBy((x - card.offsetX) * 1f, (y - card.offsetY) * 1.5f);
                    card.setX(MathUtils.clamp(card.getX(), 0, gameMultiplayerScreen.mapWidth - card.getWidth()));
                    card.setY(MathUtils.clamp(card.getY(), 0, gameMultiplayerScreen.mapHeight - card.getHeight()));
                }

                public void touchUp(InputEvent event, float x, float y, int pointer, int button){
                    final Card card = (Card) event.getTarget();
                    if ((card.overlaps(getTopOfDiscard(),false)) && (isValidMove(card, playerHand))){
                        playCard(getPlayerHand(), card);
                        playerHandGroup.removeActor(card);
                        discadPileGroup.addActor(card);
                        card.addActionBase(Actions.sequence(
                                Actions.rotateBy(GameUtils.randomFloatInRange(-15f, 15f)),
                                Actions.parallel(
                                        Actions.moveTo(topDiscardX + GameUtils.randomFloatInRange(-20f, 20f), topDiscardY + GameUtils.randomFloatInRange(-20f, 20f), 0.01f, Interpolation.linear),
                                        Actions.sizeTo(cardWidth, cardHeight, 0.01f, Interpolation.linear)
                                ),
                                run(new Runnable(){
                                        @Override
                                        public void run() {
                                            card.removeListener(cardListner);
                                            distributeCardsOnTable(playerHandGroup, initCard, gameMultiplayerScreen.mapWidth, cardPlayerWidth, darkInvalidMoves);
                                            if (card.rank == Rank.EIGHT && !isGameOver()) {
                                                messageToSend.append("@");
                                                chooseSuitRunning = true;
                                                gameMultiplayerScreen.chooseSuit.setVisible(true);
                                            }else if (card.rank == Rank.TWO){
                                                cardsToDrawRankTwo += 2;
                                                if (!hasCardInMoves(oppHand, card) || isGameOver()) {
                                                    if (!isGameOver()) {
                                                        gameMultiplayerScreen.game.playServices.sendReliableMessage(messageToSend.toString());
                                                    }
                                                    drawCardToComputer(cardsToDrawRankTwo);
                                                    cardsToDrawRankTwo = 0;
                                                }else {
                                                    messageToSend.append("@");
                                                    gameMultiplayerScreen.game.playServices.sendReliableMessage(messageToSend.toString());
                                                }
                                            }else if (card.rank == Rank.FOUR){
                                                cardsToDrawRankFour += 4;
                                                if (!hasCardInMoves(oppHand, card) || isGameOver()) {
                                                    if (!isGameOver()) {
                                                        chooseSuitRunning = true;
                                                        gameMultiplayerScreen.chooseSuit.setVisible(true);
                                                    }else if (isGameOver()){
                                                        drawCardToComputer(cardsToDrawRankFour);
                                                        cardsToDrawRankFour = 0;
                                                    }else {
                                                        gameMultiplayerScreen.game.playServices.sendReliableMessage(messageToSend.toString());
                                                        drawCardToComputer(cardsToDrawRankFour);
                                                        cardsToDrawRankFour = 0;
                                                    }
                                                }else {
                                                    messageToSend.append("@");
                                                    gameMultiplayerScreen.game.playServices.sendReliableMessage(messageToSend.toString());
                                                }
                                            }else {
                                                if (card.rank != Rank.QUEEN) {
                                                    messageToSend.append("@");
                                                }
                                                gameMultiplayerScreen.game.playServices.sendReliableMessage(messageToSend.toString());
                                            }
                                        }
                                    }
                                ))
                        );
                    }else {
                        card.addActionBase( Actions.moveTo(card.originalX - card.offsetX, card.originalY - card.offsetY, 0.1f) );
                    }
                }
            };
            card.addListener(cardListner);
            playerHandGroup.addActor(card);
            //playerHandGroup.addActor(cardBackAnimated);

            Card cardOppHand = oppHand.get(i);
            cardOppHand.createGraphicCard(cardWidth, cardHeight, gameMultiplayerScreen.mapWidth / 2, gameMultiplayerScreen.mapHeight / 2, "back");
            cardOppHand.addActionBase(Actions.sequence(
                    Actions.delay(delay+=0.5f),
                    Actions.moveTo(initCardX, (gameMultiplayerScreen.mapHeight - cardOppHand.getHeight()) + 60f, 0.5f, Interpolation.exp10)
            ));
            oppHandGroup.addActor(cardOppHand);
            Gdx.app.log("card",cardOppHand.suit.name() + " - " + cardOppHand.rank.name());
            initCardX += distanceCardX;
            initCardPlayerX += distanceCardX;
        }
        this.gameMultiplayerScreen.mainStage.addActor(playerHandGroup);
        this.gameMultiplayerScreen.mainStage.addActor(oppHandGroup);
        //this.gameMultiplayerScreen.mainStage.addActor(cardBackAnimated);

    }

    public boolean playCard(List<Card> hand, Card card){
        if(isValidMove(card, hand)){
            Gdx.app.log((isPlayerTurn()?"Player ":" Computer ") + "Play Card: ",card.suit.name() + " " + card.rank.name());
            hand.remove(card);
            discardPile.add(0, card);
            if (getWildSuit() != null){
                suitGroup.removeActor(suitGroup.getChildren().get(0));
            }
            setWildSuit(null);
            if (isGameOver() && (card.rank == Rank.TWO || card.rank == Rank.FOUR)){
                messageToSend = new StringBuilder("GOPDPC1");
            }else {
                messageToSend = new StringBuilder("PC1");
            }
            messageToSend.append("#");
            messageToSend.append(card.suit.ordinal() + ",");
            messageToSend.append(card.rank.ordinal());
            if (card.rank != Rank.QUEEN){
                changeTurn();
            }
            return true;
        }else{
            return false;
        }
    }

    public Card getComputerPlay(String message){
        return computerPlayer.selectMultiplayerPlay(oppHand, discardPile, message);
    }

    public Card getComputerPlay(int idxCard, String message){
        return computerPlayer.drawMultiplayerPlay(deck, idxCard, message);
    }

    public void runComputerTurn(){
        Gdx.app.log("runComputerTurn", "");
        String tmpMessage = gameMultiplayerScreen.messageReceived.remove();
        final String message = (tmpMessage.contains("@")?tmpMessage.substring(0,tmpMessage.length() - 1):tmpMessage);
        isChangedTurn = tmpMessage.contains("@");//(tmpMessage.contains("@")?true:false);
        action = message.substring(0,4);
        if(action.equals("PC1#")){
            playCardToComputer(message);
        }else if (action.startsWith("DC1#")){
            drawCardToComputerFromMessage(1, message);
        }else if (action.startsWith("DC")){
            int index = message.indexOf("#");
            String ncsString = message.substring(2,index);
            int ncs =  Integer.valueOf(ncsString);
            drawCardToPlayerFromMessage(ncs, message);
        }else if (action.equals("DAR#")){
            reshuffleCardFromMessage(message);
        }else if (action.equals("GOPD")){
            String fullMessage = message.substring(4, message.length());

            String pcMessage = fullMessage.substring(0, fullMessage.indexOf("DC"));
            playCardToComputer(pcMessage);

            String dcMessage = fullMessage.substring(fullMessage.indexOf("DC"), fullMessage.length());
            int index = dcMessage.indexOf("#");
            String ncsString = dcMessage.substring(2,index);
            int ncs =  Integer.valueOf(ncsString);
            drawCardToPlayerFromMessage(ncs, dcMessage);
        }
    }

    private void playCardToComputer(final String message){
        final Card picked = getComputerPlay(message);
        for (final Actor actor:oppHandGroup.getChildren()){
            final Card card = (Card) actor;
            if (card.suit == picked.suit && card.rank == picked.rank){
                oppHandGroup.removeActor(actor);
                discadPileGroup.addActor(actor);
                card.addActionBase(Actions.sequence(
                        Actions.delay(1f),
                        Actions.parallel(
                                Actions.rotateBy(GameUtils.randomFloatInRange(-15f, 15f), 0.5f, Interpolation.exp10),
                                Actions.sequence(
                                        Actions.moveTo(topDiscardX + GameUtils.randomFloatInRange(-20f, 20f), topDiscardY + GameUtils.randomFloatInRange(-20f, 20f), 0.5f , Interpolation.exp10),
                                        run(new Runnable(){
                                                @Override
                                                public void run() {
                                                    card.createGraphicCard(cardWidth, cardHeight, topDiscardX, topDiscardY, "front");
                                                    distributeCardsOnTable(oppHandGroup, initCard, gameMultiplayerScreen.mapWidth, cardWidth, false);
                                                    if (getWildSuit() != null){
                                                        suitGroup.removeActor(suitGroup.getChildren().get(0));
                                                    }
                                                    setWildSuit(null);
                                                    if(picked.rank == Rank.EIGHT && !isGameOver()){
                                                        Suit selectedSuit = computerPlayer.chooseSuit(message);
                                                        setWildSuit(selectedSuit);
                                                        SuitIcon suit = new SuitIcon();
                                                        suit.createGraphicIcon(selectedSuit, gameMultiplayerScreen.mapWidth / 2 + 120f, (gameMultiplayerScreen.mapHeight / 2) + 50f);
                                                        suitGroup.addActor(suit);
                                                        gameMultiplayerScreen.mainStage.addActor(suitGroup);
                                                        Gdx.app.log("Selected Suit: ",selectedSuit.name());
                                                    }else if (card.rank == Rank.TWO){
                                                        if (!hasCardInMoves(playerHand, card) || isGameOver()) {
                                                            cardsToDrawRankTwo = 0;
                                                        }else {
                                                            cardsToDrawRankTwo += 2;
                                                        }
                                                    }else if (card.rank == Rank.FOUR){
                                                        if (!hasCardInMoves(playerHand, card) || isGameOver()) {
                                                            if (!isGameOver()) {
                                                                Suit selectedSuit = computerPlayer.chooseSuit(message);
                                                                setWildSuit(selectedSuit);
                                                                SuitIcon suit = new SuitIcon();
                                                                suit.createGraphicIcon(selectedSuit, gameMultiplayerScreen.mapWidth / 2 + 120f, (gameMultiplayerScreen.mapHeight / 2) + 50f);
                                                                suitGroup.addActor(suit);
                                                                gameMultiplayerScreen.mainStage.addActor(suitGroup);
                                                                Gdx.app.log("Selected Suit: ", selectedSuit.name());
                                                            }
                                                            cardsToDrawRankFour = 0;
                                                        }else {
                                                            cardsToDrawRankFour += 4;
                                                        }
                                                    }
                                                    if (isChangedTurn){
                                                        changeTurn();
                                                    }
                                                    if (darkInvalidMoves) {
                                                        darkInvalidMoves(playerHandGroup);
                                                    }
                                                }
                                            }
                                        ))))
                );
            }
        }
    }

    public void drawCardToComputerFromMessage(int numberCards, String message){
        Gdx.app.log("drawCard", "");
        List<Card> cardOppHandList = new LinkedList<Card>();
        for (int i=1; i<=numberCards; i++) {
            //If the deck is empty, we re-shuffle all but the first card in the discard pile
            //  back into the deck
            /*if(deck.isEmpty()){
                reshuffleCard();
            }*/
            final Card cardOppHand = getComputerPlay(i, message);
            oppHand.add(cardOppHand);
            cardOppHand.createGraphicCard(cardWidth, cardHeight, gameMultiplayerScreen.mapWidth / 2, gameMultiplayerScreen.mapHeight / 2, "back");
            cardOppHandList.add(cardOppHand);
        }
        drawAndDistributeCards(oppHandGroup, cardOppHandList, initCard, gameMultiplayerScreen.mapWidth, cardWidth, false, false);
        if (isChangedTurn){
            changeTurn();
        }
    }

    public void drawCardToComputer(int numberCards){
        //Gdx.app.log("drawCardToComputer", "");
        List<Card> cardOppHandList = new LinkedList<Card>();
        int nCardsToSend;
        if (deck.size()>=numberCards){
            nCardsToSend = numberCards;
        }else {
            nCardsToSend = deck.size();
        }
        if (isGameOver() && (getTopOfDiscard().rank == Rank.TWO || getTopOfDiscard().rank == Rank.FOUR)){
            messageToSend.append("DC" + nCardsToSend);
        }else {
            messageToSend = new StringBuilder("DC" + nCardsToSend);
        }
        messageToSend.append("#");
        for (int i=1; i<=numberCards; i++) {
            //If the deck is empty, we re-shuffle all but the first card in the discard pile
            //  back into the deck
            if(deck.isEmpty()){
                gameMultiplayerScreen.game.playServices.sendReliableMessage(messageToSend.toString());
                reshuffleCard();
                nCardsToSend = numberCards - nCardsToSend;
                messageToSend = new StringBuilder("DC" + nCardsToSend + "#");
            }
            CardManager.draw(deck, oppHand);
            final Card cardOppHand = oppHand.get(0);
            messageToSend.append(cardOppHand.suit.ordinal() + ",");
            messageToSend.append(cardOppHand.rank.ordinal() + ";");
            cardOppHandList.add(cardOppHand);
            cardOppHand.createGraphicCard(cardWidth, cardHeight, gameMultiplayerScreen.mapWidth / 2, gameMultiplayerScreen.mapHeight / 2, "back");
        }
        drawAndDistributeCards(oppHandGroup, cardOppHandList, initCard, gameMultiplayerScreen.mapWidth, cardWidth, false, false);

        /*if (isGameOver()){
            this.gameMultiplayerScreen.game.playServices.sendReliableMessage(messageToSend.toString());
            messageToSend = new StringBuilder("GO##");
            this.gameMultiplayerScreen.game.playServices.sendReliableMessage(messageToSend.toString());
            endGame = true;
        }else {*/
            messageToSend.append("@");
            this.gameMultiplayerScreen.game.playServices.sendReliableMessage(messageToSend.toString());
        /*}*/
    }

    public void drawCardToPlayerFromMessage(int numberCards, String message){
        Gdx.app.log("drawCardToPlayer", "");
        List<Card> cardList = new LinkedList<Card>();
        for (int i=1;i<=numberCards;i++) {
            /*if(deck.isEmpty()){
                reshuffleCard();
            }*/
            Card card = getComputerPlay(i, message);
            playerHand.add(card);
            card.createGraphicCard(cardWidth, cardHeight, gameMultiplayerScreen.mapWidth / 2, gameMultiplayerScreen.mapHeight / 2, "back");
            card.addListener(cardListner);
            cardList.add(card);
        }

        drawAndDistributeCards(playerHandGroup, cardList, initCard, gameMultiplayerScreen.mapWidth, cardPlayerWidth, darkInvalidMoves, true);
        if (isChangedTurn){
            changeTurn();
        }
    }

    public void drawCardToPlayer(int numberCards){
        Gdx.app.log("drawCardToPlayer", "");
        List<Card> cardList = new LinkedList<Card>();
        int nCardsToSend;
        if (deck.size()>=numberCards){
            nCardsToSend = numberCards;
        }else {
            nCardsToSend = deck.size();
        }
        messageToSend = new StringBuilder("DC" + nCardsToSend + "#");
        for (int i=1;i<=numberCards;i++) {
            if(deck.isEmpty()){
                gameMultiplayerScreen.game.playServices.sendReliableMessage(messageToSend.toString());
                reshuffleCard();
                nCardsToSend = numberCards - nCardsToSend;
                messageToSend = new StringBuilder("DC" + nCardsToSend + "#");
            }
            Card card = getTopOfDeck();
            CardManager.draw(deck, playerHand, 1);
            card.createGraphicCard(cardWidth, cardHeight, gameMultiplayerScreen.mapWidth / 2, gameMultiplayerScreen.mapHeight / 2, "back");
            card.addListener(cardListner);
            cardList.add(card);

            messageToSend.append(card.suit.ordinal() + ",");
            messageToSend.append(card.rank.ordinal() + ";");

        }
        drawAndDistributeCards(playerHandGroup, cardList, initCard, gameMultiplayerScreen.mapWidth, cardPlayerWidth, darkInvalidMoves, true);
        Card topDiscard = getTopOfDiscard();
        if ((((topDiscard.rank == Rank.TWO) || (topDiscard.rank == Rank.FOUR)) && (!hasValidMove(playerHand))) |
                (!((topDiscard.rank == Rank.TWO) || (topDiscard.rank == Rank.FOUR)) && (!hasValidMove(cardList))) ){
            messageToSend.append("@");
            changeTurn();
        }
        gameMultiplayerScreen.game.playServices.sendReliableMessage(messageToSend.toString());
    }

    private void reshuffleCard(){
        Gdx.app.log("reshuffleCard", "");
        while(discardPile.size() > 1){
            Rank rank = discardPile.get(1).rank;
            Suit suit = discardPile.get(1).suit;
            deck.add(new Card(suit, rank));
            discardPile.remove(1);
            discadPileGroup.getChildren().removeIndex(0);
        }
        CardManager.reshuffle(deck);
        createMessageAfterReshuffleAndSend();
        //Toast.makeText(context, "Shuffling Discard into Deck...", Toast.LENGTH_SHORT).show();
    }

    private void reshuffleCardFromMessage(String message){
        computerPlayer.drawReshuffleCard(deck, discardPile, discadPileGroup, message);
    }

    public void distributeCardsOnTable(Group cardsGroup, float initX, float mapWidth, float cardWidth, boolean darkInvalideMoves){
        Gdx.app.log("distributeCardsOnTable", "");
        int numberCards = cardsGroup.getChildren().size;
        float distanceCardX = (mapWidth * 0.8f) / numberCards;
        initX = initX + (distanceCardX / 2) - (cardWidth / 2);
        for (final Actor cardItem: cardsGroup.getChildren()){
            final BaseActor card = (BaseActor) cardItem;
            card.addActionBase(Actions.moveTo(initX, card.getY(), 0.3f, Interpolation.exp10));
            initX += distanceCardX;
        }
        if (darkInvalideMoves) {
            darkInvalidMoves(cardsGroup);
        }
    }

    private void drawAndDistributeCards(final Group cardsGroup, final List<Card> cardsToAdd, float initX, float mapWidth, float cardWidth,
                                        final boolean darkInvalideMoves, final boolean isPlayer){
        Gdx.app.log("drawAndDistributeCards", "");
        float cardHeightTo = cardWidth;
        int numberCards = cardsGroup.getChildren().size + cardsToAdd.size();
        float distanceCardX = (mapWidth * 0.8f) / numberCards;
        if (isPlayer){
            initX = initX + (distanceCardX / 2) - (cardPlayerWidth / 2);
            cardHeightTo = cardPlayerHeight;
        }else {
            initX = initX + (distanceCardX / 2) - (cardWidth / 2);
        }
        for (Actor cardItem: cardsGroup.getChildren()){
            final BaseActor card = (BaseActor) cardItem;
            card.addActionBase(Actions.moveTo(initX, card.getY(), 0.3f, Interpolation.exp10));
            initX += distanceCardX;
        }
        float positionY = cardsGroup.getChildren().first().getY();
        float delay = 0f;
        for (final Card cardToAdd: cardsToAdd) {
            cardToAdd.addActionBase(Actions.sequence(
                    //Actions.delay(0.5f),
                    Actions.delay(delay+=0.5f),
                    Actions.parallel(
                            Actions.moveTo(initX, positionY, 0.5f, Interpolation.exp10),
                            Actions.sizeTo(cardWidth, cardHeightTo, 0.5f, Interpolation.linear)
                    ),
                    run(new Runnable(){
                            @Override
                            public void run() {
                                if (isPlayer) {
                                    cardToAdd.createGraphicCard(cardToAdd.getWidth(), cardToAdd.getHeight(), cardToAdd.getX(), cardToAdd.getY(), "front");
                                    if (darkInvalideMoves) {
                                        darkInvalidMoves(cardsGroup);
                                    }
                                }
                            }
                        }
                    )
            ));
            initX += distanceCardX;
            cardsGroup.addActor(cardToAdd);
        }
    }

    public void darkInvalidMoves(Group group){
        for (Actor card: group.getChildren()){
            if (isValidMove((Card) card, playerHand)) {
                card.setColor(1f, 1f, 1f, 1f);
            } else {
                card.setColor(0.5f, 0.5f, 0.5f, 1f);
            }
        }
    }

    public boolean isValidMove(Card card, List<Card> cards){
        if (isGameOver()) return false;
        Card topOfDiscard = getTopOfDiscard();

        if (topOfDiscard.rank == Rank.TWO && cardsToDrawRankTwo > 0 && hasCardInMoves(cards, getTopOfDiscard()) && card.rank != Rank.TWO){
            return false;
        }
        if (topOfDiscard.rank == Rank.FOUR && cardsToDrawRankFour > 0 && hasCardInMoves(cards, getTopOfDiscard()) && card.rank != Rank.FOUR){
            return false;
        }

        if(
            ((topOfDiscard.rank == Rank.EIGHT) && (card.suit == wildSuit)) ||
            ((topOfDiscard.rank == Rank.FOUR) && (card.suit == wildSuit)) ||
            (((topOfDiscard.rank != Rank.EIGHT) && (topOfDiscard.rank != Rank.FOUR)) &&
                    (card.suit == topOfDiscard.suit) || (topOfDiscard.rank == card.rank)) ||
            //((topOfDiscard.rank != Rank.FOUR) &&  (card.suit == topOfDiscard.suit) || (topOfDiscard.rank == card.rank)) ||
            (card.rank == Rank.EIGHT) ||
            (card.rank == Rank.FOUR)
        ){
            return true;
        }else{
            return false;
        }
    }

    public boolean hasValidMove(List<Card> cards){
        for(Card card: cards){
            if(isValidMove(card, cards)){
                return true;
            }
        }
        return false;
    }

    public boolean hasCardInMoves(List<Card> cards, Card card){
        boolean isPresent = false;
        for (Card cardMove: cards){
            if (cardMove.rank == card.rank){
                isPresent = true;
            }
        }
        //Gdx.app.log("hasCardInMoves: ", (isPresent?"true":"false"));
        return isPresent;
    }

    /**
     * Upon the completion of a hand, give points to the other player for
     *   the total scores of the cards in the loser's hand
     * @return total points awarded this round
     */
    public int updateScores(){
        int pointsThisHand = 0;
        playerData.setRound(playerData.getRound() + 1);
        for(Card card : oppHand){
            playerData.setPlayerScore(playerData.getPlayerScore() + scoreCard(card));
            playerData.setPlayerWinnerHand(true);
            pointsThisHand += scoreCard(card);
            if (playerData.getPlayerScore() >= playerData.getThresholdScore()){
                playerData.setPlayerWinnerGame(true);
            }
        }
        for(Card card : playerHand){
            playerData.setOppScore(playerData.getOppScore() + scoreCard(card));
            playerData.setPlayerWinnerHand(false);
            pointsThisHand += scoreCard(card);
            if (playerData.getOppScore() >= playerData.getThresholdScore()){
                playerData.setOppWinnerGame(true);
            }
        }
        return pointsThisHand;
    }

    public int scoreCard(Card card){
        if(card.rank == Rank.EIGHT){
            return 50;
        }else if(card.rank == Rank.TWO){
            return 25;
        }else if(card.rank == Rank.FOUR){
            return 40;
        }else if(card.rank == Rank.QUEEN){
            return 15;
        }else if(card.rank == Rank.JACK || card.rank == Rank.KING){
            return 10;
        }else{
            return card.getRankNum() + 1;
        }
    }

    public Card getTopOfDiscard() { return discardPile.get(0); }
    public Card getTopOfDeck() { return deck.get(0); }
    public List<Card> getPlayerHand() { return playerHand; }
    public List<Card> getOppHand() { return oppHand; }
    public boolean isPlayerTurn() { return playerTurn; }
    public boolean isGameOver(){
        return (playerHand.isEmpty() || oppHand.isEmpty());
    }
    public Suit getWildSuit(){ return wildSuit; }
    public void setWildSuit(Suit suit){ wildSuit = suit ;}
    public void changeTurn(){ playerTurn = !playerTurn; }
    public String createMessage(){
        StringBuilder messageToSend = new StringBuilder("*");
        String turn = (isPlayerTurn()?"P":"O");
        messageToSend.append(turn);
        messageToSend.append("#");
        for (Card card:playerHand){
            messageToSend.append(card.suit.ordinal() + ",");
            messageToSend.append(card.rank.ordinal() + ";");
        }

        messageToSend.append("#");
        for (Card card:oppHand){
            messageToSend.append(card.suit.ordinal() + ",");
            messageToSend.append(card.rank.ordinal() + ";");
        }

        messageToSend.append("#");
        for (Card card:deck){
            messageToSend.append(card.suit.ordinal() + ",");
            messageToSend.append(card.rank.ordinal() + ";");
        }

        messageToSend.append("#");
        for (Card card:discardPile){
            messageToSend.append(card.suit.ordinal() + ",");
            messageToSend.append(card.rank.ordinal() + ";");
        }

        return messageToSend.toString();
    }

    public void createMessageAfterReshuffleAndSend(){
        StringBuilder messageToSend = new StringBuilder("DAR#");
        for (Card card:deck){
            messageToSend.append(card.suit.ordinal() + ",");
            messageToSend.append(card.rank.ordinal() + ";");
        }

        /*messageToSend.append("#");
        for (Card card:discardPile){
            messageToSend.append(card.suit.ordinal() + ",");
            messageToSend.append(card.rank.ordinal() + ";");
        }*/

        gameMultiplayerScreen.game.playServices.sendReliableMessage(messageToSend.toString());
    }
}
