package com.aevobits.games.crazyeights.manager;

import com.aevobits.games.crazyeights.GameUtils;
import com.aevobits.games.crazyeights.actor.BaseActor;
import com.aevobits.games.crazyeights.entity.Card;
import com.aevobits.games.crazyeights.entity.Card.Rank;
import com.aevobits.games.crazyeights.entity.Card.Suit;
import com.aevobits.games.crazyeights.entity.CardBack;
import com.aevobits.games.crazyeights.entity.CardBackAnimated;
import com.aevobits.games.crazyeights.entity.SuitIcon;
import com.aevobits.games.crazyeights.screen.GameScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;

/**
 * Created by vito on 15/11/16.
 */

public class GameManager {
    public List<Card> deck;
    private List<Card> playerHand;
    private List<Card> oppHand;
    private List<Card> discardPile;

    public Group playerHandGroup;
    private Group oppHandGroup;
    private Group discadPileGroup;
    public Group suitGroup;

    final CardBack cardBack = new CardBack();
    private Card.Suit wildSuit = null;

    private GameScreen gameScreen;
    private ComputerPlayerManager computerPlayer;
    public PlayerDataManager playerData;
    private InputListener cardListner;

    private boolean playerTurn;
    public boolean chooseSuitRunning;
    public boolean darkInvalidMoves;
    private int perHand;

    private final float topDiscardX;
    private final float topDiscardY;
    private float initCard;
    private float initCardY;
    private float distanceCardX;
    private float cardWidth = 110;
    private float cardHeight = 160;

    public GameManager(GameScreen gameScreen) {
        deck = CardManager.getShuffledDeck();
        playerHand = new LinkedList<Card>();
        oppHand = new LinkedList<Card>();
        discardPile = new LinkedList<Card>();
        playerHandGroup = new Group();
        oppHandGroup = new Group();
        discadPileGroup = new Group();
        suitGroup = new Group();
        computerPlayer = new ComputerPlayerManager(this);
        playerData = PlayerDataManager.getInstance();
        this.gameScreen = gameScreen;
        perHand = 8;
        chooseSuitRunning = false;
        darkInvalidMoves = playerData.isDarkInvalidMoves();
        topDiscardX = (gameScreen.mapWidth / 2) - (cardWidth * 3 / 2);
        topDiscardY = gameScreen.mapHeight / 2;
        initCard = gameScreen.mapWidth * 0.1f;
        distanceCardX = (gameScreen.mapWidth * 0.8f) / perHand;
    }

    /**
     * Start the game by dealing 7 cards to both players, and
     * starting the discard pile with the next card
     * Also sets the playerTurn boolean to a random value
     */
    public void startGame(){
        CardManager.reshuffle(deck, playerHand, oppHand, discardPile);
        CardManager.deal(deck, perHand, playerHand, oppHand);

        CardManager.draw(deck, discardPile);
        //Make sure we don't start with an 8
        while((getTopOfDiscard().rank == Card.Rank.EIGHT) || (getTopOfDiscard().rank == Card.Rank.FOUR) || getTopOfDiscard().rank == Rank.TWO){
            CardManager.draw(deck, discardPile);
        }
        if (playerData.getRound()==1){
            playerData.setPlayerStartTurn(new Random().nextBoolean());
        }else {
            playerData.setPlayerStartTurn(!playerData.isPlayerStartTurn());
        }
        playerTurn = playerData.isPlayerStartTurn();
        Gdx.app.log("Start playing:", (isPlayerTurn()?"Player ":" Computer "));
    }

