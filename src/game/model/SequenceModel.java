package game.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import game.board.BasicCard;
import game.board.Card;
import game.board.GameBoard;
import game.board.GameHand;
import game.board.GamePosition;
import game.board.SequenceHand;
import game.controller.GameMove;
import game.controller.SequenceController;
import game.enums.CardSuit;
import game.enums.CardValue;
import game.enums.GameChip;

public class SequenceModel implements PlayableSequenceModel {

  private List<Card> deck;
  private GameBoard board;
  private SequenceController currentPlayer;
  private Map<SequenceController, SequenceController> turnOrder;
  private Map<SequenceController, GameHand> hands;
  private Map<GameChip, Integer> sequenceCounts;
  private Map<Card, Integer> remainingCards;
  private Random shuffler;

  @Override
  public void playToCell(GameMove move) {
    GamePosition where = move.location();
    int which = move.handIndex();
    GameHand playFrom = this.hands.get(this.currentPlayer);

    if (which < 0 || which > playFrom.size() - 1) {
      throw new IllegalArgumentException("Invalid hand index for move");
    }

    Card toPlay = playFrom.getCardAt(which);

    if (this.board.getCell(where).hasChip()) {
      if (toPlay.value().equals(CardValue.ONE_EYED_JACK)) {
        this.board.setChip(where, GameChip.NONE);
        this.remainingCards.put(toPlay, this.remainingCards.get(toPlay) - 1);
      } else {
        throw new IllegalArgumentException("Cannot play to already filled position");
      }
    } else {
      this.board.setChip(where, this.currentPlayer.getTeam());
      this.remainingCards.put(toPlay, this.remainingCards.get(toPlay) - 1);
    }
    if (this.deck.size() == 0) {
      // make a discard deck that clears here into the actual deck
    }
    this.hands.get(this.currentPlayer).addCard(this.deck.remove(0));

    this.currentPlayer = this.turnOrder.get(this.currentPlayer);
    if (!this.isGameOver()) {

      this.currentPlayer.beginTurn(this);
    }
  }

  @Override
  public void initializeGame(GameBoard gameBoard, List<SequenceController> players, Random shuffler) {
    this.board = Objects.requireNonNull(gameBoard);
    this.shuffler = shuffler;
    Objects.requireNonNull(players);

    if (players.size() < 2) {
      throw new IllegalArgumentException("Minimum two players");
    }

    Objects.requireNonNull(players.get(0));
    this.currentPlayer = players.get(0);
    SequenceController prev = this.currentPlayer;
    this.hands = new HashMap<>();
    this.turnOrder = new HashMap<>();
    this.sequenceCounts = new HashMap<>();
    for (int index = 1; index < players.size(); index += 1) {
      SequenceController curr = players.get(index);
      Objects.requireNonNull(curr);
      this.turnOrder.put(prev, curr);
      prev = curr;
      this.hands.put(curr, new SequenceHand(curr.getTeam()));
      this.sequenceCounts.put(curr.getTeam(), 0);
    }
    this.turnOrder.put(prev, this.currentPlayer);
    this.deck = this.standardDeck();
    this.remainingCards = new HashMap<>();
    Collections.shuffle(this.deck, shuffler);
  }

  @Override
  public void beginPlaying() {
    this.currentPlayer.beginTurn(this);
  }

  @Override
  public SequenceController getCurrentTurn() {
    return this.currentPlayer;
  }

  @Override
  public int numOneEyedJacksRemaining() {
    return this.remainingCards.get(new BasicCard(CardValue.ONE_EYED_JACK, CardSuit.DIAMONDS))
            + this.remainingCards.get(new BasicCard(CardValue.ONE_EYED_JACK, CardSuit.SPADES))
            + this.remainingCards.get(new BasicCard(CardValue.ONE_EYED_JACK, CardSuit.HEARTS))
            + this.remainingCards.get(new BasicCard(CardValue.ONE_EYED_JACK, CardSuit.CLUBS));
  }

  @Override
  public int numTwoEyedJacksRemaining() {
    return this.remainingCards.get(new BasicCard(CardValue.TWO_EYED_JACK, CardSuit.DIAMONDS))
            + this.remainingCards.get(new BasicCard(CardValue.TWO_EYED_JACK, CardSuit.SPADES))
            + this.remainingCards.get(new BasicCard(CardValue.TWO_EYED_JACK, CardSuit.HEARTS))
            + this.remainingCards.get(new BasicCard(CardValue.TWO_EYED_JACK, CardSuit.CLUBS));
  }

  @Override
  public int numXCardRemaining(Card toCheck) {
    return this.remainingCards.get(toCheck);
  }

  @Override
  public boolean isGameOver() { // needs check for tie
    for (int count : this.sequenceCounts.values()) {
      if (count == 2) {
        return true;
      }
    }
    return false;
  }

  @Override
  public GameChip getWinner() { // needs return for tie
    for (GameChip chip : this.sequenceCounts.keySet()) {
      if (this.sequenceCounts.get(chip) == 2) {
        return chip;
      }
    }
    throw new IllegalStateException("No Winner");
  }

  private List<Card> standardDeck() {
    ArrayList<Card> deck = new ArrayList<>();
    for (CardValue val : CardValue.values()) {
      for (CardSuit suit : CardSuit.values()) {
        deck.add(new BasicCard(val, suit));
        deck.add(new BasicCard(val, suit));
        this.remainingCards.put(new BasicCard(val, suit), 2);
      }
    }
    return deck;
  }
}
