package org.hdu.chess.model;

import org.hdu.chess.model.piece.*;


public class Board {
    private ChessPiece[][] grid = new ChessPiece[10][9];

    public Board() {
        init();
    }

    public ChessPiece get(Position p) {
        if (!inside(p)) return null;
        return grid[p.row][p.col];
    }

    public void set(Position p, ChessPiece piece) {
        grid[p.row][p.col] = piece;
    }

    public ChessPiece getPieceAt(int row, int col) {
        return grid[row][col];
    }

    public boolean inside(Position p) {
        return p.row >= 0 && p.row < 10 && p.col >= 0 && p.col < 9;
    }

    public boolean isEmpty(Position p) {
        return get(p) == null;
    }

    public Position findKing(Side side) {
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 9; c++) {
                ChessPiece p = grid[r][c];
                if (p instanceof King && p.getSide() == side) {
                    return new Position(r, c);
                }
            }
        }
        return null;
    }

    private void set(int row, int col, ChessPiece piece) {
        grid[row][col] = piece;
    }

    private void init() {
        // 黑方
        set(0, 0, new Rook(Side.BLACK));
        set(0, 1, new Horse(Side.BLACK));
        set(0, 2, new Elephant(Side.BLACK));
        set(0, 3, new Advisor(Side.BLACK));
        set(0, 4, new King(Side.BLACK));
        set(0, 5, new Advisor(Side.BLACK));
        set(0, 6, new Elephant(Side.BLACK));
        set(0, 7, new Horse(Side.BLACK));
        set(0, 8, new Rook(Side.BLACK));

        set(2, 1, new Cannon(Side.BLACK));
        set(2, 7, new Cannon(Side.BLACK));

        for (int c = 0; c < 9; c += 2) {
            set(3, c, new Pawn(Side.BLACK));
        }

        // 红方
        set(9, 0, new Rook(Side.RED));
        set(9, 1, new Horse(Side.RED));
        set(9, 2, new Elephant(Side.RED));
        set(9, 3, new Advisor(Side.RED));
        set(9, 4, new King(Side.RED));
        set(9, 5, new Advisor(Side.RED));
        set(9, 6, new Elephant(Side.RED));
        set(9, 7, new Horse(Side.RED));
        set(9, 8, new Rook(Side.RED));

        set(7, 1, new Cannon(Side.RED));
        set(7, 7, new Cannon(Side.RED));

        for (int c = 0; c < 9; c += 2) {
            set(6, c, new Pawn(Side.RED));
        }
    }
}
