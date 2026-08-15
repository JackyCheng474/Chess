package org.hdu.chess.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.hdu.chess.model.Board;
import org.hdu.chess.model.ChessPiece;
import org.hdu.chess.model.Position;
import org.hdu.chess.model.Side;
import org.hdu.chess.model.piece.King;
import org.hdu.chess.tool.Evaluator;

/**
 * α-β 剪枝搜索（纯函数式：永远在棋盘副本上模拟，不改传入的棋盘）。
 * 入口 bestMove(board, aiSide, depth)：返回 aiSide 的最佳走法，同分随机。
 */
public final class AlphaBetaSearch {

    private static final double WIN = 1_000_000.0;      // 吃将 = 必胜（远大于任何子力差）
    private static final double NEG_INF = -1.0e12;
    private static final double POS_INF = 1.0e12;

    private static final Random random = new Random();

    private AlphaBetaSearch() {
    }

    /** 根节点：对 AI 的每个走法算分，选最高的（同分随机） */
    public static MoveGenerator.Move bestMove(Board board, Side aiSide, int depth) {
        // ai行棋，算出所有能走的步数
        List<MoveGenerator.Move> moves = MoveGenerator.generateLegalMoves(board, aiSide);
        // 无棋可走（引擎无困毙规则，正常不会出现）
        if (moves.isEmpty()) {
            return null;
        }

        // 选出最好的一条路径
        double bestScore = NEG_INF;
        // 对应最好分数的路径
        List<MoveGenerator.Move> bestMoves = new ArrayList<>();
        for (MoveGenerator.Move m : moves) {
            double score = search(board, m, aiSide, depth - 1, NEG_INF, POS_INF, aiSide);
            if (score > bestScore) {
                bestScore = score;
                bestMoves.clear();
                bestMoves.add(m);
            } else if (score == bestScore) {
                bestMoves.add(m);
            }
        }
        return bestMoves.get(random.nextInt(bestMoves.size()));
    }

    /** 模拟"走一步"，然后对结果局面继续搜索 */
    private static double search(Board board, MoveGenerator.Move m, Side mover,
                                 int depth, double alpha, double beta, Side aiSide) {
        Board copy = board.copy();
        Position from = new Position(m.fromRow(), m.fromCol());
        Position to = new Position(m.toRow(), m.toCol());
        ChessPiece captured = copy.get(to);   // 先记被吃子
        ChessPiece moved = copy.get(from);
        copy.set(to, moved);
        copy.set(from, null);                 // 别忘了清来源格

        // 吃将：走棋方立即获胜
        if (captured instanceof King) {
            return mover == aiSide ? WIN : -WIN;
        }

        // 轮到对方走
        return alphaBeta(copy, depth, alpha, beta, mover.opponent(), aiSide);
    }

    /** 核心递归：toMove 方走棋，返回从 aiSide 视角的估值 */
    private static double alphaBeta(Board board, int depth, double alpha, double beta,
                                    Side toMove, Side aiSide) {
        // 叶子：深度用尽，用评估函数打分
        if (depth <= 0) {
            return Evaluator.evaluate(board, aiSide);
        }

        List<MoveGenerator.Move> moves = MoveGenerator.generateLegalMoves(board, toMove);
        if (moves.isEmpty()) {
            return Evaluator.evaluate(board, aiSide);
        }

        if (toMove == aiSide) {
            // 最大化节点：AI 选对自己最好的分支
            double best = NEG_INF;
            for (MoveGenerator.Move m : moves) {
                double v = search(board, m, toMove, depth - 1, alpha, beta, aiSide);
                best = Math.max(best, v);
                alpha = Math.max(alpha, v);
                if (alpha >= beta) break;   // 剪枝：这条分支已经救不回来了
            }
            return best;
        } else {
            // 最小化节点：对手会选对 AI 最差的分支
            double best = POS_INF;
            for (MoveGenerator.Move m : moves) {
                double v = search(board, m, toMove, depth - 1, alpha, beta, aiSide);
                best = Math.min(best, v);
                beta = Math.min(beta, v);
                if (alpha >= beta) break;   // 剪枝条件两个节点一样：alpha >= beta
            }
            return best;
        }
    }
}
