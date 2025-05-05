package game.scorekeeper;

import game.enums.GameChip;

public interface ScoreKeeper {

  /**
   * Adds the given chip to the tracker.
   * @param player the chip to add
   */
  void addPlayer(GameChip player);

  /**
   * Increments some score tracker according to the game winner.
   * @param winner the chip representing the game winner
   */
  void increment(GameChip winner);

  /**
   * WILL CHANGE TO BE MORE USEFUL LATER:
   * Returns the currently kept stats as a string.
   * @return the string in format "Team x: [count x], Team y: [count y], Ties: [count t]"
   */
  String displayResults();
}
