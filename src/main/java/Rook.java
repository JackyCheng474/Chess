public class Rook extends ChessPiece {
    public Rook(Side side) {
        super(side, "车", "車");
    }

    @Override
    protected boolean movingOK(Board board, Position from, Position to) {
        if (from.row != to.row && from.col != to.col) return false;

        int dr = Integer.signum(to.row - from.row);
        int dc = Integer.signum(to.col - from.col);

        int r = from.row + dr;
        int c = from.col + dc;

        while (r != to.row || c != to.col) {
            if (board.get(new Position(r, c)) != null) return false;
            r += dr;
            c += dc;
        }
        return true;
    }
}
