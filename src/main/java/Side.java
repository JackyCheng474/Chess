public enum Side {
    RED, BLACK;

    public Side opponent() {
        return this == RED ? BLACK : RED;
    }

    public boolean isRed() {
        return this == RED;
    }
}
