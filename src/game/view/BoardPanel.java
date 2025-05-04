package game.view;

import java.awt.*;
import java.util.Objects;

import javax.swing.*;

import game.board.Cell;
import game.board.GameBoard;
import game.enums.GameChip;

public class BoardPanel extends JPanel {

  private GameBoard board;

  public BoardPanel(GameBoard board) {
    this.board = Objects.requireNonNull(board);
  }

  public void update(GameBoard newBoard) {
    this.board = newBoard;
  }

  @Override
  protected void paintComponent(Graphics g) {
    Graphics2D g2d = (Graphics2D) g;

    Cell[][] actualBoard = this.board.getBoard();

    int widthPer = (int) (double) this.getWidth() / actualBoard.length;
    int heightPer = (int) (double) this.getHeight() / actualBoard[0].length;

    for (int currCol = 0; currCol < actualBoard.length; currCol += 1) {
      for (int currRow = 0; currRow < actualBoard[0].length; currRow += 1) {
        Cell current = actualBoard[currCol][currRow];
        if (current.getChip().equals(GameChip.ALL)) {
          g2d.setColor(Color.gray);
          g2d.fillRect(currCol * widthPer, currRow * heightPer, widthPer, heightPer);
        } else {
          g2d.setColor(Color.white);
          g2d.drawRect(currCol * widthPer, currRow * heightPer, widthPer, heightPer);

          if (!current.getChip().equals(GameChip.NONE)) {
            g2d.setColor(current.getChip().color());
            g2d.fillOval(currCol * widthPer + widthPer / 4,
                    currRow * heightPer + heightPer / 4,
                    widthPer / 2, heightPer / 2);
          }

          g2d.setColor(Color.black);
          String suitLabel = current.getCard().suit().name();
          String valueLabel = current.getCard().value().name();
          g2d.drawString(suitLabel, currCol * widthPer,
                  currRow * heightPer + (int) (0.5 * heightPer));
          g2d.drawString(valueLabel, currCol * widthPer,
                  currRow * heightPer + (int) (0.75 * heightPer));


        }
      }
    }
  }

}
