package org.hdu.chess.controller;

import org.hdu.chess.dto.GameState;
import org.hdu.chess.dto.MoveRequest;
import org.hdu.chess.dto.RoomInfo;
import org.hdu.chess.service.MultiPlayerService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/multi")
public class MultiPlayerController {

    private final MultiPlayerService multiPlayerService;

    public MultiPlayerController(MultiPlayerService multiPlayerService) {
        this.multiPlayerService = multiPlayerService;
    }

    /** 开房：创建者自动成为红方 */
    @PostMapping("/create")
    public GameState create(@RequestParam String room, HttpSession session) {
        return multiPlayerService.create(normalize(room), session);
    }

    /** 加入：成为黑方 */
    @PostMapping("/join")
    public GameState join(@RequestParam String room, HttpSession session) {
        return multiPlayerService.join(normalize(room), session);
    }

    /** 走棋：身份藏在 Cookie 里，服务端自动校验 */
    @PostMapping("/move")
    public GameState move(@RequestParam String room,
                          @RequestBody MoveRequest request, HttpSession session) {
        return multiPlayerService.move(normalize(room), request, session);
    }

    /** 拉取局面（前端轮询就靠它） */
    @GetMapping("/state")
    public GameState state(@RequestParam String room) {
        return multiPlayerService.state(normalize(room));
    }

    /** 房间人数：0 不存在 / 1 等对手 / 2 已满 */
    @GetMapping("/info")
    public RoomInfo info(@RequestParam String room) {
        return multiPlayerService.info(normalize(room));
    }

    /** SSE 订阅：返回一条一直挂着、随时可被服务器写入的长连接 */
    @GetMapping("/events")
    public SseEmitter events(@RequestParam String room) {
        return multiPlayerService.subscribe(normalize(room));
    }

    /** 房号规范化：去首尾空格 + 转小写（大小写/空格不同就不算同一个房间） */
    private static String normalize(String room) {
        return room.trim().toLowerCase();
    }
}
