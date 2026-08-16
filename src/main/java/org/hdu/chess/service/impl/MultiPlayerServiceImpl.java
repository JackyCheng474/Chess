package org.hdu.chess.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.hdu.chess.dto.GameState;
import org.hdu.chess.dto.MoveRequest;
import org.hdu.chess.dto.PieceDto;
import org.hdu.chess.dto.RoomInfo;
import org.hdu.chess.service.GameService;
import org.hdu.chess.service.MultiPlayerService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import jakarta.servlet.http.HttpSession;

/**
 * 房间层：每个房间一个独立的 GameService（一盘棋）。
 * 身份（HttpSession）在这里消费完就停，绝不往下传——引擎不知道 HTTP 的存在。
 */
@Service
public class MultiPlayerServiceImpl implements MultiPlayerService {

    /** 房号 → 该房间的一盘棋 */
    private final Map<String, GameService> rooms = new ConcurrentHashMap<>();
    /** 房号 → 黑方玩家的 session id（null 表示黑方还没人，房间未满） */
    private final Map<String, String> blackPlayers = new ConcurrentHashMap<>();
    /** 房号 → 订阅了这个房间的 SSE 连接（谁在等通知） */
    private final Map<String, List<SseEmitter>> roomEmitters = new ConcurrentHashMap<>();

    @Override
    public GameState create(String room, HttpSession session) {
        if (rooms.containsKey(room)) {
            return error("房间已存在，换一个房号");
        }
        // new GameService() 的构造器已经 newGame() —— 就是全新的一盘。
        // 注意：绝不能调 side()，那是 AI 模式的语义（会重置棋盘并设 aiSide）。
        GameService game = new GameService();
        rooms.put(room, game);
        session.setAttribute("room", room);
        session.setAttribute("side", "red");    // 先到先得：创建者 = 红方
        return game.state();
    }

    @Override
    public GameState join(String room, HttpSession session) {
        GameService game = rooms.get(room);
        if (game == null) {
            return error("房间不存在");
        }
        if (session.getAttribute("side") != null) {
            return error("你已经在一个房间里了");
        }
        if (blackPlayers.containsKey(room)) {
            return error("房间已满，只能两个人下");
        }
        blackPlayers.put(room, session.getId());
        session.setAttribute("room", room);
        session.setAttribute("side", "black");  // 后加入者 = 黑方
        // 有人加入 = 房间状态变化，通知等在房间里的红方刷新（"等待对手" → 开下）
        notifyRoom(room);
        return game.state();
    }

    @Override
    public GameState move(String room, MoveRequest request, HttpSession session) {
        GameService game = rooms.get(room);
        if (game == null) {
            return error("房间不存在");
        }
        // 身份校验第一关：这个请求的玩家必须属于这个房间
        if (!room.equals(session.getAttribute("room"))) {
            return error("你不是这个房间的玩家");
        }
        // 身份校验第二关：身份 == 回合。
        // 引擎保证"当前回合只能走自己的子"，所以"该走的人 == 请求者"就锁死了身份。
        String mySide = (String) session.getAttribute("side");
        if (mySide == null
                || !mySide.equalsIgnoreCase(game.getCurrentSide().name())) {
            return error("还没轮到你行棋");
        }
        // 先落子，再通知——否则订阅者拉到的是旧棋盘（竞态）
        GameState result = game.move(request.from().row(), request.from().col(),
                request.to().row(), request.to().col());
        notifyRoom(room);
        return result;
    }

    @Override
    public SseEmitter subscribe(String room) {
        SseEmitter emitter = new SseEmitter(0L);   // 0 = 不超时，连接一直挂着
        roomEmitters.computeIfAbsent(room, k -> new CopyOnWriteArrayList<>()).add(emitter);
        // 客户端断开 / 超时时，把死连接从注册表移除（防内存泄漏）
        emitter.onCompletion(() -> removeEmitter(room, emitter));
        emitter.onTimeout(() -> removeEmitter(room, emitter));
        return emitter;
    }

    @Override
    public RoomInfo info(String room) {
        GameService game = rooms.get(room);
        if (game == null) {
            return new RoomInfo(room, 0);
        }
        return new RoomInfo(room, blackPlayers.containsKey(room) ? 2 : 1);
    }

    @Override
    public GameState state(String room) {
        GameService game = rooms.get(room);
        if (game == null) {
            return error("房间不存在");
        }
        return game.state();
    }

    /** 通知某个房间的所有订阅者："有更新，来拉 state" */
    private void notifyRoom(String room) {
        List<SseEmitter> emitters = roomEmitters.get(room);
        if (emitters == null) return;
        for (SseEmitter e : emitters) {
            try {
                e.send(SseEmitter.event().data("update"));
            } catch (IOException ex) {
                removeEmitter(room, e);   // 断线的静默移除，绝不拖垮整步棋
            }
        }
    }

    private void removeEmitter(String room, SseEmitter emitter) {
        List<SseEmitter> emitters = roomEmitters.get(room);
        if (emitters != null) {
            emitters.remove(emitter);
        }
    }

    /** 项目风格：错误走 message 字段，不抛异常 */
    private static GameState error(String message) {
        List<List<PieceDto>> grid = new ArrayList<>();
        for (int r = 0; r < 10; r++) {
            List<PieceDto> row = new ArrayList<>();
            for (int c = 0; c < 9; c++) {
                row.add(null);
            }
            grid.add(row);
        }
        return new GameState(grid, "red", false, null, message, null);
    }
}
