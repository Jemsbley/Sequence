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
 * Similar to FarsightedOffensiveSavingNetworkBuilding, except it will minimize the amount of
 * opponent cells in sequence with its own moves.
 */
public class FarsightedAvoidantSavingNetworkBuilding implements SequenceAlgorithm {

  @Override
  public void beginTurn(PlayableSequenceModel model, SequenceController receiver) {
    GameBoard bd = model.getBoard();
    Cell[][] layout = bd.getBoard();
    List<List<GamePosition>> myOpenings = model.findOpeningForSequence(receiver.getTeam());
    List<GameChip> opponents = receiver.getTeam().getOthers();
    List<List<GamePosition>> opponentOpenings = new ArrayList<>();
    for (GameChip other : opponents) {
      opponentOpenings.addAll(model.findOpeningForSequence(other));
    }

    Map<GameChip, List<GamePosition>> chips = model.getChips();

    List<GamePosition> theirs = new ArrayList<>();

    for (GameChip chip : chips.keySet()) {
      if (!chip.equals(receiver.getTeam())) {
        theirs.addAll(chips.get(chip));
      }
    }

    List<GamePosition> mine = chips.get(receiver.getTeam());

    while (true) {
      boolean deadCarded = false;
      GamePosition bestLoc = new GamePosition(-1, -1);
      int bestCard = -1;
      int bestCount = 0;
      boolean mustPlayHere = false;
      GameHand myHand = model.getHand(receiver);

      List<GamePosition> myHandLocs = new ArrayList<>();
      for (int card = 0; card < myHand.size(); card += 1) {
        myHandLocs.addAll(bd.cardLocations().get(myHand.getCardAt(card)));
      }

      for (int card = 0; card < myHand.size(); card += 1) {
        Card current = myHand.getCardAt(card);
        if (current.value().equals(CardValue.ONE_EYED_JACK)) {
          if (!opponentOpenings.isEmpty()
                  && !bd.isLocked(opponentOpenings.get(0).get(0))) {
            receiver.receiveMove(new GameMove(opponentOpenings.get(0).get(0), card));
            return;
          } if (!myOpenings.isEmpty()) {
            for (List<GamePosition> pair : myOpenings) {
              if (bd.getCell(pair.get(1)).hasChip()
                      && hasCardFor(myHand, pair.get(1), bd)
                      && !bd.isLocked(pair.get(1))) {
                receiver.receiveMove(new GameMove(pair.get(1), card));
                return;
              }
            }
          }
          continue;
        }
        if (current.value().equals(CardValue.TWO_EYED_JACK)) {
          if (!opponentOpenings.isEmpty()
                  && !bd.getCell(opponentOpenings.get(0).get(1)).hasChip()) {
            bestLoc = opponentOpenings.get(0).get(1);
            bestCard = card;
            mustPlayHere = true;
            continue;
          } else if (!myOpenings.isEmpty()
                  && !bd.getCell(myOpenings.get(0).get(1)).hasChip()) {
            bestLoc = myOpenings.get(0).get(1);
            bestCard = card;
            mustPlayHere = true;
            continue;
          }
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

              for (GamePosition pos : theirs) {
                if (new GamePosition(col, row).inSequence(pos)) {
                  count -= 1;
                }
              }

              for (GamePosition pos : myHandLocs) {
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

            if (mustPlayHere) {
              if (loc.equals(bestLoc)) {
                bestCard = card;
              }
              continue;
            }

            for (List<GamePosition> pair : opponentOpenings) {
              if (pair.get(1).equals(loc)) {
                receiver.receiveMove(new GameMove(loc, card));
                return;
              }
            }

            int count = 0;
            for (GamePosition pos : mine) {
              if (loc.inSequence(pos)) {
                count += 1;
              }
            }

            for (GamePosition pos : theirs) {
              if (loc.inSequence(pos)) {
                count -= 1;
              }
            }

            for (GamePosition pos : myHandLocs) {
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

  private boolean hasCardFor(GameHand hand, GamePosition location, GameBoard board) {
    for (int card = 0; card < hand.size(); card += 1) {
      Card currCard = hand.getCardAt(card);
      if (currCard.value().equals(CardValue.TWO_EYED_JACK)
              || board.cardLocations().get(currCard).contains(location)) {
        return true;
      }
    }
    return false;
  }

}
