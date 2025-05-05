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
import game.enums.SequenceType;
import game.scorekeeper.ScoreKeeper;
import game.view.GameFrame;
import game.view.GameView;

public class SequenceModel implements PlayableSequenceModel {

  private List<Card> deck;
  private GameBoard board;
  private SequenceController currentPlayer;
  private Map<SequenceController, SequenceController> turnOrder;
  private Map<SequenceController, GameHand> hands;
  private Map<GameChip, Integer> sequenceCounts;
  private Map<Card, Integer> remainingCards;
  private Random shuffler;
  private Map<GamePosition, List<SequenceType>> sequences = new HashMap<>();
  private final List<GameView> views = new ArrayList<>();
  private ScoreKeeper tracker;

  public void addView(GameView toAdd) {
    this.views.add(Objects.requireNonNull(toAdd));
  }

  @Override
  public void deadCard(int cardIdx) {
    GameHand playerHand = this.hands.get(this.currentPlayer);
    for (GamePosition loc : this.board.cardLocations()
            .get(playerHand.getCardAt(cardIdx))) {
      if (!this.board.getCell(loc).hasChip()) {
        throw new IllegalStateException("Card is not dead: " + playerHand.getCardAt(cardIdx) + "\n"
        + "On turn " + this.currentPlayer.getTeam());
      }
    }
    playerHand.removeCardAt(cardIdx);
    if (deck.size() == 0) {
      this.resetDeck();
    }
    playerHand.addCard(this.deck.remove(0));
  }

