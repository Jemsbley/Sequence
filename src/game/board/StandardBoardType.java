package game.board;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import game.enums.CardSuit;
import game.enums.CardValue;
import game.enums.GameChip;

/**
 * Implementation of the standard board in a game of sequence. Allows setting and changing
 * of cells as would happen in games
 */
public class StandardBoardType implements GameBoard {

  private final Cell[][] board;

  public StandardBoardType() {
    this.board = this.createStandardBoard();
  }

  public StandardBoardType(Cell[][] board) {
    this.board = this.checkBoard(board);
  }

  @Override
  public Cell getCell(GamePosition location) throws IllegalArgumentException {
    if (!isValidLocation(location)) {
      throw new IllegalArgumentException("Invalid location");
    }
    return this.board[location.x()][location.y()].copy();
  }

  @Override
  public void setChip(GamePosition location, GameChip toSet) throws IllegalArgumentException {
    if (!isValidLocation(location)) {
      throw new IllegalArgumentException("Invalid location");
    } else if (this.board[location.x()][location.y()].hasChip()
            && this.board[location.x()][location.y()].getChip().equals(GameChip.ALL)) {
      throw new IllegalArgumentException("Cannot set the chip at a free space");
    }
    this.board[location.x()][location.y()].setChip(toSet);
  }

  @Override
  public Cell[][] getBoard() {
    Cell[][] toReturn = new Cell[this.board.length][this.board[0].length];

    for (int col = 0; col < this.board.length; col += 1) {
      for (int row = 0; row < this.board[0].length; row += 1) {
        toReturn[col][row] = this.board[col][row].copy();
      }
    }

    return toReturn;
  }

  @Override
  public boolean isFull() {
    for (Cell[] c : this.board) {
      for (Cell spot : c) {
        if (!spot.hasChip()) {
          return false;
        }
      }
    }
    return true;
  }

  @Override
  public GameBoard copy() {
    return new StandardBoardType().copyStates(this);
  }

  /**
   * Copies all states from a given board to this board. This is used for copy construction.
   * @param other the board to copy
   * @return this board after finishing the copy process
   */
  private GameBoard copyStates(GameBoard other) {
    for (int col = 0; col < other.getBoard().length; col += 1) {
      for (int row = 0; row < other.getBoard()[0].length; row += 1) {
        if (this.board[col][row].getChip().equals(GameChip.ALL)) {
          continue;
        }
        this.board[col][row].setChip(other.getBoard()[col][row].getChip());
      }
    }
    return this;
  }

  private boolean isValidLocation(GamePosition location) {
    return location.x() >= 0 && location.y() >= 0
            && location.x() <= this.board.length && location.y() <= this.board[0].length;
  }

  private Cell[][] checkBoard(Cell[][] boardToCheck) {
    Map<CardSuit, Map<CardValue,Integer>> counts = new HashMap<>();
    Map<CardValue,Integer> spadeCounts = new HashMap<>();
    Map<CardValue,Integer> clubCounts = new HashMap<>();
    Map<CardValue,Integer> heartCounts = new HashMap<>();
    Map<CardValue,Integer> diamondCounts = new HashMap<>();
    counts.put(CardSuit.SPADES, spadeCounts);
    counts.put(CardSuit.CLUBS, clubCounts);
    counts.put(CardSuit.HEARTS, heartCounts);
    counts.put(CardSuit.DIAMONDS, diamondCounts);

    for (CardValue value : CardValue.values()) {
      counts.get(CardSuit.SPADES).put(value, 0);
      counts.get(CardSuit.CLUBS).put(value, 0);
      counts.get(CardSuit.HEARTS).put(value, 0);
      counts.get(CardSuit.DIAMONDS).put(value, 0);
    }

    for (int col = 0; col < this.board.length; col += 1) {
      for (int row = 0; row < this.board[0].length; row += 1) {
        if (boardToCheck[col][row].hasChip()
                && (boardToCheck[col][row].getChip().equals(GameChip.ALL))) {
          continue;
        }
        Card currCard = boardToCheck[col][row].getCard();
        if (currCard.value().equals(CardValue.ONE_EYED_JACK) ||
                currCard.value().equals(CardValue.TWO_EYED_JACK)) {
          throw new IllegalArgumentException("Jacks cannot be on the board");
        }
        if (counts.get(currCard.suit()).get(currCard.value()) > 1) {
          throw new IllegalArgumentException("Cannot have more than two of a given card");
        }
        counts.get(currCard.suit()).put(currCard.value(), 1 +
                counts.get(currCard.suit()).get(currCard.value()));
      }
    }
    return boardToCheck;
  }


