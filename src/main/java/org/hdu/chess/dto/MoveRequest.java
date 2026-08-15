package org.hdu.chess.dto;

public record MoveRequest(Cell from, Cell to) {

    public record Cell(int row, int col) {
    }
}
