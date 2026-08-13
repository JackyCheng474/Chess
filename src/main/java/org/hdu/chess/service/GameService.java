package org.hdu.chess.service;

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
import org.springframework.stereotype.Service;

@Service
public class GameService {
    private Board board;
    private Side currentSide;
    private boolean gameOver;
    private String winner;
    private String message;

    public GameService() {
        newGame();
    }

    // 重新开一把
    public synchronized GameState newGame() {
        board = new Board();
        currentSide = Side.RED;
        gameOver = false;
        winner = null;
        message = "新游戏开始，红方先手";
        return toState();
    }


    public synchronized GameState state() {
        return toState();
    }

    // 写行棋的各种限制
    public synchronized GameState move(int fromRow, int fromCol,
                                       int toRow, int toCol) {
        message = "";
        // 如果游戏结束了，返回游戏状态并提示游戏结束
        if (gameOver) {
            message = "游戏已结束，请点击重新开始";
            return toState();
        }

        Position from = new Position(fromRow, fromCol);
        Position to = new Position(toRow, toCol);

        // 选中的棋子在盘外或者棋子不存在则报没选中棋子
        if (!board.inside(from) || board.get(from) == null) {
            message = "没有选中棋子";
            return toState();
        }

        // 控制每回合谁走
        ChessPiece piece = board.get(from);
        if (piece.getSide() != currentSide) {
            message = "现在该对方行棋";
            return toState();
        }

        // 行棋必须满足棋子的规则
        if (!piece.canMove(board, from, to)) {
            message = "不符合棋子走法";
            return toState();
        }

        ChessPiece captured = board.get(to);

        // 模拟走子
        board.set(to, piece);
        board.set(from, null);

        // 非法：送将 / 照面
        if (isKingMeeting()) {
            board.set(from, piece);
            board.set(to, captured);
            message = "不能将帅照面";
            return toState();
        }


        // 吃帅/将 => 胜利
        if (captured instanceof King) {
            gameOver = true;
            winner = currentSide == Side.RED ? "红方" : "黑方";
            message = winner + "胜利！";
            return toState();
        }

        currentSide = currentSide.opponent();
        message = (currentSide == Side.RED ? "红方" : "黑方") + "回合";
        message = isInCheck(piece.getSide(),message);
        return toState();
    }

    private String isInCheck(Side side,String message) {
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

    private boolean isKingMeeting() {
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

    private GameState toState() {
        List<List<PieceDto>> grid = new ArrayList<>();
        for (int r = 0; r < 10; r++) {
            List<PieceDto> row = new ArrayList<>();
            for (int c = 0; c < 9; c++) {
                ChessPiece piece = board.get(new Position(r, c));
                row.add(piece == null ? null
                        : new PieceDto(piece.getSide().name().toLowerCase(),
                                        typeCode(piece)));
            }
            grid.add(row);
        }
        return new GameState(grid, currentSide.name().toLowerCase(),
                gameOver, winner, message);
    }

    private static String typeCode(ChessPiece p) {
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
