package org.hdu.chess.model.piece;

import org.hdu.chess.model.Board;
import org.hdu.chess.model.ChessPiece;
import org.hdu.chess.model.Position;
import org.hdu.chess.model.Side;

public class Pawn extends ChessPiece {
    public Pawn(Side side) {
        super(side, "兵", "卒");
    }

    @Override
    protected boolean movingOK(Board board, Position from, Position to) {
        int dr = to.row - from.row;
        int dc = to.col - from.col;

        if (isRed()) {
            // 没过河只能向前
            if (from.row >= 5) {
                return dc == 0 && dr == -1;
            } else {
                // 过河后可前可横
                return (dc == 0 && dr == -1) || (dr == 0 && Math.abs(dc) == 1);
            }
        } else {
            // 黑卒
            if (from.row <= 4) {
                return dc == 0 && dr == 1;
            } else {
                return (dc == 0 && dr == 1) || (dr == 0 && Math.abs(dc) == 1);
            }
        }
    }
}
