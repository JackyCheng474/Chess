package org.hdu.chess.model.piece;

import org.hdu.chess.model.Board;
import org.hdu.chess.model.ChessPiece;
import org.hdu.chess.model.Position;
import org.hdu.chess.model.Side;
import java.util.ArrayList;
import java.util.List;

public class Cannon extends ChessPiece {
    public Cannon(Side side) {
        super(side, "炮", "砲");
    }

    @Override
    public List<Position> movingGenerator(Board board, Position from) {
        int row = from.row;
        int col = from.col;
        // 把同一行每个能走的位置都加入
        List<Position> generator = new ArrayList<>();
        for(int i = 0;i < 9;i++) {
            if (this.canMove(board, from, new Position(row, i))) {
                generator.add(new Position(row,i));
            }
        }

        // 把同一列每个能走的都加入
        for (int j = 0;j < 10;j++) {
            if (this.canMove(board, from, new Position(j, col))) {
                generator.add(new Position(j,col));
            }
        }
        return generator;
    }

    @Override
    protected boolean movingOK(Board board, Position from, Position to) {
        if (from.row != to.row && from.col != to.col) return false;

        int dr = Integer.signum(to.row - from.row);
        int dc = Integer.signum(to.col - from.col);

        int barrier = 0;
        int r = from.row + dr;
        int c = from.col + dc;

        while (r != to.row || c != to.col) {
            if (board.get(new Position(r, c)) != null) barrier++;
            r += dr;
            c += dc;
        }

        ChessPiece target = board.get(to);

        if (barrier == 0) return target == null;
        if (barrier == 1) return target != null;
        return false;
    }
}
