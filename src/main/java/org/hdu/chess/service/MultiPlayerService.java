package org.hdu.chess.service;

import org.hdu.chess.dto.GameState;
import org.hdu.chess.dto.MoveRequest;
import org.hdu.chess.dto.RoomInfo;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import jakarta.servlet.http.HttpSession;

public interface MultiPlayerService {

    GameState create(String room, HttpSession session);

    GameState join(String room, HttpSession session);

    GameState move(String room, MoveRequest request, HttpSession session);

    GameState state(String room);

    /** 房间人数：0 不存在 / 1 等对手 / 2 已满 */
    RoomInfo info(String room);

    /** SSE 订阅：注册一个 emitter 到房间，返回给前端挂着 */
    SseEmitter subscribe(String room);
}
