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

public class FarsightedScoredNetworkBuilding implements SequenceAlgorithm {

  private final double threshold;

  public FarsightedScoredNetworkBuilding() {
    this.threshold = 1;
  }

  public FarsightedScoredNetworkBuilding(double threshold) {
    if (threshold < 0) {
      throw new IllegalArgumentException("Threshold must be positive: given " + threshold);
    }
    this.threshold = threshold;
  }

  @Override
  public void beginTurn(PlayableSequenceModel model, SequenceController receiver) {
//    Cell[][] layout = model.getBoard().getBoard();

    Map<GameChip, List<GamePosition>> chips = model.getChips();

    List<GamePosition> theirs = new ArrayList<>();

    for (GameChip chip : chips.keySet()) {
      if (!chip.equals(receiver.getTeam())) {
        theirs.addAll(chips.get(chip));
      }
    }


    List<GamePosition> mine = chips.get(receiver.getTeam());
//    for (int col = 0; col < layout.length; col += 1) {
//      for (int row = 0; row < layout[0].length; row += 1) {
//        if (!layout[col][row].getChip().equals(receiver.getTeam()) &&
//                !layout[col][row].getChip().equals(GameChip.NONE)) {
//          theirs.add(new GamePosition(col, row));
//        } else if (layout[col][row].getChip().equals(receiver.getTeam()) ||
//                layout[col][row].getChip().equals(GameChip.ALL)) {
//          mine.add(new GamePosition(col, row));
//        }
//      }
//    }

    GameMove def = this.getDefensiveMove(model, receiver, theirs);
    GameMove off = this.getOffensiveMove(model, receiver, mine);

    int defCount = 0;
    int offCount = 0;

    GameBoard bd = model.getBoard();
    GameHand myHand = model.getHand(receiver);

    List<GamePosition> myHandLocs = new ArrayList<>();
    for (int card = 0; card < myHand.size(); card += 1) {
      myHandLocs.addAll(bd.cardLocations().get(myHand.getCardAt(card)));
    }

    for (GamePosition loc : theirs) {
      if (loc.inSequence(def.location())) {
        defCount += 1;
      }
    }

    for (GamePosition pos : myHandLocs) {
      if (pos.inSequence(def.location())) {
        defCount += 1;
      }
    }

    for (GamePosition loc : mine) {
      if (loc.inSequence(off.location())) {
        offCount += 1;
      }
    }

    for (GamePosition pos : myHandLocs) {
      if (pos.inSequence(off.location())) {
        offCount += 1;
      }
    }

    if (defCount > offCount * this.threshold) {
      receiver.receiveMove(def);
    } else {
      receiver.receiveMove(off);
    }

  }

  private GameMove getDefensiveMove(PlayableSequenceModel model, SequenceController receiver,
                                    List<GamePosition> theirs) {
    GameBoard bd = model.getBoard();
    Cell[][] layout = bd.getBoard();


    while (true) {
      boolean deadCarded = false;
      GamePosition bestLoc = new GamePosition(-1, -1);
      int bestCard = -1;
      int bestCount = 0;
      GameHand myHand = model.getHand(receiver);

      List<GamePosition> myHandLocs = new ArrayList<>();
      for (int card = 0; card < myHand.size(); card += 1) {
        myHandLocs.addAll(bd.cardLocations().get(myHand.getCardAt(card)));
      }

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
              for (GamePosition pos : theirs) {
                if (new GamePosition(col, row).inSequence(pos)) {
                  count += 1;
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

            int count = 0;
            for (GamePosition pos : theirs) {
              if (loc.inSequence(pos)) {
                count += 1;
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
              return new GameMove(new GamePosition(col, row), 0);
            }
          }
        }
      } else {
        return new GameMove(bestLoc, bestCard);
      }
    }
  }

  private GameMove getOffensiveMove(PlayableSequenceModel model, SequenceController receiver,
                                    List<GamePosition> mine) {
    GameBoard bd = model.getBoard();
    Cell[][] layout = bd.getBoard();

    while (true) {
      boolean deadCarded = false;
      GamePosition bestLoc = new GamePosition(-1, -1);
      int bestCard = -1;
      int bestCount = 0;
      GameHand myHand = model.getHand(receiver);

      List<GamePosition> myHandLocs = new ArrayList<>();
      for (int card = 0; card < myHand.size(); card += 1) {
        myHandLocs.addAll(bd.cardLocations().get(myHand.getCardAt(card)));
      }

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

            int count = 0;
            for (GamePosition pos : mine) {
              if (loc.inSequence(pos)) {
                count += 1;
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
              return new GameMove(new GamePosition(col, row), 0);

            }
          }
        }
      } else {
        return new GameMove(bestLoc, bestCard);
      }
    }
  }

}
