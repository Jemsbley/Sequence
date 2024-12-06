package game.board;

/**
 * A GamePosition is a location of a cell in a game of Sequence, where the origin is the
 * top left cell, x increases right, and y increases downward.
 * @param x the 0-based x coordinate of the cell (or the column)
 * @param y the 0-based y coordinate of the cell (or the row)
 */
public record GamePosition(int x, int y) {}
