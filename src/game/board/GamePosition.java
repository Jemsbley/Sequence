package game.board;

import java.util.ArrayList;
import java.util.List;

/**
 * A GamePosition is a location of a cell in a game of Sequence, where the origin is the
 * top left cell, x increases right, and y increases downward.
 * @param x the 0-based x coordinate of the cell (or the column)
 * @param y the 0-based y coordinate of the cell (or the row)
 */
public record GamePosition(int x, int y) {

  /**
   * Creates a copy of this GamePosition.
   * @return the generated copy
   */
  public GamePosition copy() {
    return this.above().below();
  }

  /**
   * Generates and returns the location one above this position.
   * @return that location
   */
  public GamePosition above() {
    return new GamePosition(this.x, this.y - 1);
  }

  /**
   * Generates and returns the location one below this position.
   * @return that location
   */
  public GamePosition below() {
    return new GamePosition(this.x, this.y + 1);
  }

  /**
   * Generates and returns the location one left of this position.
   * @return that location
   */
  public GamePosition left() {
    return new GamePosition(this.x - 1, this.y);
  }

  /**
   * Generates and returns the location one right of this position.
   * @return that location
   */
  public GamePosition right() {
    return new GamePosition(this.x + 1, this.y);
  }

  /**
   * Determines all neighbors orthogonal and diagonal to this GamePosition.
   * @return the list of all neighbors
   */
  public List<GamePosition> neighbors() {
    ArrayList<GamePosition> toReturn = new ArrayList<>();
    toReturn.add(this.above().left());
    toReturn.add(this.above());
    toReturn.add(this.above().right());
    toReturn.add(this.right());
    toReturn.add(this.below().right());
    toReturn.add(this.below());
    toReturn.add(this.below().left());
    toReturn.add(this.left());
    return toReturn;
  }

  /**
   * Returns the GamePosition one unit in the given direction.
   * @param direction a vector with dimensions -1, 0, or 1
   * @return the GamePosition in that direction
   */
  public GamePosition get(GamePosition direction) {
    return new GamePosition(this.x + direction.x, this.y + direction.y);
  }

  /**
   * Inverts this GamePosition.
   * @return the inversion of this GamePosition
   */
  public GamePosition opposite() {
    return new GamePosition(-this.x, -this.y);
  }

  /**
   * Returns a GamePosition representing how to achieve this GamePosition from the given other.
   * @param origin the position of origin
   * @return the distance across both axes from origin to this
   */
  public GamePosition relation(GamePosition origin) {
    return new GamePosition(this.x - origin.x, this.y - origin.y);
  }

  /**
   * Determines if this direction heads backwards in board space. This is important when
   * finding the header of a sequence
   * @return whether this direction heads backward
   */
  public boolean goesBackward() {
    return this.x < 0 || (this.x == 0 && this.y == -1);
  }

}
