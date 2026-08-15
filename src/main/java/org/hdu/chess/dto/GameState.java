package org.hdu.chess.dto;

import java.util.List;

public record GameState(
        List<List<PieceDto>> board,
        String currentSide,
        boolean gameOver,
        String winner,
        String message,
        String aiSide) {
}
