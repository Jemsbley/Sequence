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
  private final Map<GamePosition, List<SequenceType>> sequences = new HashMap<>();
  private final List<GameView> views = new ArrayList<>();
  private Map<GameChip, List<GamePosition>> chips;
  private int numChipsPresent;
  private int numMovesMade;
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
        throw new IllegalStateException("Card is not dead: " + playerHand.getCardAt(cardIdx)
        + " on turn " + this.currentPlayer.getTeam());
      }
    }
    playerHand.removeCardAt(cardIdx);
    if (deck.isEmpty()) {
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
      throw new IllegalArgumentException("Invalid hand index for move: " + which
              + " for " + playFrom.size());
    }

    Card toPlay = playFrom.getCardAt(which);
    if (this.board.getCell(where).hasChip()) {
      if (toPlay.value().equals(CardValue.ONE_EYED_JACK)) {
        if (this.board.getCell(where).getChip().equals(this.currentPlayer.getTeam())) {
          throw new IllegalArgumentException("Cannot remove your own pieces");
        }
        this.chips.get(this.board.getCell(where).getChip()).remove(where);
        this.board.setChip(where, GameChip.NONE);
        playFrom.removeCardAt(which);
        this.remainingCards.put(toPlay, this.remainingCards.get(toPlay) - 1);
        this.numChipsPresent -= 1;
        this.numMovesMade += 1;
      } else {
        throw new IllegalArgumentException("Cannot play to already filled position "
                + playFrom.getCardAt(which) + " at "
                + where + " on turn " + currentPlayer.getTeam());
      }
    } else {
      if (toPlay.value().equals(CardValue.ONE_EYED_JACK)) {
        throw new IllegalArgumentException("Cannot remove from empty position: "
                + where + " on turn " + this.currentPlayer.getTeam());
      } else if (toPlay.value().equals(CardValue.TWO_EYED_JACK) ||
              toPlay.sameCard(this.board.getCell(where).getCard())) {
        this.chips.get(this.currentPlayer.getTeam()).add(where);
        this.board.setChip(where, this.currentPlayer.getTeam());
        playFrom.removeCardAt(which);
        this.remainingCards.put(toPlay, this.remainingCards.get(toPlay) - 1);
        this.numChipsPresent += 1;
        this.numMovesMade += 1;

        List<GamePosition> skips = new ArrayList<>();
        List<GamePosition> matches = this.matchingNeighbors(where);
        for (GamePosition match : matches) {
          if (skips.contains(match)) {
            continue;
          }
          int countUp ;
          int countDown;
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
        this.tracker.receiveNumMoves(this.numMovesMade);
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
    this.numMovesMade = 0;
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
    for (SequenceController curr : players) {
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

    this.chips = new HashMap<>();
    this.chips.put(GameChip.RED, new ArrayList<>());
    this.chips.put(GameChip.BLUE, new ArrayList<>());
    this.chips.put(GameChip.GREEN, new ArrayList<>());
    int numColors = 2;
    for (SequenceController cont : players) {
      if (cont.getTeam().equals(GameChip.GREEN)) {
        numColors = 3;
      }
    }

    this.numChipsPresent = 0;

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

  @Override
  public Map<GameChip, List<GamePosition>> getChips() {
    Map<GameChip, List<GamePosition>> toReturn = new HashMap<>();
    for (GameChip chip : this.chips.keySet()) {
      toReturn.put(chip, new ArrayList<>(this.chips.get(chip)));
    }
    return toReturn;
  }

  @Override
  public List<List<GamePosition>> findOpeningForSequence(GameChip team) {
    // For now I'm gonna have it search all played pieces anyways and maybe later ill add the
    // speedup because for now it would require a bit more work and I'd rather make it run
    List<List<GamePosition>> toReturn = new ArrayList<>();

//    if (this.numChipsPresent <= this.board.numPlayableSpaces() / 2) {
      List<GamePosition> allied = this.chips.get(team);
      for (GamePosition ally : allied) {
        List<GamePosition> neighbors = ally.neighbors();
        for (GamePosition neighbor : neighbors) {
          if (!this.board.isValidLocation(neighbor)) {
            continue;
          }
          int numEmpty = 0;
          GamePosition dir = neighbor.relation(ally);
          GamePosition opening = new GamePosition(-1,-1);
          if (!this.board.getCell(neighbor).getChip().equals(team)) {
            numEmpty = 1;
            opening = neighbor.copy();
          }
          GamePosition curr = neighbor.copy();
          boolean enoughSpace = true;
          for (int dist = 1; dist < 4; dist += 1) {
            curr = curr.get(dir);
            if (!this.board.isValidLocation(curr)) {
              enoughSpace = false;
              break;
            }
            GameChip atPos = this.board.getCell(curr).getChip();
            if (atPos.equals(GameChip.NONE) ||
                    (!atPos.equals(GameChip.ALL) && !atPos.equals(team))) {
              opening = curr.copy();
              numEmpty += 1;
            }
            if (numEmpty > 1) {
              break;
            }
          }

          if (enoughSpace && numEmpty == 1) {
            boolean skipThis = false;
            for (List<GamePosition> pair : toReturn) {
              if (pair.get(1).equals(opening)) {
                skipThis = true;
              }
            }
            if (skipThis) {
              continue;
            }
            List<GamePosition> pair = new ArrayList<>();
            pair.add(ally);
            pair.add(opening);
            toReturn.add(pair);
          }
        }
      }
//    } else {
//      throw new IllegalArgumentException("bru");
//    }
    return toReturn;
  }

  private Map<SequenceType, List<GamePosition>> getAllSequencedSpots(GameChip team) {
    Map<SequenceType, List <GamePosition>> toReturn = new HashMap<>();
    for (SequenceType type : SequenceType.values()) {
      toReturn.put(type, new ArrayList<>());
    }
    for (GamePosition header : this.sequences.keySet()) {
      GameChip atHeader = this.board.getCell(header).getChip();
      if (!atHeader.equals(team) && !atHeader.equals(GameChip.ALL)) {
        continue;
      }
      for (SequenceType type : this.sequences.get(header)) {
        GamePosition dir = type.naturalDirection();
        if (atHeader.equals(GameChip.ALL)
                && !this.board.getCell(header.get(dir)).getChip().equals(team)) {
          continue;
        }
        toReturn.get(type).add(header);
        for (int pos = 0; pos < 4; pos += 1) {
          toReturn.get(type).add(header.get(dir));
        }
      }
    }
    return toReturn;
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
