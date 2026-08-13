public class ConsoleView {
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String YELLOW = "\u001B[93m";

    public void print(Board board, Side currentSide) {
        System.out.println("    零 一 二 三 四 五 六 七 八");
        System.out.println("   -----------------------------");

        for (int r = 0; r < 10; r++) {

            System.out.print(r + " | ");
            for (int c = 0; c < 9; c++) {
                ChessPiece piece = board.getPieceAt(r, c);
                if (piece == null) {
                    System.out.print("\u001B[90m"+"空 "+RESET);
                } else {
                    String color = piece.isRed() ? RED : YELLOW;
                    System.out.print(color + piece.getSymbol() + RESET + " ");
                }
            }
            System.out.println();

            if (r == 4) {
                System.out.println("   -----------------------------");
                System.out.println("       楚   河   汉   界");
                System.out.println("   -----------------------------");
            }
        }
    }
}
