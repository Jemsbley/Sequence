package game.enums;

/**
 * A SequenceType is a useful indicator to determine which direction a successful sequence has been
 * created. This allows "header" cells to be stored for each sequence along with one enum value
 * rather than the entire list of cells included. Given a header and a sequence type you can
 * determine the locations of all cells involved in one sequence.
 */
public enum SequenceType {

  HORIZONTAL, VERTICAL, DIAGONALUP, DIAGONALDOWN;
}
