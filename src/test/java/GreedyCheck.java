import org.hdu.chess.model.*;
import org.hdu.chess.model.piece.*;
import org.hdu.chess.ai.MoveGenerator;
import org.hdu.chess.tool.Evaluator;
import java.util.List;

public class GreedyCheck {
    static Board fresh() {
        Board b = new Board();
        for (int r = 0; r < 10; r++)
            for (int c = 0; c < 9; c++)
                b.set(new Position(r, c), null);
        return b;
    }

    static double bestScore(List<MoveGenerator.Move> moves, Board board) {
        double best = -1.0;
        for (MoveGenerator.Move mv : moves) {
            double v = Evaluator.execute(board, new Position(mv.toRow(), mv.toCol()));
            if (v > best) best = v;
        }
        return best;
    }

    public static void main(String[] args) {
        // 场景1：黑车(5,0)可吃 红车(5,1)=9 或 红炮(4,0)=4.5 → 应选吃 9
        Board b1 = fresh();
        b1.set(new Position(5, 0), new Rook(Side.BLACK));
        b1.set(new Position(5, 1), new Rook(Side.RED));
        b1.set(new Position(4, 0), new Cannon(Side.RED));
        b1.set(new Position(7, 4), new King(Side.RED));
        b1.set(new Position(0, 3), new King(Side.BLACK));
        List<MoveGenerator.Move> m1 = MoveGenerator.generateLegalMoves(b1, Side.BLACK);
        double s1 = bestScore(m1, b1);
        boolean eatsRook = false;
        for (MoveGenerator.Move mv : m1)
            if (mv.toRow() == 5 && mv.toCol() == 1) eatsRook = true;
        System.out.println("场景1 最高分=" + s1 + " 存在吃车走法=" + eatsRook);
        if (s1 != 9.0 || !eatsRook) throw new AssertionError("场景1失败：贪心没选最大子");

        // 场景2：黑车(5,0)周围无红子可吃 → 全部价值0，走法数>0（兜底随机，不崩）
        Board b2 = fresh();
        b2.set(new Position(5, 0), new Rook(Side.BLACK));
        b2.set(new Position(7, 4), new King(Side.RED));
        b2.set(new Position(0, 3), new King(Side.BLACK));
        List<MoveGenerator.Move> m2 = MoveGenerator.generateLegalMoves(b2, Side.BLACK);
        double s2 = bestScore(m2, b2);
        System.out.println("场景2 最高分=" + s2 + " 走法数=" + m2.size());
        if (s2 != 0.0 || m2.isEmpty()) throw new AssertionError("场景2失败：无子可吃时应能正常走");

        System.out.println("ALL CHECKS PASSED");
    }
}