    public void drawTable(){
        Card discardCard = getTopOfDiscard();
        discardCard.createGraphicCard(cardWidth, cardHeight, gameScreen.mapWidth / 2, gameScreen.mapHeight / 2, "front");
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
        this.gameScreen.mainStage.addActor(discadPileGroup);

        cardBack.createGraphicCard(cardWidth, cardHeight, gameScreen.mapWidth / 2, gameScreen.mapHeight / 2);
        cardBack.setZIndex(1000);
        this.gameScreen.mainStage.addActor(cardBack);

        float initCardX = initCard + (distanceCardX / 2) - (cardWidth / 2);
        float delay = 2.5f;
        CardBackAnimated cardBackAnimated = new CardBackAnimated();
        for (int i = 0; i < perHand; i++) {
            //cardBackAnimated.createGraphicCard(cardWidth, cardHeight, gameScreen.mapWidth / 2, gameScreen.mapHeight / 2);
            //cardBackAnimated.setZIndex(1001);
            //cardBackAnimated.animateCard();
            //cardBackAnimated.setActiveAnimation("flip");
            //cardBackAnimated.addActionBase(Actions.delay(delay+=0.4f));

            final Card card = playerHand.get(i);
            card.createGraphicCard(cardWidth, cardHeight, gameScreen.mapWidth / 2, gameScreen.mapHeight / 2, "back");
            card.addActionBase(Actions.sequence(
                            Actions.delay(delay+=0.5f),
                            Actions.moveTo(initCardX, 70f, 0.5f, Interpolation.exp10),
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
                        if (isValidMove(card)) {
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
                    card.setX(MathUtils.clamp(card.getX(), 0, gameScreen.mapWidth - card.getWidth()));
                    card.setY(MathUtils.clamp(card.getY(), 0, gameScreen.mapHeight - card.getHeight()));
                }

                public void touchUp(InputEvent event, float x, float y, int pointer, int button){
                    final Card card = (Card) event.getTarget();
                    if ((card.overlaps(getTopOfDiscard(),false)) && (isValidMove(card))){
                        playCard(getPlayerHand(), card);
                        playerHandGroup.removeActor(card);
                        discadPileGroup.addActor(card);
                        card.addActionBase(Actions.sequence(
                                Actions.rotateBy(GameUtils.randomFloatInRange(-15f, 15f)),
                                Actions.moveTo(topDiscardX + GameUtils.randomFloatInRange(-20f, 20f), topDiscardY + GameUtils.randomFloatInRange(-20f, 20f), 0.01f, Interpolation.exp10),
                                run(new Runnable(){
                                        @Override
                                        public void run() {
                                            card.removeListener(cardListner);
                                            distributeCardsOnTable(playerHandGroup, initCard, gameScreen.mapWidth, cardWidth, darkInvalidMoves);
                                            if (card.rank == Rank.EIGHT && !isGameOver()) {
                                                chooseSuitRunning = true;
                                                gameScreen.chooseSuit.setVisible(true);
                                            }
                                            if (card.rank == Rank.TWO){
                                                drawCardToComputer(oppHand, 2);
                                            }else if (card.rank == Rank.FOUR){
                                                if (!isGameOver()) {
                                                    chooseSuitRunning = true;
                                                    gameScreen.chooseSuit.setVisible(true);
                                                }
                                                drawCardToComputer(oppHand, 4);
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
            cardOppHand.createGraphicCard(cardWidth, cardHeight, gameScreen.mapWidth / 2, gameScreen.mapHeight / 2, "back");
            cardOppHand.addActionBase(Actions.sequence(
                    Actions.delay(delay+=0.5f),
                    Actions.moveTo(initCardX, (gameScreen.mapHeight - cardOppHand.getHeight()) + 60f, 0.5f, Interpolation.exp10)
            ));
            oppHandGroup.addActor(cardOppHand);
            Gdx.app.log("card",cardOppHand.suit.name() + " - " + cardOppHand.rank.name());
            initCardX += distanceCardX;
        }
        this.gameScreen.mainStage.addActor(playerHandGroup);
        this.gameScreen.mainStage.addActor(oppHandGroup);
        //this.gameScreen.mainStage.addActor(cardBackAnimated);

    }

    public boolean playCard(List<Card> hand, Card card){
        if(isValidMove(card)){
            Gdx.app.log((isPlayerTurn()?"Player ":" Computer ") + "Play Card: ",card.suit.name() + " " + card.rank.name());
            hand.remove(card);
            discardPile.add(0, card);
            if (getWildSuit() != null){
                suitGroup.removeActor(suitGroup.getChildren().get(0));
            }
            setWildSuit(null);
            if (card.rank != Rank.QUEEN){
                changeTurn();
            }
            return true;
        }else{
            return false;
        }
    }

    public Card getComputerPlay(){
        return computerPlayer.selectPlay(oppHand);
    }

    public void runComputerTurn(){
        Gdx.app.log("runComputerTurn", "");
        final Card picked = getComputerPlay();
        if(picked != null){
            playCard(oppHand, picked);
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
                                        distributeCardsOnTable(oppHandGroup, initCard, gameScreen.mapWidth, cardWidth, false);
                                        if(picked.rank == Rank.EIGHT && !isGameOver()){
                                            Suit selectedSuit = computerPlayer.chooseSuit(oppHand);
                                            setWildSuit(selectedSuit);
                                            SuitIcon suit = new SuitIcon();
                                            suit.createGraphicIcon(selectedSuit, gameScreen.mapWidth / 2 + 120f, (gameScreen.mapHeight / 2) + 50f);
                                            suitGroup.addActor(suit);
                                            gameScreen.mainStage.addActor(suitGroup);
                                            Gdx.app.log("Selected Suit: ",selectedSuit.name());
                                        }
                                        if (card.rank == Rank.TWO){
                                            drawCardToPlayer(2);
                                        }else if (card.rank == Rank.FOUR){
                                            if (!isGameOver()) {
                                                Suit selectedSuit = computerPlayer.chooseSuit(oppHand);
                                                setWildSuit(selectedSuit);
                                                SuitIcon suit = new SuitIcon();
                                                suit.createGraphicIcon(selectedSuit, gameScreen.mapWidth / 2 + 120f, (gameScreen.mapHeight / 2) + 50f);
                                                suitGroup.addActor(suit);
                                                gameScreen.mainStage.addActor(suitGroup);
                                                Gdx.app.log("Selected Suit: ", selectedSuit.name());
                                            }
                                            drawCardToPlayer(4);
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
        }else{
            drawCardToComputer(oppHand, 1);
        }
    }

    public void drawCardToComputer(List<Card> hand, int numberCards){
        Gdx.app.log("drawCard", "");
        List<Card> cardOppHandList = new LinkedList<Card>();
        for (int i=1; i<=numberCards; i++) {
            //If the deck is empty, we re-shuffle all but the first card in the discard pile
            //  back into the deck
            if(deck.isEmpty()){
                reshuffleCard();
            }
            CardManager.draw(deck, hand);
            final Card cardOppHand = hand.get(0);
            cardOppHandList.add(cardOppHand);
            cardOppHand.createGraphicCard(cardWidth, cardHeight, gameScreen.mapWidth / 2, gameScreen.mapHeight / 2, "back");
        }
        drawAndDistributeCards(oppHandGroup, cardOppHandList, initCard, gameScreen.mapWidth, cardWidth, false, false);
        Card topDiscard = getTopOfDiscard();
        if ( (((topDiscard.rank == Rank.TWO) || (topDiscard.rank == Rank.FOUR)) && (!hasValidMove(hand))) ||
                (!((topDiscard.rank == Rank.TWO) || (topDiscard.rank == Rank.FOUR)) && (!hasValidMove(cardOppHandList))) ){
            changeTurn();
        }
    }

    private void reshuffleCard(){
        Gdx.app.log("reshuffleCard", "");
        //List<Card> allButTop = discardPile.subList(0, discardPile.size() - 1);
        //deck.addAll(allButTop);
        while(discardPile.size() > 1){
            Rank rank = discardPile.get(1).rank;
            Suit suit = discardPile.get(1).suit;
            deck.add(new Card(suit, rank));
            discardPile.remove(1);
            discadPileGroup.getChildren().removeIndex(0);
        }
        CardManager.reshuffle(deck);
        //Toast.makeText(context, "Shuffling Discard into Deck...", Toast.LENGTH_SHORT).show();
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

    public void drawCardToPlayer(int numberCards){
        Gdx.app.log("drawCardToPlayer", "");
        List<Card> cardList = new LinkedList<Card>();
        for (int i=1;i<=numberCards;i++) {
            if(deck.isEmpty()){
                reshuffleCard();
            }
            Card card = getTopOfDeck();
            CardManager.draw(deck, playerHand, 1);
            card.createGraphicCard(cardWidth, cardHeight, gameScreen.mapWidth / 2, gameScreen.mapHeight / 2, "back");
            card.addListener(cardListner);
            cardList.add(card);
        }
        drawAndDistributeCards(playerHandGroup, cardList, initCard, gameScreen.mapWidth, cardWidth, darkInvalidMoves, true);
        Card topDiscard = getTopOfDiscard();
        if ((((topDiscard.rank == Rank.TWO) || (topDiscard.rank == Rank.FOUR)) && (!hasValidMove(playerHand))) ||
                (!((topDiscard.rank == Rank.TWO) || (topDiscard.rank == Rank.FOUR)) && (!hasValidMove(cardList))) ){
            changeTurn();
        }
    }

    private void drawAndDistributeCards(final Group cardsGroup, final List<Card> cardsToAdd, float initX, float mapWidth, float cardWidth,
                                        final boolean darkInvalideMoves, final boolean isPlayer){
        Gdx.app.log("drawAndDistributeCards", "");
        int numberCards = cardsGroup.getChildren().size + cardsToAdd.size();
        float distanceCardX = (mapWidth * 0.8f) / numberCards;
        initX = initX + (distanceCardX / 2) - (cardWidth / 2);
        for (Actor cardItem: cardsGroup.getChildren()){
            final BaseActor card = (BaseActor) cardItem;
            card.addActionBase(Actions.moveTo(initX, card.getY(), 0.3f, Interpolation.exp10));
            initX += distanceCardX;
        }
        float positionY = cardsGroup.getChildren().first().getY();
        for (final Card cardToAdd: cardsToAdd) {
            cardToAdd.addActionBase(Actions.sequence(
                    Actions.delay(0.5f),
                    Actions.moveTo(initX, positionY, 0.5f, Interpolation.exp10),
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
            if (isValidMove((Card) card)) {
                card.setColor(1f, 1f, 1f, 1f);
            } else {
                card.setColor(0.5f, 0.5f, 0.5f, 1f);
            }
        }
    }

    public boolean isValidMove(Card card){
        if (isGameOver()) return false;
        Card topOfDiscard = getTopOfDiscard();
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
            if(isValidMove(card)){
                return true;
            }
        }
        return false;
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
}
