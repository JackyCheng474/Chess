package org.hdu.chess.model.piece;

import org.hdu.chess.model.Board;
import org.hdu.chess.model.ChessPiece;
import org.hdu.chess.model.Position;
import org.hdu.chess.model.Side;
import java.util.ArrayList;
import java.util.List;

public class King extends ChessPiece {
    public King(Side side) {
        super(side, "帅", "将");
    }

    @Override
    public List<Position> movingGenerator(Board board, Position from) {
        List<Position> generator = new ArrayList<>();
        generator.add(new Position(from.row, from.col+1));
        generator.add(new Position(from.row, from.col-1));
        generator.add(new Position(from.row+1, from.col));
        generator.add(new Position(from.row-1, from.col));

        List<Position> legal = new ArrayList<>();
        for (Position to : generator) {
            if (this.canMove(board, from, to)) {
                legal.add(to);
            }
        }
        return legal;
    }

    @Override
    protected boolean movingOK(Board board, Position from, Position to) {
        if (Math.abs(to.row - from.row) + Math.abs(to.col - from.col) != 1) return false;

        if (isRed()) {
            return to.row >= 7 && to.row <= 9 && to.col >= 3 && to.col <= 5;
        } else {
            return to.row >= 0 && to.row <= 2 && to.col >= 3 && to.col <= 5;
        }
    }
}
