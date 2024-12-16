package game.board;

import java.util.ArrayList;

import game.enums.GameChip;

public class SequenceHand implements GameHand {

  private final ArrayList<Card> hand;
  private final GameChip team;

  public SequenceHand(GameChip team) {
    this.hand = new ArrayList<>();
    this.team = team;
  }

  @Override
  public Card getCardAt(int index) {
    if (index < 0 || index >= this.hand.size()) {
      throw new IllegalArgumentException("Invalid hand index");
    }
    return this.hand.get(index);
  }

  @Override
  public Card removeCardAt(int index) {
    if (index < 0 || index >= this.hand.size()) {
      throw new IllegalArgumentException("Invalid hand index");
    }
    return this.hand.remove(index);
  }

  @Override
  public void addCard(Card toAdd) {
    this.hand.add(toAdd);
  }

  @Override
  public int size() {
    return this.hand.size();
  }

  public GameChip getTeam() {
    return this.team;
  }
}
