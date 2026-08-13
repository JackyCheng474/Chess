public abstract class ChessPiece {
    protected final Side side;
    private final String redSymbol;
    private final String blackSymbol;

    public ChessPiece(Side side, String redSymbol, String blackSymbol) {
        this.side = side;
        this.redSymbol = redSymbol;
        this.blackSymbol = blackSymbol;
    }

    public Side getSide() {
        return side;
    }

    public boolean isRed() {
        return side.isRed();
    }

    public String getSymbol() {
        return isRed() ? redSymbol : blackSymbol;
    }

    /**
     * 模板方法：统一处理边界、原地、吃己方棋子，具体走法交给子类
     */
    public boolean canMove(Board board, Position from, Position to) {
        if (!board.inside(to)) return false;
        if (from.equals(to)) return false;
        ChessPiece target = board.get(to);
        if (target != null && target.getSide() == side) return false;
        return movingOK(board, from, to);
    }

    protected abstract boolean movingOK(Board board, Position from, Position to);
}
