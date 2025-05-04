package game.enums;

import java.awt.*;

/**
 * A GameChip is a data representation of a chip on the game board, and can be one of the
 * provided colors. These are used to keep track of which team owns which space on the board.
 * Additionally, each team is associated with one GameChip.
 */
public enum GameChip {

  RED, GREEN, BLUE, NONE, ALL;
  // Note that the NONE value is to initialize all cells to have no chip,
  // and ALL is used to ensure that free spaces count for all teams

  public Color color(){
    if (this.equals(GameChip.RED)) {
      return new Color(200,20,100);
    } else if (this.equals(GameChip.BLUE)) {
      return new Color(20,100,200);
    } else if (this.equals(GameChip.GREEN)) {
      return new Color(100,200,20);
    } else {
      throw new IllegalStateException("Cannot get the color for this type of chip.");
    }
  }

}
