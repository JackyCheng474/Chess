package org.hdu.chess.ai.Impl;

import java.util.List;
import java.util.Random;

import org.hdu.chess.ai.AiService;
import org.hdu.chess.ai.MoveGenerator;
import org.hdu.chess.dto.GameState;
import org.hdu.chess.model.Side;
import org.hdu.chess.service.GameService;
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

        // 随机挑一步，交给引擎落子（校验/记录 path 都由 GameService 负责）
        MoveGenerator.Move m = moves.get(random.nextInt(moves.size()));
        return gameService.move(m.fromRow(), m.fromCol(), m.toRow(), m.toCol());
    }
}
