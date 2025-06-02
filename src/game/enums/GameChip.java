package game.enums;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * A GameChip is a data representation of a chip on the game board, and can be one of the
 * provided colors. These are used to keep track of which team owns which space on the board.
 * Additionally, each team is associated with one GameChip.
 */
public enum GameChip {

  RED, GREEN, BLUE, NONE, ALL;
  // Note that the NONE value is to initialize all cells to have no chip,
  // and ALL is used to ensure that free spaces count for all teams

  public List<GameChip> getOthers() {
    List<GameChip> toReturn = new ArrayList<>();
    if (this.equals(GameChip.RED)) {
      toReturn.add(GameChip.GREEN);
      toReturn.add(GameChip.BLUE);
    } else if (this.equals(GameChip.BLUE)) {
      toReturn.add(GameChip.GREEN);
      toReturn.add(GameChip.RED);
    } else if (this.equals(GameChip.GREEN)) {
      toReturn.add(GameChip.BLUE);
      toReturn.add(GameChip.RED);
    }
    return toReturn;
  }

  public Color color(){
    if (this.equals(GameChip.RED)) {
      return new Color(200,20,100);
    } else if (this.equals(GameChip.BLUE)) {
      return new Color(20,100,200);
    } else if (this.equals(GameChip.GREEN)) {
      return new Color(100,200,20);
    } else if (this.equals(GameChip.ALL)){
      return new Color(103, 103, 103);
    } else {
      throw new IllegalStateException("Cannot get the color for this type of chip.");
    }
  }

}
