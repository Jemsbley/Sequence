package game.board;

import game.enums.GameChip;

public interface GameBoard {

  Cell getCell(GamePosition location);

  void setChip(GamePosition location, GameChip toSet);

}
