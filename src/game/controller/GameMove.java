package game.controller;

import game.board.GamePosition;

/**
 * A container for game move information. This includes
 * @param location the location on the board that a move will be played to
 * @param handIndex the index of the card in the hand of the current player to be played from
 */
public record GameMove(GamePosition location, int handIndex) { }
