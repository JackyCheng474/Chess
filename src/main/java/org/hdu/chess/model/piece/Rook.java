package org.hdu.chess.model.piece;

import org.hdu.chess.model.Board;
import org.hdu.chess.model.ChessPiece;
import org.hdu.chess.model.Position;
import org.hdu.chess.model.Side;
import java.util.ArrayList;
import java.util.List;

public class Rook extends ChessPiece {
    public Rook(Side side) {
        super(side, "车", "車");
    }

    @Override
    public List<Position> movingGenerator(Board board, Position from) {
        List<Position> generator = new ArrayList<>();
        // 把每一行能加入的都加上
        for(int i = 0;i < 9;i++) {
            if (this.canMove(board, from, new Position(from.row, i))) {
                generator.add(new Position(from.row,i));
            }
        }

        // 把同一列每个能走的都加入
        for (int j = 0;j < 10;j++) {
            if (this.canMove(board, from, new Position(j, from.col))) {
                generator.add(new Position(j, from.col));
            }
        }
        return generator;
    }

    @Override
    protected boolean movingOK(Board board, Position from, Position to) {
        if (from.row != to.row && from.col != to.col) return false;

        int dr = Integer.signum(to.row - from.row);
        int dc = Integer.signum(to.col - from.col);

        int r = from.row + dr;
        int c = from.col + dc;

        while (r != to.row || c != to.col) {
            if (board.get(new Position(r, c)) != null) return false;
            r += dr;
            c += dc;
        }
        return true;
    }
}
