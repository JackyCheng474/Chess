import java.util.Scanner;

public class ChineseChess {
    private static final String EMPTY = "空";
    private static final String[] RED = {"车","马","相","仕","帅","仕","相","马","车"};
    private static final String[] BLACK = {"車","馬","象","士","将","士","象","馬","車"};
    private static final String[] RED_PIECES = {"车","马","相","仕","帅","兵","炮"};
    private static final String[] BLACK_PIECES = {"車","馬","象","士","将","卒","砲"};

    private boolean gameContinue = true;
    private int rounds = 1;

    public static void main(String[] args) {
        ChineseChess game = new ChineseChess();
        String[][] board = new String[10][9];
        game.init(board);
        game.print(board);
        Scanner scanner = new Scanner(System.in);
        while (game.gameContinue) {
            game.movePieces(board, scanner);
            if (game.gameContinue) {
                game.print(board);
            }
        }
        scanner.close();
    }

    // 初始化棋盘
    private void init(String[][] board) {
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 9; c++) {
                board[r][c] = EMPTY;
            }
        }
        // 黑方
        for (int i = 0; i < 9; i++) board[0][i] = BLACK[i];
        board[2][1] = "砲";
        board[2][7] = "砲";
        for (int i = 0; i < 9; i += 2) board[3][i] = "卒";
        // 红方
        for (int i = 0; i < 9; i++) board[9][i] = RED[i];
        board[7][1] = "炮";
        board[7][7] = "炮";
        for (int i = 0; i < 9; i += 2) board[6][i] = "兵";
    }
    // 负责打印棋局，每次循环刷新
    private void print(String[][] board) {
        System.out.println("    零 一 二 三 四 五 六 七 八");
        System.out.println("   -----------------------------");
        for (int r = 0; r < 10; r++) {

            System.out.print(r + " | ");
            for (int c = 0; c < 9; c++) {
                String p = board[r][c];
                boolean isRed = isRedPiece(p);
                System.out.print(piece(p, isRed));
            }
            System.out.println();
            if (r == 4) {
                System.out.println("   -----------------------------");
                System.out.println("       楚   河   汉   界");
                System.out.println("   -----------------------------");
            }

        }
    }


    // 走棋的核心方法，让棋手可以选择要走的棋子的坐标和要到的坐标，加上各种规则限制判断是否可以这样行棋
    private void movePieces(String[][] board, Scanner scanner) {
        String sideName = rounds % 2 == 1 ? "红方" : "黑方";
        System.out.println(sideName + "回合，请输入起点坐标（行 列），例如：0 4；认输请输 -1 -1");
        int[] start = readPosition(scanner);
        if (start == null) {
            System.out.println("输入格式错误，请重新输入两个整数，如：7 4");
            return;
        }
        int row = start[0];
        int col = start[1];

        if (row == -1 && col == -1) {
            System.out.println((rounds % 2 == 1 ? "黑方" : "红方") + "胜利！");
            gameContinue = false;
            return;
        }

        if (!isValidCell(row, col) || board[row][col].equals(EMPTY)) {
            System.out.println("没有选中棋子，请重新输入");
            return;
        }
        if (!isCurrentRoundPiece(board, row, col, rounds)) {
            System.out.println("现在该对方行棋，请重新输入");
            return;
        }

        System.out.println("请输入目标坐标（行 列）：");
        int[] end = readPosition(scanner);
        if (end == null) {
            System.out.println("输入格式错误，请重新输入两个整数，如：4 4");
            return;
        }
        int nextRow = end[0];
        int nextCol = end[1];

        if (!isValidCell(nextRow, nextCol)) {
            System.out.println("目标超出棋盘，请重新输入");
            return;
        }
        if (isTeammate(board, nextRow, nextCol, rounds)) {
            System.out.println("不能吃自己的棋子，请重新输入");
            return;
        }
        if (!canMove(board, row, col, nextRow, nextCol)) {
            System.out.println("不符合棋子走法，请重新输入");
            return;
        }

        String movingPiece = board[row][col];
        String targetPiece = board[nextRow][nextCol];
        board[row][col] = EMPTY;
        board[nextRow][nextCol] = movingPiece;

        if (targetPiece.equals("帅")) {
            System.out.println("黑方胜利！");
            gameContinue = false;
            return;
        }
        if (targetPiece.equals("将")) {
            System.out.println("红方胜利！");
            gameContinue = false;
            return;
        }

        if (isKingMeeting(board)) {
            board[row][col] = movingPiece;
            board[nextRow][nextCol] = targetPiece;
            System.out.println("帅与将不能见面，请重新输入");
            return;
        }

        String side = rounds % 2 == 1 ? "RED" : "BLACK";
        if (isInCheck(board, side)) {
            board[row][col] = movingPiece;
            board[nextRow][nextCol] = targetPiece;
            System.out.println("该走法会使己方被将军，请重新输入");
            return;
        }

        rounds++;
    }

    // 输入坐标
    private int[] readPosition(Scanner scanner) {
        if (!scanner.hasNextLine()) return null;
        String line = scanner.nextLine().trim();
        if (line.isEmpty()) return null;
        String[] parts = line.split("\\s+");
        if (parts.length != 2) return null;
        try {
            int row = Integer.parseInt(parts[0]);
            int col = Integer.parseInt(parts[1]);
            return new int[]{row, col};
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // 第一层限制，棋子不能超出棋盘
    private boolean isValidCell(int row, int col) {
        return row >= 0 && row < 10 && col >= 0 && col < 9;
    }

    // 第二层限制，只有自己的回合才能出子
    private boolean isCurrentRoundPiece(String[][] board, int row, int col, int rounds) {
        boolean redTurn = rounds % 2 == 1;
        return isRedPiece(board[row][col]) == redTurn;
    }

    // 第三层限制，无法吃自己的棋子
    private boolean isTeammate(String[][] board, int row, int col, int rounds) {
        if (board[row][col].equals(EMPTY)) return false;
        boolean redTurn = rounds % 2 == 1;
        return isRedPiece(board[row][col]) == redTurn;
    }

    // 判断是否是红棋
    private boolean isRedPiece(String piece) {
        for (String p : RED_PIECES) {
            if (p.equals(piece)) return true;
        }
        return false;
    }

    // 第四层限制，每个棋子都有自己的行棋规则
    private boolean canMove(String[][] board, int row, int col, int nextRow, int nextCol) {
        if (row == nextRow && col == nextCol) return false;
        String piece = board[row][col];
        switch (piece) {
            case "车": case "車": return canMoveRook(board, row, col, nextRow, nextCol);
            case "炮": case "砲": return canMoveCannon(board, row, col, nextRow, nextCol);
            case "马": case "馬": return canMoveHorse(board, row, col, nextRow, nextCol);
            case "相": case "象": return canMoveElephant(board, row, col, nextRow, nextCol);
            case "仕": case "士": return canMoveAdvisor(board, row, col, nextRow, nextCol);
            case "帅": case "将": return canMoveKing(board, row, col, nextRow, nextCol);
            case "兵": case "卒": return canMovePawn(board, row, col, nextRow, nextCol);
            default: return false;
        }
    }

    // 车的规则
    private boolean canMoveRook(String[][] board, int row, int col, int nextRow, int nextCol) {
        if (row != nextRow && col != nextCol) return false;
        if (row == nextRow) {
            int step = Integer.compare(nextCol, col);
            for (int c = col + step; c != nextCol; c += step) {
                if (!board[row][c].equals(EMPTY)) return false;
            }
        } else {
            int step = Integer.compare(nextRow, row);
            for (int r = row + step; r != nextRow; r += step) {
                if (!board[r][col].equals(EMPTY)) return false;
            }
        }
        return true;
    }

    // 炮的规则
    private boolean canMoveCannon(String[][] board, int row, int col, int nextRow, int nextCol) {
        if (row != nextRow && col != nextCol) return false;
        int count = 0;
        if (row == nextRow) {
            int step = Integer.compare(nextCol, col);
            for (int c = col + step; c != nextCol; c += step) {
                if (!board[row][c].equals(EMPTY)) count++;
            }
        } else {
            int step = Integer.compare(nextRow, row);
            for (int r = row + step; r != nextRow; r += step) {
                if (!board[r][col].equals(EMPTY)) count++;
            }
        }
        if (count == 0) {
            return board[nextRow][nextCol].equals(EMPTY);
        } else if (count == 1) {
            return !board[nextRow][nextCol].equals(EMPTY);
        }
        return false;
    }

    // 马的规则
    private boolean canMoveHorse(String[][] board, int row, int col, int nextRow, int nextCol) {
        int dr = nextRow - row;
        int dc = nextCol - col;
        if (Math.abs(dr) == 1 && Math.abs(dc) == 2) {
            int legCol = col + (dc > 0 ? 1 : -1);
            return board[row][legCol].equals(EMPTY);
        } else if (Math.abs(dr) == 2 && Math.abs(dc) == 1) {
            int legRow = row + (dr > 0 ? 1 : -1);
            return board[legRow][col].equals(EMPTY);
        }
        return false;
    }

    // 象的规则
    private boolean canMoveElephant(String[][] board, int row, int col, int nextRow, int nextCol) {
        if (Math.abs(nextRow - row) != 2 || Math.abs(nextCol - col) != 2) return false;
        String piece = board[row][col];
        if (piece.equals("相") && nextRow < 5) return false;
        if (piece.equals("象") && nextRow > 4) return false;
        int midRow = (row + nextRow) / 2;
        int midCol = (col + nextCol) / 2;
        return board[midRow][midCol].equals(EMPTY);
    }

    // 仕的规则
    private boolean canMoveAdvisor(String[][] board, int row, int col, int nextRow, int nextCol) {
        if (Math.abs(nextRow - row) != 1 || Math.abs(nextCol - col) != 1) return false;
        String piece = board[row][col];
        if (piece.equals("仕")) {
            return nextRow >= 7 && nextRow <= 9 && nextCol >= 3 && nextCol <= 5;
        } else {
            return nextRow >= 0 && nextRow <= 2 && nextCol >= 3 && nextCol <= 5;
        }
    }

    // 将的规则
    private boolean canMoveKing(String[][] board, int row, int col, int nextRow, int nextCol) {
        if (Math.abs(nextRow - row) + Math.abs(nextCol - col) != 1) return false;
        String piece = board[row][col];
        if (piece.equals("帅")) {
            return nextRow >= 7 && nextRow <= 9 && nextCol >= 3 && nextCol <= 5;
        } else {
            return nextRow >= 0 && nextRow <= 2 && nextCol >= 3 && nextCol <= 5;
        }
    }

    // 兵的规则
    private boolean canMovePawn(String[][] board, int row, int col, int nextRow, int nextCol) {
        String piece = board[row][col];
        if (piece.equals("兵")) {
            if (row >= 5) {
                return nextCol == col && nextRow == row - 1;
            } else {
                return (nextCol == col && nextRow == row - 1) ||
                        (nextRow == row && Math.abs(nextCol - col) == 1);
            }
        } else { // "卒"
            if (row <= 4) {
                return nextCol == col && nextRow == row + 1;
            } else {
                return (nextCol == col && nextRow == row + 1) ||
                        (nextRow == row && Math.abs(nextCol - col) == 1);
            }
        }
    }

    // 第五层限制，将帅无法对面
    private boolean isKingMeeting(String[][] board) {
        int jr = -1, jc = -1, sr = -1, sc = -1;
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 9; c++) {
                if (board[r][c].equals("将")) {
                    jr = r; jc = c;
                }
                if (board[r][c].equals("帅")) {
                    sr = r; sc = c;
                }
            }
        }
        if (jr == -1 || sr == -1) return false;
        if (jc != sc) return false;
        for (int r = Math.min(jr, sr) + 1; r < Math.max(jr, sr); r++) {
            if (!board[r][jc].equals(EMPTY)) return false;
        }
        return true;
    }

    // 判断这样行棋是否会被将军
    private boolean isInCheck(String[][] board, String side) {
        String king = side.equals("RED") ? "帅" : "将";
        int kingRow = -1, kingCol = -1;
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 9; c++) {
                if (board[r][c].equals(king)) {
                    kingRow = r; kingCol = c;
                    break;
                }
            }
            if (kingRow != -1) break;
        }
        if (kingRow == -1) return false;

        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 9; c++) {
                String p = board[r][c];
                if (p.equals(EMPTY)) continue;
                boolean pieceIsRed = isRedPiece(p);
                boolean isEnemy = side.equals("RED") ? !pieceIsRed : pieceIsRed;
                if (!isEnemy) continue;
                if (canMove(board, r, c, kingRow, kingCol)) {
                    return true;
                }
            }
        }
        return isKingMeeting(board);
    }

    //为棋子上色
    static String piece(String name, boolean isRed) {
        if (name == null || name.equals("空")) return Color.GRAY + "空 " + Color.RESET;
        String color = isRed ? Color.RED : Color.YELLOW;
        return color + name + Color.RESET + " ";
    }

}