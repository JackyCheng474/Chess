public class Horse extends ChessPiece {
    public Horse(Side side) {
        super(side, "马", "馬");
    }

    @Override
    protected boolean movingOK(Board board, Position from, Position to) {
        int dr = to.row - from.row;
        int dc = to.col - from.col;

        if (Math.abs(dr) == 1 && Math.abs(dc) == 2) {
            int legCol = from.col + (dc > 0 ? 1 : -1);
            return board.get(new Position(from.row, legCol)) == null;
        }

        if (Math.abs(dr) == 2 && Math.abs(dc) == 1) {
            int legRow = from.row + (dr > 0 ? 1 : -1);
            return board.get(new Position(legRow, from.col)) == null;
        }

        return false;
    }
}
