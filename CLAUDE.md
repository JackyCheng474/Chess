# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

A Chinese chess (Xiangqi, 中国象棋) game with a Spring Boot backend and a single-file vanilla HTML/JS/SVG frontend served by the backend itself (same-origin, no CORS needed). Java 17, Maven. Supports human-vs-human and human-vs-AI (currently an α-β search AI at depth 4, ~1s/move; next: move ordering for deeper search).

## Commands

```bash
mvn clean compile     # compile — use clean: stale classes in target/ break component scanning
mvn spring-boot:run   # run on http://localhost:8080
```

- Frontend: open `http://localhost:8080` — a side-selection overlay appears first (choose 红方/黑方). After the human moves, the frontend automatically calls `/api/ai` whenever it is the AI's turn (`state.currentSide === state.aiSide`), showing "AI 思考中…".
- No test framework configured. Files in `src/test/java/` are scratch files with `main` methods in the default package, not unit tests; `mvn test` will not run them.

## Architecture

Package layout under `org.hdu.chess`, one-way dependency: `controller → service → ai → model`, plus `dto` and stateless `tool`:

```
org.hdu.chess
├── ChineseChessApplication.java    Spring Boot entry point only
├── controller/GameController.java  HTTP layer: @RestController, returns DTOs (Jackson serializes)
├── service/GameService.java        THE single game state owner (@Service singleton): board, turn, win/loss, aiSide, move history
├── ai/
│   ├── AiService.java              interface
│   ├── Impl/AiServiceImpl.java     @Service; injects the same GameService; guards turn, generates moves, applies via GameService.move()
│   ├── MoveGenerator.java          generates all legal moves for a side (piece rules + king-meeting check on a board copy)
│   └── AlphaBetaSearch.java        recursive α-β search: bestMove(board, side, depth); pure (simulates on board.copy()); WIN on king capture; same-score moves picked randomly
├── tool/Evaluator.java             stateless evaluator: evaluate(Board, Side) = material difference (α-β leaves); execute(Board, Position) = single-square piece value
├── model/                          pure chess domain: Board, ChessPiece, Position, Side — no Spring/HTTP knowledge
│   └── piece/                      the 7 piece classes + ChessPiece base
├── dto/                            MoveRequest, MoveRecord, GameState + PieceDto
└── tool/StateAndRule.java          STATELESS rules/JSON helpers (methods take Board as a parameter)
```

### State ownership (critical)

`GameService` is the single owner of game state (board/currentSide/gameOver/winner/message/aiSide/path). Do NOT create `new GameService()` or hold another `Board` anywhere — the AI service injects the same `GameService` bean and reads/writes through it. `StateAndRule` is stateless: every method receives the `Board` it should operate on.

### Core engine (rules)

- `service.GameService` — turn management (red first), move validation, win/loss, regret. `move()` applies a simulate-then-revert pattern with the flying-general rule (`isKingMeeting`), reverts if illegal. Victory = capturing the opponent's King; no checkmate detection, only "将军"/"绝杀" status messages (Chinese). Regret in AI mode also undoes the AI's response, returning to before the human's move.
- `model.Board` — 10×9 grid; row 0 top = black, row 9 bottom = red. Has `copy()` for AI simulation.
- `model.ChessPiece` — template method: `canMove()` handles shared validation and delegates to `movingOK()`. Every piece also implements `movingGenerator(Board, Position)` returning rule-legal destination cells (uses `canMove`; does NOT check 将帅照面 — that is MoveGenerator's job).
- Piece subclasses implement standard Xiangqi rules: Rook, Horse (leg-block), Elephant (eye-block, no river crossing), Advisor/King (palace), Pawn (sideways after river), Cannon (exactly one screen piece to capture).
- `ai.MoveGenerator` — `generateLegalMoves(Board, Side)`; filters each candidate by simulating on a `board.copy()` then `!StateAndRule.isKingMeeting(copy)` (capturing the enemy King is legal). `ai.AlphaBetaSearch` — `bestMove(board, side, depth)`: recursive minimax with α/β pruning; maximizing node = AI side, minimizing = opponent; prune when `alpha >= beta`; king capture returns ±WIN; leaves scored by `Evaluator.evaluate` (material difference). `ChessPiece.value` is the immutable piece value (车9 马4 炮4.5 相/仕2 兵1 将1000).

### HTTP API

- `POST /api/side?side=red|black` — (re)start game and set player's side; AI gets the other side
- `GET /api/state` — current state
- `POST /api/move` — body: `{"from":{"row":r,"col":c},"to":{"row":r,"col":c}}`
- `POST /api/ai` — AI moves if it is the AI's turn (guarded server-side); otherwise returns state unchanged
- `POST /api/regret` — undo last move (with AI: undoes AI's response too)

Rule violations are reported via the `message` field, not HTTP status codes.

### JSON contract

Jackson serializes `dto.GameState`: `board` is a 10×9 array of `{"side":"red|black","type":"R|H|E|A|K|P|C"}` or `null`, plus `currentSide`, `gameOver`, `winner`, `message`, `aiSide` (`"red"|"black"|null`). The frontend renders purely from this — keep field names in sync when changing either side.

### Notes

- `mvn compile` without `clean` leaves stale `.class` files in `target/`; after moving/renaming classes this causes `ConflictingBeanDefinitionException` at startup — always `mvn clean` when packages/class names change.
- Remember: `@Service` goes on the implementation class, never on the interface.
- Static resources are served from `target/classes/static` — after editing `index.html`, rebuild (`mvn clean compile`) and restart to see changes.