  @Override
  public void addScoreKeeper(ScoreKeeper sk) {
    this.tracker = sk;
  }

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
        if (this.board.getCell(where).getChip().equals(this.currentPlayer.getTeam())) {
          throw new IllegalArgumentException("Cannot remove your own pieces");
        }
        this.board.setChip(where, GameChip.NONE);
        playFrom.removeCardAt(which);
        this.remainingCards.put(toPlay, this.remainingCards.get(toPlay) - 1);
      } else {
        throw new IllegalArgumentException("Cannot play to already filled position");
      }
    } else {
      if (toPlay.value().equals(CardValue.ONE_EYED_JACK)) {
        throw new IllegalArgumentException("Cannot remove from empty position");
      } else if (toPlay.value().equals(CardValue.TWO_EYED_JACK) ||
              toPlay.sameCard(this.board.getCell(where).getCard())) {
        this.board.setChip(where, this.currentPlayer.getTeam());
        playFrom.removeCardAt(which);
        this.remainingCards.put(toPlay, this.remainingCards.get(toPlay) - 1);

        List<GamePosition> skips = new ArrayList<>();
        List<GamePosition> matches = this.matchingNeighbors(where);
        for (GamePosition match : matches) {
          if (skips.contains(match)) {
            continue;
          }
          int countUp = 0;
          int countDown = 0;
          if (where.relation(match).goesBackward()) {
            countUp = extent(where, where.relation(match));
            countDown = extent(where, match.relation(where));
          } else {
            countDown = extent(where, where.relation(match));
            countUp = extent(where, match.relation(where));
          }
          if ((countUp + countDown + 1) >= 5) {
            // Ensure that we only walk down the line if we have found the direction in which we
            // can go backwards
            if (countUp > 0 && countDown > 0 &&
                    !match.relation(where).goesBackward()) {
              continue;
            }
            skips.add(where.get(where.relation(match).opposite()));
            if (countUp > 0) {
              GamePosition header = where.copy();
              for (int goneSoFar = 0; goneSoFar < countUp; goneSoFar += 1) {
                header = header.get(match.relation(where));
              }
              updateSequences(where, match, header);
            } else {
              updateSequences(where, match, where);
            }
            this.sequenceCounts.put(this.currentPlayer.getTeam(),
                    this.sequenceCounts.get(this.currentPlayer.getTeam()) + 1);
          }
        }

      } else {
        throw new IllegalArgumentException("Invalid move: Cards do not match: "
                + playFrom.getCardAt(which) + " to " + board.getCell(where).getCard());
      }
    }
    if (this.deck.isEmpty()) {
      this.resetDeck();
    }
    this.hands.get(this.currentPlayer).addCard(this.deck.remove(0));


    //Dev mode line. Comment out the deck.remove(0) above to deal out only 2EJ after game start
    /*this.hands.get(this.currentPlayer)
      .addCard(new BasicCard(CardValue.TWO_EYED_JACK, CardSuit.CLUBS));
    */

    for (GameView view : this.views) {
      view.redraw();
    }

    if (!this.isGameOver()) {
      this.currentPlayer = this.turnOrder.get(this.currentPlayer);
      this.currentPlayer.beginTurn(this);
    } else {
      GameChip winner = this.getWinner();
      for (SequenceController player : this.turnOrder.keySet()) {
        player.receiveGameOver(winner);
      }
      if (!Objects.isNull(this.tracker)){
        this.tracker.increment(winner);
      }
    }
  }

  private void updateSequences(GamePosition where, GamePosition match, GamePosition header) {
    if (this.sequences.containsKey(header)) {
      this.sequences.get(header)
              .add(SequenceType.fromDirection(where.relation(match)));
    } else {
      ArrayList<SequenceType> toAdd = new ArrayList<>();
      toAdd.add(SequenceType.fromDirection(where.relation(match)));
      this.sequences.put(header, toAdd);
      this.lockSequence(header, this.sequences.get(header));
    }
  }

  private void lockSequence(GamePosition where, List<SequenceType> sequenceTypes) {
    GamePosition start = where.copy();
    for (int loc = 0; loc < 5; loc += 1) {
      this.board.lock(start, sequenceTypes.get(sequenceTypes.size() - 1));
      start = SequenceType.toDirection(start, sequenceTypes.get(sequenceTypes.size() - 1));
    }
  }

  private int cardsPerPlayer(int numPlayers, int numColors) {
    if (numColors == 2) {
      return 8 - numPlayers / 2;
    } else {
      return 7 - numPlayers / 3;
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
    for (int index = 0; index < players.size(); index += 1) {
      SequenceController curr = players.get(index);
      Objects.requireNonNull(curr);
      this.turnOrder.put(prev, curr);
      prev = curr;
      this.hands.put(curr, new SequenceHand(curr.getTeam()));
      this.sequenceCounts.put(curr.getTeam(), 0);
    }
    this.turnOrder.put(prev, this.currentPlayer);
    this.remainingCards = new HashMap<>();
    this.deck = this.standardDeck();
    Collections.shuffle(this.deck, shuffler);

    int numColors = 2;
    for (SequenceController cont : players) {
      if (cont.getTeam().equals(GameChip.GREEN)) {
        numColors = 3;
      }
    }

    int cardsPer = this.cardsPerPlayer(players.size(), numColors);
    for (GameHand currHand : this.hands.values()) {
      for (int times = 0; times < cardsPer; times += 1) {
        currHand.addCard(this.deck.remove(0));
      }
    }
  }

  @Override
  public void beginPlaying() {
    this.currentPlayer.beginTurn(this);
  }

  @Override
  public void resetDeck() {
    List<Card> newDeck = this.standardDeck();
    for (GameHand currHand : this.hands.values()) {
      for (int card = 0; card < currHand.size(); card += 1) {
        newDeck.remove(currHand.getCardAt(card));
      }
    }
    Collections.shuffle(this.deck, this.shuffler);
    this.deck = newDeck;
  }

  private List<GamePosition> matchingNeighbors(GamePosition start) {
    ArrayList<GamePosition> toReturn = new ArrayList<>();
    GameChip team = this.board.getCell(start).getChip();
    for (GamePosition neighbor : start.neighbors()) {
      if (!this.board.isValidLocation(neighbor)
              || this.board.isLocked(neighbor, SequenceType.fromDirection(start.relation(neighbor)))) {
        continue;
      }
      GameChip neighborChip = this.board.getCell(neighbor).getChip();
      if (neighborChip.equals(team) || neighborChip.equals(GameChip.ALL)) {
        toReturn.add(neighbor);
      }
    }
    return toReturn;
  }

  private int extent(GamePosition start, GamePosition direction) {
    int count = 0;
    GamePosition current = start.get(direction);
    GameChip team = this.board.getCell(start).getChip();
    if (!this.board.isValidLocation(current)) {
      return 0;
    }
    GameChip next = this.board.getCell(current).getChip();
    while (this.board.isValidLocation(current)
            && !this.board.isLocked(current, SequenceType.fromDirection(new GamePosition(0,0)
            .relation(direction)))
            && (next.equals(team)
            || next.equals(GameChip.ALL))) {
      count += 1;
      current = current.get(direction);
      if (this.board.isValidLocation(current)) {
        next = this.board.getCell(current).getChip();
      }
    }
    return count;
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
  public boolean isGameOver() {
    if (this.board.isFull()) {
      return true;
    }
    for (int count : this.sequenceCounts.values()) {
      if (count >= 2) {
        return true;
      }
    }
    return false;
  }

  @Override
  public GameChip getWinner() {
    for (GameChip chip : this.sequenceCounts.keySet()) {
      if (this.sequenceCounts.get(chip) >= 2) {
        return chip;
      }
    }
    if (this.board.isFull()) {
      return GameChip.NONE;
    }
    throw new IllegalStateException("No Winner");
  }

  @Override
  public GameBoard getBoard() {
    return this.board.copy();
  }

  @Override
  public GameHand getHand(SequenceController controller) {
    return this.hands.get(controller).copy();
  }

  @Override
  public Map<GamePosition, List<SequenceType>> getSequences() {
    return new HashMap<>(this.sequences);
  }

  @Override
  public int numSequences(SequenceController player) {
    return this.sequenceCounts.get(player.getTeam());
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
