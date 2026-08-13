public class Cannon extends ChessPiece {
    public Cannon(Side side) {
        super(side, "炮", "砲");
    }

    @Override
    protected boolean movingOK(Board board, Position from, Position to) {
        if (from.row != to.row && from.col != to.col) return false;

        int dr = Integer.signum(to.row - from.row);
        int dc = Integer.signum(to.col - from.col);

        int barrier = 0;
        int r = from.row + dr;
        int c = from.col + dc;

        while (r != to.row || c != to.col) {
            if (board.get(new Position(r, c)) != null) barrier++;
            r += dr;
            c += dc;
        }

        ChessPiece target = board.get(to);

        if (barrier == 0) return target == null;
        if (barrier == 1) return target != null;
        return false;
    }
}
