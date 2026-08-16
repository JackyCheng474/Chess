package org.hdu.chess.controller;

import org.hdu.chess.ai.AiService;
import org.hdu.chess.dto.GameState;
import org.hdu.chess.dto.MoveRequest;
import org.hdu.chess.service.GameService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class GameController {

    private final GameService gameService;
    private final AiService aiService;

    public GameController(GameService gameService, AiService aiService) {
        this.gameService = gameService;
        this.aiService = aiService;
    }

    @PostMapping("/side")
    public GameState side(@RequestParam String side) {
        return gameService.side(side);
    }

    @GetMapping("/state")
    public GameState state() {
        return gameService.state();
    }

    @PostMapping("/move")
    public GameState move(@RequestBody MoveRequest request) {
        return gameService.move(
                request.from().row(), request.from().col(),
                request.to().row(), request.to().col());
    }

    @PostMapping("/regret")
    public GameState regret() {
        return gameService.regret();
    }

    @PostMapping("/ai")
    public GameState aiMove() {
        return aiService.move();
    }
}
