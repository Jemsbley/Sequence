package game.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
 * The best moves in the Network Builder (Minimizing One Eyes) are those which are in sequence with
 * as many other pieces that are currently on the board.
 */
public class OffensiveNetworkBuilding implements SequenceAlgorithm {
  @Override
  public void beginTurn(PlayableSequenceModel model, SequenceController receiver) {
    GameBoard bd = model.getBoard();
    Cell[][] layout = bd.getBoard();
    Map<GameChip, List<GamePosition>> chips = model.getChips();

    List<GamePosition> mine = chips.get(receiver.getTeam());

    while (true) {
      boolean deadCarded = false;
      GamePosition bestLoc = new GamePosition(-1, -1);
      int bestCard = -1;
      int bestCount = 0;
      GameHand myHand = model.getHand(receiver);
      for (int card = 0; card < myHand.size(); card += 1) {
        Card current = myHand.getCardAt(card);
        if (current.value().equals(CardValue.ONE_EYED_JACK)) {
          continue;
        }
        if (current.value().equals(CardValue.TWO_EYED_JACK)) {
          for (int col = 0; col < layout.length; col += 1) {
            for (int row = 0; row < layout[0].length; row += 1) {
              if (bd.getCell(new GamePosition(col, row)).hasChip()) {
                continue;
              }
              int count = 0;
              for (GamePosition pos : mine) {
                if (new GamePosition(col, row).inSequence(pos)) {
                  count += 1;
                }
              }
              if (bestCard == -1 || bestCount < count) {
                bestCard = card;
                bestCount = count;
                bestLoc = new GamePosition(col, row);
              }
            }
          }
        } else {
          List<GamePosition> cardLocs = bd.cardLocations().get(current);
          int deadLocs = 0;
          for (GamePosition loc : cardLocs) {
            if (bd.getCell(loc).hasChip()) {
              deadLocs += 1;
              continue;
            }

            int count = 0;
            for (GamePosition pos : mine) {
              if (loc.inSequence(pos)) {
                count += 1;
              }
            }
            if (bestCard == -1 || bestCount <= count) {
              bestCard = card;
              bestCount = count;
              bestLoc = loc;
            }
          }
          if (deadLocs == cardLocs.size()) {
            receiver.deadCard(card);
            deadCarded = true;
            break;
          }
        }
      }
      if (deadCarded) {
        continue;
      }
      if (bestCard == -1) {
        for (int col = 0; col < layout.length; col += 1) {
          for (int row = 0; row < layout[0].length; row += 1) {
            if (!layout[col][row].getChip().equals(receiver.getTeam())
                    && !layout[col][row].getChip().equals(GameChip.NONE)
                    && !layout[col][row].getChip().equals(GameChip.ALL)
                    && !bd.isLocked(new GamePosition(col, row))) {
              receiver.receiveMove(new GameMove(new GamePosition(col, row), 0));
              return;
            }
          }
        }
      } else {
        receiver.receiveMove(new GameMove(bestLoc, bestCard));
        return;
      }
    }

  }
}
