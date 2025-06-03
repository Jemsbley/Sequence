package game.enums;

import game.board.GamePosition;

/**
 * A SequenceType is a useful indicator to determine which direction a successful sequence has been
 * created. This allows "header" cells to be stored for each sequence along with one enum value
 * rather than the entire list of cells included. Given a header and a sequence type you can
 * determine the locations of all cells involved in one sequence.
 */
public enum SequenceType {

  HORIZONTAL, VERTICAL, DIAGONALUP, DIAGONALDOWN;

  public static SequenceType fromDirection(GamePosition path) {
    if ((path.x() == -1 && path.y() == 0) || (path.x() == 1 && path.y() == 0)) {
      return SequenceType.HORIZONTAL;
    } else if ((path.x() == 0 && path.y() == -1) || (path.x() == 0 && path.y() == 1)) {
      return SequenceType.VERTICAL;
    } else if ((path.x() == -1 && path.y() == -1) || (path.x() == 1 && path.y() == 1)) {
      return SequenceType.DIAGONALDOWN;
    } else {
      return SequenceType.DIAGONALUP;
    }
  }

  public static GamePosition toDirection(GamePosition from, SequenceType to) {
    if (to.equals(SequenceType.HORIZONTAL)) {
      return from.get(new GamePosition(1, 0));
    } else if (to.equals(SequenceType.VERTICAL)) {
      return from.get(new GamePosition(0, 1));
    } else if (to.equals(SequenceType.DIAGONALUP)) {
      return from.get(new GamePosition(1, -1));
    } else {
      return from.get(new GamePosition(1, 1));
    }
  }

  public GamePosition naturalDirection() {
    if (this.equals(SequenceType.HORIZONTAL)) {
      return new GamePosition(1, 0);
    } else if (this.equals(SequenceType.VERTICAL)) {
      return new GamePosition(0, 1);
    } else if (this.equals(SequenceType.DIAGONALUP)) {
      return new GamePosition(1, -1);
    } else {
      return new GamePosition(1, 1);
    }
  }
}
