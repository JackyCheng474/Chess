package org.hdu.chess.ai;

import java.util.ArrayList;
import java.util.List;

import org.hdu.chess.model.Board;
import org.hdu.chess.model.ChessPiece;
import org.hdu.chess.model.Position;
import org.hdu.chess.model.Side;
import org.hdu.chess.model.piece.King;
import org.hdu.chess.tool.StateAndRule;

/**
 * 走法生成器：枚举某方当前所有合法走法。
 * 棋子规则由各棋子类的 movingGenerator 给出，这里统一做"将帅照面"检查——
 * 在棋盘副本上模拟走子后判断，与 GameService.move() 的规则完全一致。
 */
public final class MoveGenerator {

    public record Move(int fromRow, int fromCol, int toRow, int toCol) {
    }

    private MoveGenerator() {
    }

    public static List<Move> generateLegalMoves(Board board, Side side) {
        List<Move> moves = new ArrayList<>();
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 9; c++) {
                ChessPiece piece = board.get(new Position(r, c));
                if (piece == null || piece.getSide() != side) continue;
                Position from = new Position(r, c);
                for (Position to : piece.movingGenerator(board, from)) {
                    if (isLegalAfterSimulation(board, from, to)) {
                        moves.add(new Move(r, c, to.row, to.col));
                    }
                }
            }
        }
        return moves;
    }

    private static boolean isLegalAfterSimulation(Board board, Position from, Position to) {
        Board copy = board.copy();
        ChessPiece captured = copy.get(to);
        copy.set(to, copy.get(from));
        copy.set(from, null);
        if (captured instanceof King) return true;      // 吃将即制胜，合法
        return !StateAndRule.isKingMeeting(copy);       // 走完不能将帅照面
    }
}
