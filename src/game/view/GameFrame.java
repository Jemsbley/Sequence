package game.view;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Objects;

import javax.swing.*;

import game.controller.SequenceController;
import game.enums.GameChip;
import game.model.ReadOnlySequenceModel;

public class GameFrame extends JFrame {

  private final ReadOnlySequenceModel model;
  private final BoardPanel bp;
  private final SequenceController controller;
  private final HandPanel hand;

  public GameFrame(ReadOnlySequenceModel model, SequenceController controller) {
    super();
    this.model = Objects.requireNonNull(model);
    this.bp = new BoardPanel(model.getBoard());
    this.controller = Objects.requireNonNull(controller);
    this.hand = new HandPanel(this.model.getHand(this.controller));

    setTitle("Sequence");
    setSize(1000, 1100);
    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BorderLayout());

    mainPanel.add(this.bp, BorderLayout.CENTER);
    this.bp.setPreferredSize(new Dimension(1000,1000));
    mainPanel.add(this.hand, BorderLayout.SOUTH);
    this.hand.setPreferredSize(new Dimension(1000, 100));

    this.redraw();
    this.add(mainPanel);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setResizable(false);
    this.addMouseListener(new ViewMouseListener());
  }

  public void redraw() {
    this.repaint();
    this.bp.update(this.model.getBoard());
    this.bp.repaint();
    this.hand.update(this.model.getHand(this.controller));
    this.hand.repaint();
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
        System.out.println(e);
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
