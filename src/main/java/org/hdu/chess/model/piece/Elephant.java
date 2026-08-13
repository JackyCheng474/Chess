package org.hdu.chess.model.piece;

import org.hdu.chess.model.Board;
import org.hdu.chess.model.ChessPiece;
import org.hdu.chess.model.Position;
import org.hdu.chess.model.Side;

public class Elephant extends ChessPiece {
    public Elephant(Side side) {
        super(side, "相", "象");
    }

    @Override
    protected boolean movingOK(Board board, Position from, Position to) {
        if (Math.abs(to.row - from.row) != 2 || Math.abs(to.col - from.col) != 2) return false;

        // 相不能过河
        if (isRed() && to.row < 5) return false;
        if (!isRed() && to.row > 4) return false;

        int midRow = (from.row + to.row) / 2;
        int midCol = (from.col + to.col) / 2;

        return board.get(new Position(midRow, midCol)) == null;
    }
}
