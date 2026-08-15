package org.hdu.chess.ai.Impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.hdu.chess.ai.AiService;
import org.hdu.chess.ai.MoveGenerator;
import org.hdu.chess.dto.GameState;
import org.hdu.chess.model.Board;
import org.hdu.chess.model.Position;
import org.hdu.chess.model.Side;
import org.hdu.chess.service.GameService;
import org.hdu.chess.tool.Evaluator;
import org.springframework.stereotype.Service;

@Service
public class AiServiceImpl implements AiService {

    private final GameService gameService;
    private final Random random = new Random();

    public AiServiceImpl(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public GameState move() {
        // 守卫：游戏没结束且确实轮到 AI
        if (!gameService.isAiTurn()) {
            return gameService.state();
        }

        Side aiSide = gameService.getAiSide();
        List<MoveGenerator.Move> moves =
                MoveGenerator.generateLegalMoves(gameService.getBoard(), aiSide);

        if (moves.isEmpty()) {
            return gameService.state(); // 无棋可走
        }

        // 贪心：选"吃掉最大子"的走法；同分走法之间随机挑一个
        Board board = gameService.getBoard();
        double bestValue = -1.0;
        List<MoveGenerator.Move> bestMoves = new ArrayList<>();

        for (MoveGenerator.Move mv : moves) {
            double v = Evaluator.execute(board, new Position(mv.toRow(), mv.toCol()));
            if (v > bestValue) {
                bestValue = v;
                bestMoves.clear();
                bestMoves.add(mv);
            } else if (v == bestValue) {
                bestMoves.add(mv);
            }
        }

        // 没子可吃（bestValue <= 0）：随机走一步，保证对局继续
        MoveGenerator.Move chosen = bestValue > 0
                ? bestMoves.get(random.nextInt(bestMoves.size()))
                : moves.get(random.nextInt(moves.size()));

        return gameService.move(chosen.fromRow(), chosen.fromCol(), chosen.toRow(), chosen.toCol());
    }
}
