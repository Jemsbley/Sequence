package game.enums;

/**
 * Each of the fourteen standard card values in a game of sequence. Jacks have different behaviors
 * based on the number of eyes the graphic on the card has
 */
public enum CardValue {
  ACE("A"), TWO("2"), THREE("3"), FOUR("4"), FIVE("5"),
  SIX("6"), SEVEN("7"), EIGHT("8"), NINE("9"), TEN("10"),
  QUEEN("Q"), KING("K"), ONE_EYED_JACK("J1"),
  TWO_EYED_JACK("J2");

  private final String stringRep;

  CardValue(String stringRep) {
    this.stringRep = stringRep;
  }

  public String toString() {
    return this.stringRep;
  }

}
