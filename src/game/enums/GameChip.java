package game.enums;

/**
 * A GameChip is a data representation of a chip on the game board, and can be one of the
 * provided colors. These are used to keep track of which team owns which space on the board.
 * Additionally, each team is associated with one GameChip.
 */
public enum GameChip {

  RED, GREEN, BLUE, NONE, ALL;
  // Note that the NONE value is to initialize all cells to have no chip,
  // and ALL is used to ensure that free spaces count for all teams

}
