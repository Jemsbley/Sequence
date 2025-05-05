package game.view;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.swing.*;

import game.board.Card;
import game.board.Cell;
import game.board.GameBoard;
import game.board.GamePosition;
import game.enums.CardValue;
import game.enums.GameChip;
import game.enums.SequenceType;

public class BoardPanel extends JPanel {

  private GameBoard board;
  private Card selected = null;
  private GameChip currentTurn;
  private Map<GamePosition, List<SequenceType>> sequences;

  public BoardPanel(GameBoard board, GameChip currentTurn,
                    Map<GamePosition, java.util.List<SequenceType>> sequences) {
    this.board = Objects.requireNonNull(board);
    this.currentTurn = currentTurn;
    this.sequences = Objects.requireNonNull(sequences);
  }

  public void update(GameBoard newBoard, GameChip currentTurn,
                     Map<GamePosition, java.util.List<SequenceType>> sequences) {
    this.board = newBoard;
    this.currentTurn = currentTurn;
    this.sequences = sequences;
  }

  public void select(Card toSelect) {
    this.selected = toSelect;
  }

  public void clearSelection() {
    this.selected = null;
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
          g2d.setColor(Color.lightGray);
          g2d.drawRect(currCol * widthPer, currRow * heightPer, widthPer, heightPer);
          g2d.setColor(GameChip.BLUE.color());
          g2d.fillArc(currCol * widthPer + widthPer / 4,
                  currRow * heightPer + heightPer / 4,
                  widthPer / 2, heightPer / 2,
                  0, 120);
          g2d.setColor(GameChip.RED.color());
          g2d.fillArc(currCol * widthPer + widthPer / 4,
                  currRow * heightPer + heightPer / 4,
                  widthPer / 2, heightPer / 2,
                  120, 120);
          g2d.setColor(GameChip.GREEN.color());
          g2d.fillArc(currCol * widthPer + widthPer / 4,
                  currRow * heightPer + heightPer / 4,
                  widthPer / 2, heightPer / 2,
                  240, 120);
        } else {
          if (this.selected != null) {
            if (this.selected.value().equals(CardValue.TWO_EYED_JACK) &&
                    current.getChip().equals(GameChip.NONE)) {
              g2d.setColor(new Color(255, 175, 0, 100));
              g2d.fillRect(currCol * widthPer, currRow * heightPer, widthPer, heightPer);
            } else if (this.selected.sameCard(current.getCard())) {
              g2d.setColor(Color.YELLOW);
              g2d.fillRect(currCol * widthPer, currRow * heightPer, widthPer, heightPer);
            } else if (this.selected.value().equals(CardValue.ONE_EYED_JACK)
                    && !current.getChip().equals(GameChip.NONE) &&
                    !current.getChip().equals(this.currentTurn)) {
              g2d.setColor(new Color(255, 100, 200, 100));
              g2d.fillRect(currCol * widthPer, currRow * heightPer, widthPer, heightPer);
            }
          }

          if (!current.getChip().equals(GameChip.NONE)) {
            g2d.setColor(current.getChip().color());
            g2d.fillOval(currCol * widthPer + widthPer / 4,
                    currRow * heightPer + heightPer / 4,
                    widthPer / 2, heightPer / 2);
          }

          g2d.setColor(current.getCard().suit().color());
          String suitLabel = current.getCard().suit().name().substring(0,1);
          String valueLabel = current.getCard().value().toString();
          g2d.drawString(suitLabel, currCol * widthPer,
                  currRow * heightPer + (int) (0.5 * heightPer));
          g2d.drawString(valueLabel, currCol * widthPer,
                  currRow * heightPer + (int) (0.75 * heightPer));

          g2d.setColor(Color.lightGray);
          g2d.drawRect(currCol * widthPer, currRow * heightPer, widthPer, heightPer);

        }
      }
    }

    g2d.setColor(Color.BLACK);
    for (GamePosition pos : this.sequences.keySet()) {
      java.util.List<SequenceType> seqs = new ArrayList<>(this.sequences.get(pos));
      for (SequenceType seq : seqs) {
        GamePosition end = pos.copy();
        for (int times = 0; times < 4; times += 1) {
          end = SequenceType.toDirection(end, seq);
        }
        g2d.drawLine(pos.x() * widthPer + widthPer / 2, pos.y() * heightPer  + heightPer / 2,
                end.x() * widthPer + widthPer / 2, end.y() * heightPer + heightPer / 2);
      }

    }

  }

}
