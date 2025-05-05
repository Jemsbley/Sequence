package game.view;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Objects;

import javax.swing.*;

import game.board.GamePosition;
import game.controller.GameMove;
import game.controller.SequenceController;
import game.model.ReadOnlySequenceModel;

public class GameFrame extends JFrame implements GameView {

  private ReadOnlySequenceModel model;
  private BoardPanel bp;
  private SequenceController controller;
  private HandPanel hand;
  private JPanel mainPanel;

  public GameFrame(ReadOnlySequenceModel model, SequenceController controller) {
    super();
    setTitle("Sequence");
    setSize(500, 580);
    this.mainPanel = new JPanel();
    this.reset(model, controller);


  }

  public void redraw() {
    this.repaint();
    this.bp.update(this.model.getBoard(), this.model.getCurrentTurn().getTeam(),
            this.model.getSequences());
    this.bp.repaint();
    this.hand.update(this.model.getHand(this.controller));
    this.hand.repaint();
  }

  public void reset(ReadOnlySequenceModel model, SequenceController controller) {
    this.mainPanel.removeAll();
    this.mainPanel.setLayout(new BorderLayout());
    this.model = Objects.requireNonNull(model);
    this.bp = new BoardPanel(model.getBoard(),
            this.model.getCurrentTurn().getTeam(), this.model.getSequences());
    this.controller = Objects.requireNonNull(controller);
    this.hand = new HandPanel(this.model.getHand(this.controller));


    this.mainPanel.add(this.bp, BorderLayout.CENTER);
    this.bp.setPreferredSize(new Dimension(500,500));
    this.mainPanel.add(this.hand, BorderLayout.SOUTH);
    this.hand.setPreferredSize(new Dimension(500, 50));
    this.redraw();
    this.add(mainPanel);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setResizable(false);
    if (this.controller.usesMouse()) {
      this.addMouseListener(new ViewMouseListener());
    }
    this.setVisible(true);
  }

  @Override
  public void popUp(String message) {
    JOptionPane.showMessageDialog(this, message);
  }

  @Override
  public void gameOver() {
    this.removeMouseListener(this.getMouseListeners()[0]);
  }

  private void interpretMouseClick(MouseEvent e) {
    int ht = this.getHeight();
    int wd = this.getWidth();
    int wdp = wd / 10;
    int wdpc = wd / (this.hand.getNumCards() + 1);
    int dividertop = ht / 11 * 10;
    int htp = (dividertop - 30) / 10;
    if (e.getY() <= dividertop) {
      if (this.hand.getSelected() == -1) {
        JOptionPane.showMessageDialog(this, "Must select a card first");
      } else {
        int col = e.getX() / wdp;
        int row = (e.getY() - 30) / htp;
        this.controller.receiveMove(new GameMove(new GamePosition(col, row),
                this.hand.getSelected()));
        this.hand.select(-1);
        this.bp.clearSelection();
        this.redraw();
      }
    } else {
      int card = e.getX() / wdpc;
      if (card == this.hand.getNumCards()) {
        if (this.hand.getSelected() == -1) {
          JOptionPane.showMessageDialog(this, "Must select a card first");
        } else {
          this.controller.deadCard(this.hand.getSelected());
          this.hand.select(-1);
          this.bp.clearSelection();
          this.redraw();
        }
      } else {
        this.hand.select(card);
        this.bp.select(this.hand.getCardAt(card));
        this.redraw();
      }
    }
  }


  /**
   * MouseListener class so we can properly interpret mouse inputs.
   */
  private class ViewMouseListener implements MouseListener {

    /**
     * When the mouse is clicked, we pass the mouse event to the view.
     * @param e the event to be processed.
     */
    @Override
    public void mouseClicked(MouseEvent e) {
      if (controller.equals(model.getCurrentTurn())) {
        interpretMouseClick(e);
        repaint();
      }
    }

    @Override
    public void mousePressed(MouseEvent e) {
      // not needed
    }

    @Override
    public void mouseReleased(MouseEvent e) {
      // not needed
    }

    @Override
    public void mouseEntered(MouseEvent e) {
      // not needed
    }

    @Override
    public void mouseExited(MouseEvent e) {
      // not needed
    }
  }

}
