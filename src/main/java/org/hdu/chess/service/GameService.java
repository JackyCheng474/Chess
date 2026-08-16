package org.hdu.chess.service;

import java.util.ArrayList;
import java.util.List;

import org.hdu.chess.dto.GameState;
import org.hdu.chess.dto.MoveRecord;
import org.hdu.chess.model.Board;
import org.hdu.chess.model.ChessPiece;
import org.hdu.chess.model.Position;
import org.hdu.chess.model.Side;
import org.hdu.chess.model.piece.King;
import org.hdu.chess.tool.StateAndRule;
import org.springframework.stereotype.Service;

@Service
public class GameService {
    private Board board;
    private Side currentSide;
    private boolean gameOver;
    private String winner;
    private String message;
    private Side aiSide;                          // null = 未选边
    private final List<MoveRecord> path = new ArrayList<>();

    public GameService() {
        newGame();
    }

    private void newGame() {
        board = new Board();
        currentSide = Side.RED;
        gameOver = false;
        winner = null;
        message = "新游戏开始，红方先手";
        path.clear();
    }

    // 玩家选边：side = 玩家执哪边
    public synchronized GameState side(String playerSide) {
        newGame();
        boolean playerIsRed = "red".equals(playerSide);
        aiSide = playerIsRed ? Side.BLACK : Side.RED;
        message = "你执" + (playerIsRed ? "红方" : "黑方") + "，游戏开始";
        return state();
    }

    public synchronized GameState state() {
        return StateAndRule.toState(board, currentSide.name(), gameOver, winner, message,
                aiSide == null ? null : aiSide.name().toLowerCase());
    }

    // ---- AI 专用只读接口 ----

    public synchronized boolean isAiTurn() {
        return aiSide != null && !gameOver && currentSide == aiSide;
    }

    public synchronized Side getAiSide() {
        return aiSide;
    }

    public synchronized Board getBoard() {
        return board;
    }

    // 写行棋的各种限制
    public synchronized Side getCurrentSide() {
        return currentSide;
    }

    public synchronized GameState move(int fromRow, int fromCol,
                                       int toRow, int toCol) {
        message = "";

        // 如果游戏结束了，返回游戏状态并提示游戏结束
        if (gameOver) {
            message = "游戏已结束，请点击重新开始";
            return state();
        }

        Position from = new Position(fromRow, fromCol);
        Position to = new Position(toRow, toCol);

        // 选中的棋子在盘外或者棋子不存在则报没选中棋子
        if (!board.inside(from) || board.get(from) == null) {
            message = "没有选中棋子";
            return state();
        }

        // 控制每回合谁走
        ChessPiece piece = board.get(from);
        if (piece.getSide() != currentSide) {
            message = "现在该对方行棋";
            return state();
        }

        // 行棋必须满足棋子的规则
        if (!piece.canMove(board, from, to)) {
            message = "不符合棋子走法";
            return state();
        }

        ChessPiece captured = board.get(to);

        // 模拟走子
        board.set(to, piece);
        board.set(from, null);

        // 非法：送将 / 照面
        if (StateAndRule.isKingMeeting(board)) {
            board.set(from, piece);
            board.set(to, captured);
            message = "不能将帅照面";
            return state();
        }

        // 吃帅/将 => 胜利（这一步也记录进 path，悔棋才能退到最后一手）
        if (captured instanceof King) {
            path.add(new MoveRecord(fromRow, fromCol, toRow, toCol,
                    piece, captured, piece.getSide()));
            gameOver = true;
            winner = currentSide == Side.RED ? "红方" : "黑方";
            message = winner + "胜利！";
            return state();
        }

        currentSide = currentSide.opponent();
        message = (currentSide == Side.RED ? "红方" : "黑方") + "回合";
        message = StateAndRule.isInCheck(board, piece.getSide(), message);
        path.add(new MoveRecord(fromRow, fromCol, toRow, toCol,
                piece, captured, piece.getSide()));
        return state();
    }

    // 悔棋：回退上一步（AI 对局中连 AI 的回应一起退回，回到"玩家走之前"）
    public synchronized GameState regret() {
        if (path.isEmpty()) {
            message = "没有可以悔的棋";
            return state();
        }

        MoveRecord last = path.remove(path.size() - 1);
        undo(last);
        currentSide = last.movedSide();

        // AI 对局：悔棋连 AI 的回应一起退回（回到"玩家走之前"，轮到玩家）
        if (aiSide != null && last.movedSide() == aiSide && !path.isEmpty()) {
            MoveRecord prev = path.remove(path.size() - 1);
            undo(prev);
            currentSide = prev.movedSide();
        }

        gameOver = false;
        winner = null;
        message = "已悔棋，" + (currentSide == Side.RED ? "红方" : "黑方") + "回合";
        return state();
    }

    private void undo(MoveRecord record) {
        board.set(new Position(record.fromRow(), record.fromCol()), record.moved());
        board.set(new Position(record.toRow(), record.toCol()), record.captured());
    }
}
