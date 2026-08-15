package org.hdu.chess.model.piece;

import org.hdu.chess.model.Board;
import org.hdu.chess.model.ChessPiece;
import org.hdu.chess.model.Position;
import org.hdu.chess.model.Side;
import java.util.ArrayList;
import java.util.List;

public class Elephant extends ChessPiece {
    public Elephant(Side side) {
        super(side, "相", "象",2.0);
    }

    @Override
    public List<Position> movingGenerator(Board board, Position from) {
        List<Position> generator = new ArrayList<>();
        generator.add(new Position(from.row+2, from.col+2));
        generator.add(new Position(from.row+2, from.col-2));
        generator.add(new Position(from.row-2, from.col+2));
        generator.add(new Position(from.row-2, from.col-2));
        // 加入所有可能的位置再把不符合规则的位置删除
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
        if (Math.abs(to.row - from.row) != 2 || Math.abs(to.col - from.col) != 2) return false;

        // 相不能过河
        if (isRed() && to.row < 5) return false;
        if (!isRed() && to.row > 4) return false;

        int midRow = (from.row + to.row) / 2;
        int midCol = (from.col + to.col) / 2;

        return board.get(new Position(midRow, midCol)) == null;
    }
}
