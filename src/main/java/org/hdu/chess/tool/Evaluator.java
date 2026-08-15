package org.hdu.chess.tool;

import org.hdu.chess.model.Board;
import org.hdu.chess.model.ChessPiece;
import org.hdu.chess.model.Position;
import org.hdu.chess.model.Side;

/**
 * 评估函数（无状态工具，方法接收棋盘参数）。
 * - evaluate(Board, Side)：整个局面的子力差，α-β 的叶子节点打分用；
 * - execute(Board, Position)：单格棋子的价值（空格为 0）。
 */
public final class Evaluator {

    private Evaluator() {
    }

    /** 从 perspective 视角看整个局面：我方子力 − 对方子力 */
    public static double evaluate(Board board, Side perspective) {
        double score = 0.0;
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 9; c++) {
                ChessPiece piece = board.get(new Position(r, c));
                if (piece == null) continue;
                score += piece.getSide() == perspective ? piece.getValue() : -piece.getValue();
            }
        }
        return score;
    }

    /** 目标格棋子的价值；空格返回 0（什么都没吃） */
    public static double execute(Board board, Position to) {
        ChessPiece piece = board.get(to);
        return piece == null ? 0.0 : piece.getValue();
    }
}
