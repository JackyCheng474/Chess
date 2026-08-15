package org.hdu.chess.ai.Impl;

import org.hdu.chess.ai.AiService;
import org.hdu.chess.ai.AlphaBetaSearch;
import org.hdu.chess.ai.MoveGenerator;
import org.hdu.chess.dto.GameState;
import org.hdu.chess.model.Board;
import org.hdu.chess.model.Side;
import org.hdu.chess.service.GameService;
import org.springframework.stereotype.Service;

@Service
public class AiServiceImpl implements AiService {

    private static final int SEARCH_DEPTH = 4;

    private final GameService gameService;

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
        Board board = gameService.getBoard();

        MoveGenerator.Move move = AlphaBetaSearch.bestMove(board, aiSide, SEARCH_DEPTH);
        if (move == null) {
            return gameService.state(); // 无棋可走
        }
        return gameService.move(move.fromRow(), move.fromCol(), move.toRow(), move.toCol());
    }
}
