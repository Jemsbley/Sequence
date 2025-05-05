package game.view;

import java.awt.*;
import java.util.Objects;

import javax.swing.*;

import game.board.Card;
import game.board.GameHand;

public class HandPanel extends JPanel {

  private GameHand hand;
  private int selected = -1;

  public HandPanel(GameHand hand) {
    this.hand = Objects.requireNonNull(hand);
  }

  public void update(GameHand newHand) {
    this.hand = newHand;
  }

  /**
   * Determines the number of cards in this hand. Useful when determining location of mouse clicks
   * @return the size of this panel's hand
   */
  public int getNumCards() {
    return this.hand.size();
  }

  public int getSelected() {
    return this.selected;
  }

  public void select(int selected) {
    this.selected = selected;
  }

  public Card getCardAt(int card) {
    return this.hand.getCardAt(card);
  }

  @Override
  protected void paintComponent(Graphics g) {
    Graphics2D g2d = (Graphics2D) g;

    int widthPer = (int) (double) (this.getWidth() / (this.hand.size() + 1));

    for (int currCard = 0; currCard < this.hand.size(); currCard += 1) {
      Card current = this.hand.getCardAt(currCard);
      Color tColor = this.hand.getTeam().color();

      if (currCard == this.selected) {
        g2d.setColor(new Color(tColor.getRed(), tColor.getGreen(), tColor.getBlue(), 200));
      } else {
        g2d.setColor(new Color(tColor.getRed(), tColor.getGreen(), tColor.getBlue(), 75));
      }
      g2d.fillRect(currCard * widthPer, 0, widthPer, this.getHeight());


      g2d.setColor(Color.black);
      g2d.drawRect(currCard * widthPer, 0, widthPer, this.getHeight());
      g2d.setColor(current.suit().color());
      String suitLabel = current.suit().name().substring(0,1);
      String valueLabel = current.value().toString();
      g2d.drawString(suitLabel, currCard * widthPer,
              (int) (0.5 * this.getHeight()));
      g2d.drawString(valueLabel, currCard * widthPer,
              (int) (0.75 * this.getHeight()));
    }
    g2d.setColor(Color.lightGray);
    g2d.fillRect(this.hand.size() * widthPer, 0, widthPer, this.getHeight());
    g2d.setColor(Color.black);
    g2d.drawString("Dead", this.hand.size() * widthPer + 5,
            (int) (0.5 * this.getHeight()));
    g2d.drawString("Card", this.hand.size() * widthPer + 5,
            (int) (0.75 * this.getHeight()));
    g2d.setColor(Color.black);
    g2d.drawRect(this.hand.size() * widthPer, 0, widthPer, this.getHeight());

  }
}


