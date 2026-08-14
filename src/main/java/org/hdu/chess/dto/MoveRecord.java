package org.hdu.chess.dto;

import org.hdu.chess.model.ChessPiece;
import org.hdu.chess.model.Side;

public record MoveRecord(int fromRow, int fromCol, int toRow, int toCol,
                          ChessPiece moved, ChessPiece captured, Side movedSide) {
}
