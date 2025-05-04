package game.view;

import java.awt.*;
import java.util.Objects;

import javax.swing.*;

import game.board.Card;
import game.board.GameHand;

public class HandPanel extends JPanel {

  private GameHand hand;

  public HandPanel(GameHand hand) {
    this.hand = Objects.requireNonNull(hand);
  }

  public void update(GameHand newHand) {
    this.hand = newHand;
  }

  @Override
  protected void paintComponent(Graphics g) {
    Graphics2D g2d = (Graphics2D) g;

    int widthPer = (int) this.getWidth() / this.hand.size();

    for (int currCard = 0; currCard < this.hand.size(); currCard += 1) {
      Card current = this.hand.getCardAt(currCard);
      g2d.setColor(this.hand.getTeam().color());
      g2d.fillRect(currCard * widthPer, 0, widthPer, this.getHeight());

      g2d.setColor(Color.black);
      String suitLabel = current.suit().name();
      String valueLabel = current.value().name();
      g2d.drawString(suitLabel, currCard * widthPer,
              (int) (0.5 * this.getHeight()));
      g2d.drawString(valueLabel, currCard * widthPer,
              (int) (0.75 * this.getHeight()));
    }
  }
}


