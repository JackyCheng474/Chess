package org.hdu.chess.model.piece;

import org.hdu.chess.model.Board;
import org.hdu.chess.model.ChessPiece;
import org.hdu.chess.model.Position;
import org.hdu.chess.model.Side;
import java.util.ArrayList;
import java.util.List;

public class Advisor extends ChessPiece {
    public Advisor(Side side) {
        super(side, "仕", "士");
    }

    @Override
    protected boolean movingOK(Board board, Position from, Position to) {
        if (Math.abs(to.row - from.row) != 1 || Math.abs(to.col - from.col) != 1) return false;

        if (isRed()) {
            return to.row >= 7 && to.row <= 9 && to.col >= 3 && to.col <= 5;
        } else {
            return to.row >= 0 && to.row <= 2 && to.col >= 3 && to.col <= 5;
        }
    }

    @Override
    public List<Position> movingGenerator(Board board, Position from) {
        List<Position> candidates = List.of(
                new Position(from.row + 1, from.col + 1),
                new Position(from.row + 1, from.col - 1),
                new Position(from.row - 1, from.col + 1),
                new Position(from.row - 1, from.col - 1));

        List<Position> legal = new ArrayList<>();
        for (Position to : candidates) {
            if (this.canMove(board, from, to)) {
                legal.add(to);
            }
        }
        return legal;
    }
}
