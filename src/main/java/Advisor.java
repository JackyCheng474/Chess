public class Advisor extends ChessPiece {
    public Advisor(Side side) {
        super(side, "仕", "士");
    }

    @Override
    protected boolean movingOK(Board board, Position from, Position to) {
        if (Math.abs(to.row - from.row) != 1 || Math.abs(to.col - from.col) != 1) return false;

        if (isRed()) {
            return to.row >= 7 && to.row <= 9 && to.col >= 3 && to.col <= 5;
        } else {
            return to.row >= 0 && to.row <= 2 && to.col >= 3 && to.col <= 5;
        }
    }
}
