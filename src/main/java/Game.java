import java.util.Scanner;

public class Game {
    private Board board;
    private Side currentSide;
    private boolean gameContinue = true;
    private ConsoleView view;
    private Scanner scanner;

    public Game(Scanner scanner) {
        this.board = new Board();
        this.currentSide = Side.RED;
        this.view = new ConsoleView();
        this.scanner = scanner;
    }

    public void start() {
        while (gameContinue) {
            view.print(board, currentSide);
            System.out.println(currentSide + "回合，请输入起点坐标（行 列），例如：0 4；认输请输 -1 -1");

            int[] start = readPosition();
            if (start == null) {
                System.out.println("输入格式错误，请重新输入两个整数，如：7 4");
                continue;
            }

            int row = start[0];
            int col = start[1];

            if (row == -1 && col == -1) {
                System.out.println(currentSide.opponent() + "胜利！");
                gameContinue = false;
                break;
            }

            Position from = new Position(row, col);
            if (!board.inside(from) || board.get(from) == null) {
                System.out.println("没有选中棋子，请重新输入");
                continue;
            }

            ChessPiece piece = board.get(from);
            if (piece.getSide() != currentSide) {
                System.out.println("现在该对方行棋，请重新输入");
                continue;
            }

            System.out.println("请输入目标坐标（行 列）：");
            int[] end = readPosition();
            if (end == null) {
                System.out.println("输入格式错误，请重新输入两个整数，如：4 4");
                continue;
            }

            Position to = new Position(end[0], end[1]);
            if (!piece.canMove(board, from, to)) {
                System.out.println("不符合棋子走法，请重新输入");
                continue;
            }

            // 尝试走棋
            ChessPiece captured = board.get(to);
            board.set(to, piece);
            board.set(from, null);

            // 检查将帅照面
            if (isKingMeeting()) {
                board.set(from, piece);
                board.set(to, captured);
                System.out.println("帅与将不能见面，请重新输入");
                continue;
            }

            // 检查送将
            if (isInCheck(currentSide)) {
                board.set(from, piece);
                board.set(to, captured);
                System.out.println("该走法会使己方被将军，请重新输入");
                continue;
            }

            // 吃王判定
            if (captured instanceof King) {
                view.print(board, currentSide);
                System.out.println(piece.getSide() + "胜利！");
                gameContinue = false;
                break;
            }

            currentSide = currentSide.opponent();
        }
    }

    private boolean isInCheck(Side side) {
        Position king = board.findKing(side);
        if (king == null) return false;

        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 9; c++) {
                Position p = new Position(r, c);
                ChessPiece piece = board.get(p);
                if (piece == null || piece.getSide() == side) continue;
                if (piece.canMove(board, p, king)) return true;
            }
        }

        return isKingMeeting();
    }

    private boolean isKingMeeting() {
        Position redKing = board.findKing(Side.RED);
        Position blackKing = board.findKing(Side.BLACK);

        if (redKing == null || blackKing == null) return false;
        if (redKing.col != blackKing.col) return false;

        for (int r = Math.min(redKing.row, blackKing.row) + 1;
             r < Math.max(redKing.row, blackKing.row); r++) {
            if (board.get(new Position(r, redKing.col)) != null) return false;
        }
        return true;
    }

    private int[] readPosition() {
        if (!scanner.hasNextLine()) return null;
        String line = scanner.nextLine().trim();
        if (line.isEmpty()) return null;

        String[] parts = line.split("\\s+");
        if (parts.length != 2) return null;

        try {
            return new int[]{
                    Integer.parseInt(parts[0]),
                    Integer.parseInt(parts[1])
            };
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
