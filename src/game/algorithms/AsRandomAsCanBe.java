package game.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import game.board.Card;
import game.board.Cell;
import game.board.GameBoard;
import game.board.GameHand;
import game.board.GamePosition;
import game.controller.GameMove;
import game.controller.SequenceController;
import game.enums.CardValue;
import game.model.PlayableSequenceModel;

/**
 * As close to true random as I could think. Picks a card at random and picks a random spot for it.
 */
public class AsRandomAsCanBe implements SequenceAlgorithm {

  @Override
  public void beginTurn(PlayableSequenceModel model, SequenceController receiver) {
    GameBoard bd = model.getBoard();
    Cell[][] layout = bd.getBoard();

    List<GamePosition> removeables = new ArrayList<>();
    int[][] states = new int[layout.length][layout[0].length];
    for (int col = 0; col < layout.length; col += 1) {
      for (int row = 0; row < layout[0].length; row += 1) {
        if (col == 0 & row == 0
                || col == layout.length - 1 && row == 0
                || col == 0 && row == layout[0].length - 1
                || col == layout.length - 1 && row == layout[0].length - 1) {
          continue;
        }
        GamePosition pos = new GamePosition(col, row);
        if (bd.isLocked(pos)) {
          states[col][row] = 0;
        } else if (bd.getCell(pos).hasChip()) {
          states[col][row] = -1;
          removeables.add(new GamePosition(col, row));
        } else {
          states[col][row] = 1;
        }
      }
    }
    while (true) {
      GameHand myHand = model.getHand(receiver);
      int card = new Random().nextInt(myHand.size() - 1);
      Card currCard = myHand.getCardAt(card);
      if (currCard.value().equals(CardValue.TWO_EYED_JACK)) {
        while (true) {
          int col = new Random().nextInt(layout.length);
          int row = new Random().nextInt(layout[0].length);
          if (states[col][row] == 1) {
            receiver.receiveMove(new GameMove(new GamePosition(col, row), card));
            return;
          }
        }
      } else if (currCard.value().equals(CardValue.ONE_EYED_JACK)) {
        while (!removeables.isEmpty()) {
          int choice;
          if (removeables.size() == 1) {
            choice = 0;
          } else {
            choice = new Random().nextInt(removeables.size() - 1);
          }
          if (bd.getCell(removeables.get(choice)).getChip().equals(receiver.getTeam())) {
            removeables.remove(choice);
            continue;
          }
          receiver.receiveMove(new GameMove(removeables.get(choice), card));
          return;
        }
      } else {
        List<GamePosition> cardLocs = bd.cardLocations().get(currCard);
        while (!cardLocs.isEmpty()) {
          int choice;
          if (cardLocs.size() == 1) {
            choice = 0;
          } else {
            choice = new Random().nextInt(cardLocs.size() - 1);
          }
          if (states[cardLocs.get(choice).x()][cardLocs.get(choice).y()] == 1) {
            receiver.receiveMove(new GameMove(cardLocs.get(choice), card));
            return;
          }
          cardLocs.remove(choice);
        }
        receiver.deadCard(card);
      }
    }
  }
}
