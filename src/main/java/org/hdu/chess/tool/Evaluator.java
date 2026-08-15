package org.hdu.chess.tool;

import org.hdu.chess.model.Board;
import org.hdu.chess.model.ChessPiece;
import org.hdu.chess.model.Position;

/**
 * 评估函数（无状态工具，方法接收棋盘参数）。
 * 当前实现：只看"目标格能吃到的棋子价值"——贪心 AI 用它选"吃最大子"的走法。
 * 后续做 α-β 时，会扩展成对整个局面打分的 evaluate(Board, Side)。
 */
public final class Evaluator {

    private Evaluator() {
    }

    /** 返回目标格棋子的价值；空格返回 0（什么都没吃） */
    public static double execute(Board board, Position to) {
        ChessPiece piece = board.get(to);
        return piece == null ? 0.0 : piece.getValue();
    }
}
