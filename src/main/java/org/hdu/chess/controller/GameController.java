package org.hdu.chess.controller;

import org.hdu.chess.dto.GameState;
import org.hdu.chess.dto.MoveRequest;
import org.hdu.chess.service.GameService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/new")
    public GameState newGame() {
        return gameService.newGame();
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
}