  private Cell[][] createStandardBoard() {
    Cell[][] standardBoard = new Cell[10][10];

    // Top row
    standardBoard[0][0] = new FreeSpaceCell();
    standardBoard[1][0] = new PlayableCell(new BasicCard(CardValue.TWO, CardSuit.SPADES));
    standardBoard[2][0] = new PlayableCell(new BasicCard(CardValue.THREE, CardSuit.SPADES));
    standardBoard[3][0] = new PlayableCell(new BasicCard(CardValue.FOUR, CardSuit.SPADES));
    standardBoard[4][0] = new PlayableCell(new BasicCard(CardValue.FIVE, CardSuit.SPADES));
    standardBoard[5][0] = new PlayableCell(new BasicCard(CardValue.SIX, CardSuit.SPADES));
    standardBoard[6][0] = new PlayableCell(new BasicCard(CardValue.SEVEN, CardSuit.SPADES));
    standardBoard[7][0] = new PlayableCell(new BasicCard(CardValue.EIGHT, CardSuit.SPADES));
    standardBoard[8][0] = new PlayableCell(new BasicCard(CardValue.NINE, CardSuit.SPADES));
    standardBoard[9][0] = new FreeSpaceCell();

    // Second row
    standardBoard[0][1] = new PlayableCell(new BasicCard(CardValue.SIX, CardSuit.CLUBS));
    standardBoard[1][1] = new PlayableCell(new BasicCard(CardValue.FIVE, CardSuit.CLUBS));
    standardBoard[2][1] = new PlayableCell(new BasicCard(CardValue.FOUR, CardSuit.CLUBS));
    standardBoard[3][1] = new PlayableCell(new BasicCard(CardValue.THREE, CardSuit.CLUBS));
    standardBoard[4][1] = new PlayableCell(new BasicCard(CardValue.TWO, CardSuit.CLUBS));
    standardBoard[5][1] = new PlayableCell(new BasicCard(CardValue.ACE, CardSuit.HEARTS));
    standardBoard[6][1] = new PlayableCell(new BasicCard(CardValue.KING, CardSuit.HEARTS));
    standardBoard[7][1] = new PlayableCell(new BasicCard(CardValue.QUEEN, CardSuit.HEARTS));
    standardBoard[8][1] = new PlayableCell(new BasicCard(CardValue.TEN, CardSuit.HEARTS));
    standardBoard[9][1] = new PlayableCell(new BasicCard(CardValue.TEN, CardSuit.SPADES));

    // Third row
    standardBoard[0][2] = new PlayableCell(new BasicCard(CardValue.SEVEN, CardSuit.CLUBS));
    standardBoard[1][2] = new PlayableCell(new BasicCard(CardValue.ACE, CardSuit.SPADES));
    standardBoard[2][2] = new PlayableCell(new BasicCard(CardValue.TWO, CardSuit.DIAMONDS));
    standardBoard[3][2] = new PlayableCell(new BasicCard(CardValue.THREE, CardSuit.DIAMONDS));
    standardBoard[4][2] = new PlayableCell(new BasicCard(CardValue.FOUR, CardSuit.DIAMONDS));
    standardBoard[5][2] = new PlayableCell(new BasicCard(CardValue.FIVE, CardSuit.DIAMONDS));
    standardBoard[6][2] = new PlayableCell(new BasicCard(CardValue.SIX, CardSuit.DIAMONDS));
    standardBoard[7][2] = new PlayableCell(new BasicCard(CardValue.SEVEN, CardSuit.DIAMONDS));
    standardBoard[8][2] = new PlayableCell(new BasicCard(CardValue.NINE, CardSuit.HEARTS));
    standardBoard[9][2] = new PlayableCell(new BasicCard(CardValue.QUEEN, CardSuit.SPADES));

    // Fourth row
    standardBoard[0][3] = new PlayableCell(new BasicCard(CardValue.EIGHT, CardSuit.CLUBS));
    standardBoard[1][3] = new PlayableCell(new BasicCard(CardValue.KING, CardSuit.SPADES));
    standardBoard[2][3] = new PlayableCell(new BasicCard(CardValue.SIX, CardSuit.CLUBS));
    standardBoard[3][3] = new PlayableCell(new BasicCard(CardValue.FIVE, CardSuit.CLUBS));
    standardBoard[4][3] = new PlayableCell(new BasicCard(CardValue.FOUR, CardSuit.CLUBS));
    standardBoard[5][3] = new PlayableCell(new BasicCard(CardValue.THREE, CardSuit.CLUBS));
    standardBoard[6][3] = new PlayableCell(new BasicCard(CardValue.TWO, CardSuit.CLUBS));
    standardBoard[7][3] = new PlayableCell(new BasicCard(CardValue.EIGHT, CardSuit.DIAMONDS));
    standardBoard[8][3] = new PlayableCell(new BasicCard(CardValue.EIGHT, CardSuit.HEARTS));
    standardBoard[9][3] = new PlayableCell(new BasicCard(CardValue.KING, CardSuit.SPADES));

    // Fifth row
    standardBoard[0][4] = new PlayableCell(new BasicCard(CardValue.NINE, CardSuit.CLUBS));
    standardBoard[1][4] = new PlayableCell(new BasicCard(CardValue.QUEEN, CardSuit.SPADES));
    standardBoard[2][4] = new PlayableCell(new BasicCard(CardValue.SEVEN, CardSuit.CLUBS));
    standardBoard[3][4] = new PlayableCell(new BasicCard(CardValue.SIX, CardSuit.HEARTS));
    standardBoard[4][4] = new PlayableCell(new BasicCard(CardValue.FIVE, CardSuit.HEARTS));
    standardBoard[5][4] = new PlayableCell(new BasicCard(CardValue.FOUR, CardSuit.HEARTS));
    standardBoard[6][4] = new PlayableCell(new BasicCard(CardValue.ACE, CardSuit.HEARTS));
    standardBoard[7][4] = new PlayableCell(new BasicCard(CardValue.NINE, CardSuit.DIAMONDS));
    standardBoard[8][4] = new PlayableCell(new BasicCard(CardValue.SEVEN, CardSuit.HEARTS));
    standardBoard[9][4] = new PlayableCell(new BasicCard(CardValue.ACE, CardSuit.SPADES));

    // Sixth row
    standardBoard[0][5] = new PlayableCell(new BasicCard(CardValue.TEN, CardSuit.CLUBS));
    standardBoard[1][5] = new PlayableCell(new BasicCard(CardValue.TEN, CardSuit.SPADES));
    standardBoard[2][5] = new PlayableCell(new BasicCard(CardValue.EIGHT, CardSuit.CLUBS));
    standardBoard[3][5] = new PlayableCell(new BasicCard(CardValue.SEVEN, CardSuit.HEARTS));
    standardBoard[4][5] = new PlayableCell(new BasicCard(CardValue.TWO, CardSuit.HEARTS));
    standardBoard[5][5] = new PlayableCell(new BasicCard(CardValue.THREE, CardSuit.HEARTS));
    standardBoard[6][5] = new PlayableCell(new BasicCard(CardValue.KING, CardSuit.HEARTS));
    standardBoard[7][5] = new PlayableCell(new BasicCard(CardValue.TEN, CardSuit.DIAMONDS));
    standardBoard[8][5] = new PlayableCell(new BasicCard(CardValue.SIX, CardSuit.HEARTS));
    standardBoard[9][5] = new PlayableCell(new BasicCard(CardValue.TWO, CardSuit.DIAMONDS));

    // Seventh row
    standardBoard[0][6] = new PlayableCell(new BasicCard(CardValue.QUEEN, CardSuit.CLUBS));
    standardBoard[1][6] = new PlayableCell(new BasicCard(CardValue.NINE, CardSuit.SPADES));
    standardBoard[2][6] = new PlayableCell(new BasicCard(CardValue.NINE, CardSuit.CLUBS));
    standardBoard[3][6] = new PlayableCell(new BasicCard(CardValue.EIGHT, CardSuit.HEARTS));
    standardBoard[4][6] = new PlayableCell(new BasicCard(CardValue.NINE, CardSuit.HEARTS));
    standardBoard[5][6] = new PlayableCell(new BasicCard(CardValue.TEN, CardSuit.HEARTS));
    standardBoard[6][6] = new PlayableCell(new BasicCard(CardValue.QUEEN, CardSuit.HEARTS));
    standardBoard[7][6] = new PlayableCell(new BasicCard(CardValue.QUEEN, CardSuit.DIAMONDS));
    standardBoard[8][6] = new PlayableCell(new BasicCard(CardValue.FIVE, CardSuit.HEARTS));
    standardBoard[9][6] = new PlayableCell(new BasicCard(CardValue.THREE, CardSuit.DIAMONDS));

    // Eighth row
    standardBoard[0][7] = new PlayableCell(new BasicCard(CardValue.KING, CardSuit.CLUBS));
    standardBoard[1][7] = new PlayableCell(new BasicCard(CardValue.EIGHT, CardSuit.SPADES));
    standardBoard[2][7] = new PlayableCell(new BasicCard(CardValue.TEN, CardSuit.CLUBS));
    standardBoard[3][7] = new PlayableCell(new BasicCard(CardValue.QUEEN, CardSuit.CLUBS));
    standardBoard[4][7] = new PlayableCell(new BasicCard(CardValue.KING, CardSuit.CLUBS));
    standardBoard[5][7] = new PlayableCell(new BasicCard(CardValue.ACE, CardSuit.CLUBS));
    standardBoard[6][7] = new PlayableCell(new BasicCard(CardValue.ACE, CardSuit.DIAMONDS));
    standardBoard[7][7] = new PlayableCell(new BasicCard(CardValue.KING, CardSuit.DIAMONDS));
    standardBoard[8][7] = new PlayableCell(new BasicCard(CardValue.FOUR, CardSuit.HEARTS));
    standardBoard[9][7] = new PlayableCell(new BasicCard(CardValue.FOUR, CardSuit.DIAMONDS));

    // Ninth row
    standardBoard[0][8] = new PlayableCell(new BasicCard(CardValue.ACE, CardSuit.CLUBS));
    standardBoard[1][8] = new PlayableCell(new BasicCard(CardValue.SEVEN, CardSuit.SPADES));
    standardBoard[2][8] = new PlayableCell(new BasicCard(CardValue.SIX, CardSuit.SPADES));
    standardBoard[3][8] = new PlayableCell(new BasicCard(CardValue.FIVE, CardSuit.SPADES));
    standardBoard[4][8] = new PlayableCell(new BasicCard(CardValue.FOUR, CardSuit.SPADES));
    standardBoard[5][8] = new PlayableCell(new BasicCard(CardValue.THREE, CardSuit.SPADES));
    standardBoard[6][8] = new PlayableCell(new BasicCard(CardValue.TWO, CardSuit.SPADES));
    standardBoard[7][8] = new PlayableCell(new BasicCard(CardValue.TWO, CardSuit.HEARTS));
    standardBoard[8][8] = new PlayableCell(new BasicCard(CardValue.THREE, CardSuit.HEARTS));
    standardBoard[9][8] = new PlayableCell(new BasicCard(CardValue.FIVE, CardSuit.DIAMONDS));

    // Tenth row
    standardBoard[0][9] = new FreeSpaceCell();
    standardBoard[1][9] = new PlayableCell(new BasicCard(CardValue.ACE, CardSuit.DIAMONDS));
    standardBoard[2][9] = new PlayableCell(new BasicCard(CardValue.KING, CardSuit.DIAMONDS));
    standardBoard[3][9] = new PlayableCell(new BasicCard(CardValue.QUEEN, CardSuit.DIAMONDS));
    standardBoard[4][9] = new PlayableCell(new BasicCard(CardValue.TEN, CardSuit.DIAMONDS));
    standardBoard[5][9] = new PlayableCell(new BasicCard(CardValue.NINE, CardSuit.DIAMONDS));
    standardBoard[6][9] = new PlayableCell(new BasicCard(CardValue.EIGHT, CardSuit.DIAMONDS));
    standardBoard[7][9] = new PlayableCell(new BasicCard(CardValue.SEVEN, CardSuit.DIAMONDS));
    standardBoard[8][9] = new PlayableCell(new BasicCard(CardValue.SIX, CardSuit.DIAMONDS));
    standardBoard[9][9] = new FreeSpaceCell();

    return standardBoard;
  }

  @Override
  public Map<Card, List<GamePosition>> cardLocations() {
    Map<Card, List<GamePosition>> toReturn = new HashMap<>();
    for (CardValue val : CardValue.values()) {
      for (CardSuit suit : CardSuit.values()) {
        toReturn.put(new BasicCard(val, suit), new ArrayList<>());
      }
    }

    for (int col = 0; col < this.board.length; col += 1) {
      for (int row = 0; row < this.board[0].length; row += 1) {
        if (!this.board[col][row].getChip().equals(GameChip.ALL)) {
          toReturn.get(this.board[col][row].getCard()).add(new GamePosition(col, row));
        }
      }
    }

    return toReturn;
  }
}
