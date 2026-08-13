public class Game {
    private Board board;
    private Side currentSide;
    private boolean gameOver;
    private String winner;
    private String message;

    public Game() {
        newGame();
    }

    public synchronized String newGame() {
        board = new Board();
        currentSide = Side.RED;
        gameOver = false;
        winner = null;
        message = "新游戏开始，红方先手";
        return toJson();
    }

    public synchronized String move(int fromRow, int fromCol,
                                    int toRow, int toCol) {
        message = "";
        if (gameOver) {
            message = "游戏已结束，请点击重新开始";
            return toJson();
        }

        Position from = new Position(fromRow, fromCol);
        Position to = new Position(toRow, toCol);

        if (!board.inside(from) || board.get(from) == null) {
            message = "没有选中棋子";
            return toJson();
        }

        ChessPiece piece = board.get(from);
        if (piece.getSide() != currentSide) {
            message = "现在该对方行棋";
            return toJson();
        }

        if (!piece.canMove(board, from, to)) {
            message = "不符合棋子走法";
            return toJson();
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
            return toJson();
        }

        message = isInCheck(piece.getSide());
        // 吃帅/将 => 胜利
        if (captured instanceof King) {
            gameOver = true;
            winner = currentSide == Side.RED ? "红方" : "黑方";
            message = winner + "胜利！";
            return toJson();
        }

        currentSide = currentSide.opponent();
        message = (currentSide == Side.RED ? "红方" : "黑方") + "回合";
        return toJson();
    }

    private String isInCheck(Side side) {
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
        return "";
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

    public synchronized String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"board\":[");

        for (int r = 0; r < 10; r++) {
            if (r > 0) sb.append(",");
            sb.append("[");
            for (int c = 0; c < 9; c++) {
                if (c > 0) sb.append(",");
                ChessPiece p = board.get(new Position(r, c));
                if (p == null) {
                    sb.append("null");
                } else {
                    sb.append("{\"side\":\"")
                            .append(p.getSide().name().toLowerCase())
                            .append("\",\"type\":\"")
                            .append(typeCode(p))
                            .append("\"}");
                }
            }
            sb.append("]");
        }

        sb.append("],");
        sb.append("\"currentSide\":\"")
                .append(currentSide.name().toLowerCase()).append("\",");
        sb.append("\"gameOver\":").append(gameOver).append(",");
        sb.append("\"winner\":")
                .append(winner == null ? "null" : "\"" + winner + "\"").append(",");
        sb.append("\"message\":\"")
                .append(message == null ? "" : message).append("\"");
        sb.append("}");
        return sb.toString();
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