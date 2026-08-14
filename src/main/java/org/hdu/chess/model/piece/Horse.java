package org.hdu.chess.model.piece;

import org.hdu.chess.model.Board;
import org.hdu.chess.model.ChessPiece;
import org.hdu.chess.model.Position;
import org.hdu.chess.model.Side;
import java.util.ArrayList;
import java.util.List;

public class Horse extends ChessPiece {
    public Horse(Side side) {
        super(side, "马", "馬");
    }

    @Override
    public List<Position> movingGenerator(Board board, Position from) {
        List<Position> generator = new ArrayList<>();
        generator.add(new Position(from.row+1, from.col+2));
        generator.add(new Position(from.row+1, from.col-2));
        generator.add(new Position(from.row-1, from.col+2));
        generator.add(new Position(from.row-1, from.col-2));
        generator.add(new Position(from.row+2, from.col+1));
        generator.add(new Position(from.row+2, from.col-1));
        generator.add(new Position(from.row-2, from.col-1));
        generator.add(new Position(from.row-2, from.col+1));

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
        int dr = to.row - from.row;
        int dc = to.col - from.col;

        if (Math.abs(dr) == 1 && Math.abs(dc) == 2) {
            int legCol = from.col + (dc > 0 ? 1 : -1);
            return board.get(new Position(from.row, legCol)) == null;
        }

        if (Math.abs(dr) == 2 && Math.abs(dc) == 1) {
            int legRow = from.row + (dr > 0 ? 1 : -1);
            return board.get(new Position(legRow, from.col)) == null;
        }

        return false;
    }
}
