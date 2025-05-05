package game.algorithms;

import java.util.List;

import game.board.Card;
import game.board.Cell;
import game.board.GameBoard;
import game.board.GameHand;
import game.board.GamePosition;
import game.controller.GameMove;
import game.controller.SequenceController;
import game.enums.CardValue;
import game.enums.GameChip;
import game.model.PlayableSequenceModel;

/**
 * Plays the first card in the hand when possible, prioritizing all top left locations.
 */
public class FirstCardPriorityTopLeft implements SequenceAlgorithm {

  @Override
  public void beginTurn(PlayableSequenceModel model, SequenceController receiver) {
    GameBoard bd = model.getBoard();
    Cell[][] layout = bd.getBoard();
    int firstPlayable = 0;
    while (true) {
      GameHand myHand = model.getHand(receiver);
      Card card1 = myHand.getCardAt(firstPlayable);
      if (card1.value().equals(CardValue.TWO_EYED_JACK)) {
        for (int col = 0; col < layout.length; col += 1) {
          for (int row = 0; row < layout[0].length; row += 1) {
            if (!layout[col][row].hasChip()) {
              receiver.receiveMove(new GameMove(new GamePosition(col, row), firstPlayable));
              return;
            }
          }
        }
        throw new IllegalStateException("Board full");
      } else if (card1.value().equals(CardValue.ONE_EYED_JACK)) {
        if (bd.isEmpty()) {
          firstPlayable += 1;
          continue;
        }
        for (int col = 0; col < layout.length; col += 1) {
          for (int row = 0; row < layout[0].length; row += 1) {
            if (layout[col][row].hasChip() && !layout[col][row].getChip().equals(GameChip.ALL)
                    && !bd.isLocked(new GamePosition(col, row))
                    && !layout[col][row].getChip().equals(receiver.getTeam())) {
              receiver.receiveMove(new GameMove(new GamePosition(col, row), firstPlayable));
              return;
            }
          }
        }
      }
      List<GamePosition> options = bd.cardLocations().get(card1);
      for (GamePosition pos : options) {
        if (!layout[pos.x()][pos.y()].hasChip()) {
          receiver.receiveMove(new GameMove(pos, firstPlayable));
          return;
        }
      }
      receiver.deadCard(firstPlayable);

    }

  }
}
