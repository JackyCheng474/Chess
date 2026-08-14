package org.hdu.chess.tool;

import java.util.ArrayList;
import java.util.List;

import org.hdu.chess.dto.GameState;
import org.hdu.chess.dto.PieceDto;
import org.hdu.chess.model.Board;
import org.hdu.chess.model.ChessPiece;
import org.hdu.chess.model.Position;
import org.hdu.chess.model.Side;
import org.hdu.chess.model.piece.Advisor;
import org.hdu.chess.model.piece.Cannon;
import org.hdu.chess.model.piece.Elephant;
import org.hdu.chess.model.piece.Horse;
import org.hdu.chess.model.piece.King;
import org.hdu.chess.model.piece.Pawn;
import org.hdu.chess.model.piece.Rook;

/**
 * 无状态规则工具：所有方法接收棋盘参数，不持有任何游戏状态。
 * 棋盘唯一的主人只有 GameService（单例）。
 */
public final class StateAndRule {

    private StateAndRule() {
    }

    public static GameState toState(Board board, String currentSide, boolean gameOver,
                                    String winner, String message, String aiSide) {
        List<List<PieceDto>> grid = new ArrayList<>();
        for (int r = 0; r < 10; r++) {
            List<PieceDto> row = new ArrayList<>();
            for (int c = 0; c < 9; c++) {
                ChessPiece piece = board.get(new Position(r, c));
                row.add(piece == null ? null
                        : new PieceDto(piece.getSide().name().toLowerCase(), typeCode(piece)));
            }
            grid.add(row);
        }
        return new GameState(grid, currentSide.toLowerCase(), gameOver, winner, message, aiSide);
    }

    public static String isInCheck(Board board, Side side, String message) {
        Position king = board.findKing(side);
        if (king == null) return "绝杀";

        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 9; c++) {
                Position p = new Position(r, c);
                ChessPiece piece = board.get(p);
                if (piece == null || piece.getSide() == side) continue;
                if (piece.canMove(board, p, king)) return "将军";
            }
        }
        return message;
    }

    public static boolean isKingMeeting(Board board) {
        Position red = board.findKing(Side.RED);
        Position black = board.findKing(Side.BLACK);
        if (red == null || black == null) return false;
        if (red.col != black.col) return false;

        for (int r = Math.min(red.row, black.row) + 1;
             r < Math.max(red.row, black.row); r++) {
            if (board.get(new Position(r, red.col)) != null) return false;
        }
        return true;
    }

    public static String typeCode(ChessPiece p) {
        if (p instanceof Rook) return "R";
        if (p instanceof Horse) return "H";
        if (p instanceof Elephant) return "E";
        if (p instanceof Advisor) return "A";
        if (p instanceof King) return "K";
        if (p instanceof Pawn) return "P";
        if (p instanceof Cannon) return "C";
        return "?";
    }
}
